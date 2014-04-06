package cysbml;

/*
 Copyright (c) 2014 Matthias Koenig

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


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.data.ImportHandler;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;

import cysbml.cyactions.BiomodelAction;
import cysbml.cyactions.ChangeStateAction;
import cysbml.cyactions.HelpAction;
import cysbml.cyactions.ImportAction;
import cysbml.cyactions.LoadLayoutAction;
import cysbml.cyactions.SaveLayoutAction;
import cysbml.cyactions.ValidationAction;

import cysbml.gui.NavigationPanel;
import cysbml.logging.LogCyPlugin;
import cysbml.SBMLFilter;

/**
 * CSBMLPlugin Cytoscape SBML Plugin based on JSBML.
 * Uses the CytoscapePlugin class to integrate into Cytoscape.
 * 
 * Develobers:
 * Matthias Koenig, Computational Systems Biochemistry, Charite Berlin
 * matthias.koenig [at] charite.de
 * 
 * @author Matthias Koenig
 * @date 2014-01-24
 *    BUG: if node is not in SBML, "NamedSBase for node not found" information should be displayed;
 *    BUG: Visual style can not be changed for GRN Style.
 *  
 *    FIXME: Bugs Biomodel loading and associating with networks (test the interface with BioModels.
 *  
 *	  FIXME:  problems if offline (generate offline mode -> preload all the annotation information
 *    available in the annotation files (offline mode) 
 *    
 *    IMPLEMENT: additional reaction information (kinetics and if available the parameters)
 *    		-> preprocessing of the SBML file for availability of referred information in 
 *    			kineticLaws and the Parameters (represent kinetics and parameters in proper view)
 *    
 *    TODO: test SBMLGraphReader
 *    FIXME: still problems with the offline mode
 *    IMPLEMENT: save SBML layouts
 *    IMPLEMENT: export SBML    
 *    IMPLEMENT: include CyFluxViz file exporter in SBMLSimulator !
 *    IMPLEMENT: socket connection
 *    
 * 	  FIXME: if no network view and network selected in NetworkPanel the SBML information in the
 * 		Navigation Panel is not updated, reset after session loaded
 *    TODO: BioModelDialog -> some hacks in the scrollbar to make it work
 *    TODO: SearchResults handled as List and not as Set
 *    TODO: build with MAVEN
 *******************************************
 * MAVEN BUILD INFORMATION FOR DEPENDECIES
 * CYTOSCAPE 2.8.3-SNAPSHOT 
 
 * MIRIAM
 <dependencies>
    <dependency>
      <groupId>uk.ac.ebi.miriam</groupId>
      <artifactId>miriam-lib</artifactId>
      <version>1.1.5</version>
    </dependency>
  </dependencies>
  
  <repositories>
	<repository>
	  <id>ebi-repo</id>
	  <name>The EBI internal repository</name>
	  <url>http://www.ebi.ac.uk/~maven/m2repo</url>
	  <releases>
	    <enabled>true</enabled>
	  </releases>
	  <snapshots>
	    <enabled>false</enabled>
	  </snapshots>
	</repository>
  </repositories>
  
 * BIOMODELS
   <dependencies>
    <dependency>
      <groupId>uk.ac.ebi.biomodels</groupId>
      <artifactId>biomodels-wslib</artifactId>
      <version>1.21</version>
    </dependency>
  </dependencies>
  
  <repositories>
  <repository>
    <id>ebi-repo</id>
    <name>The EBI internal repository</name>
    <url>http://www.ebi.ac.uk/~maven/m2repo</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
  </repositories>
 */
public class CySBML extends CytoscapePlugin implements PropertyChangeListener{
	public static final String NAME = "CySBML"; 
	public static final String VERSION = "v1.30";
	public static LogCyPlugin LOGGER = new LogCyPlugin(NAME);
	
	public static final boolean DEBUG = true;
	public static final boolean TESTING = false;
	
	private NavigationPanel navigationPanel;
		
	public CySBML() throws SecurityException, IOException{
		CySBML.LOGGER.info(getVersionedName());
		try {
			// Handle proxy changes & add proxy settings 
			Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
			CySBMLConnection.setProxySettings();
			
			addFilterForSBML();
			addCytoscapeActions();

			navigationPanel = NavigationPanel.getInstance();
		    navigationPanel.deactivate();
	
		    CySBML.LOGGER.config("Initialization finished");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getVersionedName(){
		return NAME + "-" + VERSION;
	}
	
	private void addFilterForSBML(){
		// Test if SBMLReader is set in Import filters and give warning if other filter is set
		ImportHandler ih = Cytoscape.getImportHandler();
		SBMLFilter filter = new SBMLFilter();
		Set<String> extensions = filter.getExtensionSet();
		for (String extension : extensions) {
			if (ih.getAllExtensions().contains(extension) && !extension.equals("xml")) {
				System.out.println("Extension : " + extension); 
				String msg = 
				 		"Currently SBMLReader is still registered with 'sbml & 'xml' files.\n" +
				 		"To activate CySBML functionality and to register CySBML with 'sbml & 'xml' files remove \n" +
				 		"'sbml-reader-2.8.x-jar-with-dependencies.jar'\n" +
				 		"from the Cytoscape plugin folder.\n" +
				 		"The full functionality of sbml-reader is available in CySBML"; 
				 JOptionPane.showMessageDialog (Cytoscape.getDesktop(), msg);
				break;
			}
		}
		// Register CySBML with sbml files
		ih.addFilter(filter);
	}
	
	private void addCytoscapeActions(){
		CyMenus menus = Cytoscape.getDesktop().getCyMenus();
    	menus.addCytoscapeAction(new ImportAction(getIconForName("import"), this));
    	menus.addCytoscapeAction(new BiomodelAction(getIconForName("biomodel"), this));
    	menus.addCytoscapeAction(new ValidationAction(getIconForName("validation"), this));
    	menus.addCytoscapeAction(new ChangeStateAction(getIconForName("changestate"), this));
    	menus.addCytoscapeAction(new HelpAction(getIconForName("help"), this));
		menus.addCytoscapeAction(new SaveLayoutAction(getIconForName("savelayout"), this));
		menus.addCytoscapeAction(new LoadLayoutAction(getIconForName("loadlayout"), this));
	}
	
	private ImageIcon getIconForName(String name){
		String imageFolder = "/cysbml/gui/help/images/";
		ImageIcon icon = new ImageIcon(getClass().getResource(imageFolder + name + ".png"));
		return icon;
	}
		
	/////////////////  EVENT HANDLERS ////////////////////////////////////
	
	/** Handle the changes in Cytoscape Proxy for the WebServices */
	public void propertyChange(PropertyChangeEvent e) {		
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.PROXY_MODIFIED))
		{	
			CySBML.LOGGER.info("propertyChange PROXY_MODIFIED");
			CySBMLConnection.setProxySettings();
			JOptionPane.showMessageDialog (Cytoscape.getDesktop(),
					"Restart Cytoscape for changes to take effect.");
		}
	}
}