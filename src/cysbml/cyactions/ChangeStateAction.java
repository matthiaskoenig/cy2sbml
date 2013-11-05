package cysbml.cyactions;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cysbml.CySBMLPlugin;
import cysbml.gui.NavigationPanel;
import cytoscape.util.CytoscapeAction;

@SuppressWarnings("serial")
public class ChangeStateAction extends CytoscapeAction {
    public ChangeStateAction() {super(CySBMLPlugin.NAME + " Hide/Show Panel");}
    
	public ChangeStateAction(ImageIcon icon, CySBMLPlugin plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBMLPlugin.NAME + " Hide/Show Panel");
	}
    
    public void actionPerformed(ActionEvent ae) {
    	NavigationPanel navPanel = NavigationPanel.getInstance();
    	if (navPanel.isActive()){
    		navPanel.deactivate();
    	}else{
    		navPanel.activate();
    	}
    }	    
    
	public boolean isInToolBar() {
		return true;
	}
	
	public boolean isInMenuBar() {
		return false;
	}
}
