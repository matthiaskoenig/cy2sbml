package cysbml.sbmltest;

import java.io.File;

import cysbml.CySBML;
import cytoscape.plugin.PluginManager;

public class CaseRunner {
	/** Performs all the tests with the plugin.
	 * Import of all the SBML models in the suite and all BioModels. */
	public static void runTests(){
		String folder = getFolderWithTestSBMLs();
		CySBML.LOGGER.info("Folder with test SBMLs: " + folder);
		AllCases tests = new AllCases(folder);
		tests.performTests();
	}
	
	public static String getFolderWithTestSBMLs(){
		return PluginManager.getPluginManager().getPluginManageDirectory() + 
				 File.separator + CySBML.NAME + "test";
	}
}
