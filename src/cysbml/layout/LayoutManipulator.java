package cysbml.layout;

import java.util.LinkedList;
import java.util.List;

import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.ReactionGlyph;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.ext.layout.SpeciesReferenceGlyph;
import org.sbml.jsbml.ext.qual.Input;
import org.sbml.jsbml.ext.qual.Output;
import org.sbml.jsbml.ext.qual.QualConstant;
import org.sbml.jsbml.ext.qual.QualitativeModel;
import org.sbml.jsbml.ext.qual.Transition;

import cysbml.mapping.OneToManyMapping;

/**
 * Models for manipulating SBML Layouts.
 * For example generate all edges for a given layout.
 * @author Matthias Koenig
 *
 */
public class LayoutManipulator{
	public static final String ALL = "all"; 
	private static int speciesIdCounter;
	private static int reactionIdCounter;
	
	private Layout layout;
	private Model model;
	private QualitativeModel qModel = null;
	private OneToManyMapping speciesToSpeciesGlyphMapping = new OneToManyMapping();
	
	// TODO: fix side effects
	// ReactionGlyphs have to be generated (additional side effects necessary), 
	// but are never accessed
	@SuppressWarnings("unused")
	private OneToManyMapping reactionToReactionGlyphMapping = new OneToManyMapping();
	
	public LayoutManipulator(Model sbmlModel, Layout sbmlLayout){
		model = sbmlModel;
		layout = sbmlLayout;
		if (hasModel()){
			qModel = (QualitativeModel) model.getExtension(QualConstant.namespaceURI);
		}
		createMissingGlyphIds();
		createGlyphMappings();
	}
	
	public Layout getLayout(){
		return layout;
	}
	
	public boolean hasModel(){
		return (model != null);
	}
	public boolean hasQualitativeModel(){
		return (qModel != null);
	}
	
	///		GENERATE MISSING IDS 	///
	public void createMissingGlyphIds(){
		speciesIdCounter = 0;
		reactionIdCounter = 0;
		createMissingSpeciesGlyphIds();
		createMissingReactionGlyphIds();
	}
	
	private void createMissingSpeciesGlyphIds(){
		if (layout.isSetListOfSpeciesGlyphs()){
			for (SpeciesGlyph glyph : layout.getListOfSpeciesGlyphs()){
				if (!glyph.isSetId()){
					String id = createSpeciesGlyphId(glyph);
					glyph.setId(id);
				}
			}
		}
	}
	
	private void createMissingReactionGlyphIds(){
		if (layout.isSetListOfReactionGlyphs()){
			for (ReactionGlyph glyph : layout.getListOfReactionGlyphs()){
				if (!glyph.isSetId()){
					String id = createReactionGlyphId(glyph);
					glyph.setId(id);
				}
			}
		}
	}
	
	public String createSpeciesGlyphId(SpeciesGlyph glyph){
		speciesIdCounter ++;
		String id = String.format("speciesGlyph_%s", speciesIdCounter);
		return id;
	}
	
	public String createReactionGlyphId(ReactionGlyph glyph){
		reactionIdCounter ++;
		String id = String.format("reactionGlyph_%s", reactionIdCounter);
		return id;
	}
	
	///   MAPPINGS    ///
	public void createGlyphMappings(){
		if (layout.isSetListOfReactionGlyphs()){
			reactionToReactionGlyphMapping = createReactionGlyphMapping(layout.getListOfReactionGlyphs());
		} else {
			reactionToReactionGlyphMapping = new OneToManyMapping();
		}
		
		if (layout.isSetListOfSpeciesGlyphs()){
			speciesToSpeciesGlyphMapping = createSpeciesGlyphMapping(layout.getListOfSpeciesGlyphs());
		} else {
			speciesToSpeciesGlyphMapping = new OneToManyMapping();
		}
	}
	
	public static OneToManyMapping createReactionGlyphMapping(ListOf<ReactionGlyph> listOfGlyphs){
		OneToManyMapping map = new OneToManyMapping();
		for (ReactionGlyph glyph : listOfGlyphs){
			if (glyph.isSetReaction()){
				map.put(glyph.getReaction(), glyph.getId());
			}
		}
		return map;
	}
	
	public static OneToManyMapping createSpeciesGlyphMapping(ListOf<SpeciesGlyph> listOfGlyphs){
		OneToManyMapping map = new OneToManyMapping();
		for (SpeciesGlyph glyph : listOfGlyphs){
			if (glyph.isSetSpecies()){
				map.put(glyph.getSpecies(), glyph.getId());
			}
		}
		return map;
	}
	
	/// GENERATE EDGES ///	
	public static boolean hasEdgeInformation(Layout layout){
		boolean hasInfo = false;
		if (layout.isSetListOfReactionGlyphs()){
			for (ReactionGlyph glyph: layout.getListOfReactionGlyphs()){
				if (hasEdgeInformation(glyph)){
					hasInfo = true;
					break;
				}
			}
		}
		return hasInfo;
	}
	public static boolean hasEdgeInformation(ReactionGlyph glyph){
		boolean result = true;
		ListOf<SpeciesReferenceGlyph> speciesReferenceGlyphs = glyph.getListOfSpeciesReferenceGlyphs();
		if (speciesReferenceGlyphs == null){
			result = false;
		} else if (speciesReferenceGlyphs.size() == 0){
			result = false;
		}
		return result;
	}
	
	public void generateAllEdges(){
		if (layout.isSetListOfReactionGlyphs()){
			for (ReactionGlyph rGlyph : layout.getListOfReactionGlyphs()){
				if (!hasEdgeInformation(rGlyph)){
					generateEdgesForReactionGlyph(rGlyph);
				}
			}
		}
	}
	
	public void generateEdgesForReactionGlyph(ReactionGlyph rGlyph){
		Reaction reaction = getReactionForReactionGlyph(rGlyph);		
		if (reaction != null){
			List<String[]> connectedSpecies = getConnectedSpecies(reaction);
			for (String[] data: connectedSpecies){
				String speciesId = data[0];
				String role = data[1];
				generateAllEdges(rGlyph, speciesId, role);
			}
		}
		Transition transition = getTransitionForReactionGlyph(rGlyph);
		if (transition != null){
			List<String[]> connectedQSpecies = getConnectedQualitativeSpecies(transition);
			for (String[] data : connectedQSpecies){
				String qSpeciesId = data[0];
				String role = data[1];
				generateAllEdges(rGlyph, qSpeciesId, role);
			}
		}
	}
	
	private void generateAllEdges(ReactionGlyph rGlyph, String speciesId, String role){
		ListOf<SpeciesReferenceGlyph> speciesReferenceGlyphList = rGlyph.getListOfSpeciesReferenceGlyphs();
		if (speciesToSpeciesGlyphMapping.containsKey(speciesId)){
			List<String> sGlyphIds = speciesToSpeciesGlyphMapping.getValues(speciesId);
			for (String sGlyphId : sGlyphIds){
				SpeciesReferenceGlyph speciesReferenceGlyph = new SpeciesReferenceGlyph(sGlyphId);
				speciesReferenceGlyph.setSpeciesGlyph(sGlyphId);
				speciesReferenceGlyph.setName(role);
				speciesReferenceGlyphList.add(speciesReferenceGlyph);
			}
		}
	}
			
	private List<String[]> getConnectedSpecies(Reaction reaction){
		List<String[]> connectedSpecies = new LinkedList<String[]>();
		if (reaction.isSetListOfReactants()){
			for (SpeciesReference speciesReference : reaction.getListOfReactants()){
				String[] data = new String[2];
				data[0] = speciesReference.getSpeciesInstance().getId();
				//data[1] = CySBMLGraphReader.EDGETYPE_REACTION_REACTANT;
				connectedSpecies.add(data);
			}
		}
		if (reaction.isSetListOfProducts()){
			for (SpeciesReference speciesReference : reaction.getListOfProducts()){
				String[] data = new String[2];
				data[0] = speciesReference.getSpeciesInstance().getId();
				//data[1] = CySBMLGraphReader.EDGETYPE_REACTION_PRODUCT;
				connectedSpecies.add(data);
			}
		}
		if (reaction.isSetListOfModifiers()){
			for (ModifierSpeciesReference speciesReference : reaction.getListOfModifiers()){
				String[] data = new String[2];
				data[0] = speciesReference.getSpeciesInstance().getId();
				//data[1] = CySBMLGraphReader.EDGETYPE_REACTION_MODIFIER;
				connectedSpecies.add(data);
			}
		}
		return connectedSpecies;
	}
	
	private List<String[]> getConnectedQualitativeSpecies(Transition transition){
		List<String[]> connectedQSpecies = new LinkedList<String[]>();
		if (transition.isSetListOfInputs()){
			for (Input input : transition.getListOfInputs()){
				String[] data = new String[2];
				data[0] = input.getQualitativeSpecies();
				//data[1] = CySBMLGraphReader.EDGETYPE_TRANSITION_INPUT;
				connectedQSpecies.add(data);
			}
		}
		if (transition.isSetListOfOutputs()){
			for (Output output : transition.getListOfOutputs()){
				String[] data = new String[2];
				data[0] = output.getQualitativeSpecies();
				//data[1] = CySBMLGraphReader.EDGETYPE_TRANSITION_INPUT;
				connectedQSpecies.add(data);
			}
		}
		return connectedQSpecies;
	}
	
	public Transition getTransitionForReactionGlyph(ReactionGlyph rGlyph) {
		if (qModel == null || !rGlyph.isSetReaction()){
			return null;
		}
		String reactionId = rGlyph.getReaction();
		Transition transition = null;
		if (qModel.isSetListOfTransitions()) {
			for (Transition t : qModel.getListOfTransitions()) {
				if (t.getId().equals(reactionId)) {
					transition = t; 
					break;
				}
			}
		}
		return transition;
	}
	
	public Reaction getReactionForReactionGlyph(ReactionGlyph rGlyph) {
		if (!rGlyph.isSetReaction()){
			return null;
		}
		String reactionId = rGlyph.getReaction();
		Reaction reaction = null;
		if (model.isSetListOfReactions()) {
			for (Reaction r : model.getListOfReactions()) {
				if (r.getId().equals(reactionId)) {
					reaction = r; 
					break;
				}
			}
		}
		return reaction;
	}
	
}
