package cysbml.sbmltest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import cysbml.SBMLFilter;
import cysbml.logging.LogCySBML;


/**
 * Perform all tests with the sbmlreader.
 * All test-cases from SBML.org are used. 
 */
public class AllTests {
	private static File testFolder;
	private HashMap<String, File> sbmlFiles;
	
	/** Get a list of all SBML files in the given testFolder */ 
	public AllTests(String folderName){
		testFolder = new File(folderName);
		// Get all SBML files in the testFolder and subfolders sorted by name
		sbmlFiles = new HashMap<String, File>();
		getFilesInDirectory(testFolder, new SBMLFilter());
	}
	
	private void getFilesInDirectory(File a, SBMLFilter filter){
		if (a.isDirectory()) {
			File[] listOfFiles = a.listFiles();
		    if(listOfFiles!=null) {
		    	File tmp = null;
		    	for (int i = 0; i < listOfFiles.length; i++){
		    		tmp = listOfFiles[i]; 
		    		if (tmp.isDirectory()){
		    			getFilesInDirectory(tmp, filter);
		    		}
		    		else if (tmp.isFile() && filter.accept(tmp)){
		    			// Don't use the special xml files in the test folder
		    			if (!tmp.getName().contains("sedml"))
		    				sbmlFiles.put(tmp.getAbsolutePath(), tmp);
		    		}
		    	}
		    }
		}
	}
		
	/**
	 * Read the sbmlFiles for the class and store the results in a HashMap
	 * @throws IOException 
	 * @throws XMLStreamException 
	 */
	public Map<String, Boolean> performTests() {
		Map<String, Boolean> results = new HashMap<String, Boolean>();		
		Set<String> keySet = (Set<String>) sbmlFiles.keySet();
		ArrayList<String> keyList = new ArrayList<String>(keySet);     
		Collections.sort(keyList);
		
		SingleTest test = null;
		boolean success = false;
		int count = 0;
		for (String key: keyList){
			count++;
			//if (count < 800)
			//	continue;
			File f = sbmlFiles.get(key);
			System.out.println("Validation: "+count + " " +f.getName());
			try {
				test = new SingleTest(f.getCanonicalPath());
				success = test.performTest();
			} catch (Exception e) {
				success = false;
				LogCySBML.warning("TEST: SBML could not be imported -> " + f.getName());
				e.printStackTrace();
			}
			results.put(f.getName(), success);
			//if (count==20){break;}
		}
		return results;
	}
	
	/** Comparator for sorting file names in a folder */
	@SuppressWarnings("rawtypes")
	class FileNameComparator implements Comparator{
		// Comparator interface requires defining compare method.
		public int compare(File filea, File fileb) {
		//... Sort directories before files,
		// otherwise alphabetical ignoring case.
		if (filea.isDirectory() && !fileb.isDirectory()) {
		return -1;

		} else if (!filea.isDirectory() && fileb.isDirectory()) {
		return 1;

		} else {
		return filea.getName().compareToIgnoreCase(fileb.getName());
		}
		}
		public int compare(Object o1, Object o2) {
			return 0;
		}
	}
}
