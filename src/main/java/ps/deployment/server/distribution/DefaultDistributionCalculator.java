package ps.deployment.server.distribution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.tuple.Pair;
import org.jpl7.Atom;
import org.jpl7.Compound;
import org.jpl7.PrologException;
import org.jpl7.Query;
import org.jpl7.Term;
import org.jpl7.Util;
import org.jpl7.Variable;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import ps.deployment.server.data.DeployClient;
import ps.deployment.server.data.Distribution;
import ps.deployment.server.distribution.suggestion.Suggestion;
import ps.deployment.server.exception.DistributionException;

public class DefaultDistributionCalculator implements IDistributionCalculator {
  
  private static final String PROLOG_VAR_DROPPEDCONSTRAINTS = "DroppedConstraints";
  
  private String plFacts, plQuery;
  private File fileFacts, fileQuery, workingDir;
  
  /**
   * @param plFacts facts as Prolog-source
   * @param plQuery query as Prolog-source
   * @param workingDir The (temporary) working directory.
   */
  public DefaultDistributionCalculator(String plFacts, String plQuery, File workingDir) {
    this.plFacts = plFacts;
    this.plQuery = plQuery;
    this.workingDir = workingDir.getAbsoluteFile();
    this.fileFacts = new File(this.workingDir, "facts.pl");
    this.fileQuery = new File(this.workingDir, "query.pl");
  }
  
  private Distribution computeDistributionSync(Pair<Collection<DeployClient>,List<String>> param) throws DistributionException {
    Collection<DeployClient> deployTargets = param.getLeft();
    List<String> components = param.getRight();
    try {
      prepareWorkspace();
      
      // change working directory
      new Query(new Compound("working_directory", wrap(new Variable(), new Atom(workingDir.getAbsolutePath())))).oneSolution();
      // load Prolog files
      new Query(new Compound("consult", wrap(new Atom("query.pl")))).oneSolution();
      
      // compute distribution solution
      Query query = new Query(this.constructQueryTerm(components, false));
      Map<String, Term> solution = query.oneSolution();
      Distribution distribution = null;
      
      // parse solution
      if (solution != null) {
        Map<String, List<String>> dmap = new HashMap<>();
        
        // initialize empty list for every target 
        for(DeployClient client : deployTargets) {
          dmap.put(client.getClientID(), new ArrayList<>());
        }
        
        for (Entry<String, Term> e : solution.entrySet()) {
          String instanceName = e.getKey();
          String[] clients = Util.atomListToStringArray(e.getValue());
          
          for (String clientID : clients) {
            List<String> instances = dmap.get(clientID);
            instances.add(instanceName);
          }
        }
        
        distribution = Distribution.from(dmap);
      }
      
      cleanup();
      return distribution;
    }
    catch (Exception e) {
      e.printStackTrace();
      // wrap in DistributionException for CompletableFuture-compatibility
      throw new DistributionException(e);
    }
  }
  
  private Map<Distribution, List<Suggestion>> computeDistributionSuggestionSync(DistributionSuggestionRequest request) throws DistributionException {
    Collection<DeployClient> deployTargets = request.getTargets();
    List<String> components = request.getComponents();
    try {
      prepareWorkspace();
      
      // change working directory
      new Query(new Compound("working_directory", wrap(new Variable(), new Atom(workingDir.getAbsolutePath())))).oneSolution();
      // load Prolog files
      new Query(new Compound("consult", wrap(new Atom("query.pl")))).oneSolution();
      
      // compute distribution solutions
      Query query = new Query(this.constructQueryTerm(components, true));
      Map<Distribution, List<Suggestion>> results = new LinkedHashMap<>();
      
      int index = 0;
      int count = 0;
      
      while(hasMoreSolutions(query) && count < request.getMaxCount()) {
        Map<String,Term> solution = query.nextSolution();
        
        index++;
        if(index <= request.getOffset()) {
          // Ignore this suggestion as it should be skipped.
          continue;
        }
        
        // parse solution
        if (solution != null) {
          // extract DroppedConstraints from solution
          String[] droppedConstraints = Util.atomListToStringArray(solution.get(PROLOG_VAR_DROPPEDCONSTRAINTS));
          List<Suggestion> suggestions = new ArrayList<Suggestion>(droppedConstraints.length);
          Lists.newArrayList(droppedConstraints).stream()
            .map(Suggestion::parseProlog)
            .forEach(suggestions::add);
          System.out.println(suggestions);
          solution.remove(PROLOG_VAR_DROPPEDCONSTRAINTS);
          
          Map<String, List<String>> dmap = new HashMap<>();
          
          // initialize empty list for every target
          for(DeployClient client : deployTargets) {
            dmap.put(client.getClientID(), new ArrayList<>());
          }
          
          for (Entry<String, Term> e : solution.entrySet()) {
            String instanceName = e.getKey();
            String[] clients = Util.atomListToStringArray(e.getValue());
            if(clients == null) clients = new String[0];
            
            for (String clientID : clients) {
              List<String> instances = dmap.get(clientID);
              instances.add(instanceName);
            }
          }
          
          Distribution distribution = Distribution.from(dmap);
          results.put(distribution, suggestions);
        }
        
        count++;
      }
      
      cleanup();
      return results;
    }
    catch (Exception e) {
      // wrap in DistributionException for CompletableFuture-compatibility
      throw new DistributionException(e);
    }
  }
  
  private Term constructQueryTerm(List<String> components, boolean withDroppedConstraints) {
    LinkedList<Variable> vars = new LinkedList<>();
    components.stream()
      .map((str) -> new Variable(str))
      .forEach(vars::add);
    
    // add variable for constraint output
    if(withDroppedConstraints) {
      vars.add(new Variable(PROLOG_VAR_DROPPEDCONSTRAINTS));
    }
    
    // select proper goal
    String goalName = withDroppedConstraints ? "distribution_suggest" : "distribution";
    return new Compound(goalName, vars.toArray(new Variable[vars.size()]));
  }
  
  private void prepareWorkspace() throws IOException {
    // Ensure the working directory exists.
    this.workingDir.mkdirs();
    
    Files.write(plFacts, fileFacts, StandardCharsets.UTF_8);
    Files.write(plQuery, fileQuery, StandardCharsets.UTF_8);
    
    // Write helpers.pl prolog file.
    File fileHelpers = new File(workingDir, "helpers.pl");
    try (InputStream inputHelpers = getClass().getResourceAsStream("/scripts/helpers.pl")) {
      try (OutputStream outputHelpers = new FileOutputStream(fileHelpers)) {
        ByteStreams.copy(inputHelpers, outputHelpers);
      }
    }
    
    // Retract facts from previous queries.
    new Query(new Compound("retract", wrap(new Atom("property")))).oneSolution();
    new Query(new Compound("retract", wrap(new Atom("distribution")))).oneSolution();
  }
  
  private void cleanup() throws IOException {
    if (this.fileFacts != null) {
      this.fileFacts.delete();
    }
    if (this.fileQuery != null) {
      this.fileQuery.delete();
    }
  }
  
  @Override
  public CompletableFuture<Distribution> computeDistribution(Collection<DeployClient> targets, List<String> components) {
    return CompletableFuture.supplyAsync(() -> Pair.of(targets, components)).thenApply(this::computeDistributionSync);
  }
  
  @Override
  public CompletableFuture<Map<Distribution, List<Suggestion>>> computeDistributionSuggestion(DistributionSuggestionRequest request) {
    return CompletableFuture.supplyAsync(() -> request).thenApply(this::computeDistributionSuggestionSync);
  }
  
  private static Term[] wrap(Term... terms) {
    return terms;
  }
  
  /**
   * Calls {@code query.hasMoreSolutions()}. In case this call throws a PrologException, false is returned.
   * */
  private static boolean hasMoreSolutions(Query query) {
    try {
      return query.hasMoreSolutions();
    } catch(PrologException e) {
      return false;
    }
  }
  
}
