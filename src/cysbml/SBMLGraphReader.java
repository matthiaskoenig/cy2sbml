package cysbml;
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.ArrayUtils;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.QualConstant;
import org.sbml.jsbml.ext.qual.QualitativeModel;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.data.readers.GraphReader;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyDesktopManager;
import cytoscape.view.CyNetworkView;

import cysbml.grn.GRNGraphReader;
import cysbml.grn.GeneRegulatoryNetwork;
import cysbml.gui.NavigationPanel;
import cysbml.layout.LayoutExtension;
import cysbml.miriam.NamedSBaseInfoThread;
import cysbml.tools.AttributeUtils;
import cysbml.visual.VisualStyleManager;
import cysbml.visual.VisualStyleManager.CustomStyle;

/**
 * Modified: 2013-11-05
 * @author Matthias Koenig
 * 
 * Instances of the CySBMLGraphReader class should normally only
 * be generated from within the Cytoscape.createNetwork(new GraphReader, ...),
 * to be sure all the necessary Cytoscape processing is performed when the
 * SBML is loaded.
 * 
 * The work is based upon the original SBML2 Reader for Cytoscape.
 * Created on September 27, 2005, 9:23 AM and reuses the way the filter
 * for SBML files is generated and how the GraphReader is hooked into
 * Cytoscape.
 * @author  W.P.A. Ligtenberg, Eindhoven University of Technology
 * @author  Mike Smoot 
 */  
public class SBMLGraphReader extends AbstractGraphReader implements GraphReader {
	static final boolean DEBUG = true;
	static final int LAYOUT_NODE_NUMBER = 200;
	
	URL fileURL;
	InputStream sbmlStream;
	protected SBMLDocument document;
	protected String networkName;
	protected ArrayList<Integer> nodeIds;
	protected ArrayList<Integer> edgeIds;
	
	/** Create CySBMLGraphReader from SBML filename. */
	public SBMLGraphReader(String filename) {
		super(filename);
		fileURL = null;
		sbmlStream = null;
	}

	/** Create CySBMLGraphReader from SBML URL. */
	public SBMLGraphReader() {
		super(null);
		fileURL = null;
		fileName = null;
		sbmlStream = null;
	}
	
	/** Create CySBMLGraphReader from SBML URL. */
	public SBMLGraphReader(URL url) {
		super(null);
		fileURL = url;
		fileName = null;
		sbmlStream = null;
	}
	
	/** Create CySBMLGraphReader from SBML InputStream. */
	public SBMLGraphReader(InputStream stream) {
		super(null);
		fileURL = null;
		fileName = null;
		sbmlStream = stream;
	}
	
	@Override
	public int[] getNodeIndicesArray() {
		Integer[] ids = nodeIds.toArray(new Integer[nodeIds.size()]);
		return ArrayUtils.toPrimitive(ids); 
	}

	@Override
	public int[] getEdgeIndicesArray() {
		Integer[] ids = edgeIds.toArray(new Integer[edgeIds.size()]);
		return ArrayUtils.toPrimitive(ids); 
	}
	
	@Override
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		CyLayoutAlgorithm algorithm = CyLayouts.getDefaultLayout();
		if (nodeIds.size() > LAYOUT_NODE_NUMBER){
			CySBML.LOGGER.info(String.format("More than %d nodes, no layout applied.", LAYOUT_NODE_NUMBER));
		} else { 
			algorithm = CyLayouts.getLayout("force-directed");
		}
		return algorithm;
	}
	
	@Override
	public String getNetworkName(){
		return networkName;
	}
	
	/** Reading the SBML and creating cytoscape network. */
	@Override
	public void read() throws IOException {		
		InputStream instream;
		if ((fileURL == null) && (fileName != null))
			instream = new FileInputStream(fileName);
		else if ((fileURL != null) && (fileName == null))
			instream = fileURL.openStream();
		else if ((fileURL == null) && (fileName == null) && (sbmlStream != null))
			instream = sbmlStream;
		else 
			throw new IOException("No file to open!");
		
		// Read SBML document if valid SBML		
	    SBMLReader reader = new SBMLReader();
		try {
			document = reader.readSBMLFromStream(instream);
			createCytoscapeGraphFromSBMLDocument();
		}
		catch (XMLStreamException e) {
			e.printStackTrace();
			document = null;
		}
	}


	/** Get available network name from the SBML model identifier. */
	private String getAvailableNetworkName(String modelId){
		String name = modelId;
		boolean exists = existsNetwork(name);
		int count = 1;
		while (exists == false){
			name = modelId + "_" + count;
			exists = existsNetwork(name); 
			count++;
			}
		return name;
	}
	
	private boolean existsNetwork(String name){
		return (Cytoscape.getNetwork(name) == Cytoscape.getNullNetwork());
	}
	
	/** Read the SBML model information to create the Cytoscape Graph.
	 * Additional information is stored as Node and Edge Attributes.*/
	public void createCytoscapeGraphFromSBMLDocument(){
		
		Model model = document.getModel();
		networkName = getAvailableNetworkName(model.getId());
		model.setId(networkName);
		
		// Handle the node and edge information
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		
		nodeIds = new ArrayList<Integer>();
		edgeIds = new ArrayList<Integer>();
		
		// Model
		String id = networkName;
		netAttributes.setAttribute(id, CySBMLConstants.ATT_ID, model.getId());
		if (model.isSetName()){
			netAttributes.setAttribute(id, CySBMLConstants.ATT_NAME, model.getName());
		} else {
			netAttributes.setAttribute(id, CySBMLConstants.ATT_NAME, model.getId());
		}
		if (model.isSetMetaId()){
			netAttributes.setAttribute(id, CySBMLConstants.ATT_METAID, model.getMetaId());
		}
		if (model.isSetSBOTerm()){
			netAttributes.setAttribute(id, CySBMLConstants.ATT_SBOTERM, model.getSBOTermID());
		}
		
		// Species
		for (Species species : model.getListOfSpecies()) {
			id = species.getId();
			CyNode node = Cytoscape.getCyNode(id, true);
			
			nodeAttributes.setAttribute(id, CySBMLConstants.ATT_ID, id);
			nodeAttributes.setAttribute(id, CySBMLConstants.ATT_TYPE, CySBMLConstants.NODETYPE_SPECIES);
			if (species.isSetName()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_NAME, species.getName());
			} else {
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_NAME, id);
			}
			if (species.isSetInitialConcentration()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_INITIAL_CONCENTRATION,
						new Double(species.getInitialConcentration()));
			}
			if (species.isSetInitialAmount()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_INITIAL_AMOUNT,
						new Double(species.getInitialAmount()));
			}
			if (species.isSetSBOTerm()){
					nodeAttributes.setAttribute(id, CySBMLConstants.ATT_SBOTERM,
						species.getSBOTermID());
			}
			if (species.isSetCompartment()){
				nodeAttributes.setAttribute(id, 
						CySBMLConstants.ATT_COMPARTMENT, species.getCompartment());
			}
			if (species.isSetBoundaryCondition()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_BOUNDARY_CONDITION, 
						new Boolean(species.getBoundaryCondition()));
			}
			if (species.isSetConstant()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_CONSTANT, 
						new Boolean(species.getConstant()));
			}
			if (species.isSetMetaId()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_METAID, species.getMetaId());
			}
			if (species.isSetHasOnlySubstanceUnits()){
				nodeAttributes.setAttribute(id, CySBMLConstants.ATT_HAS_ONLY_SUBSTANCE_UNITS, 
						new Boolean(species.getHasOnlySubstanceUnits()));
			}
			nodeIds.add(node.getRootGraphIndex());
		}

		// Reactions
		String rid;
		for (Reaction reaction : model.getListOfReactions()) {
			rid = reaction.getId();
			CyNode node = Cytoscape.getCyNode(rid, true);
			nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_TYPE, CySBMLConstants.NODETYPE_REACTION);
			nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_ID, reaction.getId());
			
			if (reaction.isSetSBOTerm()){
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_SBOTERM, reaction.getSBOTermID());
			}
			if (reaction.isSetName()){
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_NAME, reaction.getName());
			} else {
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_NAME, rid);
			}
		
			if (reaction.isSetCompartment()){
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_COMPARTMENT, reaction.getCompartment());
			} else {
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_COMPARTMENT, "-");
			}
			// Reactions are reversible by default
			if (reaction.isSetReversible()){
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_REVERSIBLE, 
						new Boolean(reaction.getReversible()));
			} else {
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_REVERSIBLE, 
						new Boolean(true));
			}
			if (reaction.isSetMetaId()){
				nodeAttributes.setAttribute(rid, CySBMLConstants.ATT_METAID, reaction.getMetaId());
			}
			nodeIds.add(node.getRootGraphIndex());

			//products
			Double stoichiometry;
			for (SpeciesReference speciesRef : reaction.getListOfProducts()) {
				CyNode product = Cytoscape.getCyNode(speciesRef.getSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(node, product, Semantics.INTERACTION,
						CySBMLConstants.EDGETYPE_REACTION_PRODUCT, true);
				if (speciesRef.isSetStoichiometry()){
					stoichiometry = speciesRef.getStoichiometry();
				} else {
					stoichiometry = 1.0;
				}
				edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY, stoichiometry);
				if (speciesRef.isSetSBOTerm()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, speciesRef.getSBOTermID());
				}
				if (speciesRef.isSetMetaId()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_METAID, speciesRef.getMetaId());
				}
				edgeIds.add(edge.getRootGraphIndex());
			}

			//reactants
			for (SpeciesReference speciesRef : reaction.getListOfReactants()) {
				CyNode reactant = Cytoscape.getCyNode(speciesRef.getSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(node, reactant, Semantics.INTERACTION,
						CySBMLConstants.EDGETYPE_REACTION_REACTANT, true);
				if (speciesRef.isSetStoichiometry()){
					stoichiometry = speciesRef.getStoichiometry();
				} else {
					stoichiometry = 1.0;
				}
				edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY, stoichiometry);
				if (speciesRef.isSetSBOTerm()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, speciesRef.getSBOTermID());
				}
				if (speciesRef.isSetMetaId()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_METAID, speciesRef.getMetaId());
				}
				edgeIds.add(edge.getRootGraphIndex());
			}
			
			//modifier
			for (ModifierSpeciesReference msref : reaction.getListOfModifiers()) {
				CyNode modifier = Cytoscape.getCyNode(msref.getSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(modifier, node, Semantics.INTERACTION,
						CySBMLConstants.EDGETYPE_REACTION_MODIFIER, true);
				stoichiometry = 1.0;
				edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY, stoichiometry);
				if (msref.isSetSBOTerm()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, msref.getSBOTermID());
				}
				if (msref.isSetMetaId()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_METAID, msref.getMetaId());
				}
				edgeIds.add(edge.getRootGraphIndex());
			}
			
			//parse the parameters from the kinetic laws
			//TODO: better handling of the kinetic information
			if (reaction.isSetKineticLaw()){
				KineticLaw law = reaction.getKineticLaw();
				if (law.isSetListOfLocalParameters()){
					for (LocalParameter parameter: law.getListOfLocalParameters()){
						String attName = "kineticLaw-" + parameter.getId();
						String attUnitsName = "kineticLaw-" + parameter.getId() + "-units";
						if (parameter.isSetValue()){
							nodeAttributes.setAttribute(rid, attName, parameter.getValue());
						}
						if (parameter.isSetUnits()){
							nodeAttributes.setAttribute(rid, attUnitsName, parameter.getValue());
						}
					}
				}
			}
		}
		
		////////////// QUALITATIVE SBML MODEL ////////////////////////////////////////////
		//Must the network be generated again for the qual model ??
		 QualitativeModel qModel = (QualitativeModel) model.getExtension(QualConstant.namespaceURI);
		 if (qModel != null){
			 
		 //QualSpecies 
		 String qsid;
		 for (QualitativeSpecies qSpecies : qModel.getListOfQualitativeSpecies()){	
			 qsid = qSpecies.getId(); 
		     CyNode node = Cytoscape.getCyNode(qsid, true);
			 nodeAttributes.setAttribute(qsid, CySBMLConstants.ATT_ID, qsid);
			 nodeAttributes.setAttribute(qsid, CySBMLConstants.ATT_TYPE, CySBMLConstants.NODETYPE_QUAL_SPECIES);
			 
			 if (qSpecies.isSetName()){
				 nodeAttributes.setAttribute(qsid, CySBMLConstants.ATT_NAME, qSpecies.getName());
			 } else {
				 nodeAttributes.setAttribute(qsid, CySBMLConstants.ATT_NAME, qsid);
			 }
			
			 if (qSpecies.isSetInitialLevel()){
				 nodeAttributes.setAttribute(qsid, 
						 CySBMLConstants.ATT_INITIAL_LEVEL, new Integer(qSpecies.getInitialLevel()));	
			 }
			 if (qSpecies.isSetMaxLevel()){
				 nodeAttributes.setAttribute(qsid, 
						 CySBMLConstants.ATT_MAX_LEVEL, new Double(qSpecies.getMaxLevel()));	
			 }
			 if (qSpecies.isSetSBOTerm()){
				 nodeAttributes.setAttribute(qsid, 
						 CySBMLConstants.ATT_SBOTERM, qSpecies.getSBOTermID());
			 }
			 if (qSpecies.isSetCompartment()){
				 nodeAttributes.setAttribute(qsid, 
						 CySBMLConstants.ATT_COMPARTMENT, qSpecies.getCompartment());
			 }
			 if (qSpecies.isSetConstant()){
				 nodeAttributes.setAttribute(qsid,
						 CySBMLConstants.ATT_CONSTANT, new Boolean(qSpecies.getConstant()));
			 }
			 if (qSpecies.isSetMetaId()){
				 nodeAttributes.setAttribute(qsid, CySBMLConstants.ATT_METAID, qSpecies.getMetaId());
			 }
			 nodeIds.add(node.getRootGraphIndex());
		}
		  
		// QualTransitions
		String qtid;
		for (Transition qTransition : qModel.getListOfTransitions()){
			qtid = qTransition.getId();
			CyNode tNode = Cytoscape.getCyNode(qtid, true);
		
			nodeAttributes.setAttribute(qtid, CySBMLConstants.ATT_TYPE, CySBMLConstants.NODETYPE_QUAL_TRANSITION);
			nodeAttributes.setAttribute(qtid, CySBMLConstants.ATT_ID, qtid);
			nodeAttributes.setAttribute(qtid,  CySBMLConstants.ATT_COMPARTMENT, "-");
			if (qTransition.isSetSBOTerm()){
				nodeAttributes.setAttribute(qtid, CySBMLConstants.ATT_SBOTERM, qTransition.getSBOTermID());
			}
			if (qTransition.isSetName()){
				nodeAttributes.setAttribute(qtid, CySBMLConstants.ATT_NAME, qTransition.getName());
			} else {
				nodeAttributes.setAttribute(qtid, CySBMLConstants.ATT_NAME, qtid);
			}	
			if (qTransition.isSetMetaId()){
				nodeAttributes.setAttribute(qtid,  CySBMLConstants.ATT_METAID, qTransition.getMetaId());
			}
			nodeIds.add(tNode.getRootGraphIndex());
			
			//outputs
			for (Output output : qTransition.getListOfOutputs()) {
				CyNode outNode = Cytoscape.getCyNode(output.getQualitativeSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(tNode, outNode, Semantics.INTERACTION,
						CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, true);
				edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY, new Double(1.0));
				if (output.isSetSBOTerm()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, output.getSBOTermID());
				}
				if (output.isSetMetaId()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_METAID, output.getMetaId());
				}
				edgeIds.add(edge.getRootGraphIndex());
			}
			//inputs
			for (Input input : qTransition.getListOfInputs()) {
				CyNode inNode = Cytoscape.getCyNode(input.getQualitativeSpecies(), false);
				CyEdge edge = Cytoscape.getCyEdge(tNode, inNode, Semantics.INTERACTION,
						CySBMLConstants.EDGETYPE_TRANSITION_INPUT, true);
				edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_STOICHIOMETRY, new Double(1.0));
				if (input.isSetSBOTerm()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_SBOTERM, input.getSBOTermID());
				}
				if (input.isSetMetaId()){
					edgeAttributes.setAttribute(edge.getIdentifier(), CySBMLConstants.ATT_METAID, input.getMetaId());
				}
				edgeIds.add(edge.getRootGraphIndex());
			}
		}
	   }
	}
	
	/**
	 *  PostProcessing of the network after the file is read.
	 *  - applies CySBML visual style
	 *  - applies standard layout to the network
	 *  - selects the important SBML attributes in the data browser
	 */  
	public void doPostProcessing(CyNetwork network) {
		// Apply Layout
		// applyLayout(network);
		// Set Visual Style
		VisualStyleManager.setVisualStyleForNetwork(network, CustomStyle.DEFAULT_STYLE);
		// Select SBML Attributes in Data Panel
		selectSBMLTableAttributes();
		// Update the CySBML Navigator
		updateNavigationPanel(network);
		// Generate additional networks
		generateGRN(network);
		generateLayoutNetworks(network);

		// Preload SBML WebService information
		NamedSBaseInfoThread.preloadAnnotationInformationForModel(document.getModel());
		
		// [8] Arrange Windows and fit views
		CyDesktopManager.arrangeFrames(CyDesktopManager.Arrange.GRID);
		for(CyNetworkView view: Cytoscape.getNetworkViewMap().values()){
			view.fitContent();
		}
		
	}
	
	protected void updateNavigationPanel(CyNetwork network){
		NavigationPanel panel = NavigationPanel.getInstance();
		panel.putSBMLDocument(network.getIdentifier(), document, network);
	}
	
	private void generateGRN(CyNetwork network){
		if (GeneRegulatoryNetwork.isSBMLDocumentGRN(document)){
			try {
				Cytoscape.createNetwork(new GRNGraphReader(document, network), true, null);
			} catch (Exception e){
				CySBML.LOGGER.error("Error generating SBML GRN network");
				e.printStackTrace();
			}
		}
	}
	
	private void generateLayoutNetworks(CyNetwork network){
		if (LayoutExtension.existLayoutInSBMLDocument(document)){
			CySBML.LOGGER.info("Create additional networks for layouts.");
			ListOf<Layout> layoutList = LayoutExtension.getLayoutsInSBMLDocument(document);
			for (int k=0; k<layoutList.size(); ++k){
				Layout layout = layoutList.get(k);
				// Create the layout networks
				try {
					Cytoscape.createNetwork(
						new SBMLLayoutGraphReader(document, network, layout), true, null);
				} catch (Exception e){
					CySBML.LOGGER.error("Error generating SBML Layout networks");
					e.printStackTrace();
				}
			}
		}
	}

	/** Applies force-directed layout to the network. */
	protected void applyLayout(CyNetwork network) {
		if (nodeIds.size() > LAYOUT_NODE_NUMBER){
			CySBML.LOGGER.info(String.format("More than %d nodes, no layout applied.", LAYOUT_NODE_NUMBER));
		} else { 
			CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
			CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
			view.applyLayout(layout);
		}
	}
	
	private void selectSBMLTableAttributes(){
		String[] nAtts = {CySBMLConstants.ATT_TYPE,
						  CySBMLConstants.ATT_NAME,
						  CySBMLConstants.ATT_COMPARTMENT,
						  CySBMLConstants.ATT_METAID,
						  CySBMLConstants.ATT_SBOTERM};
		
		String[] eAtts = {Semantics.INTERACTION,
						  CySBMLConstants.ATT_STOICHIOMETRY,
						  CySBMLConstants.ATT_METAID,
						  CySBMLConstants.ATT_SBOTERM};
		AttributeUtils.selectTableAttributes(Arrays.asList(nAtts), Arrays.asList(eAtts));
	}
	

}
