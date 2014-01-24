package cysbml.visual.style;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;

import cysbml.visual.VisualStyleManager.CustomStyle;
import cysbml.CySBML;

/** The parent class of all styles. */
public abstract class StyleGeneric {
	protected CustomStyle style;
	protected Collection<Calculator> ecalcs = new HashSet<Calculator>();
	protected Collection<Calculator> ncalcs = new HashSet<Calculator>();	
	protected Map<VisualPropertyDependency.Definition, Boolean> depMap = 
					new HashMap<VisualPropertyDependency.Definition, Boolean>();
	
	public StyleGeneric(CustomStyle style){
		this.style = style;
		createEdgeCalculators();
		createNodeCalculators();
		createVisualPropertyDependencies();
		
	}
	protected abstract void createEdgeCalculators();
	protected abstract void createNodeCalculators();
	protected abstract void createVisualPropertyDependencies();
	
	public VisualStyle createVisualStyle(){
		VisualStyle vs = createVisualStyle(style, ncalcs, ecalcs);
		return vs;
		
	}
	
	private VisualPropertyDependency createVisualPropertyDependencies(VisualStyle vs){
		VisualPropertyDependency vsDeps = vs.getDependency();
		for (VisualPropertyDependency.Definition def: depMap.keySet()){
			vsDeps.set(def, depMap.get(def));
		}
		return vsDeps;
	}
	
	/** Creates VisualStyle from the node and edge appearance calculators. */
	private VisualStyle createVisualStyle(CustomStyle style, Collection<Calculator> nodeAppearanceCalculators,
			Collection<Calculator> edgeAppearanceCalculators) {
		String name = style.toString();
		CySBML.LOGGER.config("createVisualStyle( " + name + " )");
		VisualStyle vs = new VisualStyle(name);
		VisualPropertyDependency deps = createVisualPropertyDependencies(vs);
		
		// add node appearance
		NodeAppearanceCalculator nac = new NodeAppearanceCalculator(deps);
		for (Calculator nc: nodeAppearanceCalculators){
			nac.setCalculator(nc);
		}
		vs.setNodeAppearanceCalculator(nac);
		
		// add edge appearance
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator(deps);
		for (Calculator ec: edgeAppearanceCalculators){
			eac.setCalculator(ec);
		}
		vs.setEdgeAppearanceCalculator(eac);
		
		// add global appearance
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		GlobalAppearanceCalculator gac = vmm.getVisualStyle().getGlobalAppearanceCalculator();
		gac.setDefaultBackgroundColor(new Color(new Float(1.0), new Float(1.0), new Float(1.0)));
		vs.setGlobalAppearanceCalculator(gac);
		return vs;
	}
	
	
	protected static String createCalculatorName(CustomStyle style, VisualPropertyType pType){
		return style.toString() + "_" + pType.toString();
	}
}
