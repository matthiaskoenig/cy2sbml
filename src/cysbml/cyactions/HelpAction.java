package cysbml.cyactions;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Action;
import javax.swing.ImageIcon;

import cysbml.CySBML;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.OpenBrowser;

/** Opens the Help in a Browser. */
@SuppressWarnings("serial")
public class HelpAction extends CytoscapeAction {
	public static final String HELP_URL = "http://www.charite.de/sysbio/people/koenig/software/cysbml/index.html";
	
	
    public HelpAction() {super(CySBML.NAME + "Help");}
    
	public HelpAction(ImageIcon icon, CySBML plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBML.NAME + " Help");
	}
    
    /** This method is called when the user selects the menu item.*/
    public void actionPerformed(ActionEvent ae) {
    	URL url;
		try {
			url = new URL(HELP_URL);
			OpenBrowser.openURL(url.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }
    
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
}