package cysbml.biomodel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import uk.ac.ebi.biomodels.ws.SimpleModel;

public class SearchBioModel {
	private SearchContent searchContent;
	private List<String> modelIds;
	private LinkedHashMap<String, SimpleModel> simpleModels;
	
	private BioModelWSInterface bmInterface;
	
	public SearchBioModel(String proxyHost, String proxyPort){
		bmInterface = new BioModelWSInterface(proxyHost, proxyPort);
		resetSearch();
	}
	
	private void resetSearch(){
		searchContent = null;
		modelIds = new LinkedList<String>();
		simpleModels = new LinkedHashMap<String, SimpleModel>();
	}
		
	public List<String> getModelIds() {
		return modelIds;
	}

	public String getModelId(int index){
		return modelIds.get(index);
	}
	
	public LinkedHashMap<String, SimpleModel> getSimpleModels(){
		return simpleModels;
	}
	public SimpleModel getSimpleModel(int index){
		return simpleModels.get(index);
	}
	
	public int getSize(){
		return modelIds.size();
	}
	
	public void searchBioModels(SearchContent sContent){
		resetSearch();
		searchContent = sContent;
		modelIds = searchModelIdsForSearchContent(searchContent);
		simpleModels = getSimpleModelsForSearchResult(modelIds);
	}
	
	public void getBioModelsByParsedIds(Set<String> parsedIds){
		resetSearch();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(SearchContent.CONTENT_MODE, SearchContent.PARSED_IDS);
		searchContent = new SearchContent(map);
		modelIds = new LinkedList<String>(parsedIds);
		simpleModels = getSimpleModelsForSearchResult(modelIds);
	}
	
	private LinkedHashMap<String, SimpleModel> getSimpleModelsForSearchResult(List<String> idsList){
		String[] ids = (String[]) idsList.toArray();
		return bmInterface.getSimpleModelsByIds(ids);
	}
	
	private List<String> searchModelIdsForSearchContent(SearchContent content){
				
		SearchBioModelTask task = new SearchBioModelTask(content, bmInterface);
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		TaskManager.executeTask(task, jTaskConfig);
		return task.getIds();
	}
	
	public static void addIdsToResultIds(final List<String> ids, List<String> resultIds, final String mode){
		// OR -> combine all results
		if (mode.equals(SearchContent.CONNECT_OR)){
			resultIds.addAll(ids);
		}
		// AND -> only the combination results of all search terms	
		if (mode.equals(SearchContent.CONNECT_AND)){
			if (resultIds.size() > 0){
				resultIds.retainAll(ids);
			} else {
				resultIds.addAll(ids);
			}
		}
	}
	
	public String getHTMLInformation(final List<String> selectedModelIds){
		String info = getHTMLHeaderForModelSearch();
		info += BioModelWSInterfaceTools.getHTMLInformationForSimpleModels(simpleModels, selectedModelIds);
		return BioModelGUIText.getString(info);
	}
	
	private String getHTMLHeaderForModelSearch(){
		String info = String.format(
				"<h2>%d BioModels found for </h2>" +
				"<hr>", getSize());
		info += searchContent.toHTML();
		info += "<hr>";
		return info;
	}
		
	public String getHTMLInformationForModel(int modelIndex){
		SimpleModel simpleModel = getSimpleModel(modelIndex);
		return BioModelWSInterfaceTools.getHTMLInformationForSimpleModel(simpleModel);
	}
}
