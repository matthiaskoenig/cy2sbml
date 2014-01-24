package cysbml;

import java.util.*;


import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.layout.AbstractReferenceGlyph;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.QualConstant;
import org.sbml.jsbml.ext.qual.QualitativeModel;
import org.sbml.jsbml.ext.qual.Transition;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.GraphReader;

import cysbml.gui.NavigationPanel;
import cysbml.layout.LayoutManipulator;
import cysbml.layout.NetworkLayout;
import cysbml.visual.VisualStyleManager;
import cysbml.visual.VisualStyleManager.CustomStyle;


/** TODO: read the attributes from the nodes and edges properly. */
public class SBMLLayoutGraphReader extends SBMLGraphReader implements GraphReader {	

	private CyNetwork network;
	private Layout layout;
	CyAttributes nodeAttributes;
	CyAttributes edgeAttributes;
	
	public SBMLLayoutGraphReader(SBMLDocument document, CyNetwork network, Layout sbmlLayout) {
		super();
		this.document = document;
		this.network = network;
		this.layout = sbmlLayout;
	}
	
	@Override
	public void read(){	
		if (layout != null && network != null){
			createCytoscapeGraphFromLayout();
		}
	}

	/** Creates the next available name for a layout */
	private String createNetworkName(){
		String netId = network.getIdentifier();
		String name = netId + "_Layout_" + layout.getId();
		return name;
	}
		
	public void createCytoscapeGraphFromLayout(){
		networkName = createNetworkName();
		
		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		nodeIds = new ArrayList<Integer>();
		edgeIds = new ArrayList<Integer>();
		
		// Process the layouts (Generate full id set and all edges for elements)
		LayoutManipulator layoutManipulator = new LayoutManipulator(document.getModel(), layout);
		layoutManipulator.generateAllEdges();
		layout = layoutManipulator.getLayout();
		addSpeciesGlyphNodes();	
		addReactionGlyphNodes();
		addModelEdges();
		addQualitativeModelEdges();
	}
	
	public void addSpeciesGlyphNodes() {
		for (SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()) {
			String id = glyph.getId();
			CyNode node = Cytoscape.getCyNode(id, true);
			if (glyph.isSetSpecies()){
				CyNode sNode = Cytoscape.getCyNode(glyph.getSpecies(), false);
				copyNodeInformation(sNode, node);
			} else {
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_ID, id);
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_TYPE, "SpeciesGlyph");
			}
			readNodeAttributesFromSpeciesGlyph(node, glyph);
			nodeIds.add(node.getRootGraphIndex());
		}
	}
	
	public void addReactionGlyphNodes(){
		for (ReactionGlyph glyph: layout.getListOfReactionGlyphs()){
			String id = glyph.getId();
			CyNode node = Cytoscape.getCyNode(id, true);
			if (glyph.isSetReaction()){
				CyNode rNode = Cytoscape.getCyNode(glyph.getReaction(), false);
				copyNodeInformation(rNode, node);
			} else {
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_ID, id);
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_TYPE, "ReactionGlyph");
			}
			readNodeAttributesFromReactionGlyph(node, glyph);
			nodeIds.add(node.getRootGraphIndex());
		}
	}
	
	/** Probably not all information parsed */
	public void copyNodeInformation(CyNode sourceNode, CyNode targetNode){
		String sId = sourceNode.getIdentifier();
		String tId = targetNode.getIdentifier();
		String info = null;
		info = (String) nodeAttributes.getAttribute(sId, CySBMLConstants.ATT_ID);
		nodeAttributes.setAttribute(tId, CySBMLConstants.ATT_ID, info);
		
		info = (String) nodeAttributes.getAttribute(sId, CySBMLConstants.ATT_TYPE);
		nodeAttributes.setAttribute(tId, CySBMLConstants.ATT_TYPE, info);
		
		info = (String) nodeAttributes.getAttribute(sId, CySBMLConstants.ATT_NAME);
		if (info != null){
			nodeAttributes.setAttribute(tId, CySBMLConstants.ATT_NAME, info);
		}
		info = (String) nodeAttributes.getAttribute(sId, CySBMLConstants.ATT_COMPARTMENT);
		if (info != null){
			nodeAttributes.setAttribute(tId, CySBMLConstants.ATT_COMPARTMENT, info);
		}
		info = (String) nodeAttributes.getAttribute(sId, CySBMLConstants.ATT_SBOTERM);
		if (info != null){
			nodeAttributes.setAttribute(tId, CySBMLConstants.ATT_SBOTERM, info);
		}
	}
	
	/** Additional attributes can be defined in the layout and have to be parsed. */
	private void readEdgeAttributesFromSpeciesReferenceGlyph(CyEdge edge, SpeciesReferenceGlyph sRefGlyph){
		if (sRefGlyph.isSetSBOTerm()){
			edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, sRefGlyph.getSBOTermID());
		}	
		if (sRefGlyph.isSetMetaId()){
			edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_METAID, sRefGlyph.getMetaId());
		}
		if (sRefGlyph.isSetName()){
			edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_NAME, sRefGlyph.getName());
		}	
	}
	
	
	private void readNodeAttributesFromSpeciesGlyph(CyNode node, SpeciesGlyph sGlyph){
		readNodeAttributesFromGlyph(node, sGlyph);
	}
	
	private void readNodeAttributesFromReactionGlyph(CyNode node, ReactionGlyph rGlyph){
		readNodeAttributesFromGlyph(node, rGlyph);
	}
	
	/** Additional attributes can be defined in the layout and have to be parsed. */
	private void readNodeAttributesFromGlyph(CyNode node, AbstractReferenceGlyph glyph){
		if (glyph.isSetSBOTerm()){
			nodeAttributes.setAttribute(node.getIdentifier(), CySBMLConstants.ATT_SBOTERM, glyph.getSBOTermID());
		}
		if (glyph.isSetMetaId()){
			nodeAttributes.setAttribute(node.getIdentifier(), CySBMLConstants.ATT_METAID, glyph.getMetaId());
		}
		if (glyph.isSetName()){
			nodeAttributes.setAttribute(node.getIdentifier(), CySBMLConstants.ATT_NAME, glyph.getName());
		}		
	}
	
	
	public void addModelEdges() {
		Model model = document.getModel();
		if (model == null) {
			return;
		}
		for (ReactionGlyph rGlyph : layout.getListOfReactionGlyphs()) {
			CyNode rNode = Cytoscape.getCyNode(rGlyph.getId(), false);
			if (rGlyph.isSetListOfSpeciesReferencesGlyphs()) {
				
				// if the reaction is not set
				if (!rGlyph.isSetReaction()) {
					for (SpeciesReferenceGlyph sRefGlyph : rGlyph.getListOfSpeciesReferenceGlyphs()) {
						String sGlyphId = sRefGlyph.getSpeciesGlyph();
						CyNode sNode = Cytoscape.getCyNode(sGlyphId, false);
						CyEdge edge = Cytoscape.getCyEdge(rNode, sNode, Semantics.INTERACTION,
															CySBMLConstants.EDGETYPE_UNDEFINED, true);
						readEdgeAttributesFromSpeciesReferenceGlyph(edge, sRefGlyph);
						edgeIds.add(edge.getRootGraphIndex());
					}
				}

				// if the reaction is set
				if (rGlyph.isSetReaction()) {
					String reactionId = rGlyph.getReaction();
					Reaction reaction = model.getReaction(reactionId);
					if (reaction == null) {
						System.err.println("reactionId in in ReactionGlyph is not in SBML model: " + reactionId);
						continue;
					}
					for (SpeciesReferenceGlyph sRefGlyph : rGlyph.getListOfSpeciesReferenceGlyphs()) {
						String sGlyphId = sRefGlyph.getSpeciesGlyph();
						SpeciesGlyph sGlyph = layout.getSpeciesGlyph(sGlyphId);
						
						String edgeType = getEdgeTypeFromSpeciesReferenceGlyphRole(sRefGlyph);
						// if not found try via SBML model
						if (edgeType.equals(CySBMLConstants.EDGETYPE_UNDEFINED)){
							edgeType = getEdgeTypeFromSBMLModel(reaction, sGlyph);
						}
						
						CyNode sNode = Cytoscape.getCyNode(sGlyphId, false);
						CyEdge edge = Cytoscape.getCyEdge(rNode, sNode, Semantics.INTERACTION, edgeType, true);
						
						// Read additional edge information
						readEdgeAttributesFromSpeciesReferenceGlyph(edge, sRefGlyph);
						edgeIds.add(edge.getRootGraphIndex());
					}
				}
			}
		}
	}
	
	public void addQualitativeModelEdges() {
		Model model = document.getModel();
		if (model == null) {
			return;
		}
		QualitativeModel qModel = (QualitativeModel) model.getExtension(QualConstant.namespaceURI);
		if (qModel == null){
			return;
		}
				
		for (ReactionGlyph rGlyph : layout.getListOfReactionGlyphs()) {
			CyNode rNode = Cytoscape.getCyNode(rGlyph.getId(), false);
			if (rGlyph.isSetListOfSpeciesReferencesGlyphs()) {
				
				if (rGlyph.isSetReaction()) {
					String reactionId = rGlyph.getReaction();
					Transition transition = (Transition) qModel.getTransition(reactionId);
					if (transition != null){
						for (SpeciesReferenceGlyph sRefGlyph: rGlyph.getListOfSpeciesReferenceGlyphs()){
							String sGlyphId = sRefGlyph.getSpeciesGlyph();
							SpeciesGlyph sGlyph = layout.getSpeciesGlyph(sGlyphId);
							
							String edgeType = getEdgeTypeFromSpeciesReferenceGlyphRole(sRefGlyph);
							// if not found try via SBML model
							if (edgeType.equals(CySBMLConstants.EDGETYPE_UNDEFINED)){
								edgeType = getEdgeTypeFromQualModel(transition, sGlyph);
							}
							CyNode sNode = Cytoscape.getCyNode(sGlyphId, false);
							CyEdge edge = Cytoscape.getCyEdge(rNode, sNode, Semantics.INTERACTION, edgeType, true);
				
							// Read additional edge information
							readEdgeAttributesFromSpeciesReferenceGlyph(edge, sRefGlyph);
							
							edgeIds.add(edge.getRootGraphIndex());
						}
					}
				}
			}
		}
	}
	
	/** Get edgeType from the SpeciesReferenceGlyphRole. */
	private String getEdgeTypeFromSpeciesReferenceGlyphRole(SpeciesReferenceGlyph sRefGlyph){
		String edgeType = CySBMLConstants.EDGETYPE_UNDEFINED;
		
		if (sRefGlyph.isSetSpeciesReferenceRole()){
			switch (sRefGlyph.getSpeciesReferenceRole()){
				case ACTIVATOR:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_ACTIVATOR;
					break;
				case INHIBITOR:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_INHIBITOR;
					break;
				case MODIFIER:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_MODIFIER;
					break;
				case PRODUCT:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_PRODUCT;
					break;
				case SIDEPRODUCT:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_PRODUCT;
					break;
				case SIDESUBSTRATE:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_SIDEREACTANT;
					break;
				case SUBSTRATE:
					edgeType = CySBMLConstants.EDGETYPE_REACTION_REACTANT;
					break;
				case UNDEFINED:
					edgeType = CySBMLConstants.EDGETYPE_UNDEFINED;
					break;
			}
		}
		return edgeType;
	}
	
	/** Get the edge type from SBML model.
	 * 	 * ! Only first matching entry is returned.
	 */
	private String getEdgeTypeFromSBMLModel(Reaction reaction, SpeciesGlyph sGlyph){
		String edgeType = CySBMLConstants.EDGETYPE_UNDEFINED;
		if (sGlyph.isSetSpecies()) {
			String speciesId = sGlyph.getSpecies();
			if (reaction.isSetListOfReactants()) {
				for (SpeciesReference speciesReference : reaction.getListOfReactants()) {
					if (speciesReference.getSpecies().equals(speciesId)) {
						edgeType = CySBMLConstants.EDGETYPE_REACTION_REACTANT;
					}
				}
			}
			if (reaction.isSetListOfProducts()) {
				for (SpeciesReference speciesReference : reaction.getListOfProducts()) {
					if (speciesReference.getSpecies().equals(speciesId)) {
						edgeType = CySBMLConstants.EDGETYPE_REACTION_PRODUCT;
					}
				}
			}
			if (reaction.isSetListOfModifiers()) {
				for (ModifierSpeciesReference speciesReference : reaction.getListOfModifiers()) {
					if (speciesReference.getSpecies().equals(speciesId)) {
						edgeType = CySBMLConstants.EDGETYPE_REACTION_MODIFIER;
					}
				}
			}
		}
		return edgeType;
	}
	
	/** Get the edge type from Qual model. 
	 * ! Only first matching entry is returned.
	 */
	private String getEdgeTypeFromQualModel(Transition transition, SpeciesGlyph sGlyph){
		String speciesId = sGlyph.getSpecies();
		if (transition.isSetListOfInputs()){
			for (Input input :transition.getListOfInputs()){
				if (input.getQualitativeSpecies().equals(speciesId)){
					return CySBMLConstants.EDGETYPE_TRANSITION_INPUT;
				}
			}
		}
		if (transition.isSetListOfOutputs()){
			for (Output output : transition.getListOfOutputs()){
				if (output.getQualitativeSpecies().equals(speciesId)){
					return CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT;
				}
			}
		}
		return CySBMLConstants.EDGETYPE_UNDEFINED;
	}
	
	
	@Override
	public void doPostProcessing(CyNetwork network) {
		// [1] Apply Layout
		applyLayout(network, layout);
		// [2] Set Layout
		VisualStyleManager.setVisualStyleForNetwork(network, CustomStyle.LAYOUT_STYLE);
		// [3] Update the CySBML Navigator
		updateNavigationPanel(network);
	}
	
	@Override
	public void updateNavigationPanel(CyNetwork network){
		NavigationPanel cySBMLPanel = NavigationPanel.getInstance();
		cySBMLPanel.putSBMLDocumentForLayout(getNetworkName(), document, network, layout);
	}
	
	
	/** Use the layout extension to make the Layout */
	public void applyLayout(CyNetwork network, Layout layout){
		NetworkLayout networkLayout = new NetworkLayout(document, layout);
		networkLayout.setNetworkAttributesFromBoundingBoxes(network);
		networkLayout.applyLayoutPositionsToLayoutNetwork(network);
	}
}
