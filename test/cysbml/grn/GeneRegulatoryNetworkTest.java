package cysbml.grn;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;

public class GeneRegulatoryNetworkTest {

	@Test
	public void testIsSBMLDocumentGRN() throws XMLStreamException, IOException {
		// test all the networks under example
		
		// get all the xml files under examples/grn
		String folderName = "./examples/grn";
		File folder = new File(folderName);
		List<File> files = getXMLFilesFromFolder(folder);
		for (File f: files){
			SBMLDocument doc = SBMLReader.read(f);
			boolean isGRN = GeneRegulatoryNetwork.isSBMLDocumentGRN(doc);
			System.out.println(f.getName() + " is GRN: " + isGRN);
			assertTrue(isGRN);
		}
	}
	
	public List<File> getXMLFilesFromFolder(final File folder) {
		List<File> files = new LinkedList<File>();
		for (final File fileEntry : folder.listFiles()) {
	    	if (fileEntry.isDirectory()) {
	          
	        } else {
	        	if (fileEntry.getName().endsWith(".xml")){
	        		files.add(fileEntry);
		            System.out.println(fileEntry.getName());
	        	}
	        }
	    }
	    return files;
	}

}
