package cysbml.visual;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

import cysbml.CySBML;
import cysbml.visual.style.StyleDefault;
import cysbml.visual.style.StyleGRN;
import cysbml.visual.style.StyleGeneric;
import cysbml.visual.style.StyleLayout;


/** Manage the Cytoscape Visual Styles associated with CySBML. */
public class VisualStyleManager {
	
	/** Visual Styles defined for CySBML. */
	public enum CustomStyle {
		 DEFAULT_STYLE(CySBML.NAME),
		 LAYOUT_STYLE(CySBML.NAME + "-Layout"),
		 GRN_STYLE(CySBML.NAME + "-GRN");
		 
		 private String name;
		 private CustomStyle(String n) {
		   name = n;
		 }	 
		 public String toString(){
			 return name;
		 }
	}
	
	public static VisualStyle createVisualStyle(CustomStyle style){
		StyleGeneric styleGen = null;
		switch (style){
			case DEFAULT_STYLE:
				styleGen = new StyleDefault(style);
				break;
			case LAYOUT_STYLE:
				styleGen = new StyleLayout(style);
				break;
			case GRN_STYLE:
				styleGen = new StyleGRN(style);
				break;
			default:
				CySBML.LOGGER.error("VisualStyle not supported by VisualStyleFactory -> " + style.toString());
				return null;
		}
		return styleGen.createVisualStyle(); 
	}
	
	/// SET VISUAL STYLE ///
	public static void setVisualStyleForNetwork(CyNetwork network, CustomStyle style) {
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		
		// TODO: less brutal (properties are overwritten)
		String vsName = style.toString();
		VisualStyle vs = catalog.getVisualStyle(vsName);
		if (vs != null) {
			catalog.removeVisualStyle(vsName);
		}
		// only creates the style
		vs = createVisualStyle(style);
		catalog.addVisualStyle(vs);
		manager.setVisualStyle(vs);
		
		// TODO: better get the view for the network
		Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
		Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
	}
	
	public static void setVisualStyleForCurrentView(){
		setVisualStyleForView(Cytoscape.getCurrentNetworkView());
	}
	
    public static void setVisualStyleForView(CyNetworkView view){
        VisualStyle vs = getCurrentVisualStyle();
        view.setVisualStyle(vs.getName());
    }
	    
	
	/** Get the currently set VisualStyle from the Visual mapping manager.
	 * Returns null if the VisualStyle does not exist. */
	public static VisualStyle getCurrentVisualStyle(){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		return vmm.getVisualStyle();
	}
	
	/** Gets visual style from Calculator Catalog. */
	private static VisualStyle getVisualStyle(String vsName){
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calcCatalog = vmm.getCalculatorCatalog();
		VisualStyle vs = calcCatalog.getVisualStyle(vsName);
		return vs;
	}
	
	@SuppressWarnings("unused")
	private static VisualStyle getVisualStyle(CustomStyle style){
		return getVisualStyle(style.toString());
	}
}
