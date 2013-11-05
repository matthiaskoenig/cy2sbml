package cysbml.mapping;

import java.util.List;

import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class NamedSBaseToNodeMapping extends OneToManyMapping{	
	
	/* One to one Mapping between network and tree, so
	 * network can be used to create the mapping. 
	 */
	public NamedSBaseToNodeMapping(CyNetwork network){
		super();
		@SuppressWarnings("unchecked")
		List<CyNode> nodes = network.nodesList();
		for (CyNode node: nodes){
			String value = node.getIdentifier();
			String key = value;
			put(key, value);
		}
	}
	
	/* In the Layout case the LayoutNodes are mapped to species and reaction nodes.
	 * Mapping is one to many !, meaning that multiple 
	 */
	public NamedSBaseToNodeMapping(Layout layout){
		super();	
		for (ReactionGlyph glyph: layout.getListOfReactionGlyphs()){
			String value = glyph.getId();
			if (glyph.isSetReaction()){
				String key = glyph.getReaction();
				put(key, value);
			}
		}
		for (SpeciesGlyph glyph: layout.getListOfSpeciesGlyphs()){
			String value = glyph.getId();
			if (glyph.isSetSpecies()){
				String key = glyph.getSpecies();
				put(key, value);
			}
		}
	}
}
