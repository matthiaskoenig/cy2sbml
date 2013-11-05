package cysbml.grn;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cysbml.CySBMLConstants;
import cysbml.logging.LogCySBML;
import cysbml.visual.VisualStyleManager;
import cysbml.visual.VisualStyleManager.CustomStyle;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.algorithms.GridNodeLayout;
import cytoscape.view.CyNetworkView;

/**
 * Creates additional networks from the SBML. 
 * SpeciesNetwork (consists of the species in the network with edges if the species 
 * are connected via a reaction.
 * ReactionNetwork (consists of the reactions in the network if connected via an intermediate
 * species.
 */
public class NetworkTransformationFactory {

	private CyNetwork network;
	private CyAttributes eAtts;
	private CyAttributes nAtts;
	
	public NetworkTransformationFactory(CyNetwork network){
		this.network = network;
		eAtts = Cytoscape.getEdgeAttributes();
		nAtts = Cytoscape.getNodeAttributes();
	}
	
	public void createSpeciesNetworkAndView(){
		CyNetwork net = createSpeciesNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());
		// FIXME: copy the node positions from the old view
		/*
		// Set positions according to orignal view
		for (Object obj: net.nodesList()){
			CyNode node = (CyNode) obj;
			CyNodeView nview = view.getNodeView(node);
		}
		*/
		view.redrawGraph(true, true);
		view.updateView();
	}
	public void createReactionNetworkAndView(){
		CyNetwork net = createReactionNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());
		view.redrawGraph(true, true);
		view.updateView();
	}
	
	public void createGeneRegulatoryNetworkAndView(){
		CyNetwork net = createGeneRegulatoryNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());
		
		// apply layout
		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		if (layout == null){
			layout = new GridNodeLayout();
		}
		view.applyLayout(layout);
		
		// apply the visual style for gene networks
		VisualStyleManager.setVisualStyleForNetwork(net, CustomStyle.GRN_STYLE);
		
		view.redrawGraph(true, true);
		view.updateView();
	}
	
	
	
	/** Creates Species Network (SN) */
	private CyNetwork createSpeciesNetwork(){
		if (network == null || network.equals(Cytoscape.getNullNetwork())){
			return Cytoscape.getNullNetwork();
		}
		String id = network.getIdentifier() + "_" + "SpeciesNet"; 
		CyNetwork net = Cytoscape.createNetwork(id, true);
		
		LogCySBML.info("Add species to SN");
		addSpeciesNodesToNetwork(net);
		LogCySBML.info("Add qual species to SN");
		addQualSpeciesNodesToNetwork(net);
		LogCySBML.info("Create species edges in SN");
		createSpeciesEdgesInNetwork(net);
		return net;
	}

	
	/** Creates Reaction Network (RN) */
	private CyNetwork createReactionNetwork(){
		if (network == null || network.equals(Cytoscape.getNullNetwork())){
			return Cytoscape.getNullNetwork();
		}
		String id = network.getIdentifier() + "_" + "ReactionNet"; 
		CyNetwork net = Cytoscape.createNetwork(id, true);
		// nodes
		addReactionNodesToNetwork(net);
		addQualTransitionNodesToNetwork(net);
		// edges
		createReactionEdgesInNetwork(net);
		return net;
	}
	
	
	/** Creates Gene Regulatory Network (GRN) */
	private CyNetwork createGeneRegulatoryNetwork(){
		if (network == null || network.equals(Cytoscape.getNullNetwork())){
			return Cytoscape.getNullNetwork();
		}
		String id = network.getIdentifier() + "_" + "GRN"; 
		CyNetwork net = Cytoscape.createNetwork(id, true);

		// add gene nodes
		addNodesToGeneRegulatoryNetwork(net);
		// collect all the interactions
		createGeneEdgesToGeneNetwork(net);
		
		return net;
	}
	
	
	private void addSpeciesNodesToNetwork(CyNetwork net){
		addNodesOfTypeToNetwork(CySBMLConstants.NODETYPE_SPECIES, net);		
	}
	private void addQualSpeciesNodesToNetwork(CyNetwork net){
		addNodesOfTypeToNetwork(CySBMLConstants.NODETYPE_QUAL_SPECIES, net);		
	}
	
	private void addReactionNodesToNetwork(CyNetwork net){
		addNodesOfTypeToNetwork(CySBMLConstants.NODETYPE_REACTION, net);		
	}
	private void addQualTransitionNodesToNetwork(CyNetwork net){
		addNodesOfTypeToNetwork(CySBMLConstants.NODETYPE_QUAL_TRANSITION, net);	
		
	}
	
	private void addNodesOfTypeToNetwork(String nodeType, CyNetwork net){
		for (CyNode node: getNodesOfType(nodeType)){
			net.addNode(node);	
		}
	}
		
	/** Get all nodes with the type from the origin network */
	private List<CyNode> getNodesOfType(String nodeType){
		List<CyNode> netNodes = new LinkedList<CyNode>();
		for (Object obj: network.nodesList()){
			CyNode node = (CyNode) obj;
			String type = (String) nAtts.getAttribute(node.getIdentifier(), CySBMLConstants.ATT_TYPE);
			if (type.equals(nodeType)){
				netNodes.add(node);
			}
		}
		return netNodes;
	}
	
	/** Add edges if species are connected via reactions. */
	private void createSpeciesEdgesInNetwork(CyNetwork net){
		for (CyNode reaction : getNodesOfType(CySBMLConstants.NODETYPE_REACTION)){
			createSpeciesEdgesInNetworkForReaction(net, reaction);
		}
		for (CyNode transition: getNodesOfType(CySBMLConstants.NODETYPE_QUAL_TRANSITION)){
			createSpeciesEdgesInNetworkForReaction(net, transition);
		}		
	}
	
	/** Add edges if reactions are connected via species. */
	private void createReactionEdgesInNetwork(CyNetwork net){
		for (CyNode species : getNodesOfType(CySBMLConstants.NODETYPE_SPECIES)){
			createReactionEdgesInNetworkForSpecies(net, species);
		}
		for (CyNode qualSpecies: getNodesOfType(CySBMLConstants.NODETYPE_QUAL_SPECIES)){
			createReactionEdgesInNetworkForSpecies(net, qualSpecies);
		}		
	}
	
	
	/** Check all species connected with the reaction. 
	 * TODO: unchecked for Qual networks */
	private void createSpeciesEdgesInNetworkForReaction(CyNetwork net, CyNode reaction){
		LogCySBML.info("Create species edges for reaction: " + reaction.getIdentifier());
		int[] inEdgeInds = network.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, true, false);
		int[] outEdgeInds = network.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, false, true);
		for (int in: inEdgeInds){
			// Get the two edges and end nodes;  n1 --inEdge-> reaction --outEdge-> n2 
			CyEdge inEdge  = (CyEdge) network.getEdge(in);
			String inEdgeType = (String) eAtts.getAttribute(inEdge.getIdentifier(), Semantics.INTERACTION);
			CyNode n1 = (CyNode) inEdge.getSource();
			
			for (int out: outEdgeInds){
				CyEdge outEdge = (CyEdge) network.getEdge(out);
				String outEdgeType = (String) eAtts.getAttribute(outEdge.getIdentifier(), Semantics.INTERACTION);	
				// only certain combination will be converted to edges in the compound network
				// TODO: better a strict positive list what to take
				if (   inEdgeType.equals(CySBMLConstants.EDGETYPE_REACTION_ACTIVATOR)
					|| inEdgeType.equals(CySBMLConstants.EDGETYPE_REACTION_INHIBITOR)
					|| inEdgeType.equals(CySBMLConstants.EDGETYPE_REACTION_MODIFIER)){
					// ignore the modifier edges
					continue;
				}
				CyNode n2 = (CyNode) outEdge.getTarget();
				
				// source and target node are in the network, add the respective edge between these nodes
				if (net.containsNode(n1) && net.containsNode(n2)){
					String type = inEdgeType + "2" + outEdgeType;
					CyEdge edge = Cytoscape.getCyEdge(n1, n2, Semantics.INTERACTION, type, true);
					
					// Set reversibility from reaction
					eAtts.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_REVERSIBLE, 
							(String) nAtts.getAttribute(reaction.getIdentifier(), CySBMLConstants.ATT_REVERSIBLE));
					
					net.addEdge(edge);
				}	
			}
		}	
	}
	
	private void createReactionEdgesInNetworkForSpecies(CyNetwork net, CyNode reaction){
		int[] inEdgeInds = network.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, true, false);
		int[] outEdgeInds = network.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, false, true);
		for (int in: inEdgeInds){
			// Get the two edges and end nodes;  n1 --inEdge-> reaction --outEdge-> n2 
			CyEdge inEdge  = (CyEdge) network.getEdge(in);
			String inEdgeType = (String) eAtts.getAttribute(inEdge.getIdentifier(), Semantics.INTERACTION);
			CyNode n1 = (CyNode) inEdge.getSource();
			
			for (int out: outEdgeInds){
				CyEdge outEdge = (CyEdge) network.getEdge(out);
				String outEdgeType = (String) eAtts.getAttribute(outEdge.getIdentifier(), Semantics.INTERACTION);
				// only certain combination will be converted to edges in the compound network
				if (   outEdgeType.equals(CySBMLConstants.EDGETYPE_REACTION_ACTIVATOR)
					|| outEdgeType.equals(CySBMLConstants.EDGETYPE_REACTION_INHIBITOR)
					|| outEdgeType.equals(CySBMLConstants.EDGETYPE_REACTION_MODIFIER)){
					
					// ignore the modifier edges
					continue;
				}
				CyNode n2 = (CyNode) outEdge.getTarget();
				
				// source and target node are in the network, add the respective edge between these nodes
				if (net.containsNode(n1) && net.containsNode(n2)){
					String type = inEdgeType + "2" + outEdgeType;
					CyEdge edge = Cytoscape.getCyEdge(n1, n2, Semantics.INTERACTION, type, true);
					net.addEdge(edge);
				}	
			}
		}	
	}
	
	/** Only certain species nodes are added to the GeneNetwork. 
	 * EmptysSet is ignored. */
	private void addNodesToGeneRegulatoryNetwork(CyNetwork net){ 
		CyAttributes nAtts = Cytoscape.getNodeAttributes();
		for (Object obj: network.nodesList()){
			CyNode node = (CyNode) obj;
			// add nodes if in GRN node types
			String type = (String) nAtts.getAttribute(node.getIdentifier(), CySBMLConstants.ATT_SBOTERM);
			if (GeneRegulatoryNetwork.speciesSBOforGRN.contains(type)){
				net.addNode(node);
			}
		}		
	}

	private void createGeneEdgesToGeneNetwork(CyNetwork net){
		for (Object obj: network.nodesList()){
			CyNode node = (CyNode) obj;
			String type = (String) nAtts.getAttribute(node.getIdentifier(), CySBMLConstants.ATT_TYPE);
			if (type.equals(CySBMLConstants.NODETYPE_REACTION)){
				createGeneEdgesInNetworkForReaction(net, node);
			}
		}		
	}
	
	/** Check all the edge combinations. 
	 * 	Add the meta edges created from reducing the reaction-species connections. 
	 *  This adds species-species edges to the core network (not bipartite any more!), 
	 *  but should not have effect on other parts.
	 *  
	 *  Edges to add:
	 *  1. gene -> gene edges if connected via reaction
	 *  2. gene -> reaction become autoregulations
	 *  3. forget about empty set edges
	 *  
	 *  FIXME: only certain edges allowed
	 */
	private void createGeneEdgesInNetworkForReaction(CyNetwork net, CyNode reaction){
		System.out.println("\nReaction: " + reaction.getIdentifier());
		int[] inEdgeInds = network.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, true, false);
		int[] outEdgeInds = network.getAdjacentEdgeIndicesArray(reaction.getRootGraphIndex(), false, false, true);
		System.out.println("# inEdges: " + inEdgeInds.length);
		System.out.println("# outEdges: " + outEdgeInds.length);
		
		for (int in: inEdgeInds){

			CyEdge inEdge  = (CyEdge) network.getEdge(in);
			System.out.println("IN edge: " + inEdge.getIdentifier());
			String inEdgeSBO = (String) eAtts.getAttribute(inEdge.getIdentifier(), CySBMLConstants.ATT_SBOTERM);
			CyNode n1 = (CyNode) inEdge.getSource();
			if (inEdgeSBO == null){
				continue;
			}
			
			for (int out: outEdgeInds){
				// Get the two edges and end nodes;  n1 --inEdge-> reaction --outEdge-> n2 
				CyEdge outEdge = (CyEdge) network.getEdge(out);
				String outEdgeSBO = (String) eAtts.getAttribute(outEdge.getIdentifier(), CySBMLConstants.ATT_SBOTERM);
				System.out.println("OUT edge: " + outEdge.getIdentifier());
				
				CyNode n2 = (CyNode) outEdge.getTarget();				
				if (outEdgeSBO == null){
					continue;
				}
				// Edges to add
				if (outEdgeSBO.equals("SBO:0000011")){
					if (inEdgeSBO.equals("SBO:0000020") ||
						inEdgeSBO.equals("SBO:0000019") ||
						inEdgeSBO.equals("SBO:0000459")){
				
						String type = inEdgeSBO;
						CyEdge edge = Cytoscape.getCyEdge(n1, n2, Semantics.INTERACTION, type, true);
						net.addEdge(edge);
					
						// Set reversibility from reaction
						eAtts.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, type);
					}	
				}	
			}
		}	
	}

}
