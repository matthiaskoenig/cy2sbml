package cysbml.logging;

import static org.junit.Assert.*;
import org.junit.*;

public class LogCyPluginTest {

	private LogCyPlugin logger;
    
	@Before
    public void setUp() {
        logger = new LogCyPlugin("CyTest");
    }
    @After
    public void tearDown() {
        logger = null;
    }

	    
	@Test
	public void testInfo() {
		assertEquals("CyTest[INFO]: ", logger.info("", false));
		assertEquals("CyTest[INFO]: test message", logger.info("test message", false));
	}
	
	@Test 
	public void testError(){
		assertEquals("CyTest[ERROR]: ", logger.error("", false));
		assertEquals("CyTest[ERROR]: test message", logger.error("test message", false));
		
	}
	
	@Test 
	public void testWarning(){
		assertEquals("CyTest[WARNING]: ", logger.warning("", false));
		assertEquals("CyTest[WARNING]: test message", logger.warning("test message", false));
	}
	
	@Test 
	public void testConfig(){
		assertEquals("CyTest[CONFIG]: ", logger.config("", false));
		assertEquals("CyTest[CONFIG]: test message", logger.config("test message", false));
	}
	
	@Test 
	public void testTest(){
		assertEquals("CyTest[TEST]: ", logger.test("", false));
		assertEquals("CyTest[TEST]: test message", logger.test("test message", false));
	}
}
