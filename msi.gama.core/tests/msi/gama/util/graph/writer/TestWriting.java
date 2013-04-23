package msi.gama.util.graph.writer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import msi.gama.TestUtils;
import msi.gama.util.PostponedWarningList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.graph.TestUtilsGraphs;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests graph writing:  for many graphs and all the graph writers, 
 * ensures no error raised and a file is exported.
 * 
 * @author Samuel Thiriot
 *
 */
@RunWith(value = Parameterized.class)
public class TestWriting {

	@Parameters
	 public static Collection data() {
		
	   LinkedList params = new LinkedList();
	   for (String format: AvailableGraphWriters.getAvailableWriters()) {
		   for (GamaGraph g: TestUtilsGraphs.getGamaGraphsForTest()) {
			   Object[] p = new Object[2];
			   p[0] = format;
			   p[1] = g;
			   params.add(p);   
		   }
	   }
	   return params;
	   
	 }
	
	String format;
	GamaGraph graph;
	
	public TestWriting(String format, GamaGraph graph) {
		this.format = format;
		this.graph = graph;
	}
	
  @BeforeClass 
    public static void setUpOnce() {

		// view graph loader & writers warnings
		PostponedWarningList.writeSystemOut = true; 

		
    }
  
  	@Test
	public void testWriting() {
		
		System.out.println("testing the writing for each accepted format for this graph...");
		
		System.out.println("testing format : "+format);
		
		File file = TestUtils.getTmpFile("GamaTestWriteGraph");
		
		IGraphWriter writer = AvailableGraphWriters.getGraphWriter(format);
		writer.writeGraph(
				null, 
				graph, 
				null, 
				file.getAbsolutePath()
				);
		
		assertTrue(file.exists());
		assertFalse(file.isDirectory());
		assertTrue(file.isFile());

		System.out.println("(file was created)");
		
		file.delete();
		
	}
	

	

}
