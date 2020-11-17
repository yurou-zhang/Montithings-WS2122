// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen;

import de.se_rwth.commons.configuration.Configuration;
import de.se_rwth.commons.configuration.ConfigurationContributorChainBuilder;
import de.se_rwth.commons.configuration.DelegatingConfigurationContributor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MontiThingsConfiguration implements Configuration {
  public static final String CONFIGURATION_PROPERTY = "_configuration";

  public static final String DEFAULT_OUTPUT_DIRECTORY = "out";

  public static final String DEFAULT_HWC_DIRECTORY = "src";

  public static final ConfigParams configParams = new ConfigParams();

  /**
   * The names of the specific MontiArc options used in this configuration.
   */
  public enum Options {

    MODELPATH("modelPath"), MODELPATH_SHORT("mp"),TESTPATH("testPath"), HANDWRITTENCODEPATH("handwrittenCode"),
    HANDWRITTENCODEPATH_SHORT("hwc"), OUT("out"), OUT_SHORT("o"), PLATFORM("platform"),
    SPLITTING("splitting");

    String name;

    Options(String name) {
      this.name = name;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }

  }

  private final Configuration configuration;

  /**
   * Factory method for {@link TemplateClassGeneratorConfiguration}.
   */
  public static MontiThingsConfiguration withConfiguration(Configuration configuration) {
    return new MontiThingsConfiguration(configuration);
  }

  /**
   * Constructor for {@link TemplateClassGeneratorConfiguration}
   */
  private MontiThingsConfiguration(Configuration internal) {
    this.configuration = ConfigurationContributorChainBuilder.newChain()
        .add(DelegatingConfigurationContributor.with(internal)).build();
    configParams.setTargetPlatform(getPlatform());
    configParams.setSplittingMode(getSplittingMode());
    configParams.hwcTemplatePath = Paths.get(getHWCPath().getAbsolutePath());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValues()
   */
  @Override
  public Map<String, Object> getAllValues() {
    return this.configuration.getAllValues();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAllValuesAsStrings()
   */
  @Override
  public Map<String, String> getAllValuesAsStrings() {
    return this.configuration.getAllValuesAsStrings();
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBoolean(java.lang.String)
   */
  @Override
  public Optional<Boolean> getAsBoolean(String key) {
    return this.configuration.getAsBoolean(key);
  }

  public Optional<Boolean> getAsBoolean(Enum<?> key) {
    return getAsBoolean(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsBooleans(java.lang.String)
   */
  @Override
  public Optional<List<Boolean>> getAsBooleans(String key) {
    return this.configuration.getAsBooleans(key);
  }

  public Optional<List<Boolean>> getAsBooleans(Enum<?> key) {
    return getAsBooleans(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDouble(java.lang.String)
   */
  @Override
  public Optional<Double> getAsDouble(String key) {
    return this.configuration.getAsDouble(key);
  }

  public Optional<Double> getAsDouble(Enum<?> key) {
    return getAsDouble(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsDoubles(java.lang.String)
   */
  @Override
  public Optional<List<Double>> getAsDoubles(String key) {
    return this.configuration.getAsDoubles(key);
  }

  public Optional<List<Double>> getAsDoubles(Enum<?> key) {
    return getAsDoubles(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsInteger(java.lang.String)
   */
  @Override
  public Optional<Integer> getAsInteger(String key) {
    return this.configuration.getAsInteger(key);
  }

  public Optional<Integer> getAsInteger(Enum<?> key) {
    return getAsInteger(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsIntegers(java.lang.String)
   */
  @Override
  public Optional<List<Integer>> getAsIntegers(String key) {
    return this.configuration.getAsIntegers(key);
  }

  public Optional<List<Integer>> getAsIntegers(Enum<?> key) {
    return getAsIntegers(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsString(java.lang.String)
   */
  @Override
  public Optional<String> getAsString(String key) {
    return this.configuration.getAsString(key);
  }

  public Optional<String> getAsString(Enum<?> key) {
    return getAsString(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getAsStrings(java.lang.String)
   */
  @Override
  public Optional<List<String>> getAsStrings(String key) {
    return this.configuration.getAsStrings(key);
  }

  public Optional<List<String>> getAsStrings(Enum<?> key) {
    return getAsStrings(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValue(java.lang.String)
   */
  @Override
  public Optional<Object> getValue(String key) {
    return this.configuration.getValue(key);
  }

  public Optional<Object> getValue(Enum<?> key) {
    return getValue(key.toString());
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#getValues(java.lang.String)
   */
  @Override
  public Optional<List<Object>> getValues(String key) {
    return this.configuration.getValues(key);
  }

  public Optional<List<Object>> getValues(Enum<?> key) {
    return getValues(key.toString());
  }

  public File getModelPath() {
    Optional<String> modelPath = getAsString(Options.MODELPATH);
    if (modelPath.isPresent()) {
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    modelPath = getAsString(Options.MODELPATH_SHORT);
    if (modelPath.isPresent()) {
      Path mp = Paths.get(modelPath.get());
      return mp.toFile();
    }
    return null;
  }

  public File getTestPath() {
    Optional<String> testPath = getAsString(Options.TESTPATH);
    if (testPath.isPresent()) {
      Path mp = Paths.get(testPath.get());
      return mp.toFile();
    }
    else if(getModelPath()!=null) {
      Path defaultTestPath = getModelPath().toPath();
      for (int i = 0; i < 3; i++) {
        defaultTestPath = defaultTestPath.getParent();
        if(defaultTestPath==null){
          return null;
        }
      }
      if(Paths.get(defaultTestPath.toString(),"test","resources","gtests").toFile().isDirectory()) {
        return Paths.get(defaultTestPath.toString(), "test","resources","gtests").toFile();
      }
    }
    return new File("");
  }

  public File getHWCPath() {
    Optional<String> hwcPath = getAsString(Options.HANDWRITTENCODEPATH);
    if (hwcPath.isPresent()) {
      Path hwc = Paths.get(hwcPath.get());
      return hwc.toFile();
    }
    hwcPath = getAsString(Options.HANDWRITTENCODEPATH_SHORT);
    if (hwcPath.isPresent()) {
      Path hwc = Paths.get(hwcPath.get());
      return hwc.toFile();
    }
    return Paths.get(DEFAULT_HWC_DIRECTORY).toFile();
  }

  /**
   * Getter for the output directory stored in this configuration. A fallback
   * default is "out".
   *
   * @return output directory file
   */
  public File getOut() {
    Optional<String> out = getAsString(Options.OUT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    out = getAsString(Options.OUT_SHORT);
    if (out.isPresent()) {
      return new File(out.get());
    }
    // fallback default is "out"
    return new File(DEFAULT_OUTPUT_DIRECTORY);
  }

  public ConfigParams.TargetPlatform getPlatform() {
    Optional<String> platform = getAsString(Options.PLATFORM);
    if (platform.isPresent()) {
      switch (platform.get()) {
        case "GENERIC":
          return ConfigParams.TargetPlatform.GENERIC;
        case "DSA_VCG":
        case "l06":
        case "DSA":
        case "VCG":
          return ConfigParams.TargetPlatform.DSA_VCG;
        case "DSA_LAB":
        case "LAB":
          return ConfigParams.TargetPlatform.DSA_LAB;
        case "ARDUINO":
        case "ESP32":
          return ConfigParams.TargetPlatform.ARDUINO;
        default:
          throw new IllegalArgumentException("0xMT300 Platform " + platform + " in pom.xml is unknown");
      }
    }
    // fallback default is "generic"
    return ConfigParams.TargetPlatform.GENERIC;
  }

  public ConfigParams.SplittingMode getSplittingMode() {
    Optional<String> platform = getAsString(Options.SPLITTING);
    if (platform.isPresent()) {
      switch (platform.get()) {
        case "OFF":
          return ConfigParams.SplittingMode.OFF;
        case "LOCAL":
          return ConfigParams.SplittingMode.LOCAL;
        case "DISTRIBUTED":
          return ConfigParams.SplittingMode.DISTRIBUTED;
        default:
          throw new IllegalArgumentException("0xMT300 Platform " + platform + " in pom.xml is unknown");
      }
    }
    // fallback default is "generic"
    return ConfigParams.SplittingMode.OFF;
  }

  /**
   * @param files as String names to convert
   * @return list of files by creating file objects from the Strings
   */
  protected static List<File> toFileList(List<String> files) {
    return files.stream().collect(Collectors.mapping(file -> new File(file), Collectors.toList()));
  }

  /**
   * @see de.se_rwth.commons.configuration.Configuration#hasProperty(java.lang.String)
   */
  @Override
  public boolean hasProperty(String key) {
    return this.configuration.hasProperty(key);
  }
}
