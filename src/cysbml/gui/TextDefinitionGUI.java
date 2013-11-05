package cysbml.gui;

public class TextDefinitionGUI {
	public static String getHeaderString(){
		String imgsrc_sbml = 
				TextDefinitionGUI.class.getClassLoader().getResource("http://sbml.org/images/b/b1/Official-sbml-supported-32.jpg").toString();
		String info = "<a href=\"http://sbml.org\"><img src=\""+imgsrc_sbml+"\"alt=\"SBML Logo\" border=0></img></a>";
		return info;
	}
	
	public static String webserviceSearch(){
		String info = "<p>Searching BioModels ...</ br>" +
				"... WebService request can take a few seconds.</p>";
		return info;
	}
	public static String webserviceSBMLRequest(){
		String info = "<p>Getting SBML information via Webservice ... </br>" +
				"... this can take a few seconds.</p>";
		return info;
	}
}
