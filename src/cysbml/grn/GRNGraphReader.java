package cysbml.grn;

import java.util.*;


import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.GraphReader;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.algorithms.GridNodeLayout;
import cytoscape.view.CyNetworkView;

import cysbml.CySBMLConstants;
import cysbml.SBMLGraphReader;
import cysbml.gui.NavigationPanel;
import cysbml.visual.VisualStyleManager;
import cysbml.visual.VisualStyleManager.CustomStyle;


public class GRNGraphReader extends SBMLGraphReader implements GraphReader {	
	CyNetwork sbmlNetwork;
	Model model; 
	CyAttributes nAtts;
	CyAttributes eAtts;
	
	public GRNGraphReader(SBMLDocument document, CyNetwork sbmlNetwork) {
	
		super();
		this.document = document;
		this.sbmlNetwork = sbmlNetwork;
		model = document.getModel();
		nAtts = Cytoscape.getNodeAttributes();
		eAtts = Cytoscape.getEdgeAttributes();
	}
	
	@Override
	public void read(){	
		createCytoscapeGraphGRN();
	}
	
	private void createCytoscapeGraphGRN(){
		networkName = createNetworkName();
		nodeIds = new ArrayList<Integer>();
		edgeIds = new ArrayList<Integer>();
		addNodesToGRN();
		addEdgesToGRN();
	}

	private String createNetworkName(){		
		String netId = sbmlNetwork.getIdentifier();
		String name = netId + "_GRN";
		return name;
	}
	
	public void addNodesToGRN() {
		CyAttributes nAtts = Cytoscape.getNodeAttributes();
		for (Object obj: sbmlNetwork.nodesList()){
			CyNode node = (CyNode) obj;
			String type = (String) nAtts.getAttribute(node.getIdentifier(), CySBMLConstants.ATT_SBOTERM);
			if (GeneRegulatoryNetwork.speciesSBOforGRN.contains(type)){
				nodeIds.add(node.getRootGraphIndex());
			}
		}	
	}
	
	public void addEdgesToGRN() {
		for (Object obj: sbmlNetwork.nodesList()){
			CyNode node = (CyNode) obj;
			String type = (String) nAtts.getAttribute(node.getIdentifier(), CySBMLConstants.ATT_TYPE);
			if (type.equals(CySBMLConstants.NODETYPE_REACTION)){
				addEdgesToGRNForReaction(node);
			}
		}		
	}
	
	private void addEdgesToGRNForReaction(CyNode reaction){
		int[] inEdgeInds = sbmlNetwork.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, true, false);
		int[] outEdgeInds = sbmlNetwork.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, false, true);
		
		for (int in: inEdgeInds){
			CyEdge inEdge  = (CyEdge) sbmlNetwork.getEdge(in);
			String inEdgeSBO = (String) eAtts.getAttribute(inEdge.getIdentifier(), CySBMLConstants.ATT_SBOTERM);
			CyNode n1 = (CyNode) inEdge.getSource();
			if (inEdgeSBO == null){
				continue;
			}
			for (int out: outEdgeInds){
				// Get the two edges and end nodes;  n1 --inEdge-> reaction --outEdge-> n2 
				CyEdge outEdge = (CyEdge) sbmlNetwork.getEdge(out);
				String outEdgeSBO = (String) eAtts.getAttribute(outEdge.getIdentifier(), CySBMLConstants.ATT_SBOTERM);
				System.out.println("OUT edge: " + outEdge.getIdentifier());
				
				CyNode n2 = (CyNode) outEdge.getTarget();				
				if (outEdgeSBO == null){
					continue;
				}
				// Edges to add
				if (GeneRegulatoryNetwork.productSBOforGRN.contains(outEdgeSBO) &&
					GeneRegulatoryNetwork.modifierSBOforGRN.contains(inEdgeSBO)){
						CyEdge edge = Cytoscape.getCyEdge(n1, n2, Semantics.INTERACTION, inEdgeSBO, true);
						edgeIds.add(edge.getRootGraphIndex());
					
						// Set reversibility from reaction
						eAtts.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, inEdgeSBO);
				}	
			}	
		}
	}	
		
	@Override
	public void doPostProcessing(CyNetwork network) {
		// [1] Apply Layout
		applyLayout(network);
		
		// [2] Set Layout
		VisualStyleManager.setVisualStyleForNetwork(network, CustomStyle.GRN_STYLE);
		
		// [3] Update the CySBML Navigator
		updateNavigationPanel(network);
	}
	
	@Override
	public void updateNavigationPanel(CyNetwork network){
		NavigationPanel panel = NavigationPanel.getInstance();
		panel.putSBMLDocumentForGRN(getNetworkName(), document, network);
	}
	
	private void applyLayout(CyNetwork network){
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		if (layout == null){
			layout = new GridNodeLayout();
		}
		CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
		view.applyLayout(layout);
	}
	
}
