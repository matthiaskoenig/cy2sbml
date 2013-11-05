package cysbml.gui;

import java.awt.Font;
import java.util.LinkedList;
import java.util.List;
import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLError;

import cysbml.validation.CySBMLValidator;
import cysbml.validation.CySBMLValidatorTask;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class ValidationDialog extends JDialog implements
		ListSelectionListener {

	private JEditorPane errorPane;
	private JTable errorTable;
	
	private CySBMLValidator validator;

	/**
	 * @wbp.parser.constructor
	 */
	private ValidationDialog(JFrame parentFrame) {
		super(parentFrame, true);
		this.setSize(600, 800);
		this.setResizable(true);
		this.setLocationRelativeTo(parentFrame);
		getContentPane().setLayout(null);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		panel.add(splitPane);
		splitPane.setResizeWeight(0.1);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JScrollPane errorScrollPane = new JScrollPane();
		splitPane.setRightComponent(errorScrollPane);

		errorPane = new JEditorPane();
		errorPane.setFont(new Font("Dialog", Font.PLAIN, 10));
		errorPane.setEditable(false);
		errorPane.setContentType("text/html");
		errorScrollPane.setViewportView(errorPane);
		
				errorTable = new JTable();
				errorTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				splitPane.setLeftComponent(errorTable);
				errorTable.setModel(new DefaultTableModel(
					new Object[][] {
						{"Info", ""},
						{"Warning", ""},
						{"Error", ""},
						{"Fatal", ""},
						{"All", ""},
					},
					new String[] {
						"Severity", "Error count"
					}
				));
				errorTable.getSelectionModel().addListSelectionListener(this);
	}

	public ValidationDialog(JFrame parentFrame, SBMLDocument document) {
		this(parentFrame);

		String title = "CySBML Validator : " + document.getModel().getId();
		setTitle(title);

		validator = CySBMLValidatorTask.getValidator(document);
		setErrorTableFromValidator(validator);
	}
	
	private void setErrorTableFromValidator(final CySBMLValidator sbmlValidator){
		if (sbmlValidator.getErrorMap() != null) {
			errorTable.setModel(new DefaultTableModel(
					sbmlValidator.getErrorTable(),
					new String[] { "Severity", "Error count" }));
			errorTable.setRowSelectionInterval(2, 3);
			updateErrorTable();
		} else {
			errorPane.setText("Online SBML validation currently not possible.");
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		updateErrorTable();
	}
	
	private void updateErrorTable(){
		List<SBMLError> eList = new LinkedList<SBMLError>();
		// if categories selected get the errors
		if (errorTable.getSelectedRowCount() > 0){
			int[] selectedRows = errorTable.getSelectedRows();
			String[] keys = new String[selectedRows.length];
			for (int k=0; k<keys.length; ++k){
				keys[k] = (String) errorTable.getModel().getValueAt(selectedRows[k], 0);
			}
			eList = validator.getErrorListForKeys(keys);
		}
		errorPane.setText(validator.getErrorListString(eList));
	}
}
