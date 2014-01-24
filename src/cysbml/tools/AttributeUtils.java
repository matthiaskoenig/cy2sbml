package cysbml.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import browser.AttributeBrowser;
import browser.AttributeBrowserPlugin;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

public class AttributeUtils {
	/** Get the set of different values used in the attribute. 
	 * Here for every attribute the values which can be used for the attribute subnetworks
	 * are defined.
	 * Identical function used for different types. */
	public static Set<Object> getValueSet(String attributeName){
		Set<Object> valueSet = new HashSet<Object>();
    	if (attributeName == null){ return valueSet;}
		
		CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
		@SuppressWarnings("unchecked")
		List<CyNode> nodeList = Cytoscape.getCyNodesList();
		for (CyNode node: nodeList){
			if (nodeAttrs.getAttribute(node.getIdentifier(), attributeName) != null){
				valueSet.add(nodeAttrs.getAttribute(node.getIdentifier(), attributeName));
			}
		}
		return valueSet;
	}
	
	
	/** Selects Node and Edge Attributes in the Attribute Table Viewer. */
	public static void selectTableAttributes(List<String> nAtts, List<String> eAtts){
		// selected node attributes
		AttributeBrowser nodeAttributeBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES);
		nodeAttributeBrowser.setSelectedAttributes(nAtts);
		
		// selected edge attributes
		AttributeBrowser edgeAttributeBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.EDGES);
		edgeAttributeBrowser.setSelectedAttributes(eAtts);
	}
}
