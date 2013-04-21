package msi.gama.util.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import msi.gama.TestUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.graph.loader.AvailableGraphParsers;
import msi.gama.util.graph.loader.GraphLoader;
import msi.gama.util.graph.writer.AvailableGraphWriters;
import msi.gama.util.graph.writer.IGraphWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests graph writing and loading: take a graph, write it;
 * if a loader enables its reading, also read it, and compare results.
 * Ensures that declared formats are coherent: if i can save a graph in "GML", 
 * and a "GML" reader is available, they should work and apply an identity.
 * In the same way, if "gephi.gml" is available both as a reader and writer, 
 * they should work together. 
 * 
 * @author Samuel Thiriot
 */
@RunWith(value = Parameterized.class)
public class TestWriteGraphAndLoadAutoDetection {

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

	
	
	private String format;
	private GamaGraph graph;

	public TestWriteGraphAndLoadAutoDetection(String writingFormat, GamaGraph graph) {
		this.format = writingFormat;
		this.graph = graph;
		System.out.println("*** created test for format:"+writingFormat+" and graph:");
		System.out.println(graph.toString());
	}
	

	
	
	/**
	 * Attempts to write the graph in the current format.
	 * ensures a file was created and returns its path.
	 * @param graph
	 * @return
	 */
	private String testWriteInDefinedFormat(GamaGraph graph, String extension) {
		
		System.out.println("testing the writing in this format: "+format);
		
		File file = TestUtils.getTmpFile("emptyGraph", extension);
		
		IGraphWriter writer = AvailableGraphWriters.getGraphWriter(format);
		System.out.println("will use writer: "+writer.getClass().getCanonicalName());
		writer.writeGraph(
				null, 
				graph, 
				null, 
				file.getAbsolutePath()
				);
		
		assertTrue(file.exists());
		assertFalse(file.isDirectory());
		
		System.out.println("(file was created)");
		return file.getAbsolutePath();
	}


	

	@Test
	public void writeNeutralExtensionAndReadAutoIfPossible() {
		
		// write with neutral ext
		String filename = testWriteInDefinedFormat(graph, "tmp");
				
		GamaGraph readen = GraphLoader.loadGraph(
				null, 
				filename, 
				null, 
				null, 
				null, 
				null, 
				null
				);
		assertFalse(graph == readen);
		TestUtilsGraphs.compareGamaGraphs(format, graph, readen, 0); // TODO
		
	}
	
	
}
