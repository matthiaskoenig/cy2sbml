package cysbml.cyactions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cysbml.CySBML;
import cysbml.layout.CytoscapeLayoutTools;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

@SuppressWarnings("serial")
public class SaveLayoutAction extends CytoscapeAction {
    public SaveLayoutAction() {super(CySBML.NAME + " Save Layout");}
    
	public SaveLayoutAction(ImageIcon icon, CySBML plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBML.NAME + " Save Layout");
	}
    
    public void actionPerformed(ActionEvent ae) {
    	File xmlFile = FileUtil.getFile("Save Layout in XML", FileUtil.SAVE);
    	CytoscapeLayoutTools.saveLayoutOfCurrentViewInFile(xmlFile);
    }
    
	public boolean isInToolBar() {
		return true;
	}
	
	public boolean isInMenuBar() {
		return false;
	}
}