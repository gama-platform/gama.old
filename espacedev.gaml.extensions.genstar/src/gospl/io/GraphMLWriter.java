package gospl.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import core.metamodel.IMultitypePopulation;
import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;

/**
 * Util to export populations as graphs in different formats
 * 
 * @author Samuel Thiriot
 */
public class GraphMLWriter {


	/**
	 * Exports a population as GraphML, which preserves attributes.
	 * @param file
	 * @param population
	 * @throws FileNotFoundException
	 */
	public static void writePopulationAsGraphML(
									File file, 
									IPopulation<ADemoEntity, Attribute<? extends IValue>> population) 
									throws FileNotFoundException {

		PrintStream ps = new PrintStream(file);	
		writeHeader(ps);
		writeDeclarationNodeAttributes(ps, population);
		ps.println("  <graph id=\"G\" edgedefault=\"directed\">");

		// export nodes
		for (ADemoEntity e: population) {
			writeNodeAndAttributes(ps, e);
			// attributes
		}
		
		// export edges
		for (ADemoEntity e: population) {
			if (!e.hasParent())
				continue;
			writeEdgeToParent(ps, e);
		}
	    
		writeFooter(ps);
		ps.close();
	}
	
	public static void writePopulationAsGraphML(
			File file, 
			IMultitypePopulation<ADemoEntity, Attribute<? extends IValue>> population) 
			throws FileNotFoundException {
		
		PrintStream ps = new PrintStream(file);	
		writeHeader(ps);
		writeDeclarationNodeAttributes(ps, population);

		ps.println("  <graph id=\"G\" edgedefault=\"directed\">");

		// export nodes
		for (ADemoEntity e: population) {
			writeNodeAndAttributes(ps, e);
			// attributes
		}
		
		// export edges
		for (ADemoEntity e: population) {
			if (!e.hasParent())
				continue;
			writeEdgeToParent(ps, e);
		}
		
		writeFooter(ps);
		ps.close();
	}
	
	
	protected static void writeHeader(PrintStream ps) {
		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		ps.println("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"  \n" + 
				"    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
				"    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n" + 
				"     http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">"  
				);
		
	}
	
	protected static void writeDeclarationNodeAttributes(PrintStream ps, 
			IPopulation<ADemoEntity, Attribute<? extends IValue>> population) {

		// the type
		ps.println("<key id=\"__type\" attr.name=\"Type of entity\" attr.type=\"String\">");
		ps.println("  <default>untyped</default>");
		ps.println("</key>");
		
		for (Attribute<? extends IValue> a: population.getPopulationAttributes()) {
			ps.print("<key id=\"");
			ps.print(a.getAttributeName()); // TODO escape
			ps.print("\" for=\"node\" attr.name=\"");
			ps.print(a.getDescription()); // TODO escape
			ps.print("\" attr.type=\"");
			switch (a.getValueSpace().getType()) {
			case Boolean:
				ps.print("boolean");
				break;
			case Continue:
				ps.print("double");
				break;
			case Integer:
				ps.print("integer");
				break;
			case Nominal:
			case Order:
			case Range:
				ps.print("string");
				break;
			}
			ps.println("\">");
			
			ps.print("  <default>");
			try {
				ps.print(a.getEmptyValue().toString());
			} catch (NullPointerException e) {
				ps.print("?");
			}
			ps.println("  </default>");
			
			ps.println("</key>");
		}
		

	}
	
	protected static void writeFooter(PrintStream ps) {
		ps.println("  </graph>");
		ps.println("</graphml>");
	}
	
	protected static void writeNodeAndAttributes(PrintStream ps, ADemoEntity e) {

		// id 
		ps.print("    <node id=\"");
		ps.print(e.getEntityId());
		ps.println("\">");
		
		// export the type
		ps.print("        <data key=\"__type\">");
		ps.print(e.getEntityType());
		ps.println("</data>");
		
		// attributes
		for (Attribute<? extends IValue> a: e.getAttributes()) {
			ps.print("        <data key=\"");
			ps.print(a.getAttributeName()); // TODO escape
			ps.print("\">");
			ps.print(e.getValueForAttribute(a).getStringValue());
			ps.println("</data>");
		}
		
		ps.println("    </node>");

	}
	
	protected static void writeEdgeToParent(PrintStream ps, ADemoEntity e) {

		// id 
		ps.print("    <edge source=\"");
		ps.print(e.getEntityId());
		ps.print("\" target=\"");
		ps.print(e.getParent().getEntityId());
		ps.println("\"/>");
		
	}
	


}
