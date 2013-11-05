package cysbml.validation;

import org.sbml.jsbml.SBMLDocument;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

public class CySBMLValidatorTask implements Task{
	
	private cytoscape.task.TaskMonitor taskMonitor;
	private SBMLDocument doc;
	private CySBMLValidator validator;
	
	public CySBMLValidatorTask(SBMLDocument doc) {
		this.doc = doc;
	}
	
	public CySBMLValidator getValidator(){
		return validator;
	}

	public void setTaskMonitor(TaskMonitor monitor)
			throws IllegalThreadStateException {
		taskMonitor = monitor;
	}

	public void halt() {
		// not implemented
	}

	public String getTitle() {
		return "Validate SBML " + doc.getModel().getId();
	}

	public void run() {
		taskMonitor.setStatus("Online SBML Validation ...");
		taskMonitor.setPercentCompleted(-1);
		try {
			taskMonitor.setPercentCompleted(40);
			validator = new CySBMLValidator(doc);
			taskMonitor.setStatus("Validating SBML ...");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		taskMonitor.setPercentCompleted(100);
	}
	
	public static CySBMLValidator getValidator(SBMLDocument document){
		CySBMLValidatorTask vTask = new CySBMLValidatorTask(document);
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		TaskManager.executeTask(vTask, jTaskConfig);
		return vTask.getValidator();
	}
}