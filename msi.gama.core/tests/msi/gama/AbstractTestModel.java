package msi.gama;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * base test case for a model. Pass it a directory and file, 
 * it will attempt to compile this model.
 * 
 * @author Samuel Thiriot
 *
 */
public class AbstractTestModel {

	// parameters of the test
	protected File modelDirectory;
	protected File modelFile;
	
	// created objects during setUp
	// TODO
	
	
	public void AbstractTestModel(File modelDirectory, File modelFile) {
		this.modelDirectory = modelDirectory;
		this.modelFile = modelFile;
	}
	
	@Before
	public void setUp() {
		// TODO load the model as a Gama model
		
	}
	
	@After
	public void tearDown() {
		
	}
	
	@Test
	public void compilationOk() {
		// TODO: compilation of this model
		// assert...
		
	}
	
}
