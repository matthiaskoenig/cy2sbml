package cysbml.miriam;

import java.util.Properties;

import uk.ac.ebi.miriam.lib.*;

public class MiriamWSExample
{
    public static void main(String[] args)
    {
    	// Set the proxy properties
    	Properties props= new Properties(System.getProperties());
    	props.put("http.proxySet", "true");
    	props.put("http.proxyHost", "proxy.charite.de");
    	props.put("http.proxyPort", "8080");
    	Properties newprops = new Properties(props);
    	System.setProperties(newprops);
    	
        // Creation of the link to the Web Services
    	// TODO: Set the proxy for the MIRIAM Link
        MiriamLink link = new MiriamLink();
        
        // Sets the address to access the Web Services
        link.setAddress("http://www.ebi.ac.uk/miriamws/main/MiriamWebServices");
        
        // Gets some information about the library
        System.out.print("0. Are you using the latest Java library available: ");
        System.out.println(link.isLibraryUpdated());
        
        // Gets the URI of a data type
        System.out.print("1. (official) URI of 'ChEBI': ");
        System.out.println(link.getDataTypeURI("ChEBI"));
        
        // Gets the physical locations corresponding to an obsolete MIRIAM URI
        System.out.println("2. Physical locations available for 'http://www.pubmed.gov/#16333295':");
        for (String loc: link.getLocations("http://www.pubmed.gov/#16333295"))
        {
            System.out.println("   - " + loc);
        }
        
        // Gets the physical locations corresponding to a MIRIAM URI
        System.out.println("3. Physical locations available for 'urn:miriam:obo.go:GO%3A0045202':");
        for (String loc: link.getLocations("urn:miriam:obo.go:GO%3A0045202"))
        {
            System.out.println("   - " + loc);
        }
        
        // Converts an obsolete URI into the valid one
        System.out.print("4. URI convertion: http://www.ebi.ac.uk/chebi/#CHEBI:17891 --> ");
        System.out.println(link.getMiriamURI("http://www.ebi.ac.uk/chebi/#CHEBI:17891"));
        
        // Tests if the URI of a data type is obsolete or not
        System.out.print("5. Is 'http://www.ebi.ac.uk/IntEnz/' deprecated? ");
        System.out.println(link.isDeprecated("http://www.ebi.ac.uk/IntEnz/"));
        
        // Gets the official URI of a data type
        System.out.println("6. ...so the official URI is: " + link.getOfficialDataTypeURI("http://www.ebi.ac.uk/IntEnz/"));
        
        // Test if the identifier of an entity matches the pattern stored for its data type
        System.out.print("7. Is 'X62158' a valid identifier for a Uniprot entity? ");
        System.out.println(link.checkRegExp("X62158", "urn:lsid:uniprot.org"));      
        
        // and much more...
    }
}
