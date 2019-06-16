/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator;

import de.montiarcautomaton.generator.codegen.xtend.MAAGenerator;
import de.monticore.cd2pojo.Modelfinder;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguage;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * TODO
 *
 * @author (last commit) JFuerste
 */
public class MAAGeneratorTool extends MontiArcTool {

  public void generate(File modelPath, File target, File hwcPath) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath, MontiArcLanguage.FILE_ENDING);

    Log.info("Initializing symboltable", "MontiArcGeneratorTool");
    Scope symTab = initSymbolTable(modelPath, hwcPath);

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

      // 2. parse + resolve model
      Log.info("Parsing model:"+ qualifiedModelName, "MontiArcGeneratorTool");
      ComponentSymbol comp = symTab.<ComponentSymbol> resolve(qualifiedModelName, ComponentSymbol.KIND).get();

      // 3. check cocos
      Log.info("Check model: " + qualifiedModelName, "MontiArcGeneratorTool");
      checkCoCos((ASTMontiArcNode) comp.getAstNode().get());

      // 4. generate
      Log.info("Generate model: " + qualifiedModelName, "MontiArcGeneratorTool");
      MAAGenerator.generateAll(Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(), hwcPath, comp, foundModels);
      
      //4.5 generate make file
      Log.info("Generate CMake file", "MontiArcGeneratorTool");
      MAAGenerator.generateMakeFile(Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(), comp);
    }

  }

}