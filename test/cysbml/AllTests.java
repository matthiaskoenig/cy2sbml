package cysbml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ cysbml.biomodels.BioModelInterfaceTest.class,
				cysbml.grn.GeneRegulatoryNetworkTest.class,
				cysbml.logging.LogCyPluginTest.class,
				cysbml.SBMLGraphReaderTest.class})
public class AllTests {

	/* Here generell settings for all the tests. */
	public static final String PHOST = "proxy.charite.de";
	public static final String PPORT = "888";
	
}