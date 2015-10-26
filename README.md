# cy2sbml for Cytoscape-2.8.3

cy2sbml is a Cytoscape 2 plugin for the import and work with SBML files in Cytoscape providing the visualisation of SBML network annotations within the network context.

SBML models can be imported from BioModels.net or via file or urls. One click access to the annotation resources is provided. SBML validation information about the imported files is available.

TODO: cite the following publication

Thanks and have fun 
The CySBML team.

## Features

* Java based SBML parser for Cytoscape based on JSBML ( http://sourceforge.net/projects/jsbml/ )
* access to models and annotations via BioModel ( http://www.biomodels.org/ ) and MIRIAM WebServices
  ( http://www.ebi.ac.uk/miriam/main/ )
* supports all versions of SBML
* SBML validation (SBML warnings and errors accessible)
* Standard network layout based on the species/reaction model
* Provides access to RDF based annotation information within
  the network context
* Navigation menu based on the SBML structure linked to layout 
  and annotation information
* succesfully tested with all SBML.org and Biomodels.org test
  cases (sbml-test-cases-2.0.2, BioModels_Database-r21-sbml_files)


## Installation
[1] install Cytoscape v2.8.3
    http://www.cytoscape.org/download.html

[2] download latest release 
    http://sourceforge.net/projects/cysbml/

[3] move the downloaded 
         cySBML-vx.xx.jar' 
    in the Cytoscape plugin folder under 
    'Cytoscape_v2.8.*/plugins/'.
    In Windows this folder is normally located under 
    C:/Program Files/Cytoscape_v2.8.*/plugins.

[4] remove 'sbml-reader-2.8.*-jar-with-dependencies.jar' from 
    the plugin folder.

cy2sbml is installed and available in Cytoscape under 
plugins after the next startup of Cytoscape.


## Uninstall
[1] remove the cySBML-vx.xx.jar from the plugin folder.


*** Build instructions ***
Clone
	git clone git://git.code.sf.net/p/cysbml/code cysbml-code
	cd cysbml-code
Build in ./build
	ant cysbml

## Changelog
### v1.30 [2014-01-24]
- java 1.6 compatibility (bug fix)


### v1.29 [2013-11-05]
- Source code with ANT build files from sourceforge available
- Implemented: Gene Regulatory Network support (GRN2SBML with ExTILAR & NetGenerator)
- Speed Improvements: Caching of Miriram resource loading
- Speed Improvements: Deactivation of CySBML results in hybernating of all lookups
- Fixed: offline not starting problems -> offline mode implemented
- Fixed: working offline mode without Miriram connection
- Implemented: Improved display of loading networks with layout
- Fixed: bug with fast selection of nodes (wrong information was displayed, due to 
		 different Thread runtime
- Implemented: reading of model attributes in networkAttributes

### v1.27 [2013-10-29]
- Update of libraries: biomodels-1.21; MiriamJavaLib-1.1.5; 
			jsbml-1.0-a1; jsbml-layout-1.0-b1; jsbml-qual-2.1-b1.jar 
- Fixed: direction of modifier-reaction edges & naming of the edge interaction (also in qual)
- Fixed: additional information from the SpeciesGlyphs & ReactionGlyphs is parsed in layout, 
		 namely SBOId, MetaId, Name
- Fixed: layout:role is now handled correctly to set edge interaction type for layout (multiple
		edges in layout with different layout:role correctly handled).

### v1.25
- Layout bug fixes (width & height)
- Parsing of kinetic information to node attributes

### v1.23
- full compatibility with CyFluxViz
- smaller bug fixes

### v1.2
- Dialoques, Tutorial, Usage examples
- Reversibility Bug fixed

### v1.1
- BioModel Search Interface renewed
- BioModel Search via arbitrary Text with IDs
- Compability with NetMatch, NetworkAnalyzer and FluxViz


