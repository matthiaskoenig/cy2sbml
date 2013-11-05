package cysbmlexample;
/*
 Copyright (c) 2013, Matthias Koenig, Computational Systems Biochemistry, 
 Charite Berlin
 matthias.koenig [at] charite.de

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

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;

import cysbml.gui.NavigationPanel;
import cysbml.CySBMLConstants;
import cysbml.CySBMLPlugin;

public class CySBMLExample extends CytoscapePlugin {
	public final String NAME = CySBMLPlugin.NAME + "Example";
	
	public CySBMLExample() {
		System.out.println(NAME + "[INFO]->" + NAME + "-" + CySBMLPlugin.VERSION);
		Example1Action action1 = new Example1Action();
		action1.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action1);
		
		Example2Action action2 = new Example2Action();
		action2.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action2);
	}

	//// EXAMPLE 1 ///////////////////////////////////////////////
	@SuppressWarnings("serial")
	public class Example1Action extends CytoscapeAction {
	    public Example1Action() {super("CySBML Example 1 - Select reversible reaction nodes");}
	    public void actionPerformed(ActionEvent ae) {
	    	selectReversibleReactionNodes();
	    }
	}
		
	public void selectReversibleReactionNodes(){
		CyNetwork network = Cytoscape.getCurrentNetwork();
		network.unselectAllNodes();
		Collection<CyNode> reversibleReactionNodes = getReversibleReactionNodes();
		network.setSelectedNodeState(reversibleReactionNodes, true);		
		Cytoscape.getCurrentNetworkView().updateView();
		
		String text = String.format("%s reversible reactions", reversibleReactionNodes.size());
		JOptionPane.showMessageDialog(
				Cytoscape.getDesktop(),
				text,
				"CySBML Example - Select Reversible Reactions",
				JOptionPane.INFORMATION_MESSAGE);
    }
	
	private Collection<CyNode> getReversibleReactionNodes(){
		Collection<CyNode> rrNodes = new LinkedList<CyNode>();
		@SuppressWarnings("unchecked")
		List<CyNode> nodes = Cytoscape.getCyNodesList();
		for (CyNode n : nodes){
			if (isReversibleReactionNode(n)){
				rrNodes.add(n);
			}
		}
		return rrNodes;
	}
	
	private boolean isReversibleReactionNode(CyNode n){
		boolean result = false;
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String id = n.getIdentifier();
		
		// Get the SBMLType 
		Object tmp = nodeAttributes.getAttribute(id, CySBMLConstants.ATT_TYPE);
		if (tmp == null){
			return false;
		}
		String sbmlType = (String) tmp; 
		
		// Get the reversibility information
		tmp = nodeAttributes.getAttribute(id, CySBMLConstants.ATT_REVERSIBLE);
		if (tmp == null){
			return false;
		}
		boolean reversible = (Boolean) tmp;
		
		// Test if the node is a reaction and reversible
		if (sbmlType.equals(CySBMLConstants.NODETYPE_REACTION) 
				&& reversible == true){
			result = true;
		}
		return result;
	}
	
	////EXAMPLE 2 ///////////////////////////////////////////////
	@SuppressWarnings("serial")
	public class Example2Action extends CytoscapeAction {
	    public Example2Action() {super("CySBML Example 2 - Access to current SBMLDocument");}
	    public void actionPerformed(ActionEvent ae) {
	    	displayReactionsInCurrentSBMLDocument();
	    }
	}
	
	public void displayReactionsInCurrentSBMLDocument(){
		// Get the current SBML document via Singleton instance of the navigatin panel
		NavigationPanel panel = NavigationPanel.getInstance();
		SBMLDocument doc = panel.getCurrentSBMLDocument();
		
		// Work with the current SBML document
		String text = null;
		if (doc == null){
			text = "No SBML document available for current network";
		}
		else {
			// Access to the SBMLDocument
			Model model = doc.getModel();
			text = String.format("Current SBML document: id=%s", model.getId());
		}
		JOptionPane.showMessageDialog(
				Cytoscape.getDesktop(),
				text,
				"CySBML Current SBMLDocument",
				JOptionPane.INFORMATION_MESSAGE);
	}
}
