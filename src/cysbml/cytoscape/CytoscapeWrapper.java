package cysbml.cytoscape;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

public class CytoscapeWrapper {
	/**
	 * Sets the Status Bar Message.
	 * Feature only available in Cytoscape 2.2.
	 *
	 * @param msg User msg.
	 */
	public static void setStatusBarMsg(String msg) {
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		desktop.setStatusBarMsg(msg);
	}

	/**
	 * Clears the Status Bar Message.
	 * Feature only available in Cytoscape 2.2.
	 */
	public static void clearStatusBar() {
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		desktop.clearStatusBar();
	}
}