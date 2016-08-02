package a;

component AutoConnectGenericUsageHierarchie {
    
    autoconnect type;
    
    port 
        in String strIn,
        out Object objOut;
    
    
    component Inner<T> sInner<Object> {
        port
            in T tIn,
            out T tOut;
    }
 
}