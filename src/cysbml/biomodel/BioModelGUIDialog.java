package cysbml.biomodel;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cysbml.cytoscape.CytoscapeWrapper;
import cysbml.tools.ProxyTools;
import cytoscape.util.OpenBrowser;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.KeyAdapter;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class BioModelGUIDialog extends JDialog {
	private static BioModelGUIDialog uniqueInstance; 
	
	private final JTextArea idTextArea;
	private final JTextField nameField;
	private final JTextField personField;
	private final JTextField publicationField;
	private final JTextField chebiField;
	private final JTextField uniprotField;
	
	private final JCheckBox chckbxAND;
	private final JCheckBox chckbxOR;
	
	private final JButton loadSelectedButton;
	private final JPanel panel;	
	private final JScrollPane infoScrollPane;
	private final JEditorPane infoPane;
	
	private SearchBioModel searchBioModel;
	@SuppressWarnings("rawtypes")
	private JList biomodelsList;

	public static synchronized BioModelGUIDialog getInstance(JFrame parentFrame){
		if (uniqueInstance == null){
			uniqueInstance = new BioModelGUIDialog(parentFrame);
		}
		return uniqueInstance;
	}
	
	@SuppressWarnings("rawtypes")
	private BioModelGUIDialog(final JFrame parentFrame) {
		super(parentFrame, true);
		this.setSize(1000, 886);
		this.setResizable(false);
		this.setTitle("CySBML BioModel Import");
		this.setLocationRelativeTo(parentFrame);
		panel = new JPanel();
		getContentPane().setLayout(null);
		panel.setBounds(0, 0, 1000, 900);
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(null);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		// Labels
		JLabel lblLoadByBiomodel = new JLabel("BioModel Ids");
		lblLoadByBiomodel.setBounds(33, 708, 160, 15);
		panel.add(lblLoadByBiomodel);
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(33, 17, 160, 15);
		panel.add(lblName);
		JLabel lblPerson = new JLabel("Person");
		lblPerson.setBounds(33, 49, 160, 15);
		panel.add(lblPerson);
		JLabel lblPublication = new JLabel("Publication");
		lblPublication.setBounds(33, 81, 160, 15);
		panel.add(lblPublication);
		JLabel lblChebi = new JLabel("ChEBI");
		lblChebi.setBounds(33, 113, 160, 15);
		panel.add(lblChebi);
		JLabel lblUniprot = new JLabel("UniProt");
		lblUniprot.setBounds(33, 145, 160, 15);
		panel.add(lblUniprot);
		
		// Search By Name Field
		nameField = new JTextField();
		nameField.setToolTipText("Search BioModels by name");
		nameField.setBounds(112, 12, 160, 25);
		nameField.setText("glycolysis");
		nameField.setColumns(10);
		panel.add(nameField);
		nameField.addKeyListener(new EnterKeyAdapter());

		// Search By Person Field
		personField = new JTextField();
		personField.setToolTipText("Search Biomodels by Person");
		personField.setBounds(112, 44, 160, 25);
		personField.setColumns(10);
		panel.add(personField);
		personField.addKeyListener(new EnterKeyAdapter());
		
		// Search By Publication/Abstract
		publicationField = new JTextField();
		publicationField.setToolTipText("Search Biomodels by Publication/Abstract");
		publicationField.setBounds(112, 76, 160, 25);
		publicationField.setColumns(10);
		publicationField.addKeyListener(new EnterKeyAdapter());
		panel.add(publicationField);
		
		// Search by Chebi
		chebiField = new JTextField();
		chebiField.setToolTipText("Search Biomodels by Publication/Abstract");
		chebiField.setColumns(10);
		chebiField.setBounds(112, 108, 160, 25);
		chebiField.addKeyListener(new EnterKeyAdapter());
		panel.add(chebiField);
		
		// Search by UniProt
		uniprotField = new JTextField();
		uniprotField.setToolTipText("Search Biomodels by Publication/Abstract");
		uniprotField.setColumns(10);
		uniprotField.setBounds(112, 140, 160, 25);
		uniprotField.addKeyListener(new EnterKeyAdapter());
		panel.add(uniprotField);
		
		// Load Ids Button
		JButton loadIdsButton = new JButton("Load Ids");
		loadIdsButton.setToolTipText("Parse BioModel Ids and load the models.");
		loadIdsButton.setBounds(170, 821, 102, 25);
		panel.add(loadIdsButton);
		loadIdsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadBioModelByIdsAndDisposeDialog();
			}
		});
		
		JButton parseIdsButton = new JButton("Parse Ids");
		parseIdsButton.setToolTipText("Parse BioModel Ids from text.");
		parseIdsButton.setBounds(33, 821, 102, 25);
		panel.add(parseIdsButton);
		parseIdsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parseBioModelByIds();
			}
		});
				
		// Search Button
		JButton searchButton = new JButton("Search");
		searchButton.setToolTipText("Search Biomodels");
		searchButton.setBounds(33, 203, 102, 25);
		panel.add(searchButton);
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				searchBioModels();
			}
		});
		// Reset Button
		JButton resetButton = new JButton("Reset");
		resetButton.setToolTipText("Reset Search Fields");
		resetButton.setBounds(170, 203, 102, 25);
		panel.add(resetButton);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetFields();
			}
		});
		
		// Load Selected Models
		loadSelectedButton = new JButton("Load Selected");
		loadSelectedButton.setToolTipText("Load selected BioModels from the List");
		loadSelectedButton.setBounds(33, 666, 160, 25);
		loadSelectedButton.setEnabled(false);
		panel.add(loadSelectedButton);
		loadSelectedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadSelectedBioModelsAndDisposeDialog();
		}});
		
		// ScrollBars
		JScrollPane listScrollPane = new JScrollPane();
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane.setBounds(33, 240, 239, 414);
		panel.add(listScrollPane);
		
		// Set the empty Lists
		biomodelsList = new JList();
		biomodelsList.setToolTipText("Search results, select for information.");
		biomodelsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				// activate load button & get information for selection
				if (biomodelsList.isSelectionEmpty()){
					loadSelectedButton.setEnabled(false);
				}
				else{
					loadSelectedButton.setEnabled(true);
				}
				handleModelSelectionInModelList();
			}});
	
		listScrollPane.setViewportView(biomodelsList);	
		
		// information area
		infoScrollPane = new JScrollPane();
		infoScrollPane.setBounds(288, 12, 687, 833);
		panel.add(infoScrollPane);
		
		infoPane = new JEditorPane();
		infoPane.setToolTipText("Information Area");
		infoPane.setContentType("text/html");
		infoPane.setEditable(false);
		infoPane.setText(BioModelGUIText.getInfo());
		infoPane.addHyperlinkListener(new HyperlinkListener() {
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
		});
		infoScrollPane.setViewportView(infoPane);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(33, 703, 239, 2);
		panel.add(separator);
				
		JLabel lblComposeBy = new JLabel("Compose by");
		lblComposeBy.setBounds(33, 176, 102, 15);
		panel.add(lblComposeBy);
		
		chckbxAND = new JCheckBox("AND");
		chckbxAND.setBounds(125, 172, 61, 23);
		panel.add(chckbxAND);
		chckbxOR = new JCheckBox("OR");
		chckbxOR.setBounds(188, 172, 61, 23);
		panel.add(chckbxOR);
		chckbxAND.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				chckbxOR.setSelected(!chckbxAND.isSelected());
			}
		});
		chckbxOR.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				chckbxAND.setSelected(!chckbxOR.isSelected());
			}
		});
		chckbxAND.setSelected(true);
		
		JScrollPane idsScrollPane = new JScrollPane();
		idsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		idsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		idsScrollPane.setBounds(33, 725, 239, 90);
		panel.add(idsScrollPane);
		
		idTextArea = new JTextArea();
		idsScrollPane.setViewportView(idTextArea);
		idTextArea.setWrapStyleWord(true);
		idTextArea.setToolTipText("Past BioModel Ids to load.");
		idTextArea.setLineWrap(true);
		idTextArea.setRows(4);
		idTextArea.setTabSize(4);
		idTextArea.setText("BIOMD0000000070, BIOMD0000000071");
	}
    
	class EnterKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent keyE) {
			int key = keyE.getKeyCode();
		     if (key == KeyEvent.VK_ENTER) {
		    	 searchBioModels();
		     }
		}
	}
	
	public void showBioModelsPanel() {
        
		JFrame frame = new JFrame("CySBML BioModel Import");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.getContentPane().add(this);
        frame.setSize(600, 600);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	///////// SEARCH MODELS ////////////
	public void searchBioModels(){
		System.out.println("CySBML[INFO] -> Search BioModels");
		infoPane.setText(BioModelGUIText.performBioModelSearch());
		
		SearchContent searchContent = getSearchContent();
		searchBioModel = new SearchBioModel(
					ProxyTools.getCytoscapeProxyHost(),
					ProxyTools.getCytoscapeProxyPort()
				);
		
		searchBioModel.searchBioModels(searchContent);
		updateBioModelListAndInformationAfterSearch(searchBioModel.getModelIds());
	}
	
	public SearchContent getSearchContent(){
		String mode = SearchContent.CONNECT_AND;
		if (chckbxOR.isSelected()){
			mode = SearchContent.CONNECT_OR;
		}
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(SearchContent.CONTENT_NAME, nameField.getText());
		map.put(SearchContent.CONTENT_PERSON, personField.getText());
		map.put(SearchContent.CONTENT_PUBLICATION, publicationField.getText());
		//map.put(SearchContent.CONTENT_TAXONOMY, taxonomyField.getText());
		map.put(SearchContent.CONTENT_CHEBI, chebiField.getText());
		map.put(SearchContent.CONTENT_UNIPROT, uniprotField.getText());
		map.put(SearchContent.CONTENT_MODE, mode);
		
		return (new SearchContent(map));
	}
	
	///////// UPDATE GUI ////////////
	private void updateBioModelListAndInformationAfterSearch(List<String> ids){
		updateModelListInDialog(ids);
		updateBioModelInformation(getListOfSelectedModelIds());
		
		int topPosition = 0;
		infoPane.setCaretPosition(topPosition);
		JScrollBar scrollBar = infoScrollPane.getVerticalScrollBar();
		scrollBar.setValue(topPosition);
	}
	
	
	// working on raw JList - yes this should be like that
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateModelListInDialog(final List<String> modelIds){
		biomodelsList.setModel(new AbstractListModel() {
			List<String> values = modelIds;
			public int getSize() {
				return values.size();
			}
			public Object getElementAt(int index) {
				return values.get(index);
			}
		});
	}
		
	public void updateBioModelInformation(List<String> selectedModelIds){
		final int caretPosition = infoPane.getCaretPosition();
		final int scrollPosition = infoScrollPane.getVerticalScrollBar().getValue();
		Point location = infoScrollPane.getViewport().getLocation();
		
		String searchInfo = searchBioModel.getHTMLInformation(selectedModelIds);
		infoPane.setText(searchInfo);
		// TODO: cursor position not handled correctly
		try {
			infoPane.setCaretPosition(caretPosition);
		} catch (java.lang.IllegalArgumentException e) {}

		infoScrollPane.getViewport().setLocation(location);
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			   public void run() {
			       infoScrollPane.getVerticalScrollBar().setValue(scrollPosition);
			   }
		});
	}
	
	///////// SELECT MODELS ////////////
	private void handleModelSelectionInModelList(){
		List<String> selectedModelIds = getListOfSelectedModelIds();
		updateBioModelInformation(selectedModelIds);
	}
	
	public List<String> getListOfSelectedModelIds(){
		@SuppressWarnings("unchecked")
		List<String> selected = biomodelsList.getSelectedValuesList();
		return selected;
	}
	
	///////// LOAD MODELS ////////////
	public void loadSelectedBioModelsAndDisposeDialog(){
		if (biomodelsList.isSelectionEmpty() == false){
			this.dispose();
			List<String> ids = getListOfSelectedModelIds();
			for (String id: ids){
				LoadBioModel.loadBioModelById(id);
			}
		}
	}
	
	public void loadBioModelByIdsAndDisposeDialog(){
		Set<String> ids = parseBioModelIdsFromString(idTextArea.getText());
		this.dispose();
		for (String id : ids){
			LoadBioModel.loadBioModelById(id);
		}
	}
	
	public void parseBioModelByIds(){
		String text = idTextArea.getText();
		Set<String> ids = parseBioModelIdsFromString(text);
		
		String newText = "";
		for (String id : ids){
			newText += id + " ";
		}
		idTextArea.setText(newText);
		searchBioModel = new SearchBioModel(
				ProxyTools.getCytoscapeProxyHost(),
				ProxyTools.getCytoscapeProxyPort()
			);
		searchBioModel.getBioModelsByParsedIds(ids);
		updateBioModelListAndInformationAfterSearch(searchBioModel.getModelIds());
	}
	
	public static Set<String> parseBioModelIdsFromString(String text){
		Set<String> ids = new HashSet<String>();
		String bioModelPattern = "((BIOMD|MODEL)\\d{10})|(BMID\\d{12})";
		Pattern pattern = Pattern.compile(bioModelPattern);
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()){
			String id = matcher.group();
			ids.add(id);
		}
		return ids;
	}
	
	// Clear fields
	public void resetFields(){
		String reset = "";
		nameField.setText(reset);
		personField.setText(reset);
		publicationField.setText(reset);
		//taxonomyField.setText(reset);
		chebiField.setText(reset);
		uniprotField.setText(reset);
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			BioModelGUIDialog dialog = new BioModelGUIDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
