package msi.gama.util.graph.writer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import msi.gama.TestUtils;
import msi.gama.util.graph.GamaGraph;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests graph writing: no error raised and a file is exported.
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
		   String[] p = new String[1];
		   p[0] = format;
		   params.add(p);
	   }
	   return params;
	 }
	
	String format;
	
	public TestWriting(String format) {
		this.format = format;
	}
	
	public void testAllWritersFileCreated(GamaGraph graph) {
		
		System.out.println("testing the writing for each accepted format for this graph...");
		
		System.out.println("testing format : "+format);
		
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
		
		
		
	}
	
	@Test
	public void testAllWritersFileCreatedForEmptyGraph() {
		System.err.println("testing the writing of empty graphs...");
		testAllWritersFileCreated(new GamaGraph());
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraph1WithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex("1");
		g.addVertex("2");
		g.addVertex("3");
		
		testAllWritersFileCreated(g);
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraph2WithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		
		testAllWritersFileCreated(g);
	}
	
	@Test
	public void testAllWritersFileCreatedSimpleGraph3WithoutAgents() {
		
		GamaGraph g = new GamaGraph();
		g.addVertex(1);
		g.addVertex(2);
		g.addVertex(3);
		
		g.addEdge(1, 2);
		g.addEdge(1, 3);
		
		testAllWritersFileCreated(g);
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
		
		testAllWritersFileCreated(g);
	}

}
