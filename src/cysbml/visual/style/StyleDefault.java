package cysbml.visual.style;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import cytoscape.data.Semantics;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import cysbml.CySBMLConstants;
import cysbml.tools.AttributeUtils;
import cysbml.visual.VisualStyleManager.CustomStyle;

public class StyleDefault extends StyleGeneric{

	public StyleDefault(CustomStyle style) {
		super(style);
	}

	@Override
	protected void createEdgeCalculators() {
		ecalcs.add(StyleDefault.createEdgeTargetArrowCalculator(style));
		ecalcs.add(StyleDefault.createEdgeSourceArrowCalculator(style));
		ecalcs.add(StyleDefault.createEdgeColorCalculator(style));
		ecalcs.add(StyleDefault.createEdgeOpacityCalculator(style));
	}

	@Override
	protected void createNodeCalculators() {
		ncalcs.add(StyleDefault.createNodeShapeCalculator(style));
		ncalcs.add(StyleDefault.createNodeLabelCalculator(style));
		ncalcs.add(StyleDefault.createNodeFillColorCalculator(style));
		ncalcs.add(StyleDefault.createNodeBorderColorCalculator(style));
		ncalcs.add(StyleDefault.createNodeLineWidthCalculator(style));
	}

	@Override
	protected void createVisualPropertyDependencies() {
		depMap.put(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, true);
	}
	
	
	/// NODE CALCULATORS ///
	
	public static Calculator createNodeShapeCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_SHAPE;
		DiscreteMapping map = new DiscreteMapping(NodeShape.class, CySBMLConstants.ATT_TYPE);
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, NodeShape.ELLIPSE);
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, NodeShape.DIAMOND);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, NodeShape.ELLIPSE);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, NodeShape.DIAMOND);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	
	public static Calculator createNodeLabelOpacityCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_LABEL_OPACITY;
		DiscreteMapping map = new DiscreteMapping(Double.class, CySBMLConstants.ATT_TYPE);
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, new Double(255.0));
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, new Double(1.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, new Double(255.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, new Double(1.0));
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	
	public static Calculator createNodeSizeCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_SIZE;
		DiscreteMapping map = new DiscreteMapping(Double.class, CySBMLConstants.ATT_TYPE);	
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, new Double(35.0));
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, new Double(30.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, new Double(35.0));
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, new Double(30.0));
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);	
	}
	
	public static Calculator createNodeLabelCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_LABEL;
		PassThroughMapping map = new PassThroughMapping(String.class , CySBMLConstants.ATT_NAME);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	
	public static Calculator createNodeFillColorCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_FILL_COLOR;
		DiscreteMapping map = new DiscreteMapping(Color.class, CySBMLConstants.ATT_REVERSIBLE);
		Color gray = new Color(204,204,204);  
		Color red = new Color(255,102,102); 
		map.putMapValue(true, gray);
		map.putMapValue(false, red);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}

	public static Calculator createNodeLineWidthCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_LINE_WIDTH;
		DiscreteMapping map = new DiscreteMapping(Double.class, CySBMLConstants.ATT_TYPE);
		map.putMapValue(CySBMLConstants.NODETYPE_SPECIES, 5.0);
		map.putMapValue(CySBMLConstants.NODETYPE_REACTION, 1.0);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_SPECIES, 5.0);
		map.putMapValue(CySBMLConstants.NODETYPE_QUAL_TRANSITION, 1.0);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);	
	}
	
	public static Calculator createNodeBorderColorCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_BORDER_COLOR;
		Set<Object> compartments = AttributeUtils.getValueSet(CySBMLConstants.ATT_COMPARTMENT);
		List<String> colorClasses = new LinkedList<String>();
		for (Object obj: compartments){
			colorClasses.add( (String) obj);
		}
		List<Color> colorSet = new LinkedList<Color>();
		colorSet.add(Color.BLACK);
		colorSet.add(Color.RED);
		colorSet.add(Color.BLUE);
		colorSet.add(Color.ORANGE);
		colorSet.add(Color.GREEN);
		colorSet.add(Color.CYAN);
		colorSet.add(Color.MAGENTA);
		colorSet.add(Color.PINK);
				
		DiscreteMapping map = new DiscreteMapping(Color.class, CySBMLConstants.ATT_COMPARTMENT);
		map.putMapValue("-", Color.LIGHT_GRAY);
		map.putMapValue("", Color.WHITE);
		int k=0;
		for (String colorClass: colorClasses){
			if (colorClass.equals("-") || colorClass.equals("")){ continue; }
			if (k<colorSet.size()){
				map.putMapValue(colorClass, colorSet.get(k));
			} else {
				map.putMapValue(colorClass, Color.LIGHT_GRAY);
			}
			k++;
		}
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);	
	}
	
	/// EDGE CALCULATORS ///
		
	public static Calculator createEdgeTargetArrowCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_TGTARROW_SHAPE;
		DiscreteMapping map = new DiscreteMapping(ArrowShape.class, Semantics.INTERACTION);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, ArrowShape.ARROW);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, ArrowShape.ARROW);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, ArrowShape.NONE);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, ArrowShape.NONE);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, ArrowShape.DIAMOND);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	
	public static Calculator createEdgeSourceArrowCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_SRCARROW_SHAPE;
		DiscreteMapping map = new DiscreteMapping(ArrowShape.class, Semantics.INTERACTION);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, ArrowShape.NONE);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, ArrowShape.NONE);	
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, ArrowShape.NONE);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, ArrowShape.NONE);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, ArrowShape.CIRCLE);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	
	public static Calculator createEdgeColorCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_COLOR;
		DiscreteMapping map = new DiscreteMapping(Color.class, Semantics.INTERACTION);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, Color.BLACK);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, Color.BLACK);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, Color.BLUE);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, Color.BLACK);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, Color.BLACK);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
		
	public static Calculator createEdgeOpacityCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_OPACITY;
		DiscreteMapping map = new DiscreteMapping(Integer.class, Semantics.INTERACTION);
		Integer opacity = 200;
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_REACTANT, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_MODIFIER, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_OUTPUT, opacity);
		map.putMapValue(CySBMLConstants.EDGETYPE_TRANSITION_INPUT, opacity);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
}
