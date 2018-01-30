package component.body.connectors;

/**
 * Invalid model. Port aOut does not exist.
 */
component SimpleConnectorSourceNonExistant {
  component A {
  }
  
  component B {
    port in String bIn;
  }
  
  component A myA [aOut -> b.bIn];
}