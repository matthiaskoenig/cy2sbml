package cysbml.miriam;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JEditorPane;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;

import cysbml.CySBML;
import cysbml.gui.NavigationPanel;

/** Generates information for from web resources in separate Thread. */
public class NamedSBaseInfoThread extends Thread{
	Collection<Object> objSet;
	NavigationPanel panel;
	String infoText;
	   
    public NamedSBaseInfoThread(Collection<Object> objSet, NavigationPanel panel) {
        this.objSet = objSet;
        this.panel = panel;
        infoText = "";
    }

    public void run() {
    	if (panel != null){
    		// Info creation mode
    		for (Object obj : objSet){	
    			NamedSBaseInfoFactory infoFac = new NamedSBaseInfoFactory(obj);
    			infoFac.createInfo();
    			infoText += infoFac.getInfo();
    			updateText();
    		}
    	} else {
    		// Cache filling mode
    		for (Object obj : objSet){	
    			NamedSBaseInfoFactory infoFac = new NamedSBaseInfoFactory(obj);
    			infoFac.cacheMiriamInformation();
    		}
    	}
    }
    
	/** Update Text in the navigation panel.
	 * Only updates information if the current thread is the last requested thread 
	 * for updating text. */
    private void updateText(){
    	if (this.getId() == panel.getLastInfoThreadId()){
    		JEditorPane textPane = panel.getTextPane();
    		textPane.setText(infoText);
    	}
    }
    
	/** Reads the annotation information in the Miriam Cash */
	public static void preloadAnnotationInformationForModel(Model model){
		CySBML.LOGGER.info("Preload Miriam for compartments");
		preloadAnnotationForListOf(model.getListOfCompartments());
		CySBML.LOGGER.info("Preload Miriam for species");
		preloadAnnotationForListOf(model.getListOfSpecies());
		CySBML.LOGGER.info("Preload Miriam for reactions");
		preloadAnnotationForListOf(model.getListOfReactions());
	}
	
	private static void preloadAnnotationForListOf(@SuppressWarnings("rawtypes") ListOf list){
		Set<Object> nsbSet = new HashSet<Object>();
		for (Object nsb: list){
			nsbSet.add(nsb);
		}
		NamedSBaseInfoThread thread = new NamedSBaseInfoThread((Collection<Object>) nsbSet, null);
		thread.start();
	}
}