package sim.generic;

import sim.IScheduler;
import sim.error.ISimulationErrorHandler;

/**
 * The interface for a component in the simulation framework.
 * 
 * <br>
 * <br>
 * Copyright (c) 2010 RWTH Aachen. All rights reserved.
 * 
 * @author Arne Haber
 * @version 28.10.2008
 */
public interface IComponent {
    
    /**
     * Checks, if all constraints are hold. Injured constraints are reported
     * to the associated {@link ISimulationErrorHandler}.
     */
    void checkConstraints();
    
    /**
     * @return the _errorHandler
     */
    ISimulationErrorHandler getErrorHandler();
    
    /**
     * 
     * @return the name of this component.
     */
    String getComponentName();
    
    /**
     * Initializes all subcomponents, sets the scheduler from the contained
     * ports and combines them.
     * 
     * @param scheduler scheduler to set
     * @param errorHandler handles all occurred errors
     */
    void setup(IScheduler scheduler, ISimulationErrorHandler errorHandler);
}