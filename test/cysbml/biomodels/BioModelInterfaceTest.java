package cysbml.biomodels;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

import cysbml.biomodel.BioModelWSInterface;
import uk.ac.ebi.biomodels.ws.BioModelsWSException;

/**
 * How to handle the before and after things in the tests.
 * Would be could do handle common tasks necessary for the tests 
 * before the test cases are started.
 * 
 * @author Matthias König
 *
 */
public class BioModelInterfaceTest {
	static final String VALID_BIOMODEL_ID = "BIOMD0000000070";
	static final String VALID_BIOMODEL_PERSON = "holzhütter";
	static final String VALID_BIOMODEL_NAME = "glycolysis";
	static final String INVALID_STRING = "xcvsfsfasdfa1323452342";
	
	static final String PHOST = null;
	static final String PPORT = null;
	
	@Test
	public void testGetBioModelIdsByName() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> modelIds = bmInterface.getBioModelIdsByName(VALID_BIOMODEL_NAME);
		assertNotNull("Models have to exist.", modelIds);
		assertTrue("More than 0 models have to exist.", modelIds.size() > 0);
	}
	@Test
	public void testGetBioModelIdsByName2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> modelIds = bmInterface.getBioModelIdsByName(INVALID_STRING);
		assertNotNull("If invalid name empty list is returned.", modelIds);
		assertTrue("No models in list for invalid name,", modelIds.size() == 0);
	}

	@Test
	public void testGetBioModelIdsByPerson() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> modelIds = bmInterface.getBioModelIdsByPerson(VALID_BIOMODEL_PERSON);		
		assertNotNull("Models have to exist.", modelIds);
		assertTrue("More than 0 models have to exist.", modelIds.size() > 0);
	}
	@Test
	public void testGetBioModelIdsByPerson2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> modelIds = bmInterface.getBioModelIdsByPerson(INVALID_STRING);
		assertNotNull("If invalid name empty list is returned.", modelIds);
		assertTrue("No models in list for invalid name,", modelIds.size() == 0);
	}

	@Test
	public void testGetBioModelNameById() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		String name = bmInterface.getBioModelNameById(VALID_BIOMODEL_ID);
		assertNotNull("Name has to exist.", name);
	}
	public void testGetBioModelNameById2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		String name = bmInterface.getBioModelNameById(INVALID_STRING);
		assertNull("If invalid id, null is returned.", name);
	}
	
	@Test
	public void testGetAuthorsByModelId() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> authors = bmInterface.getAuthorsByModelId(VALID_BIOMODEL_ID);
		assertNotNull("Authors have to exist.", authors);
		assertTrue("Authors have to exist.", authors.size() > 0);
	}
	@Test
	public void testGetAuthorsByModelId2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> authors = bmInterface.getAuthorsByModelId(INVALID_STRING);
		assertNotNull("Empty list for invalid search term.", authors);
		assertTrue("Empty list for invalid search term,", authors.size() == 0);
	}
	
	@Test
	public void testGetEncodersByModelId() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> encoders = bmInterface.getEncodersByModelId(VALID_BIOMODEL_ID);
		assertNotNull("Encoders have to exist.", encoders);
		assertTrue("More than 0 models have to exist.", encoders.size() > 0);
	}
	@Test
	public void testGetEncodersByModelId2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		List<String> encoders = bmInterface.getEncodersByModelId(INVALID_STRING);
		assertNotNull("Empty list for invalid search term.", encoders);
		assertTrue("Empty list for invalid search term,", encoders.size() == 0);
	}
	
	@Test
	public void testGetDateLastModifiedByModelId() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		String dateString = bmInterface.getDateLastModifiedByModelId(VALID_BIOMODEL_ID);
		assertNotNull("DateString has to exist.", dateString);	
	}
	@Test
	public void testGetDateLastModifiedByModelId2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		String dateString = bmInterface.getDateLastModifiedByModelId(INVALID_STRING);
		assertNull("If invalid id, null is returned", dateString);	
	}

	@Test
	public void testGetBioModelSBMLById() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		String sbml = bmInterface.getBioModelSBMLById(VALID_BIOMODEL_ID);
		assertNotNull("SBML has to exist.", sbml);
	}
	@Test
	public void testGetBioModelSBMLById2() throws BioModelsWSException {
		BioModelWSInterface bmInterface = new BioModelWSInterface(PHOST, PPORT);
		String sbml = bmInterface.getBioModelSBMLById(INVALID_STRING);
		assertNotNull("If invalid id, null is returned.", sbml);
	}
	
	@Test
	public void testGetSimpleModelById() {
		/* Test for the simple Model and the content of the simple model.
		 * Content has to be identical to calling the individual functions.
		 */
		fail("Not yet implemented");
	}

	@Test
	public void testGetSimpleModelsByIds() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testGetSimpleModelStringById() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSimpleModelString() {
		fail("Not yet implemented");
	}

}
