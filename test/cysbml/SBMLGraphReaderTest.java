package cysbml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;


/** Test the core functionality. 
 *  TODO: implement edge and edge attribute tests
 *  	  implement tests for directionality test of edges.
 */
public class SBMLGraphReaderTest {
	private final static String FOLDER = "./examples/sbmlreader/";
	CyNetwork network;
	
	@Before
    public void setUp() {
		String fname = FOLDER + "minimal_1.xml";
		SBMLGraphReader reader = new SBMLGraphReader(fname);
		network = Cytoscape.createNetwork(reader, false, null);
    }
    @After
    public void tearDown() {
        network = null;
    }
	
	@Test
	public void networkTest() throws IOException {
		assertNotNull(network);
		assertEquals(3, network.getNodeCount());
		assertEquals(2, network.getEdgeCount());
	}
	
	@Test
	public void nodeTest() throws IOException {
		CyNode s1 = Cytoscape.getCyNode("s1");
		CyNode s2 = Cytoscape.getCyNode("s2");
		CyNode r1 = Cytoscape.getCyNode("r1");
		assertNotNull(s1);
		assertNotNull(s2);
		assertNotNull(r1);	
	}

	@Test
	// TODO: get all the edges and test the interactions
	public void edgeTest() throws IOException {
		assertEquals(2, network.getEdgeCount());
	}

	@Test
	public void speciesAttributeTest() throws IOException {
		CyAttributes nAtt = Cytoscape.getNodeAttributes();
		assertEquals("s1", (String) nAtt.getAttribute("s1", CySBMLConstants.ATT_ID));
		assertEquals("s1", (String) nAtt.getAttribute("s1", CySBMLConstants.ATT_NAME));
		assertEquals("c1", (String) nAtt.getAttribute("s1", CySBMLConstants.ATT_COMPARTMENT));
		assertEquals(false, (Boolean) nAtt.getAttribute("s1", CySBMLConstants.ATT_BOUNDARY_CONDITION));
		assertEquals(false, (Boolean) nAtt.getAttribute("s1", CySBMLConstants.ATT_CONSTANT));
		assertEquals(false, (Boolean) nAtt.getAttribute("s1", CySBMLConstants.ATT_HAS_ONLY_SUBSTANCE_UNITS));
		
		assertEquals("s2", (String) nAtt.getAttribute("s2", CySBMLConstants.ATT_ID));
		assertEquals("s2", (String) nAtt.getAttribute("s2", CySBMLConstants.ATT_NAME));
		assertEquals("c1", (String) nAtt.getAttribute("s2", CySBMLConstants.ATT_COMPARTMENT));
		assertEquals(false, (Boolean) nAtt.getAttribute("s2", CySBMLConstants.ATT_BOUNDARY_CONDITION));
		assertEquals(false, (Boolean) nAtt.getAttribute("s2", CySBMLConstants.ATT_CONSTANT));
		assertEquals(false, (Boolean) nAtt.getAttribute("s2", CySBMLConstants.ATT_HAS_ONLY_SUBSTANCE_UNITS));	
	}

}
