# cy2sbml: SBML for Cytoscape 2

**cy2sbml** is a [Cytoscape 2](http://www.cytoscape.org) plugin for the Systems Biology Markup Language [SBML](http://www.sbml.org).  
For Cytoscape 3 use [cy3sbml](https://github.com/matthiaskoenig/cy3sbml).

<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=RYHNRJFBMWD5N" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>

**cy2sbml** provides advanced functionality for the import and work with models encoded in SBML, amongst others the 
visualization of SBML network annotations within the network context, direct import of models from repositories like [biomodels](http://www.biomodels.org) and one-click access to annotation resources and SBML model information and SBML validation.

**Status** : released  
**Documentation** :  http://matthiaskoenig.github.io/cy2sbml  
**Support & Forum** : https://groups.google.com/forum/#!forum/cysbml-cyfluxviz  
**Bug Tracker** : https://github.com/matthiaskoenig/cy2sbml/issues  

## Features
* Java based SBML parser for Cytoscape based on [JSBML](http://sourceforge.net/projects/jsbml/)
* access to models and annotations via [BioModel](http://www.biomodels.org/) and [MIRIAM WebServices](http://www.ebi.ac.uk/miriam/main/)
* supports all versions of SBML
* support of SBML Layout Package
* support of SBML Qualitative Models Package
* SBML validation (SBML warnings and errors accessible)
* Standard network layout based on the species/reaction model
* Provides access to RDF based annotation information within
  the network context
* Navigation menu based on the SBML structure linked to layout 
  and annotation information
* succesfully tested with all SBML.org and Biomodels.org testcases (sbml-test-cases-2.0.2, BioModels_Database-r21-sbml_files)

The [cy2sbml tutorial](./doc/tutorial/CySBML-v1.2-tutorial.pdf)  covers the following topics
* Installation
* CySBML interface
* Import of SBML models
* Access to annotation information
* SBML validation in CySBML
* Programmatic Interaction with CySBML

## License
* Source Code: [GPLv3](http://opensource.org/licenses/GPL-3.0)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)

## Citation
Matthias König, Andreas Dräger and Hermann-Georg Holzhütter  
*CySBML: a Cytoscape plugin for SBML*  
Bioinformatics. 2012 Jul 5. [PubMed](http://www.ncbi.nlm.nih.gov/pubmed/22772946) 

## Install
* install Cytoscape v2.8.3  
    http://www.cytoscape.org/download.html  
    http://chianti.ucsd.edu/Cyto-2_8_3/
* download latest release jar  
    https://github.com/matthiaskoenig/cy2sbml/releases
* move downloaded `cy2sbml-vx.x.x.jar` in the Cytoscape plugin folder under `Cytoscape_v2.8.3/plugins/`.  
In Windows this folder is located in `C:/Program Files/Cytoscape_v2.8.3/plugins.`
* remove `sbml-reader-2.8.3-jar-with-dependencies.jar` from the plugin folder.

cy2sbml is installed and available in Cytoscape under plugins after the next startup of Cytoscape.

## Uninstall
* remove the `cy2sbml-vx.xx.jar` from the plugin folder.

## Build instructions
Clone and build from source
```
git clone https://github.com/matthiaskoenig/cy2sbml.git cy2sbml
cd cy2sbml
ant cy2sbml
```

## Changelog
**v1.30** [2014-01-24]
* java 1.6 compatibility (bug fix)

**v1.29** [2013-11-05]  
* Source code with ANT build files from sourceforge available
* Implemented: Gene Regulatory Network support (GRN2SBML with ExTILAR & NetGenerator)
* Speed Improvements: Caching of Miriram resource loading
* Speed Improvements: Deactivation of CySBML results in hybernating of all lookups
* Fixed: offline not starting problems -> offline mode implemented
* Fixed: working offline mode without Miriram connection
* Implemented: Improved display of loading networks with layout
* Fixed: bug with fast selection of nodes (wrong information was displayed, due to 
		 different Thread runtime
* Implemented: reading of model attributes in networkAttributes

**v1.27** [2013-10-29]  
* Update of libraries: biomodels-1.21; MiriamJavaLib-1.1.5; 
			jsbml-1.0-a1; jsbml-layout-1.0-b1; jsbml-qual-2.1-b1.jar 
* Fixed: direction of modifier-reaction edges & naming of the edge interaction (also in qual)
* Fixed: additional information from the SpeciesGlyphs & ReactionGlyphs is parsed in layout, namely SBOId, MetaId, Name
* Fixed: layout:role is now handled correctly to set edge interaction type for layout (multiple
		edges in layout with different layout:role correctly handled).

**v1.25**
* Layout bug fixes (width & height)
* Parsing of kinetic information to node attributes

**v1.23**
* full compatibility with CyFluxViz
* smaller bug fixes

**v1.2**
* Dialoques, Tutorial, Usage examples
* Reversibility Bug fixed

**v1.1**
* BioModel Search Interface renewed
* BioModel Search via arbitrary Text with IDs
* Compability with NetMatch, NetworkAnalyzer and FluxViz
