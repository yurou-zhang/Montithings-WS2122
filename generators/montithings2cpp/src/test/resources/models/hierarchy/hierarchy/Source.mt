// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {

  port
    out int value;

  //guarantee : value < 3;
  
  update interval 1s;
}
