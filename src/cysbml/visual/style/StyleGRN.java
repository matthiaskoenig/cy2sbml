package cysbml.visual.style;

import java.awt.Color;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineStyle;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;

import cysbml.CySBMLConstants;
import cysbml.visual.VisualStyleManager.CustomStyle;

public class StyleGRN extends StyleGeneric{
	
	public StyleGRN(CustomStyle style) {
		super(style);
	}

	@Override
	protected void createEdgeCalculators() {
		ecalcs.add(StyleDefault.createEdgeOpacityCalculator(style));
		ecalcs.add(StyleGRN.createEdgeSourceArrowCalculator(style));
		ecalcs.add(StyleGRN.createEdgeTargetArrowCalculator(style));
		ecalcs.add(StyleGRN.createEdgeLineStyleCalculator(style));
		ecalcs.add(StyleGRN.createEdgeLineWidthCalculator(style));
		ecalcs.add(StyleGRN.createEdgeColorCalculator(style));
	}

	@Override
	protected void createNodeCalculators() {
		ncalcs.add(StyleDefault.createNodeLabelCalculator(style));
		ncalcs.add(StyleGRN.createNodeShapeCalculator(style));
		ncalcs.add(StyleGRN.createNodeFillColorCalculator(style));
	}

	@Override
	protected void createVisualPropertyDependencies() {
		depMap.put(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, true);
	}

	/// NODE CALCULATORS ///
		
	public static Calculator createNodeShapeCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_SHAPE;
		DiscreteMapping map = new DiscreteMapping(NodeShape.class, CySBMLConstants.ATT_SBOTERM);
		map.putMapValue("SBO:0000405", NodeShape.VEE);       //INPUT
		map.putMapValue("SBO:0000250", NodeShape.ELLIPSE);   //GENE
		map.putMapValue("SBO:0000252", NodeShape.DIAMOND);   //TF
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	public static Calculator createNodeFillColorCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.NODE_FILL_COLOR;
		DiscreteMapping map = new DiscreteMapping(Color.class, CySBMLConstants.ATT_SBOTERM);
		map.putMapValue("SBO:0000405", Color.GRAY);     //INPUT
		map.putMapValue("SBO:0000250", Color.WHITE);     //GENE
		map.putMapValue("SBO:0000252", Color.GRAY);      //TF
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	
	/// EDGE CALCULATORS ///
	
	public static Calculator createEdgeTargetArrowCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_TGTARROW_SHAPE;
		DiscreteMapping map = new DiscreteMapping(ArrowShape.class, CySBMLConstants.ATT_SBOTERM);		
		map.putMapValue("SBO:0000459", ArrowShape.ARROW); 	//STIMULATOR
		map.putMapValue("SBO:0000020", ArrowShape.T); 		//INHIBITOR
		map.putMapValue("SBO:0000019", ArrowShape.DIAMOND); //MODIFIER
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	public static Calculator createEdgeSourceArrowCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_SRCARROW_SHAPE;
		DiscreteMapping map = new DiscreteMapping(ArrowShape.class, CySBMLConstants.ATT_SBOTERM);
		map.putMapValue("SBO:0000459", ArrowShape.NONE); 	    //STIMULATOR
		map.putMapValue("SBO:0000020", ArrowShape.CIRCLE); 		//INHIBITOR
		map.putMapValue("SBO:0000019", ArrowShape.CIRCLE); 	    //MODIFIER
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	public static Calculator createEdgeLineStyleCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_LINE_STYLE;
		DiscreteMapping map = new DiscreteMapping(LineStyle.class, CySBMLConstants.ATT_SBOTERM);
		map.putMapValue("SBO:0000459", LineStyle.SOLID); 	    //STIMULATOR
		map.putMapValue("SBO:0000020", LineStyle.DASH_DOT); 	//INHIBITOR
		map.putMapValue("SBO:0000019", LineStyle.DASH_DOT); 	//MODIFIER
		map.putMapValue(CySBMLConstants.EDGETYPE_REACTION_PRODUCT, LineStyle.SOLID);
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	public static Calculator createEdgeColorCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_COLOR;
		DiscreteMapping map = new DiscreteMapping(Color.class, CySBMLConstants.ATT_SBOTERM);
		map.putMapValue("SBO:0000459", Color.BLACK); 	//STIMULATOR
		map.putMapValue("SBO:0000020", Color.RED); 		//INHIBITOR
		map.putMapValue("SBO:0000019", Color.BLUE); 	//MODIFIER
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}
	public static Calculator createEdgeLineWidthCalculator(CustomStyle style){
		VisualPropertyType pType = VisualPropertyType.EDGE_LINE_WIDTH;
		DiscreteMapping map = new DiscreteMapping(Integer.class, CySBMLConstants.ATT_SBOTERM);
		Integer width = 5;
		map.putMapValue("SBO:0000459", width); 	    //STIMULATOR
		map.putMapValue("SBO:0000020", width); 		//INHIBITOR
		map.putMapValue("SBO:0000019", width); 	    //MODIFIER
		return new BasicCalculator(createCalculatorName(style, pType), map, pType);
	}	
}
