package de.montiarcautomaton.generator.codegen.xtend.util

import java.io.File
import java.util.List
import java.util.ArrayList
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

class CMake {
	
	def static printCMake(File[] files, ComponentSymbol comp, String hwcPath, String libraryPath) {
		
		
		return '''
		cmake_minimum_required(VERSION 3.02)
		project(«comp.name»)
		
		set(CMAKE_CXX_STANDARD 11)
		
		
		include_directories("«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»")
		include_directories("«libraryPath.replace("\\","/")»")
		include_directories(.)
		file(GLOB SOURCES 
		"./*.cpp"
		"./*.h"
		"«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»/*.cpp"
		"«hwcPath.replace("\\","/")»/«comp.name.toFirstLower»/*.h"
		"«libraryPath.replace("\\","/")»/*.cpp"
		"«libraryPath.replace("\\","/")»/*.h")
		
		add_executable(«comp.name» ${SOURCES})
		'''
		}
	
}