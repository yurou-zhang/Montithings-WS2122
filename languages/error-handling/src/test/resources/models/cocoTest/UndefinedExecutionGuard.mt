// (c) https://github.com/MontiCore/monticore
package cocoTest;

component UndefinedExecutionGuard{

    port in String inPort;

    behavior {
        if (inPort2 == "test") : compute();
        else compute();
    }
}