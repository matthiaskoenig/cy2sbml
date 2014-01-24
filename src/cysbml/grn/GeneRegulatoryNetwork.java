package cysbml.grn;

import java.util.HashSet;
import java.util.Set;


import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;

/** Main class to handle the GeneRegulatoryNetworks (Vlaic et al). */
public class GeneRegulatoryNetwork {

	public static Set<String> speciesSBOforGRN;
	public static Set<String> reactionSBOforGRN;
	public static Set<String> productSBOforGRN;
	public static Set<String> modifierSBOforGRN;
	
	static {
		speciesSBOforGRN = new HashSet<String>();
		speciesSBOforGRN.add("SBO:0000250"); // GENE (ribonucleic acid)
		speciesSBOforGRN.add("SBO:0000252"); // TF (polypeptide chain)
		speciesSBOforGRN.add("SBO:0000291"); // EMPTY SET (empty set)
		speciesSBOforGRN.add("SBO:0000405"); // INPUT (perturbing agent)
		
		reactionSBOforGRN = new HashSet<String>();
		reactionSBOforGRN.add("SBO:0000589"); // EXPRESSION OF G (genetic production)
		reactionSBOforGRN.add("SBO:0000374"); // REGULATION OF TF (relationship)
		
		productSBOforGRN = new HashSet<String>();
		productSBOforGRN.add("SBO:0000011"); // product
		
		modifierSBOforGRN = new HashSet<String>();
		modifierSBOforGRN.add("SBO:0000020"); // inhibitor
		modifierSBOforGRN.add("SBO:0000459"); // stimulator
		modifierSBOforGRN.add("SBO:0000019"); // modifier
	}
	
	public static boolean isSBMLDocumentGRN(SBMLDocument doc){
		if (doc.isSetModel()){
			return isModelGRN(doc.getModel());
		}
		return false;
	}
	
	/** This is the main test for the model in the SBML. 
	 * Depending on the answer all the GRN functionality is available or not. 
	 *
	 * It checks the species SBOterms and the products and modifier SBOterms for the 
	 * reactions. 
	 */
	private static boolean isModelGRN(Model model){
		// Check for model id
		if (!model.isSetId()){ return false; }
		
		// Check all species
		if (!model.isSetListOfSpecies()){ return false; }
		for (Species s: model.getListOfSpecies()){
			if (!isSpeciesGRN(s)){ return false; }
		}			
		
		// Check all reactions
		if (!model.isSetListOfReactions()){ return false; }
		for (Reaction r: model.getListOfReactions()){
			if (!isReactionGRN(r, model)){ return false; }
		}
		
		// All tests successful
		return true;
	}
	
	/** Test if all species SBO terms are set and in the 
	 * allowed values for GRNs. */
	private static boolean isSpeciesGRN(Species species){
		if (!species.isSetSBOTerm()){ 
			return false; 
		}
		String sbo = species.getSBOTermID();
		return speciesSBOforGRN.contains(sbo);
	}
	
	/** Test if all reaction SBO terms are set and in the 
	 * allowed values for GRNs. 
	 * Than tests if the products and modfiers have SBO terms and 
	 * are in the allowed values for GRNs.
	 */
	private static boolean isReactionGRN(Reaction reaction, Model model){
		if (!reaction.isSetSBOTerm()){ 
			return false; 
		}
		// test reaction SBO
		String sbo = reaction.getSBOTermID();
		if (!reactionSBOforGRN.contains(sbo)){
			return false;
		}
		// test product SBO
		for (SpeciesReference sref: reaction.getListOfProducts()){
			if (!sref.isSetSBOTerm()){
				return false;
			}
			sbo = sref.getSBOTermID();
			if (!productSBOforGRN.contains(sbo)){
				return false;
			}
		}
		// test modifier SBO
		if (!reaction.isSetListOfModifiers()){ return false; }
		for (ModifierSpeciesReference sref: reaction.getListOfModifiers()){
			if (!sref.isSetSBOTerm()){
				return false;
			}
			sbo = sref.getSBOTermID();
			if (!modifierSBOforGRN.contains(sbo)){
				return false;
			}
		}
		return true;
	}
	
}
