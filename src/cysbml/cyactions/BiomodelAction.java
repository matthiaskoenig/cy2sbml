package cysbml.cyactions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import cysbml.CySBML;
import cysbml.biomodel.BioModelGUIDialog;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

@SuppressWarnings("serial")
public class BiomodelAction extends CytoscapeAction {
    public BiomodelAction() {super(CySBML.NAME + " BioModel Import");}
    
	public BiomodelAction(ImageIcon icon, CySBML plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBML.NAME + " BioModel Import");
	}
    
    /** This method is called when the user selects the menu item.*/
    public void actionPerformed(ActionEvent ae) {
	    BioModelGUIDialog bioModelsDialog = BioModelGUIDialog.getInstance((JFrame) Cytoscape.getDesktop());
	    bioModelsDialog.setVisible(true);
    }
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
}
