// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceNotFound;

import cocoTest.genericBindingTest.interfaceNotFound.*;
import cocoTest.genericBindingTest.interfaceNotFound.sensors.*;

component Bind<T1 extends SmokeSensorInterface, T2 extends IntermediateInterface> {

  /* Subcomponents */
   T2 intermediate;
   T1 smokeSensor;
   Accept a;

   smokeSensor.value -> a.accept;
}
