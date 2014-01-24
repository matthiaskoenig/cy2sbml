package cysbml.miriam;

import uk.ac.ebi.miriam.lib.MiriamLink;

public class MiriamWSInterface {
	public static final String MIRIAM_URL = "http://www.ebi.ac.uk/miriamws/main/MiriamWebServices";
	
	/** Test MIRIAM connection */
	   public static boolean testMIRIAMConnection() {
	       String test = null;
	   	try {
	       	   MiriamLink link = new MiriamLink();
	           link.setAddress(MIRIAM_URL);
	           test = link.getDataTypeURI("ChEBI");
	           
	       } catch (Exception e) {
	           // we couldn't connect:
	       	e.printStackTrace();
	           return false;
	       }
	   	return (test != null);
	   }
	   
}
