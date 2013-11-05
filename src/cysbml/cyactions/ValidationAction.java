package cysbml.cyactions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.sbml.jsbml.SBMLDocument;

import cysbml.CySBMLPlugin;
import cysbml.gui.NavigationPanel;
import cysbml.gui.ValidationDialog;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

@SuppressWarnings("serial")
public class ValidationAction extends CytoscapeAction {
    public ValidationAction() {super(CySBMLPlugin.NAME + " Validator");}
    
	public ValidationAction(ImageIcon icon, CySBMLPlugin plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBMLPlugin.NAME + " Validator");
	}
 
    public void actionPerformed(ActionEvent ae) {
    	openValidationPanel();
    }
    
    public void openValidationPanel(){
    	
    	SBMLDocument doc = NavigationPanel.getInstance().getCurrentSBMLDocument();
    	if (doc == null){
    		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"<html>SBML network has to be loaded before validation.<br>" +
					"Import network from BioModel or load network from file or URL first.");
    	}
    	else{
    		ValidationDialog validationDialog = new ValidationDialog((JFrame) Cytoscape.getDesktop(), doc);
    		validationDialog.setVisible(true);
    	}
    }
    
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
}
