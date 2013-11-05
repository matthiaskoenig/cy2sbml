package cysbml.tools;

import cytoscape.CytoscapeInit;
import cytoscape.util.ProxyHandler;

public class ProxyTools {
	public static String getCytoscapeProxyPort(){
		return CytoscapeInit.getProperties().getProperty(
				ProxyHandler.PROXY_PORT_PROPERTY_NAME,null);
	}
	public static String getCytoscapeProxyHost(){
		return CytoscapeInit.getProperties().getProperty(
				ProxyHandler.PROXY_HOST_PROPERTY_NAME,null);
	}
}
