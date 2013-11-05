package cysbml.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
