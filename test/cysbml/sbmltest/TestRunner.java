package cysbml.sbmltest;

import java.io.File;

import cysbml.CySBMLPlugin;
import cysbml.logging.LogCySBML;
import cytoscape.plugin.PluginManager;

public class TestRunner {

	/** Performs all the tests with the plugin.
	 * Import of all the SBML models in the suite and all BioModels. */
	public static void runTests(){
		String folder = getFolderWithTestSBMLs();
		LogCySBML.info("Folder with test SBMLs: " + folder);
		AllTests tests = new AllTests(folder);
		tests.performTests();
	}
	
	public static String getFolderWithTestSBMLs(){
		return PluginManager.getPluginManager().getPluginManageDirectory() + 
				 File.separator + CySBMLPlugin.NAME + "test";
	}
}
