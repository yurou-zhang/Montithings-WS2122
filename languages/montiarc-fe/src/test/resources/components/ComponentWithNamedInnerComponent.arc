package components;

component ComponentWithNamedInnerComponent {
    
    port 
        in String sIn,
        out String sOut;
    
    component NamedInnerComponent instance {
        port 
            in String sIn,
            out String sOut;
    }
    
    connect sIn -> instance.sIn;
    connect instance.sOut -> sOut;
}