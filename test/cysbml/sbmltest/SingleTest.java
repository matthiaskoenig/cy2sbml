package cysbml.sbmltest;

import giny.model.Edge;
import giny.model.Node;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cysbml.SBMLGraphReader;
import cysbml.logging.LogCySBML;

/**
 * Reads the network for the current file.
 */
public class SingleTest {
	private String filename;
	
	public SingleTest(String filename){
		this.filename = filename;
	}
	
	/** Test for single file. Reads the SBML in an empty session. 
	 * Uses the CySBMLGraphReader with the given SBML file. 
	 */
	public boolean performTest(){
		boolean success = false;
		try {
			SBMLGraphReader reader = new SBMLGraphReader(filename);
			reader.read();
			Cytoscape.getCurrentNetwork().getIdentifier();
			//reader.doPostProcessing(Cytoscape.getCurrentNetwork());
			
			// Destroy the network nodes
			for (Object node : Cytoscape.getCyNodesList()) {
					Cytoscape.getRootGraph().removeNode((Node) node);	
			}
			// Destroy the edges
			for (Object edge : Cytoscape.getCyEdgesList()) {
					Cytoscape.getRootGraph().removeEdge((Edge) edge);	
			}
			// Destroy the network attributes
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			for (String name: nodeAttributes.getAttributeNames()){
				nodeAttributes.deleteAttribute(name);
			}
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			for (String name: edgeAttributes.getAttributeNames()){
				edgeAttributes.deleteAttribute(name);
			}
			
			success = true;
			System.out.println("--> Number of nodes:" + Cytoscape.getCyNodesList().size());
			System.out.println("--> Number of edges:" + Cytoscape.getCyEdgesList().size());
			
		} catch (Exception e) {
			e.printStackTrace();
			LogCySBML.warning("SingleTest FAIL: "+ filename);
		}
		return success;
	}
}
