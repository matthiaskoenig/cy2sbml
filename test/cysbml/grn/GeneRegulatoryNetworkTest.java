package cysbml.grn;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;


@RunWith(value = Parameterized.class)
public class GeneRegulatoryNetworkTest {
    
	private File file;
    
    public GeneRegulatoryNetworkTest(File file){
    	this.file = file;
    }
	
	@Parameters
    public static Collection<Object[]> data() {        
		// get all the xml files under examples/grn
		String folderName = "./examples/grn";
		File folder = new File(folderName);
		List<File> files = getXMLFilesFromFolder(folder);
		List<Object[]> data = new LinkedList<Object[]>();
		for (File file: files){
			Object[] obj = {file};
			data.add(obj);
		}
		return data;
    }

    @Test
	public void isSBMLDocumentGRNTest() throws XMLStreamException, IOException {
		SBMLDocument doc = SBMLReader.read(file);
		boolean isGRN = GeneRegulatoryNetwork.isSBMLDocumentGRN(doc);
		assertTrue(isGRN);
	}
	
	private static List<File> getXMLFilesFromFolder(final File folder) {
		List<File> files = new LinkedList<File>();
		System.out.println("--------------------------------");
		for (final File fileEntry : folder.listFiles()) {
	    	if (fileEntry.isDirectory()) {
	          
	        } else {
	        	if (fileEntry.getName().endsWith(".xml")){
	        		files.add(fileEntry);
		            System.out.println(fileEntry.getName());
	        	}
	        }
	    }
		System.out.println("--------------------------------");
	    return files;
	}

}

