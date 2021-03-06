// (c) https://github.com/MontiCore/monticore
package generation;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.ConfigParams;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(Theories.class)
public class SimpleGenerationTest {

  protected static final Path MODELPATH = Paths.get("src/test/resources/models/hierarchy/");

  protected static final Path HWCPATH = Paths.get("src/test/resources/hwc/hierarchy/");

  protected static final Path TARGETPATH = Paths
    .get("target/generated-test-sources/SimpleGenerationTest");

  protected static final Path RTEPATH = Paths.get("src/main/resources/rte/montithings-RTE");

  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    try {
      FileUtils.copyDirectoryToDirectory(RTEPATH.toFile(), Paths.get("target/").toFile());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  @DataPoints("targetPlatforms")
  public static ConfigParams.TargetPlatform[] targetPlatforms = {
    ConfigParams.TargetPlatform.GENERIC,
    ConfigParams.TargetPlatform.DSA_VCG,
    ConfigParams.TargetPlatform.DSA_LAB,
    ConfigParams.TargetPlatform.ARDUINO
  };

  @DataPoints("splittingModes")
  public static ConfigParams.SplittingMode[] splittingModes = {
    ConfigParams.SplittingMode.OFF,
    ConfigParams.SplittingMode.LOCAL,
    ConfigParams.SplittingMode.DISTRIBUTED
  };

  @DataPoints("messageBroker")
  public static ConfigParams.MessageBroker[] messageBrokers = {
    ConfigParams.MessageBroker.OFF,
    ConfigParams.MessageBroker.MQTT,
    ConfigParams.MessageBroker.DDS
  };

  @Theory
  public void generatorDoesNotCrash(
    @FromDataPoints("targetPlatforms") ConfigParams.TargetPlatform targetPlatform,
    @FromDataPoints("splittingModes") ConfigParams.SplittingMode splittingMode,
    @FromDataPoints("messageBroker") ConfigParams.MessageBroker messageBroker
  ) throws IOException {
    MontiThingsGeneratorTool script = new MontiThingsGeneratorTool();
    Log.info("Target: " + targetPlatform.toString() + ", " +
      "Splitting: " + splittingMode.toString() + ", " +
      "Broker: " + messageBroker.toString(),
      "SimpleGenerationTest");
    ConfigParams params = new ConfigParams();
    params.setTargetPlatform(targetPlatform);
    params.setSplittingMode(splittingMode);
    params.setMessageBroker(messageBroker);
    params.setHwcPath(HWCPATH.toFile());
    params.setProjectVersion("unspecified");
    params.setMainComponent("hierarchy.Example");
    script.generate(MODELPATH.toFile(), TARGETPATH.toFile(), HWCPATH.toFile(), null, params);
  }

}
