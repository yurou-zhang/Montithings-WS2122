//package components;
package components.body.subcomponents;

/*
 * Invalid model.
 *
 * Formerly named "CG9" in MontiArc3 with 6 errors
 *
 * @implements TODO
 */
component UniqueInnerCompDefinition {
    
    port 
        in String sIn,
        out String sOut;
        
    component NotUniqueDef {
        port 
            in String sIn,
            in Integer inInt,
            out String sOut;
    }
    
    component NotUniqueDef {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniquDefWithInstance {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniquDefWithInstance instanceName {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniqueDefBothInstanceNames n1 {
        port 
            in String sIn,
            out String sOut;
    }
    
    component NotUniqueDefBothInstanceNames n2 {
        port 
            in String sIn,
            out String sOut;
    }
}