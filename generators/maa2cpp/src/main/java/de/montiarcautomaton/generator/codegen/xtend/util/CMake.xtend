package de.montiarcautomaton.generator.codegen.xtend.util

import java.io.File
import java.util.List
import java.util.ArrayList
import montiarc._symboltable.ComponentSymbol
import de.montiarcautomaton.generator.helper.ComponentHelper

class CMake {
	
	def static printCMake(File[] files, ComponentSymbol comp) {
		
		var cppfiles = ComponentHelper.getCPPFilesString(files)
		
		return '''
		cmake_minimum_required(VERSION 3.14)
		project(«comp.name»)
		
		set(CMAKE_CXX_STANDARD 14)
		
		include_directories(.)
		
		add_executable(«comp.name»
			«FOR filename: cppfiles»
			«filename»
			«ENDFOR»
		)
		'''
		}
	
}