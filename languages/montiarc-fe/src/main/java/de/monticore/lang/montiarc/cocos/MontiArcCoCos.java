/*
 * Copyright (c) 2015 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.cocos;

import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTConnectorCoCo;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTSimpleConnectorCoCo;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcCoCoChecker;

/**
 * Bundle of CoCos for the MontiArc language.
 *
 * @author Robert Heim
 */
public class MontiArcCoCos {
  public static MontiArcCoCoChecker createChecker() {
    return new MontiArcCoCoChecker()
        .addCoCo(new UniqueConstraint())
        .addCoCo(new UniquePorts())
        .addCoCo(new ComponentInstanceNamesUnique())
        .addCoCo(new PortUsage())
        .addCoCo(new SubComponentsConnected())
        .addCoCo(new PackageLowerCase())
        .addCoCo(new ComponentCapitalized())
        .addCoCo(new DefaultParametersHaveCorrectOrder())
        .addCoCo(new ComponentWithTypeParametersHasInstance())
        .addCoCo(new TypeParameterNamesUnique())
        .addCoCo(new ParameterNamesUnique())
        .addCoCo(new TopLevelComponentHasNoInstanceName())
        .addCoCo((MontiArcASTConnectorCoCo) new ConnectorEndPointCorrectlyQualified())
        .addCoCo((MontiArcASTSimpleConnectorCoCo) new ConnectorEndPointCorrectlyQualified())
        .addCoCo(new InPortUniqueSender())
        .addCoCo(new SimpleConnectorSourceExists())
        .addCoCo(new ReferencedSubComponentExists());
  }
}