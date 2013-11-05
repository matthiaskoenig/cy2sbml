package cysbml.cyactions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cysbml.CySBMLPlugin;
import cysbml.layout.CytoscapeLayoutTools;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

@SuppressWarnings("serial")
public class LoadLayoutAction extends CytoscapeAction {

	public LoadLayoutAction() {
		super(CySBMLPlugin.NAME+" Load Layout");
	}

	public LoadLayoutAction(ImageIcon icon, CySBMLPlugin plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBMLPlugin.NAME+" Load Layout");
	}

	public void actionPerformed(ActionEvent ae) {
		File xmlFile = FileUtil.getFile(
				"Load Layout for current CyNetworkView", FileUtil.LOAD);
		CytoscapeLayoutTools.loadLayoutOfCurrentViewFromFile(xmlFile);
	}

	public boolean isInToolBar() {
		return true;
	}

	public boolean isInMenuBar() {
		return false;
	}
}
