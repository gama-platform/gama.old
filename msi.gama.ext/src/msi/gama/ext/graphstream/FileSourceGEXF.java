/*
 * Copyright 2006 - 2016 Stefan Balev <stefan.balev@graphstream-project.org> Julien Baudry
 * <julien.baudry@graphstream-project.org> Antoine Dutot <antoine.dutot@graphstream-project.org> Yoann Pigné
 * <yoann.pigne@graphstream-project.org> Guilhelm Savin <guilhelm.savin@graphstream-project.org>
 *
 * This file is part of GraphStream <http://graphstream-project.org>.
 *
 * GraphStream is a library whose purpose is to handle static or dynamic graph, create them from scratch, file or any
 * source and display them.
 *
 * This program is free software distributed under the terms of two licenses, the CeCILL-C license that fits European
 * law, and the GNU Lesser General Public License. You can use, modify and/ or redistribute the software under the terms
 * of the CeCILL-C license as circulated by CEA, CNRS and INRIA at the following URL <http://www.cecill.info> or under
 * the terms of the GNU LGPL as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL-C and LGPL licenses and
 * that you accept their terms.
 */
package msi.gama.ext.graphstream;

import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 * File source for the <a href="http://gexf.net/format/">GEXF</a> file format used by
 * <a href="http://www.gephi.org">Gephi</a>.
 *
 * @author Guilhelm Savin
 */
public class FileSourceGEXF extends FileSourceXML {
	private static final Pattern IS_DOUBLE = Pattern.compile("^-?\\d+([.]\\d+)?$");

	/**
	 * The GEXF parser.
	 */
	protected GEXFParser parser;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSourceXML#afterStartDocument()
	 */
	@Override
	protected void afterStartDocument() throws IOException, XMLStreamException {
		parser = new GEXFParser();
		parser.__gexf();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSourceXML#nextEvents()
	 */
	@Override
	public boolean nextEvents() throws IOException {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.graphstream.stream.file.FileSourceXML#beforeEndDocument()
	 */
	@Override
	protected void beforeEndDocument() {
		parser = null;
	}

	@SuppressWarnings ("unused")
	private class Attribute implements GEXFConstants {
		final String id;
		final String title;
		final AttributeType type;
		Object def;
		String options;

		Attribute(final String id, final String title, final AttributeType type) {
			this.id = id;
			this.title = title;
			this.type = type;
		}

		Object getValue(final String value) {
			Object r;

			switch (type) {
				case INTEGER:
					r = Integer.valueOf(value);
					break;
				case LONG:
					r = Long.valueOf(value);
					break;
				case FLOAT:
					r = Float.valueOf(value);
					break;
				case DOUBLE:
					r = Double.valueOf(value);
					break;
				case BOOLEAN:
					r = Boolean.valueOf(value);
					break;
				case LISTSTRING:
					final String[] list = value.split("\\|");

					boolean isDouble = true;

					for (final String element : list) {
						isDouble = isDouble && IS_DOUBLE.matcher(element).matches();
					}

					if (isDouble) {
						final double[] dlist = new double[list.length];

						for (int i = 0; i < list.length; i++) {
							dlist[i] = Double.parseDouble(list[i]);
						}

						r = dlist;
					} else {
						r = list;
					}

					break;
				case ANYURI:
					try {
						r = new URI(value);
					} catch (final URISyntaxException e) {
						throw new IllegalArgumentException(e);
					}
					break;
				default:
					r = value;
			}

			return r;
		}

		void setDefault(final String value) {
			this.def = getValue(value);
		}

		void setOptions(final String options) {
			this.options = options;
		}
	}

	private class GEXFParser extends Parser implements GEXFConstants {
		EdgeType defaultEdgeType;
		TimeFormatType timeFormat;
		HashMap<String, Attribute> nodeAttributesDefinition;
		HashMap<String, Attribute> edgeAttributesDefinition;

		GEXFParser() {
			defaultEdgeType = EdgeType.UNDIRECTED;
			timeFormat = TimeFormatType.INTEGER;
			nodeAttributesDefinition = new HashMap<>();
			edgeAttributesDefinition = new HashMap<>();
		}

		@SuppressWarnings ("unused")
		private long getTime(final String time) {
			long t = 0;

			switch (timeFormat) {
				case INTEGER:
					t = Integer.valueOf(time);
					break;
				case DOUBLE:
					// TODO
					break;
				case DATE:
					// TODO
					break;
				case DATETIME:
					// TODO
					break;
			}

			return t;
		}

		/**
		 * name : GEXF attributes : GEXFAttribute structure : META ? GRAPH
		 */
		private void __gexf() throws IOException, XMLStreamException {
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "gexf");

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "meta")) {
				pushback(e);
				__meta();
			} else {
				pushback(e);
			}

			__graph();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "gexf");
		}

		/**
		 * name : META attributes : METAttribute structure : ( CREATOR | KEYWORDS | DESCRIPTION )*
		 */
		private void __meta() throws IOException, XMLStreamException {
			EnumMap<METAAttribute, String> attributes;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "meta");

			attributes = getAttributes(METAAttribute.class, e.asStartElement());

			if (attributes.containsKey(METAAttribute.LASTMODIFIEDDATE)) {
				sendGraphAttributeAdded(sourceId, "lastmodifieddate", attributes.get(METAAttribute.LASTMODIFIEDDATE));
			}

			e = getNextEvent();

			while (!isEvent(e, XMLEvent.END_ELEMENT, "meta")) {
				try {
					String str;
					final Balise b = Balise.valueOf(toConstantName(e.asStartElement().getName().getLocalPart()));

					pushback(e);

					switch (b) {
						case CREATOR:
							str = __creator();
							sendGraphAttributeAdded(sourceId, "creator", str);
							break;
						case KEYWORDS:
							str = __keywords();
							sendGraphAttributeAdded(sourceId, "keywords", str);
							break;
						case DESCRIPTION:
							str = __description();
							sendGraphAttributeAdded(sourceId, "description", str);
							break;
						default:
							newParseError(e, false,
									"meta children should be one of 'creator','keywords' or 'description'");
					}
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true, "unknown element '%s'", e.asStartElement().getName().getLocalPart());
				}

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "meta");
		}

		/**
		 * name : CREATOR attributes : structure : string
		 */
		private String __creator() throws IOException, XMLStreamException {
			String creator;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "creator");

			creator = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "creator");

			return creator;
		}

		/**
		 * name : KEYWORDS attributes : structure : string
		 */
		private String __keywords() throws IOException, XMLStreamException {
			String keywords;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "keywords");

			keywords = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "keywords");

			return keywords;
		}

		/**
		 * <pre>
		 * name 		: DESCRIPTION
		 * attributes 	:
		 * structure 	: string
		 * </pre>
		 */
		private String __description() throws IOException, XMLStreamException {
			String description;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "description");

			description = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "description");

			return description;
		}

		/**
		 * <pre>
		 * name 		: GRAPH
		 * attributes 	: GRAPHAttribute
		 * structure 	: ATTRIBUTES * ( NODES | EDGES )*
		 * </pre>
		 */
		private void __graph() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<GRAPHAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "graph");

			attributes = getAttributes(GRAPHAttribute.class, e.asStartElement());

			if (attributes.containsKey(GRAPHAttribute.DEFAULTEDGETYPE)) {
				try {
					defaultEdgeType = EdgeType.valueOf(toConstantName(attributes.get(GRAPHAttribute.DEFAULTEDGETYPE)));
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true,
							"'defaultedgetype' value should be one of 'directed', 'undirected' or 'mutual'");
				}
			}

			if (attributes.containsKey(GRAPHAttribute.TIMEFORMAT)) {
				try {
					timeFormat = TimeFormatType.valueOf(toConstantName(attributes.get(GRAPHAttribute.TIMEFORMAT)));
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true,
							"'timeformat' value should be one of 'integer', 'double', 'date' or 'datetime'");
				}
			}

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "attributes")) {
				pushback(e);

				__attributes();
				e = getNextEvent();
			}

			while (isEvent(e, XMLEvent.START_ELEMENT, "nodes") || isEvent(e, XMLEvent.START_ELEMENT, "edges")) {
				if (isEvent(e, XMLEvent.START_ELEMENT, "nodes")) {
					pushback(e);
					__nodes();
				} else {
					pushback(e);
					__edges();
				}

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "graph");
		}

		/**
		 * <pre>
		 * name 		: ATTRIBUTES
		 * attributes 	: ATTRIBUTESAttributes { CLASS!, MODE, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: ATTRIBUTE *
		 * </pre>
		 */
		private void __attributes() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<ATTRIBUTESAttribute, String> attributes;
			Attribute a;
			ClassType type = null;
			HashMap<String, Attribute> attr;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "attributes");

			attributes = getAttributes(ATTRIBUTESAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, ATTRIBUTESAttribute.CLASS);

			try {
				type = ClassType.valueOf(toConstantName(attributes.get(ATTRIBUTESAttribute.CLASS)));
			} catch (final IllegalArgumentException ex) {
				newParseError(e, true, "'class' value shoudl be one of 'node' or 'edge'");
			}

			if (type == ClassType.NODE) {
				attr = nodeAttributesDefinition;
			} else {
				attr = edgeAttributesDefinition;
			}

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "attribute")) {
				pushback(e);

				a = __attribute();
				attr.put(a.id, a);
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "attributes");
		}

		/**
		 * <pre>
		 * name 		: ATTRIBUTE
		 * attributes 	: ATTRIBUTEAttribute { ID, TITLE, TYPE }
		 * structure 	: ( DEFAULT | OPTIONS ) *
		 * </pre>
		 */
		private Attribute __attribute() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<ATTRIBUTEAttribute, String> attributes;
			String id, title;
			AttributeType type = null;
			Attribute theAttribute;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "attribute");

			attributes = getAttributes(ATTRIBUTEAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, ATTRIBUTEAttribute.ID, ATTRIBUTEAttribute.TITLE,
					ATTRIBUTEAttribute.TYPE);

			id = attributes.get(ATTRIBUTEAttribute.ID);
			title = attributes.get(ATTRIBUTEAttribute.TITLE);

			try {
				type = AttributeType.valueOf(toConstantName(attributes.get(ATTRIBUTEAttribute.TYPE)));
			} catch (final IllegalArgumentException ex) {
				newParseError(e, true,
						"'type' of attribute should be one of 'integer', 'long', 'float, 'double', 'string', 'liststring', 'anyURI' or 'boolean'");
			}

			theAttribute = new Attribute(id, title, type);

			e = getNextEvent();

			while (!isEvent(e, XMLEvent.END_ELEMENT, "attribute")) {
				try {
					final Balise b = Balise.valueOf(toConstantName(e.asStartElement().getName().getLocalPart()));

					pushback(e);

					switch (b) {
						case DEFAULT:
							try {
								theAttribute.setDefault(__default());
							} catch (final Exception invalid) {
								newParseError(e, false, "invalid 'default' value");
							}

							break;
						case OPTIONS:
							theAttribute.setOptions(__options());
							break;
						default:
							newParseError(e, true, "attribute children should be one of 'default' or 'options'");
					}
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true, "unknown element '%s'", e.asStartElement().getName().getLocalPart());
				}

				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "attribute");

			return theAttribute;
		}

		/**
		 * <pre>
		 * name 		: DEFAULT
		 * attributes 	:
		 * structure 	: string
		 * </pre>
		 */
		private String __default() throws IOException, XMLStreamException {
			String def;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "default");

			def = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "default");

			return def;
		}

		/**
		 * <pre>
		 * name 		: OPTIONS
		 * attributes 	:
		 * structure 	: string
		 * </pre>
		 */
		private String __options() throws IOException, XMLStreamException {
			String options;
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "options");

			options = __characters();

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "options");

			return options;
		}

		/**
		 * <pre>
		 * name 		: NODES
		 * attributes 	: NODESAttribute { 'count' }
		 * structure 	: NODE *
		 * </pre>
		 */
		private void __nodes() throws IOException, XMLStreamException {
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "nodes");

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "node")) {
				pushback(e);

				__node();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "nodes");

		}

		/**
		 * <pre>
		 * name 		: NODE
		 * attributes 	: NODEAttribute { 'pid', 'id', 'label', 'start', 'startopen', 'end', 'endopen' }
		 * structure 	: ( ATTVALUES | SPELLS | ( NODES | EDGES ) | PARENTS | ( COLOR | POSITION | SIZE | NODESHAPE ) ) *
		 * </pre>
		 */
		private void __node() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<NODEAttribute, String> attributes;
			String id;
			final HashSet<String> defined = new HashSet<>();

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "node");

			attributes = getAttributes(NODEAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, NODEAttribute.ID);

			id = attributes.get(NODEAttribute.ID);
			sendNodeAdded(sourceId, id);

			if (attributes.containsKey(NODEAttribute.LABEL)) {
				sendNodeAttributeAdded(sourceId, id, "label", attributes.get(NODEAttribute.LABEL));
			}

			e = getNextEvent();

			while (!isEvent(e, XMLEvent.END_ELEMENT, "node")) {
				try {
					final Balise b = Balise.valueOf(toConstantName(e.asStartElement().getName().getLocalPart()));

					pushback(e);

					switch (b) {
						case ATTVALUES:
							defined.addAll(__attvalues(ClassType.NODE, id));
							break;
						case COLOR:
							__color(ClassType.NODE, id);
							break;
						case POSITION:
							__position(id);
							break;
						case SIZE:
							__size(id);
							break;
						case SHAPE:
							__node_shape(id);
							break;
						case SPELLS:
							__spells();
							break;
						case NODES:
							__nodes();
							break;
						case EDGES:
							__edges();
							break;
						case PARENTS:
							__parents(id);
							break;
						default:
							newParseError(e, true,
									"attribute children should be one of 'attvalues', 'color', 'position', 'size', shape', 'spells', 'nodes, 'edges' or 'parents'");
					}
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true, "unknown element '%s'", e.asStartElement().getName().getLocalPart());
				}

				e = getNextEvent();
			}

			for (final Attribute theAttribute : nodeAttributesDefinition.values()) {
				if (!defined.contains(theAttribute.id)) {
					sendNodeAttributeAdded(sourceId, id, theAttribute.title, theAttribute.def);
				}
			}

			checkValid(e, XMLEvent.END_ELEMENT, "node");
		}

		/**
		 * <pre>
		 * name : ATTVALUES attributes : structure : ATTVALUE * </spell>
		 */
		private HashSet<String> __attvalues(final ClassType type, final String elementId)
				throws IOException, XMLStreamException {
			XMLEvent e;
			final HashSet<String> defined = new HashSet<>();

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "attvalues");

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "attvalue")) {
				pushback(e);

				defined.add(__attvalue(type, elementId));
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "attvalues");

			return defined;
		}

		/**
		 * <pre>
		 * name 		: ATTVALUE
		 * attributes 	: ATTVALUEAttribute { FOR!, VALUE!, START, STARTOPEN, END, ENDOPEN }
		 * structure 	:
		 * </pre>
		 */
		private String __attvalue(final ClassType type, final String elementId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<ATTVALUEAttribute, String> attributes;
			Attribute theAttribute;
			Object value = null;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "attvalue");

			attributes = getAttributes(ATTVALUEAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, ATTVALUEAttribute.FOR, ATTVALUEAttribute.VALUE);

			if (type == ClassType.NODE) {
				theAttribute = nodeAttributesDefinition.get(attributes.get(ATTVALUEAttribute.FOR));
			} else {
				theAttribute = edgeAttributesDefinition.get(attributes.get(ATTVALUEAttribute.FOR));
			}

			if (theAttribute == null) {
				newParseError(e, false, "undefined attribute \"%s\"", attributes.get(ATTVALUEAttribute.FOR));
			} else {
				try {
					value = theAttribute.getValue(attributes.get(ATTVALUEAttribute.VALUE));
				} catch (final Exception ex) {
					newParseError(e, true, "invalid 'value' value");
				}

				switch (type) {
					case NODE:
						sendNodeAttributeAdded(sourceId, elementId, theAttribute.title, value);
						break;
					case EDGE:
						sendEdgeAttributeAdded(sourceId, elementId, theAttribute.title, value);
						break;
				}
			}

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "attvalue");

			return theAttribute == null ? null : theAttribute.id;
		}

		/**
		 * <pre>
		 * name 		: SPELLS
		 * attributes 	:
		 * structure 	: SPELL +
		 * </pre>
		 */
		private void __spells() throws IOException, XMLStreamException {
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "spells");

			do {
				__spell();
				e = getNextEvent();
			} while (isEvent(e, XMLEvent.START_ELEMENT, "spell"));

			checkValid(e, XMLEvent.END_ELEMENT, "spells");
		}

		/**
		 * <pre>
		 * name 		: SPELL
		 * attributes 	: SPELLAttribute
		 * structure 	:
		 * </pre>
		 */
		@SuppressWarnings ("unused")
		private void __spell() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<SPELLAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "spell");

			attributes = getAttributes(SPELLAttribute.class, e.asStartElement());

			// TODO Handle spell

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "spell");
		}

		/**
		 * <pre>
		 * name 		: PARENTS
		 * attributes 	:
		 * structure 	: PARENT *
		 * </pre>
		 */
		private void __parents(final String nodeId) throws IOException, XMLStreamException {
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "parents");

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "parent")) {
				__parent(nodeId);
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "parents");
		}

		/**
		 * <pre>
		 * name 		: PARENT
		 * attributes 	: PARENTAttribute { FOR! }
		 * structure 	:
		 * </pre>
		 */
		private void __parent(final String nodeId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<PARENTAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "parent");

			attributes = getAttributes(PARENTAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, PARENTAttribute.FOR);
			sendNodeAttributeAdded(sourceId, attributes.get(PARENTAttribute.FOR), "parent", nodeId);

			e = getNextEvent();
			checkValid(e, XMLEvent.END_ELEMENT, "parent");
		}

		/**
		 * <pre>
		 * name 		: COLOR
		 * attributes 	: COLORAttribute { R!, G!, B!, A, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: SPELLS ?
		 * </pre>
		 */
		private void __color(final ClassType type, final String id) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<COLORAttribute, String> attributes;
			Color color;
			int r, g, b, a = 255;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "color");

			attributes = getAttributes(COLORAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, COLORAttribute.R, COLORAttribute.G, COLORAttribute.B);

			r = Integer.valueOf(attributes.get(COLORAttribute.R));
			g = Integer.valueOf(attributes.get(COLORAttribute.G));
			b = Integer.valueOf(attributes.get(COLORAttribute.B));

			if (attributes.containsKey(COLORAttribute.A)) {
				a = Integer.valueOf(attributes.get(COLORAttribute.A));
			}

			color = new Color(r, g, b, a);

			switch (type) {
				case NODE:
					sendNodeAttributeAdded(sourceId, id, "ui.color", color);
					break;
				case EDGE:
					sendEdgeAttributeAdded(sourceId, id, "ui.color", color);
					break;
			}

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "spells")) {
				pushback(e);

				__spells();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "color");
		}

		/**
		 * <pre>
		 * name 		: POSITION
		 * attributes 	: POSITIONAttribute { X!, Y!, Z!, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: SPELLS ?
		 * </pre>
		 */
		private void __position(final String nodeId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<POSITIONAttribute, String> attributes;
			final double[] xyz = { 0, 0, 0 };

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "position");

			attributes = getAttributes(POSITIONAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, POSITIONAttribute.X, POSITIONAttribute.Y, POSITIONAttribute.Z);

			xyz[0] = Double.valueOf(attributes.get(POSITIONAttribute.X));
			xyz[1] = Double.valueOf(attributes.get(POSITIONAttribute.Y));
			xyz[2] = Double.valueOf(attributes.get(POSITIONAttribute.Z));

			sendNodeAttributeAdded(sourceId, nodeId, "xyz", xyz);

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "spells")) {
				pushback(e);

				__spells();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "position");
		}

		/**
		 * <pre>
		 * name 		: SIZE
		 * attributes 	: SIZEAttribute { VALUE!, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: SPELLS ?
		 * </pre>
		 */
		private void __size(final String nodeId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<SIZEAttribute, String> attributes;
			double value;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "size");

			attributes = getAttributes(SIZEAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, SIZEAttribute.VALUE);

			value = Double.valueOf(attributes.get(SIZEAttribute.VALUE));

			sendNodeAttributeAdded(sourceId, nodeId, "ui.size", value);

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "spells")) {
				pushback(e);

				__spells();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "size");
		}

		/**
		 * <pre>
		 * name 		: NODESHAPE
		 * attributes 	: NODESHAPEAttributes { VALUE!, URI, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: SPELLS ?
		 * </pre>
		 */
		private void __node_shape(final String nodeId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<NODESHAPEAttribute, String> attributes;
			NodeShapeType type = null;
			String uri;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "shape");

			attributes = getAttributes(NODESHAPEAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, NODESHAPEAttribute.VALUE);

			try {
				type = NodeShapeType.valueOf(toConstantName(attributes.get(NODESHAPEAttribute.VALUE)));
			} catch (final IllegalArgumentException ex) {
				newParseError(e, true, "'value' should be one of 'disc', 'diamond', 'triangle', 'square' or 'image'");
			}

			switch (type) {
				case IMAGE:
					if (!attributes.containsKey(NODESHAPEAttribute.URI)) {
						newParseError(e, true, "'image' shape type needs 'uri' attribute");
					}

					uri = attributes.get(NODESHAPEAttribute.URI);
					sendNodeAttributeAdded(sourceId, nodeId, "ui.style",
							String.format("fill-mode: image-scaled; fill-image: url('%s');", uri));

					break;
				default:
					sendNodeAttributeAdded(sourceId, nodeId, "ui.style",
							String.format("shape: %s;", type.name().toLowerCase()));
			}

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "spells")) {
				pushback(e);

				__spells();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "shape");
		}

		/**
		 * <pre>
		 * name 		: EDGES
		 * attributes 	: EDGESAttribute { 'count' }
		 * structure 	: EDGE *
		 * </pre>
		 */
		private void __edges() throws IOException, XMLStreamException {
			XMLEvent e;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "edges");

			e = getNextEvent();

			while (isEvent(e, XMLEvent.START_ELEMENT, "edge")) {
				pushback(e);

				__edge();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "edges");
		}

		/**
		 * <pre>
		 * name 		: EDGE
		 * attributes 	: EDGEAttribute { START, STARTOPEN, END, ENDOPEN, ID!, TYPE, LABEL, SOURCE!, TARGET!, WEIGHT }
		 * structure 	: ( ATTVALUES | SPELLS | ( COLOR | THICKNESS | EDGESHAPE ) ) *
		 * </pre>
		 */
		private void __edge() throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<EDGEAttribute, String> attributes;
			String id, source, target;
			EdgeType type = defaultEdgeType;
			final HashSet<String> defined = new HashSet<>();

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "edge");

			attributes = getAttributes(EDGEAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, EDGEAttribute.ID, EDGEAttribute.SOURCE, EDGEAttribute.TARGET);

			id = attributes.get(EDGEAttribute.ID);
			source = attributes.get(EDGEAttribute.SOURCE);
			target = attributes.get(EDGEAttribute.TARGET);

			if (attributes.containsKey(EDGEAttribute.TYPE)) {
				try {
					type = EdgeType.valueOf(toConstantName(attributes.get(EDGEAttribute.TYPE)));
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true, "edge type should be one of 'undirected', 'undirected' or 'mutual'");
				}
			}

			switch (type) {
				case DIRECTED:
					sendEdgeAdded(sourceId, id, source, target, true);
					break;
				case MUTUAL:
				case UNDIRECTED:
					sendEdgeAdded(sourceId, id, source, target, false);
					break;
			}

			if (attributes.containsKey(EDGEAttribute.LABEL)) {
				sendEdgeAttributeAdded(sourceId, id, "ui.label", attributes.get(EDGEAttribute.LABEL));
			}

			if (attributes.containsKey(EDGEAttribute.WEIGHT)) {
				try {
					final double d = Double.valueOf(attributes.get(EDGEAttribute.WEIGHT));
					sendEdgeAttributeAdded(sourceId, id, "weight", d);
				} catch (final NumberFormatException ex) {
					newParseError(e, true, "'weight' attribute of edge should be a real");
				}
			}

			e = getNextEvent();

			while (!isEvent(e, XMLEvent.END_ELEMENT, "edge")) {
				try {
					final Balise b = Balise.valueOf(toConstantName(e.asStartElement().getName().getLocalPart()));

					pushback(e);

					switch (b) {
						case ATTVALUES:
							defined.addAll(__attvalues(ClassType.EDGE, id));
							break;
						case SPELLS:
							__spells();
							break;
						case COLOR:
							__color(ClassType.EDGE, id);
							break;
						case THICKNESS:
							__thickness(id);
							break;
						case SHAPE:
							__edge_shape(id);
							break;
						default:
							newParseError(e, true,
									"edge children should be one of 'attvalues', 'color', 'thicknes', 'shape' or 'spells'");
					}
				} catch (final IllegalArgumentException ex) {
					newParseError(e, true, "unknown tag '%s'", e.asStartElement().getName().getLocalPart());
				}

				e = getNextEvent();
			}

			for (final String key : edgeAttributesDefinition.keySet()) {
				if (!defined.contains(key)) {
					sendEdgeAttributeAdded(sourceId, id, key, edgeAttributesDefinition.get(key).def);
				}
			}

			checkValid(e, XMLEvent.END_ELEMENT, "edge");
		}

		/**
		 * <pre>
		 * name 		: EDGESHAPE
		 * attributes 	: EDGESHAPEAttributes { VALUE!, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: SPELLS ?
		 * </pre>
		 */
		@SuppressWarnings ("unused")
		private void __edge_shape(final String edgeId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<EDGESHAPEAttribute, String> attributes;
			EdgeShapeType type;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "shape");

			attributes = getAttributes(EDGESHAPEAttribute.class, e.asStartElement());
			checkRequiredAttributes(e, attributes, EDGESHAPEAttribute.VALUE);

			try {
				type = EdgeShapeType.valueOf(toConstantName(attributes.get(EDGESHAPEAttribute.VALUE)));
			} catch (final IllegalArgumentException ex) {
				newParseError(e, true, "'value' of shape should be one of 'solid', 'dotted', 'dashed' or 'double'");
			}

			// TODO Handle shape of edges

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "spells")) {
				pushback(e);

				__spells();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "shape");
		}

		/**
		 * <pre>
		 * name 		: THICKNESS
		 * attributes 	: THICKNESSAttribute { VALUE!, START, STARTOPEN, END, ENDOPEN }
		 * structure 	: SPELLS ?
		 * </pre>
		 */
		private void __thickness(final String edgeId) throws IOException, XMLStreamException {
			XMLEvent e;
			EnumMap<THICKNESSAttribute, String> attributes;

			e = getNextEvent();
			checkValid(e, XMLEvent.START_ELEMENT, "thickness");

			attributes = getAttributes(THICKNESSAttribute.class, e.asStartElement());

			checkRequiredAttributes(e, attributes, THICKNESSAttribute.VALUE);

			e = getNextEvent();

			if (isEvent(e, XMLEvent.START_ELEMENT, "spells")) {
				pushback(e);

				__spells();
				e = getNextEvent();
			}

			checkValid(e, XMLEvent.END_ELEMENT, "thickness");
		}
	}

	public interface GEXFConstants {
		public enum Balise {
			GEXF,
			GRAPH,
			META,
			CREATOR,
			KEYWORDS,
			DESCRIPTION,
			NODES,
			NODE,
			EDGES,
			EDGE,
			COLOR,
			POSITION,
			SIZE,
			SHAPE,
			THICKNESS,
			DEFAULT,
			OPTIONS,
			ATTVALUES,
			PARENTS,
			SPELLS
		}

		public enum GEXFAttribute {
			XMLNS, VERSION
		}

		public enum METAAttribute {
			LASTMODIFIEDDATE
		}

		public enum GRAPHAttribute {
			TIMEFORMAT, START, STARTOPEN, END, ENDOPEN, DEFAULTEDGETYPE, IDTYPE, MODE
		}

		public enum ATTRIBUTESAttribute {
			CLASS, MODE, START, STARTOPEN, END, ENDOPEN
		}

		public enum ATTRIBUTEAttribute {
			ID, TITLE, TYPE
		}

		public enum NODESAttribute {
			COUNT
		}

		public enum NODEAttribute {
			START, STARTOPEN, END, ENDOPEN, PID, ID, LABEL
		}

		public enum ATTVALUEAttribute {
			FOR, VALUE, START, STARTOPEN, END, ENDOPEN
		}

		public enum PARENTAttribute {
			FOR
		}

		public enum EDGESAttribute {
			COUNT
		}

		public enum SPELLAttribute {
			START, STARTOPEN, END, ENDOPEN
		}

		public enum COLORAttribute {
			R, G, B, A, START, STARTOPEN, END, ENDOPEN
		}

		public enum POSITIONAttribute {
			X, Y, Z, START, STARTOPEN, END, ENDOPEN
		}

		public enum SIZEAttribute {
			VALUE, START, STARTOPEN, END, ENDOPEN
		}

		public enum NODESHAPEAttribute {
			VALUE, URI, START, STARTOPEN, END, ENDOPEN
		}

		public enum EDGEAttribute {
			START, STARTOPEN, END, ENDOPEN, ID, TYPE, LABEL, SOURCE, TARGET, WEIGHT
		}

		public enum THICKNESSAttribute {
			VALUE, START, STARTOPEN, END, ENDOPEN
		}

		public enum EDGESHAPEAttribute {
			VALUE, START, STARTOPEN, END, ENDOPEN
		}

		public enum IDType {
			INTEGER, STRING
		}

		public enum ModeType {
			STATIC, DYNAMIC
		}

		public enum WeightType {
			FLOAT
		}

		public enum EdgeType {
			DIRECTED, UNDIRECTED, MUTUAL
		}

		public enum NodeShapeType {
			DISC, SQUARE, TRIANGLE, DIAMOND, IMAGE
		}

		public enum EdgeShapeType {
			SOLID, DOTTED, DASHED, DOUBLE
		}

		public enum AttributeType {
			INTEGER, LONG, FLOAT, DOUBLE, BOOLEAN, ANYURI, LISTSTRING, STRING
		}

		public enum ClassType {
			NODE, EDGE
		}

		public enum TimeFormatType {
			INTEGER, DOUBLE, DATE, DATETIME
		}
	}
}
