package cysbml.gui;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JSplitPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.tree.TreePath;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.sbml.jsbml.NamedSBase;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.util.OpenBrowser;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

import cysbml.CySBML;
import cysbml.cytoscape.CytoscapeWrapper;
import cysbml.mapping.NamedSBaseToNodeMapping;
import cysbml.mapping.NavigationTree;
import cysbml.mapping.OneToManyMapping;
import cysbml.mapping.SBMLDocuments;
import cysbml.miriam.NamedSBaseInfoThread;

import javax.swing.JTabbedPane;

/** 
 * Main information panel for CySBML.
 * 
 * => deactivation results in hybernating of all NavigationPanel associated activities
 * 	  until reactivated.
 *
 */

@SuppressWarnings("serial")
public class NavigationPanel extends JPanel implements TreeSelectionListener, 
														     PropertyChangeListener, SelectEventListener,
														     HyperlinkListener{	
	private static NavigationPanel uniqueInstance;
	
	// for synchronisation of the tree and network selection
	private boolean makeTreeSelectionChanges = true;
	private boolean makeNetworkSelectionChanges = true;
	
	// for handling the size and visibility of GUI
	private boolean active = false;
	private Dimension tmpDimension;

	private JTree sbmlTree;
	private JEditorPane textPane;
	private NavigationTree navigationTree = new NavigationTree();
	private SBMLDocuments sbmlDocuments = new SBMLDocuments();
	
	private long lastInformationThreadId = -1;
	
	public static synchronized NavigationPanel getInstance(){
		if (uniqueInstance == null){
			uniqueInstance = new NavigationPanel();
		}
		return uniqueInstance;
	}
	
	
	private NavigationPanel(){
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED, this);
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane);
		
		// Annotation Area
		textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(this);
		textPane.setFont(new Font("Dialog", Font.PLAIN, 11));
		textPane.setContentType("text/html");
		setHelpInNavigationPanel();

		JScrollPane annotationScrollPane = new JScrollPane();
		annotationScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		annotationScrollPane.setViewportView(textPane);
		splitPane.setRightComponent(annotationScrollPane);
		
		JTabbedPane navTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(navTabbedPane);
		
		// Navigation Tree
		sbmlTree = new JTree();
		sbmlTree.setVisibleRowCount(12);
		sbmlTree.setEditable(false);
		sbmlTree.addTreeSelectionListener(this);
		sbmlTree.setFont(new Font("Dialog", Font.PLAIN, 10));
		setNavigationTreeInJTree();
		
		JScrollPane treeScrollPane = new JScrollPane();
		navTabbedPane.addTab("SBMLTree", null, treeScrollPane, null);
		treeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setViewportView(sbmlTree);
		
		/*
		JScrollPane grnScrollPane = new JScrollPane();
		navTabbedPane.addTab("GeneRegulatorNet (GRN)", null, grnScrollPane, null);
		
		JPanel grnPane = new JPanel();
		grnPane.setBackground(Color.WHITE);
		grnScrollPane.setViewportView(grnPane);
		grnPane.setLayout(null);
		
		JButton btnCreateSpeciesNetwork = new JButton("Create");
		btnCreateSpeciesNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CyNetwork network = Cytoscape.getCurrentNetwork();
				NetworkTransformationFactory ntf = new NetworkTransformationFactory(network);
				LogCySBML.info("Create Species Network for network: "  + network.getIdentifier());
				ntf.createSpeciesNetworkAndView();
			}
		});
		btnCreateSpeciesNetwork.setBounds(214, 11, 89, 23);
		grnPane.add(btnCreateSpeciesNetwork);
		
		JLabel lblSpeciesNetwork = new JLabel("Species Network (SN)");
		lblSpeciesNetwork.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSpeciesNetwork.setBounds(10, 20, 182, 14);
		grnPane.add(lblSpeciesNetwork);
		
		JLabel lblReactionNetwork = new JLabel("Reaction Network (RN)");
		lblReactionNetwork.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblReactionNetwork.setBounds(10, 45, 182, 14);
		grnPane.add(lblReactionNetwork);
		
		JLabel lblGeneRegulatoryNetwork = new JLabel("Gene Regulatory Network (GRN)");
		lblGeneRegulatoryNetwork.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblGeneRegulatoryNetwork.setBounds(10, 70, 182, 14);
		grnPane.add(lblGeneRegulatoryNetwork);
		
		JButton btnCreateReactionNetwork = new JButton("Create");
		btnCreateReactionNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CyNetwork network = Cytoscape.getCurrentNetwork();
				NetworkTransformationFactory ntf = new NetworkTransformationFactory(network);
				LogCySBML.info("Create Reaction Network for network: " + network.getIdentifier());
				ntf.createReactionNetworkAndView();
			}
		});
		btnCreateReactionNetwork.setBounds(214, 36, 89, 23);
		grnPane.add(btnCreateReactionNetwork);
		
		JButton btnCreateGeneRegulatoryNetwork = new JButton("Create");
		btnCreateGeneRegulatoryNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CyNetwork network = Cytoscape.getCurrentNetwork();
				NetworkTransformationFactory ntf = new NetworkTransformationFactory(network);
				LogCySBML.info("Create Gene Regulatory Network for network: " + network.getIdentifier());
				ntf.createGeneRegulatoryNetworkAndView();
			}
		});
		btnCreateGeneRegulatoryNetwork.setBounds(214, 61, 89, 23);
		grnPane.add(btnCreateGeneRegulatoryNetwork);
		*/
	}
	
	//////////// HANDLE THE SBML DOCUMENTS ///////////////////////////////////////////////////////
	
	public void putSBMLDocument(String networkName, SBMLDocument document, CyNetwork network){
		NamedSBaseToNodeMapping mapping = new NamedSBaseToNodeMapping(network);
		putSBMLDocument(networkName, document, mapping);
	}
	public void putSBMLDocumentForLayout(String networkName, SBMLDocument document, CyNetwork network, Layout layout){
		NamedSBaseToNodeMapping mapping = new NamedSBaseToNodeMapping(layout);
		putSBMLDocument(networkName, document, mapping);
	}
	public void putSBMLDocumentForGRN(String networkName, SBMLDocument document, CyNetwork network){
		NamedSBaseToNodeMapping mapping = new NamedSBaseToNodeMapping(network);
		putSBMLDocument(networkName, document, mapping);
	}
	private void putSBMLDocument(String networkName, SBMLDocument doc, NamedSBaseToNodeMapping mapping){
		sbmlDocuments.putDocument(networkName, doc, mapping);
		this.activate();
		updateNavigationTree();
	}
	
	public SBMLDocument getCurrentSBMLDocument(){
		return sbmlDocuments.getCurrentDocument();
	}
	public SBMLDocument getSBMLDocumentForCyNetwork(CyNetwork network){
		SBMLDocument doc = null;
		if (network != null){
			String networkId = network.getIdentifier();
			doc = sbmlDocuments.getDocumentForNetworkId(networkId);
		}
		return doc;
	}
	
	public long getLastInfoThreadId(){
		return lastInformationThreadId;
	}
	
	/////////////////// PANEL STUFF ////////////////////////////////
	public JEditorPane getTextPane(){
		return textPane;
	}
	
    public void activate(){
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		int index = cytoPanel.indexOfComponent(CySBML.NAME);
		if (index == -1){
			cytoPanel.add(CySBML.NAME, this);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
		selectNavigationPanel();
		active = true;
	}
	
	public void deactivate(){
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		int index = cytoPanel.indexOfComponent(CySBML.NAME);
		if (index != -1){
			cytoPanel.remove(index);
		}
		// Test if still other Components, otherwise hide
		if (cytoPanel.getCytoPanelComponentCount() == 0){
			cytoPanel.setState(CytoPanelState.HIDE);
		}
		
		active = false;
	}
	
	public boolean isActive(){
		return active;
	}
	
	public static void selectNavigationPanel(){
		CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
		cytoPanel.setSelectedIndex(cytoPanel.indexOfComponent(CySBML.NAME));
	}

	public void setText(String text){
		textPane.setText(text);
	}
	
	private void setHelpInNavigationPanel(){
		try {
			URL url = new URL(NavigationPanel.class.getResource("help/info.html").toString());
			textPane.setPage(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Deprecated
	private void setPanelSize(){
		// ? Swing resize behaviour
		tmpDimension.setSize(tmpDimension.getHeight(), tmpDimension.getWidth()-5.0);
		this.setSize(tmpDimension);
		this.setPreferredSize(tmpDimension);
		this.setMaximumSize(tmpDimension);		
	}
	
	/////////////////// HANDLE EVENTS ///////////////////////////////////
	
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		URL url = evt.getURL();
		if (url != null) {
			if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
				CytoscapeWrapper.setStatusBarMsg(url.toString());
			} else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
				CytoscapeWrapper.clearStatusBar();
			} else if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				OpenBrowser.openURL(url.toString());
			}
		}
	}
	 
	public void propertyChange(PropertyChangeEvent e) {		
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{	
			updateNavigationTree();
			
			//Change the target for the network Event Listener
			CyNetwork network = Cytoscape.getCurrentNetwork();
			if (network != null)
				network.removeSelectEventListener(this);
			network = Cytoscape.getCurrentNetwork();
			if (network != null) {
				network.addSelectEventListener(this);
			}
		}
		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.NETWORK_DESTROYED)){
			String deletedNetworkKey = Cytoscape.getCurrentNetwork().getIdentifier();
			sbmlDocuments.removeDocument(deletedNetworkKey);
			updateNavigationTree();
		}
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.SESSION_LOADED)){
			sbmlDocuments.updateDocuments();
			updateNavigationTree();
		}
	}
	
	private void updateNavigationTree(){
		SBMLDocument doc = sbmlDocuments.getCurrentDocument();
		if (doc != null){
			navigationTree = new NavigationTree(doc);	
		} else{
			navigationTree = new NavigationTree();
		}
		setNavigationTreeInJTree();
	}
	
	private void setNavigationTreeInJTree(){
		sbmlTree.setModel(navigationTree.getTreeModel());
	}
	
	
	/* Cytoscape node selection */
	@Override
	public void onSelectEvent(SelectEvent event) {
		
		// only if active and SBMLdocument
		if (isActive()==false || sbmlDocuments.getCurrentDocument() == null){
			return;
		}
		
		tmpDimension = this.getSize();
		if (makeNetworkSelectionChanges){
			if ( (event.getTargetType() == SelectEvent.SINGLE_NODE) 
					|| (event.getTargetType() == SelectEvent.NODE_SET)) {
				makeNetworkSelectionChanges = false;
				makeTreeSelectionChanges = false;
				
				List<String> selectedNodeIds = getSelectedNodeIds();
				List<String> selectedNamedSBaseIds = getNamedSBaseIdsFromNodeIds(selectedNodeIds);
				// Reverse for synchronization
				// selectedNodeIds = getNodeIdsFromNamedSBaseIds(selectedNamedSBaseIds);
				makeNetworkAndTreeChanges(selectedNodeIds, selectedNamedSBaseIds);
			}
		}
	}
	
	/* Tree node selection */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (isActive()==false || sbmlDocuments.getCurrentDocument() == null){
			return;
		}
		tmpDimension = this.getSize();
		
		if (makeTreeSelectionChanges){
			makeNetworkSelectionChanges = false;
			makeTreeSelectionChanges = false;
			List<String> selectedNamedSBaseIds = getSelectedNamedSBaseIds();
			List<String> selectedNodeIds = getNodeIdsFromNamedSBaseIds(selectedNamedSBaseIds);
			// Reverse for synchronization
			// selectedNamedSBaseIds = getNamedSBaseIdsFromNodeIds(selectedNodeIds);
			makeNetworkAndTreeChanges(selectedNodeIds, selectedNamedSBaseIds);
		}
	}	
	
	private List<String> getNodeIdsFromNamedSBaseIds(List<String> nsbIds){ 
		OneToManyMapping mapping = sbmlDocuments.getCurrentNSBToNodeMapping();
		return mapping.getValues(nsbIds);
	}
	private List<String> getNamedSBaseIdsFromNodeIds(List<String> nodeIds){ 
		OneToManyMapping mapping = sbmlDocuments.getCurrentNodeToNSBMapping();
		return mapping.getValues(nodeIds);
	}
	
	/* Synchronize node and tree selection */
	private void makeNetworkAndTreeChanges(List<String> nodeIds, List<String> namedSBaseIds){		
		updateTreeSelection(namedSBaseIds);
		updateNetworkSelection(nodeIds);
		updateAnnotationInformation(namedSBaseIds);
	
		makeNetworkSelectionChanges = true;
		makeTreeSelectionChanges = true;
		setPanelSize();
	}
	
	private void updateTreeSelection(List<String> selectedIds){
		sbmlTree.clearSelection();

		// only update tree if less than 50 nodes (can take long time otherwise)
		TreePath path = null;
		Map<String, NamedSBase> objectMap = navigationTree.getObjectMap();
		Map<String, TreePath> objectPathMap = navigationTree.getObjectPathMap();
		if (selectedIds.size() <50){
			for (String id : selectedIds){
				if (objectMap.containsKey(id)){
					path = objectPathMap.get(id);
					sbmlTree.addSelectionPath(path);
				}
			}
		}
		if (path != null){
			sbmlTree.scrollPathToVisible(path);
		}
	}
	
	private void updateNetworkSelection(List<String> selectedIds){
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		Set<CyNode> cyNodes = new HashSet<CyNode>();
		for (String id : selectedIds){
			cyNodes.add(Cytoscape.getCyNode(id, false));	
		}
		network.setSelectedNodeState(cyNodes, true);	
		Cytoscape.getCurrentNetworkView().updateView();
	}
	
	@Deprecated
	public void updateAnnotationInformation(List<String> selectedIds){
		/* TODO: handle multiple ids properly
		int size = selectedIds.size();
		if ( size > 0 && size <= MAX_SELECTION_DISPLAY){
			Set<NamedSBase> nsbSet = new HashSet<NamedSBase>();
			for (String namedSBaseId: selectedIds){
				nsbSet.add(navigationTree.getNamedSBaseById(namedSBaseId));
			}
			showNodeObjectInfo(nsbSet);
		} else {
			textPane.setText(String.format("> %d nodes selected, no information displayed", MAX_SELECTION_DISPLAY ));	
		}
		*/
		
		if (selectedIds.size() > 0){
			String nsbId = selectedIds.get(0);
			showNodeObjectInfo(navigationTree.getNamedSBaseById(nsbId));
		}
	}
	
	private LinkedList<String> getSelectedNodeIds(){
		LinkedList<String> selectedIds = new LinkedList<String>();	
		@SuppressWarnings("unchecked")
		Set<CyNode> cyNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
		for (CyNode node : cyNodes){
			String id = node.getIdentifier();
			selectedIds.add(id);
		}
		return selectedIds;
	}
	
	// TODO: Change this setting -> make it better 
	private LinkedList<String> getSelectedNamedSBaseIds(){
		LinkedList<String> selectedIds = new LinkedList<String>();
		TreePath[] paths = sbmlTree.getSelectionPaths();
		
		for (int k=0; k<sbmlTree.getSelectionCount(); ++k){
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[k].getLastPathComponent();
			Object obj = node.getUserObject();
			if (obj instanceof NamedSBase){
				NamedSBase namedSBase  = (NamedSBase) node.getUserObject();
				Class<? extends NamedSBase> currentClass = namedSBase.getClass();
				if ( currentClass.equals(Reaction.class) || 
					 currentClass.equals(Species.class) ||
					 currentClass.equals(QualitativeSpecies.class) ||
					 currentClass.equals(Transition.class)
				   ){
					String id = namedSBase.getId();
		  			selectedIds.add(id);	 
		  		}
			}
		}
		return selectedIds;
	}

	/////////////////// MIRIAM INFORMATION /////////////////////////////
	
   /** Create the information string for the SBML Node and display in the textPane. */ 
	private void showNodeObjectInfo(Object obj) {
	   Set<Object> objSet = new HashSet<Object>();
	   objSet.add(obj);
	   showNodesObjectInfo(objSet);
   }
   
   private void showNodesObjectInfo(Set<Object> objSet) {
	   NamedSBaseInfoThread thread = new NamedSBaseInfoThread(objSet, this);
	   lastInformationThreadId = thread.getId();
	   thread.start();
   }
}
