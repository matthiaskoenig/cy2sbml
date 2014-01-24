package cysbml.visual.style;

import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import cysbml.CySBMLConstants;
import cysbml.layout.NetworkLayout;
import cysbml.visual.VisualStyleManager.CustomStyle;

/** Style applied to SBML Layouts. */
public class StyleLayout extends StyleGeneric{
	public StyleLayout(CustomStyle style) {
		super(style);
	}

	@Override
	protected void createEdgeCalculators() {
		ecalcs.add(StyleDefault.createEdgeTargetArrowCalculator(style));
		ecalcs.add(StyleDefault.createEdgeSourceArrowCalculator(style));
		ecalcs.add(StyleDefault.createEdgeOpacityCalculator(style));		
	}

	@Override
	protected void createNodeCalculators() {
		ncalcs.add(StyleDefault.createNodeLabelCalculator(style));
		ncalcs.add(StyleDefault.createNodeFillColorCalculator(style));
		ncalcs.add(StyleDefault.createNodeBorderColorCalculator(style));
		ncalcs.add(StyleLayout.createNodeShapeCalculator(style));
		ncalcs.add(StyleLayout.createNodeHeightCalculator(style));
		ncalcs.add(StyleLayout.createNodeWidthCalculator(style));
	}

	@Override
	protected void createVisualPropertyDependencies() {
		depMap.put(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, false);
	}

	
	/// NODE CALCULATORS ///
	
	public static Calculator createNodeShapeCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_SHAPE;
		DiscreteMapping map = new DiscreteMapping(NodeShape.class, CySBMLConstants.ATT_TYPE);
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, NodeShape.RECT);
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, NodeShape.RECT);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, NodeShape.RECT);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, NodeShape.RECT);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}

	public static Calculator createNodeHeightCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_HEIGHT;
		PassThroughMapping map = new PassThroughMapping(Double.class, NetworkLayout.ATT_LAYOUT_HEIGHT);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);	
	}
	
	public static Calculator createNodeWidthCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_WIDTH;
		PassThroughMapping map = new PassThroughMapping(Double.class, NetworkLayout.ATT_LAYOUT_WIDTH);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);	
	}
}
