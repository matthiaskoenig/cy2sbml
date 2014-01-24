package cysbml.miriam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import org.sbml.jsbml.AbstractNamedSBase;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.qual.QualitativeSpecies;
import org.sbml.jsbml.ext.qual.Transition;

import cysbml.CySBML;
import cysbml.biomodel.BioModelGUIText;
import uk.ac.ebi.miriam.lib.MiriamLink;

/** Gets information from the MIRIAM webresources.
 * This is the critical event.
 * Loaded objects should be cashed. 
 * Background Thread which tries to fully cash all the objects.
 * 
 * TODO: hash the resources already read so future node information
 * speeds up extremely. No additional lookup of resources needed. 
 *
 * FIXME: handle the cases when the resources are not found, for instance
 * due to wrong encoding in the SBML.
 * 
 * FIXME: offline mode & better overview over the information in the SBML
 *  Holds the available link information for already read resources 
 * 
 * TODO: Cache information
 */
 
/** Creating information for the SBML objects which were selected. 
 * TODO: bug if nodes are selected with no correspondence in the SBML.
 * 
 * TODO: get the additional information from KEGG, ...
 * The SBML information has to be changed to empty. 
 * 
 */
public class NamedSBaseInfoFactory {
	
	AbstractNamedSBase sbmlObject;
	String info = ""; 
	MiriamLink link;
	
	public NamedSBaseInfoFactory(Object obj){
		if (    obj.getClass().equals(Compartment.class)  || 
	  			obj.getClass().equals(Reaction.class) || 
	  			obj.getClass().equals(Species.class) ||
	  			obj.getClass().equals(QualitativeSpecies.class) ||
	  			obj.getClass().equals(Transition.class) ){
	  	sbmlObject = (AbstractNamedSBase) obj;
		}
		
		// Creation of the link to the Web Services
		link = new MiriamLink();
		link.setAddress(MiriamWSInterface.MIRIAM_URL);
	}
	
	public String getInfo() {
		return info;
	}
	
	public void cacheMiriamInformation(){
		for (CVTerm term : sbmlObject.getCVTerms()){
			for (String rURI : term.getResources()){
				MiriamResourceInfo.getLocationsFromMiriamResource(link, rURI);
			}
		}
	}
	
	/** Get information for the given Object */
	public void createInfo() {
		if (sbmlObject == null){
			return;
		}
		
		// SBML information
		info = createHeader(sbmlObject);
		info += createSBOInfo(sbmlObject);
  		// Add type specific additional SBML information
  		// TODO: read the const, boundary condition, ... kinetic law, assignment, ...
  		
  		// CVterm annotations (MIRIAM action)
  		info += getCVTermsString(sbmlObject.getCVTerms());
  		
  		// notes and annotations if available
  		// !!! have to be set at the end due to the html content which
  		// breaks the rest of the html.
  		String notes = sbmlObject.getNotesString();
  		if (!notes.equals("") && notes != null ){
  			info += String.format("<p>%s</p>", notes);
  		}
  		// TODO: read everything available according to standard
	   }
	
	
	private String createHeader(AbstractNamedSBase item){
		return String.format("<h2><span color=\"gray\">%s</span> : %s (%s)</h2>",
								getUnqualifiedClassName(item), item.getId(), item.getName());
	}
	
	/** Returns the unqualified class name of a given object. */
	private static String getUnqualifiedClassName(Object obj){
		String name = obj.getClass().getName();
		if (name.lastIndexOf('.') > 0) {
		    name = name.substring(name.lastIndexOf('.')+1);
		}
		// The $ can be converted to a .
		name = name.replace('$', '.');  
		return name;
	}
	
	
	/** Returns the SBOTermId if available. */
	private String createSBOInfo(AbstractNamedSBase item){
		String info = "";
		if (item.isSetSBOTerm()){
  			info = getSBOTermString(item.getSBOTermID());
  		}
		return info;
	}
	
	/** Public get SBOTerm string */
	private String getSBOTermString(String sboTerm){
		String text = "<p>";
		CVTerm term = new CVTerm(CVTerm.Qualifier.BQB_IS, "urn:miriam:biomodels.sbo:" + sboTerm);
		text += String.format("<b><span color=\"green\">%s</span></b><br>", sboTerm);
		for (String rURI : term.getResources()){
			text += MiriamResourceInfo.getInfoFromMiriamResource(link, rURI);
		}
		text += "</p>";
		return text;
	}
	
	
	/** Get a String HTML representation of the CVTerm information */
	private String getCVTermsString(List<CVTerm> cvterms){
		String text = "<hr>";
		if (cvterms.size() > 0){
			for (CVTerm term : cvterms){
				text += String.format("<p><b>%s : %s</b><br>", term.getQualifierType(), term.getBiologicalQualifierType());
				Map<String, String> map = null;
				for (String rURI : term.getResources()){
					map = getKeyAndId(rURI);
					text += String.format("<span color=\"red\">%s</span> (%s)<br>", map.get("id"), map.get("key"));
					text += MiriamResourceInfo.getInfoFromMiriamResource(link, rURI);
				}
				text += "</p>";
			}
			text += "<hr>";
		}
  		return text;
	}
	
	/** Get additional image information for the database and identifier.
	 * TODO: This has to be done offline and in the background (images have to be cashed) !
	 * TODO: Create background database of information.  
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private String getAdditionalInformation(String r){
		Map<String, String> map = getKeyAndId(r);
		String text = "";
		String id = map.get("id");
		String key = map.get("key");
		String[] keyitems = key.split(":");
		String item = keyitems[keyitems.length-1];
		
		// Add chebi info
		if (item.equals("obo.chebi")){
			try{
				String[] tmps = id.split("%3A");
				String cid = tmps[tmps.length-1];
				text += "<img src=\"http://www.ebi.ac.uk/chebi/displayImage.do?defaultImage=true&imageIndex=0&chebiId="+cid+"&dimensions=160\"></img>";
			} catch (Exception e){
				//e.printStackTrace();
				CySBML.LOGGER.warning("obo.chebi image not available");
			}
		
		// Add kegg info
		}else if (item.equals("kegg.compound")){
			try{
				String imgsrc = 
					BioModelGUIText.class.getClassLoader().getResource("http://www.genome.jp/Fig/compound/"+id+".gif").toString();
				text += "<img src=\""+imgsrc+"\"></img>";
			} catch (Exception e){
				//e.printStackTrace();
				CySBML.LOGGER.warning("kegg.compound image not available");
			}
		
		// TODO resize image and use KEGG
		/* Uncomment for reactions, but problems with large images 
		}else if (item.equals("kegg.reaction")){
			try{
				String imgsrc = 
					BioModelText.class.getClassLoader().getResource("http://www.genome.jp/Fig/reaction/"+id+".gif").toString();
				text += "<img src=\""+imgsrc+"\"></img>";
			} catch (Exception e){
				//e.printStackTrace();
				System.out.println("CySBML -> kegg.reaction image not available");
			}
		}*/
			
			
		}
		return text;
	}
	
	@Deprecated
	private Map<String, String> getKeyAndId(final String rURI) {
		Map<String, String> map = new HashMap<String, String>();
		// split into key and identifier
		String[] items = rURI.split(":");
		String[] keyitems = new String[items.length - 1];
		for (int i = 0; i < keyitems.length; ++i) {
			keyitems[i] = items[i];
		}
		map.put("id", items[items.length - 1]);
		map.put("key", StringUtils.join(keyitems, ":"));
		return map;
	}
	
}
