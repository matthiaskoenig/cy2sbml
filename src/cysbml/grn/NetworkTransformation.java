package cysbml.grn;

import java.util.LinkedList;
import java.util.List;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;

import cysbml.CySBMLConstants;

/** Creates derived networks from the bipartite reaction-species SBML network. 
 * 	SpeciesNetwork (SN): consists of the species in the network with edges between species 
 * 						 if connected vian a reaction.
 *  ReactionNetwork (RN): consists of the reactions in the network with edges between
 *  					 reactions if connected via intermediate species.
 *  
 *  TODO: copy the node positions from the old view
		for (Object obj: net.nodesList()){
			CyNode node = (CyNode) obj;
			CyNodeView nview = view.getNodeView(node);
		}
	TODO: handle the Qual networks properly
 */
public class NetworkTransformation {

	private CyNetwork network;
	private CyAttributes eAtts;
	private CyAttributes nAtts;
	
	public NetworkTransformation(CyNetwork network){
		this.network = network;
		eAtts = Cytoscape.getEdgeAttributes();
		nAtts = Cytoscape.getNodeAttributes();
	}
	
	public void createSpeciesNetworkAndView(){
		CyNetwork net = createSpeciesNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());

		view.redrawGraph(true, true);
		view.updateView();
	}
	
	public void createReactionNetworkAndView(){
		CyNetwork net = createReactionNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());
		view.redrawGraph(true, true);
		view.updateView();
	}
		
	private CyNetwork createSpeciesNetwork(){
		if (network == null || network.equals(Cytoscape.getNullNetwork())){
			return Cytoscape.getNullNetwork();
		}
		String id = network.getIdentifier() + "_" + "SpeciesNet"; 
		CyNetwork net = Cytoscape.createNetwork(id, true);
		
		addSpeciesNodesToNetwork(net);
		createSpeciesEdgesInNetwork(net);
		return net;
	}

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
		
	private void addSpeciesNodesToNetwork(CyNetwork net){
		addNodesOfTypeToNetwork(CySBMLConstants.NODETYPE_SPECIES, net);		
	}
	
	@SuppressWarnings("unused")
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
	
	/** Check all species connected with the reaction. */
	private void createSpeciesEdgesInNetworkForReaction(CyNetwork net, CyNode reaction){
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
	
}
