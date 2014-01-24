package cysbml.cyactions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBException;

import cysbml.CySBML;
import cytoscape.Cytoscape;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.dialogs.ImportNetworkDialog;
import cytoscape.util.CytoscapeAction;

@SuppressWarnings("serial")
public class ImportAction extends CytoscapeAction {
	/** The constructor sets the text that should appear on the menu item.*/
    public ImportAction() {super(CySBML.NAME + " Import SBML");}
    
	public ImportAction(ImageIcon icon, CySBML plugin) {
		super("", icon);
		this.putValue(Action.SHORT_DESCRIPTION, CySBML.NAME + " Import SBML");
	}
    
	public void actionPerformed(ActionEvent e) {
		// open new dialog
		ImportNetworkDialog fd = null;

		try {
			fd = new ImportNetworkDialog(Cytoscape.getDesktop(), true);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		fd.pack();
		fd.setLocationRelativeTo(Cytoscape.getDesktop());
		fd.setVisible(true);

		if (fd.getStatus() == false) {
			return;
		}

		if (fd.isRemote()) {
			String URLstr = fd.getURLStr();
			try {
				LoadNetworkTask.loadURL(new URL(URLstr), false);
			} catch (MalformedURLException e3) {
				JOptionPane.showMessageDialog(fd, "URL error!", "Warning",
			 	                             JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			final File[] files = fd.getFiles();
			boolean skipMessage = false;

			if ((files != null) && (files.length != 0)) {
				if (files.length != 1) {
					skipMessage = true;
				}

				List<String> messages = new ArrayList<String>();
				messages.add("Successfully loaded the following files:");
				messages.add(" ");

				for (int i = 0; i < files.length; i++) {
					if (fd.isRemote() == true) {
						messages.add(fd.getURLStr());
					} else {
						messages.add(files[i].getName());
					}
	
					LoadNetworkTask.loadFile(files[i], skipMessage);
				}

				if (files.length != 1) {
					JOptionPane messagePane = new JOptionPane();
					messagePane.setLocation(Cytoscape.getDesktop().getLocationOnScreen());
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(), messages.toArray(),
					                              "Multiple Network Files Loaded",
					                              JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	}
	public boolean isInToolBar() {
		return true;
	}
	public boolean isInMenuBar() {
		return false;
	}
}