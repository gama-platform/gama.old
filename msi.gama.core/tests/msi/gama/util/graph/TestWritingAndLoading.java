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
 * Tests graph writing and loading: take a graph, write it, read it, compare.
 * 
 * @author Samuel Thiriot
 *
 */
@RunWith(value = Parameterized.class)
public class TestWritingAndLoading {

	@Parameters
	 public static Collection data() {
	   LinkedList params = new LinkedList();
	   for (String format: AvailableGraphWriters.getAvailableWriters()) {
		   String[] p = new String[1];
		   p[0] = format;
		   params.add(p);
	   }
	   return params;
	 }

	private String format;
	
	public TestWritingAndLoading(String writingFormat) {
		this.format = writingFormat;
		System.out.println("*** created test for format:"+writingFormat);
	}
	
	
	protected void compareGamaGraphs(GamaGraph original, GamaGraph tested) {
		
		assertEquals(
				"format: "+format+"; wrong number of vertices",
				original.getVertices().size(), 
				tested.getVertices().size()
				);
		assertEquals(
				"format: "+format+"; wrong number of edges",
				original.getEdges().size(), 
				tested.getEdges().size()
				);
		
	}
	
	

	
	public void testAllWritersExportAndRead(GamaGraph graph) {
		
		System.out.println("testing the writing in this format: "+format);
		
		File file = TestUtils.getTmpFile("emptyGraph");
		
		IGraphWriter writer = AvailableGraphWriters.getGraphWriter(format);
		writer.writeGraph(
				null, 
				new GamaGraph(), 
				null, 
				file.getAbsolutePath()
				);
		
		assertTrue(file.exists());
		assertFalse(file.isDirectory());
		
		System.out.println("(file was created)");
		
		if (AvailableGraphParsers.getAvailableLoaders().contains(format)) {
			System.out.println("this format is supported for readen, attempting to re read this graph");
			
			try {
				GamaGraph readen = GraphLoader.loadGraph(
						null, 
						file.getAbsolutePath(), 
						null, 
						null, 
						null, 
						null, 
						format
						);
				assertFalse(graph == readen);
				compareGamaGraphs(graph, readen);

			} catch (GamaRuntimeException e) {
				e.printStackTrace();
				fail("catched a gamaruntime exception "+e.getMessage());
			}
			
			
		} else {
			System.out.println("this format is not supported for reading, and error should be thrown !");
			try {
				GamaGraph readen = GraphLoader.loadGraph(
						null, 
						file.getAbsolutePath(), 
						null, 
						null, 
						null, 
						null, 
						format
						);
				fail("expected a GamaRuntimeException");
			} catch (GamaRuntimeException e) {
				System.out.println(e.getMessage());
			} 
		}
	
	
	
	}
	
	@Test
	public void testAllWritersFileCreatedForEmptyGraph() {
		System.err.println("testing the writing of empty graphs...");
		testAllWritersExportAndRead(new GamaGraph());
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraph1WithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex("1");
		g.addVertex("2");
		g.addVertex("3");
		
		testAllWritersExportAndRead(g);
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraph2WithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		
		testAllWritersExportAndRead(g);
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraph3WithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		
		testAllWritersExportAndRead(g);
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraphRedondantEdgesWithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		
		g.addEdge(1, 2);
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		
		testAllWritersExportAndRead(g);
	}
	
}
