package cysbml;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;


//import uk.ac.ebi.miriam.lib.MiriamLink;
import cytoscape.CytoscapeInit;
import cytoscape.util.ProxyHandler;

import cysbml.biomodel.BioModelWSInterface;
import cysbml.miriam.MiriamWSInterface;

public class CySBMLConnection {

	/** 
	 * Use the current Cytoscape proxy settings for the plugin.
	 */
	public static void setProxySettings(){
		// Get Cytoscape Settings
		String proxyHost = CytoscapeInit.getProperties().getProperty(
				ProxyHandler.PROXY_HOST_PROPERTY_NAME,null);
		String proxyPort = CytoscapeInit.getProperties().getProperty(
				ProxyHandler.PROXY_PORT_PROPERTY_NAME,null);
		
		Properties props= new Properties(System.getProperties());
		if (proxyHost == null && proxyPort == null){
			props.put("http.proxySet", "false");
			props.put("http.proxyHost", proxyHost);
			props.put("http.proxyPort", proxyPort);
		}
		else {
			props.put("http.proxySet", "true");
			props.put("http.proxyHost", proxyHost);
			props.put("http.proxyPort", proxyPort);
		}
		Properties newprops = new Properties(props);
		System.setProperties(newprops);
	}
	
	/** Test the necessary connections for CySBML. 
	 * 
	 * FIXME: offline mode must be possible, 
	 * TODO: perform in a separate Task;
	 */
	 public static boolean performConnectionTest() {
		 String proxyHost = CytoscapeInit.getProperties().getProperty(
	   				ProxyHandler.PROXY_HOST_PROPERTY_NAME,null);
	   	 String proxyPort = CytoscapeInit.getProperties().getProperty(
	   				ProxyHandler.PROXY_PORT_PROPERTY_NAME,null);
		 
		 boolean internet = testNetConnection (ProxyHandler.getProxyServer(), 2000);;
		 boolean miriam = MiriamWSInterface.testMIRIAMConnection();
		 boolean biomodel = BioModelWSInterface.testBioModelConnection(proxyHost, proxyPort);
		 
		 CySBML.LOGGER.info(String.format("[internet=%s | miriam=%s | biomodel=%s]", internet, miriam, biomodel));
		 if (!internet || !miriam || !biomodel){
			 /*
			 String msg = "<html><h3><span color=\"red\">cySBML Connection problems</span></h3>";
			 if (!internet){
				 msg += "Internet connection -> <b color=\"red\">false</b><br>";
			 }
			 if (!miriam){
				 msg += "MIRIAM WebService connection -> <b color=\"red\">false</b><br>";
			 }
			 if (!biomodel){
				 msg += "BioModel WebService connection -> <b color=\"red\">false</b><br>";
			 }
			 msg += "<p>For connection through a proxy set properties at<br>" +
				   	"<span color=\"blue\">Edit -> Preferences -> Proxy Server</p>";
			 JOptionPane.showMessageDialog (Cytoscape.getDesktop(), msg);
			 */
			 return false;
		 }
		 
		 return true;
	 }
	
	/** Test internet connection */
   private static boolean testNetConnection(final Proxy proxy, final int ms_timeout) {
   	final String WELL_KNOWN_URL = "http://google.com";
       try {
           final URL           u  = new URL(WELL_KNOWN_URL);
           URLConnection uc = null;
	    if (proxy != null) {
		uc = u.openConnection(proxy);
	    } else {
		uc = u.openConnection();
	    }
           uc.setAllowUserInteraction(false);
           uc.setUseCaches(false); // don't use a cached page
           uc.setConnectTimeout(ms_timeout);
           uc.connect();
           return true;
       } catch (IOException e) {
    	   CySBML.LOGGER.warning("No internet connection");
           return false;
       }
   }

}
