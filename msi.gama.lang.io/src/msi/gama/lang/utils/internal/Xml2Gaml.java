/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.utils.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class Xml2Gaml {
	private final StringBuilder gaml;
	private String tab;
	private boolean isString;

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		// Xml2Gaml x2g = new Xml2Gaml(args[1]);
		final Xml2Gaml x2g = new Xml2Gaml();
		x2g.doConv("model/ants_classic.xml");
		System.out.println(x2g);
	}

	public Xml2Gaml() {
		gaml = new StringBuilder();
		tab = "";
	}

	public static void print(final Document o) {
		final Xml2Gaml x2g = new Xml2Gaml();
		x2g.doConv(o);
		System.out.println(x2g.toString());
	}

	public String doConv(final String path2xml) {
		try {
			return doConv(getDocumentJDOM(path2xml));
		} catch (final JDOMException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String doConv(final InputStream in) {
		try {
			return doConv(getDocumentJDOM(in));
		} catch (final JDOMException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String doConv(final Document doc) {
		final Element model = doc.getRootElement();
		gaml.append("model "
				+ model.getAttributeValue("name")
				+ "\n"
				+ "// gen by Xml2Gaml\n"
				+ "import \"platform:/plugin/msi.gama.gui.application/generated/std.gaml\"\n\n");

		appendStatments(model.getChildren());
		return gaml.toString();
	}

	@SuppressWarnings("rawtypes")
	private void appendStatments(final List statments) {
		for (final Object statment : statments) {
			appendStatment((Element) statment);
		}
	}

	private void appendStatment(final Element statment) {
		gaml.append(tab);
		final String markupName = statment.getName();
		if (markupName.equals("include")) {
			gaml.append("import \"" + statment.getAttributeValue("file")
					+ ".gaml\"\n");
			return;
		}
		final Attribute cst = statment.getAttribute("const");
		final Attribute name = statment.getAttribute("name");
		final Attribute var = statment.getAttribute("var");
		final Attribute type = statment.getAttribute("type");
		if (cst != null && cst.getValue().equals("true")) {
			gaml.append("const ");
		} else {
			gaml.append(markupName + " ");
		}
		isString = false;
		if (type != null && type.getValue().equals("string")) {
			isString = true;
		}
		if (markupName.equals("chart") || markupName.equals("inspect")
				|| markupName.equals("image")) {
			gaml.append("name: '" + name.getValue() + "' ");
		} else if (name != null) {
			gaml.append(name.getValue() + " ");
		} else if (var != null) {
			gaml.append(var.getValue() + " ");
		}
		for (final Object ofacet : statment.getAttributes()) {
			final Attribute facet = (Attribute) ofacet;
			if (!facet.getName().equals("name")
					&& !facet.getName().equals("const")) {
				appendFacet(facet);
			}
		}
		@SuppressWarnings("rawtypes")
		final List statments = statment.getChildren();
		if (!statments.isEmpty()) {
			// if (markupName.equals("do")) {
			// String s = "(";
			// for (Object arg : statments) {
			// if (s.length()>1) s+=", ";
			// s += ((Element) arg).getAttributeValue("value");
			// }
			// gaml.append(s+");\n");
			// } else
			{
				gaml.append("{\n");
				tab += '\t';
				appendStatments(statments);
				tab = tab.substring(1);
				gaml.append(tab + "}\n");
			}
		} else {
			gaml.append(";\n");
		}
	}

	private void appendFacet(final Attribute facet) {
		final String name = facet.getName();
		final String value = facet.getValue();

		if ((isString && (name.equals("value") || name.equals("init"))
				|| name.equals("parameter") || name.equals("category"))
				&& value.indexOf('\'') < 0) {
			gaml.append(name + ": '" + value + "' ");
		} else if (name.equals("skills") && value.indexOf(',') > 0) {
			gaml.append(name + ": [" + value + "] ");
		} else {
			// http://download.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html#sum
			String replaced = value.replaceAll("rgb '(\\w+)'", "rgb('$1')");
			replaced = replaced.replaceAll("rgb '#(\\w+)'", "rgb('#$1')");
			replaced = replaced.replaceAll("file '(\\S+)'", "file('$1')");
			replaced = replaced.replaceAll("last (\\w+)", "last($1)");
			replaced = replaced.replaceAll("first (\\w+)", "first($1)");
			replaced = replaced.replaceAll("rnd (\\w+)", "rnd($1)");
			replaced = replaced.replaceAll("flip (\\w+)", "flip($1)");
			// replaced = replaced.replaceAll("(\\w+) location",
			// "location as $1");
			replaced = replaced.replaceAll("list (\\w+)", "$1 as list");
			gaml.append(name + ": " + replaced + " ");
		}
	}

	// ===== UTILS =====

	private Document getDocumentJDOM(final String path2xml)
			throws JDOMException, IOException {
		final SAXBuilder sax = new SAXBuilder();
		final Document doc = sax.build(new File(path2xml));
		return doc;
	}

	private Document getDocumentJDOM(final InputStream in)
			throws JDOMException, IOException {
		final SAXBuilder sax = new SAXBuilder();
		final Document doc = sax.build(in);
		return doc;
	}

	public static String doc2str(final Document doc) {
		final XMLOutputter xo = new XMLOutputter();
		return xo.outputString(doc);
	}

	@Override
	public String toString() {
		return gaml.toString();
	}
}
