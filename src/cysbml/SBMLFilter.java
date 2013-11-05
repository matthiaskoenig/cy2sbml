package cysbml;

import java.io.File;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;
import cysbml.SBMLGraphReader;

/**
 * SBMLFilter extends CyFileFilter for integration into the Cytoscape ImportHandler
 * framework. The typical SBML file associations are associated to open with CySBML. 
 */
public class SBMLFilter extends CyFileFilter {
	/** SBML Files are Graphs. */
	private static String fileNature = ImportHandler.GRAPH_NATURE;

	private static String[] fileExtensions = { "xml", "sbml" };
	private static String description = "SBML files";

	public SBMLFilter() {
		super(fileExtensions, description, fileNature);
	}

	/** Indicates which files the SBMLFilter accepts (only endings important). */
	public boolean accept(File file) {
		String fileName = file.getName();
		boolean pass = false;
		//  file must have one of the file extensions.
		for (int i = 0; i < fileExtensions.length; i++) {
			if (fileName.endsWith(fileExtensions[i])) {
				pass = true;
			}
		}
		return pass;
	}

	/** Gets the SBML Graph Reader. */
	public GraphReader getReader(String fileName) {
		  return new SBMLGraphReader(fileName);
	}
}
