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
package msi.gama.util.graph.graphstream_copy;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import msi.gama.util.graph.graphstream_copy.CumulativeSpells.Spell;

public class FileSinkGEXF extends FileSinkBase {
	public enum TimeFormat {
		INTEGER(new DecimalFormat("#", new DecimalFormatSymbols(Locale.ROOT))),
		DOUBLE(new DecimalFormat("#.0###################", new DecimalFormatSymbols(Locale.ROOT))),
		DATE(new SimpleDateFormat("yyyy-MM-dd")),
		DATETIME(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ"));
		Format format;

		TimeFormat(final Format f) {
			this.format = f;
		}
	}

	XMLStreamWriter stream;
	boolean smart;
	int depth;
	int currentAttributeIndex = 0;
	GraphSpells graphSpells;
	TimeFormat timeFormat;

	public FileSinkGEXF() {
		smart = true;
		depth = 0;
		graphSpells = null;
		timeFormat = TimeFormat.DOUBLE;
	}

	public void setTimeFormat(final TimeFormat format) {
		this.timeFormat = format;
	}

	protected void putSpellAttributes(final Spell s) throws XMLStreamException {
		if (s.isStarted()) {
			final String start = s.isStartOpen() ? "startopen" : "start";
			final String date = timeFormat.format.format(s.getStartDate());

			stream.writeAttribute(start, date);
		}

		if (s.isEnded()) {
			final String end = s.isEndOpen() ? "endopen" : "end";
			final String date = timeFormat.format.format(s.getEndDate());

			stream.writeAttribute(end, date);
		}
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		try {
			if (graphSpells != null) {
				exportGraphSpells();
				graphSpells = null;
			}

			endElement(stream, false);
			stream.writeEndDocument();
			stream.flush();
		} catch (final XMLStreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void outputHeader() throws IOException {
		final Calendar cal = Calendar.getInstance();
		final Date date = cal.getTime();
		final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

		try {
			stream = XMLOutputFactory.newFactory().createXMLStreamWriter(output);
			stream.writeStartDocument("UTF-8", "1.0");

			startElement(stream, "gexf");
			stream.writeAttribute("xmlns", "http://www.gexf.net/1.2draft");
			stream.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			stream.writeAttribute("xsi:schemaLocation",
					"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd");
			stream.writeAttribute("version", "1.2");

			startElement(stream, "meta");
			stream.writeAttribute("lastmodifieddate", df.format(date));
			startElement(stream, "creator");
			stream.writeCharacters("GraphStream - " + getClass().getName());
			endElement(stream, true);
			endElement(stream, false);
		} catch (final XMLStreamException e) {
			throw new IOException(e);
		} catch (final FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}

	protected void startElement(final XMLStreamWriter stream, final String name) throws XMLStreamException {
		if (smart) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++) {
				stream.writeCharacters(" ");
			}
		}

		stream.writeStartElement(name);
		depth++;
	}

	protected void endElement(final XMLStreamWriter stream, final boolean leaf) throws XMLStreamException {
		depth--;

		if (smart && !leaf) {
			stream.writeCharacters("\n");

			for (int i = 0; i < depth; i++) {
				stream.writeCharacters(" ");
			}
		}

		stream.writeEndElement();
	}

	@Override
	protected void exportGraph(final Graph g) {
		final GEXFAttributeMap nodeAttributes = new GEXFAttributeMap("node", g);
		final GEXFAttributeMap edgeAttributes = new GEXFAttributeMap("edge", g);

		try {
			startElement(stream, "graph");
			stream.writeAttribute("defaultedgetype", "undirected");

			nodeAttributes.export(stream);
			edgeAttributes.export(stream);

			startElement(stream, "nodes");
			for (final Node n : g.getEachNode()) {
				startElement(stream, "node");
				stream.writeAttribute("id", n.getId());

				if (n.hasAttribute("label")) {
					stream.writeAttribute("label", n.getAttribute("label").toString());
				}

				if (n.getAttributeCount() > 0) {
					startElement(stream, "attvalues");
					for (final String key : n.getAttributeKeySet()) {
						nodeAttributes.push(stream, n, key);
					}
					endElement(stream, false);
				}

				endElement(stream, n.getAttributeCount() == 0);
			}
			endElement(stream, false);

			startElement(stream, "edges");
			for (final Edge e : g.getEachEdge()) {
				startElement(stream, "edge");

				stream.writeAttribute("id", e.getId());
				stream.writeAttribute("source", e.getSourceNode().getId());
				stream.writeAttribute("target", e.getTargetNode().getId());

				if (e.getAttributeCount() > 0) {
					startElement(stream, "attvalues");
					for (final String key : e.getAttributeKeySet()) {
						edgeAttributes.push(stream, e, key);
					}
					endElement(stream, false);
				}

				endElement(stream, e.getAttributeCount() == 0);
			}
			endElement(stream, false);

			endElement(stream, false);
		} catch (final XMLStreamException e1) {
			e1.printStackTrace();
		}
	}

	protected void exportGraphSpells() {
		final GEXFAttributeMap nodeAttributes = new GEXFAttributeMap("node", graphSpells);
		final GEXFAttributeMap edgeAttributes = new GEXFAttributeMap("edge", graphSpells);

		try {
			startElement(stream, "graph");
			stream.writeAttribute("mode", "dynamic");
			stream.writeAttribute("defaultedgetype", "undirected");
			stream.writeAttribute("timeformat", timeFormat.name().toLowerCase());

			nodeAttributes.export(stream);
			edgeAttributes.export(stream);

			startElement(stream, "nodes");
			for (final String nodeId : graphSpells.getNodes()) {
				startElement(stream, "node");
				stream.writeAttribute("id", nodeId);

				final CumulativeAttributes attr = graphSpells.getNodeAttributes(nodeId);
				final Object label = attr.getAny("label");

				if (label != null) {
					stream.writeAttribute("label", label.toString());
				}

				final CumulativeSpells spells = graphSpells.getNodeSpells(nodeId);

				if (!spells.isEternal()) {
					startElement(stream, "spells");
					for (int i = 0; i < spells.getSpellCount(); i++) {
						final Spell s = spells.getSpell(i);

						startElement(stream, "spell");
						putSpellAttributes(s);
						endElement(stream, true);
					}
					endElement(stream, false);
				}

				if (attr.getAttributesCount() > 0) {
					startElement(stream, "attvalues");
					nodeAttributes.push(stream, nodeId, graphSpells);
					endElement(stream, false);
				}

				endElement(stream, spells.isEternal() && attr.getAttributesCount() == 0);
			}
			endElement(stream, false);

			startElement(stream, "edges");
			for (final String edgeId : graphSpells.getEdges()) {
				startElement(stream, "edge");

				final GraphSpells.EdgeData data = graphSpells.getEdgeData(edgeId);

				stream.writeAttribute("id", edgeId);
				stream.writeAttribute("source", data.getSource());
				stream.writeAttribute("target", data.getTarget());

				final CumulativeAttributes attr = graphSpells.getEdgeAttributes(edgeId);

				final CumulativeSpells spells = graphSpells.getEdgeSpells(edgeId);

				if (!spells.isEternal()) {
					startElement(stream, "spells");
					for (int i = 0; i < spells.getSpellCount(); i++) {
						final Spell s = spells.getSpell(i);

						startElement(stream, "spell");
						putSpellAttributes(s);
						endElement(stream, true);
					}
					endElement(stream, false);
				}

				if (attr.getAttributesCount() > 0) {
					startElement(stream, "attvalues");
					edgeAttributes.push(stream, edgeId, graphSpells);
					endElement(stream, false);
				}

				endElement(stream, spells.isEternal() && attr.getAttributesCount() == 0);
			}
			endElement(stream, false);

			endElement(stream, false);
		} catch (final XMLStreamException e1) {
			e1.printStackTrace();
		}
	}

	protected void checkGraphSpells() {
		if (graphSpells == null) {
			graphSpells = new GraphSpells();
		}
	}

	@Override
	public void edgeAttributeAdded(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object value) {
		checkGraphSpells();
		graphSpells.edgeAttributeAdded(sourceId, timeId, edgeId, attribute, value);
	}

	@Override
	public void edgeAttributeChanged(final String sourceId, final long timeId, final String edgeId,
			final String attribute, final Object oldValue, final Object newValue) {
		checkGraphSpells();
		graphSpells.edgeAttributeChanged(sourceId, timeId, edgeId, attribute, oldValue, newValue);
	}

	@Override
	public void edgeAttributeRemoved(final String sourceId, final long timeId, final String edgeId,
			final String attribute) {
		checkGraphSpells();
		graphSpells.edgeAttributeRemoved(sourceId, timeId, edgeId, attribute);
	}

	@Override
	public void graphAttributeAdded(final String sourceId, final long timeId, final String attribute,
			final Object value) {
		checkGraphSpells();
		graphSpells.graphAttributeAdded(sourceId, timeId, attribute, value);
	}

	@Override
	public void graphAttributeChanged(final String sourceId, final long timeId, final String attribute,
			final Object oldValue, final Object newValue) {
		checkGraphSpells();
		graphSpells.graphAttributeChanged(sourceId, timeId, attribute, oldValue, newValue);
	}

	@Override
	public void graphAttributeRemoved(final String sourceId, final long timeId, final String attribute) {
		checkGraphSpells();
		graphSpells.graphAttributeRemoved(sourceId, timeId, attribute);
	}

	@Override
	public void nodeAttributeAdded(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object value) {
		checkGraphSpells();
		graphSpells.nodeAttributeAdded(sourceId, timeId, nodeId, attribute, value);
	}

	@Override
	public void nodeAttributeChanged(final String sourceId, final long timeId, final String nodeId,
			final String attribute, final Object oldValue, final Object newValue) {
		checkGraphSpells();
		graphSpells.nodeAttributeChanged(sourceId, timeId, nodeId, attribute, oldValue, newValue);
	}

	@Override
	public void nodeAttributeRemoved(final String sourceId, final long timeId, final String nodeId,
			final String attribute) {
		checkGraphSpells();
		graphSpells.nodeAttributeRemoved(sourceId, timeId, nodeId, attribute);
	}

	@Override
	public void edgeAdded(final String sourceId, final long timeId, final String edgeId, final String fromNodeId,
			final String toNodeId, final boolean directed) {
		checkGraphSpells();
		graphSpells.edgeAdded(sourceId, timeId, edgeId, fromNodeId, toNodeId, directed);
	}

	@Override
	public void edgeRemoved(final String sourceId, final long timeId, final String edgeId) {
		checkGraphSpells();
		graphSpells.edgeRemoved(sourceId, timeId, edgeId);
	}

	@Override
	public void graphCleared(final String sourceId, final long timeId) {
		checkGraphSpells();
		graphSpells.graphCleared(sourceId, timeId);
	}

	@Override
	public void nodeAdded(final String sourceId, final long timeId, final String nodeId) {
		checkGraphSpells();
		graphSpells.nodeAdded(sourceId, timeId, nodeId);
	}

	@Override
	public void nodeRemoved(final String sourceId, final long timeId, final String nodeId) {
		checkGraphSpells();
		graphSpells.nodeRemoved(sourceId, timeId, nodeId);
	}

	@Override
	public void stepBegins(final String sourceId, final long timeId, final double step) {
		checkGraphSpells();
		graphSpells.stepBegins(sourceId, timeId, step);
	}

	class GEXFAttribute {
		int index;
		String key;
		String type;

		GEXFAttribute(final String key, final String type) {
			this.index = currentAttributeIndex++;
			this.key = key;
			this.type = type;
		}
	}

	class GEXFAttributeMap extends HashMap<String, GEXFAttribute> {
		private static final long serialVersionUID = 6176508111522815024L;
		protected String type;

		GEXFAttributeMap(final String type, final Graph g) {
			this.type = type;

			Iterable<? extends Element> iterable;

			if (type.equals("node")) {
				iterable = g.getNodeSet();
			} else {
				iterable = g.getEdgeSet();
			}

			for (final Element e : iterable) {
				for (final String key : e.getAttributeKeySet()) {
					final Object value = e.getAttribute(key);
					check(key, value);
				}
			}
		}

		GEXFAttributeMap(final String type, final GraphSpells spells) {
			this.type = type;

			if (type.equals("node")) {
				for (final String nodeId : spells.getNodes()) {
					final CumulativeAttributes attr = spells.getNodeAttributes(nodeId);

					for (final String key : attr.getAttributes()) {
						for (final Spell s : attr.getAttributeSpells(key)) {
							final Object value = s.getAttachedData();
							check(key, value);
						}
					}
				}
			} else {
				for (final String edgeId : spells.getEdges()) {
					final CumulativeAttributes attr = spells.getEdgeAttributes(edgeId);

					for (final String key : attr.getAttributes()) {
						for (final Spell s : attr.getAttributeSpells(key)) {
							final Object value = s.getAttachedData();
							check(key, value);
						}
					}
				}
			}
		}

		void check(final String key, final Object value) {
			final String id = getID(key, value);
			String attType = "string";

			if (containsKey(id)) { return; }

			if (value instanceof Integer || value instanceof Short) {
				attType = "integer";
			} else if (value instanceof Long) {
				attType = "long";
			} else if (value instanceof Float) {
				attType = "float";
			} else if (value instanceof Double) {
				attType = "double";
			} else if (value instanceof Boolean) {
				attType = "boolean";
			} else if (value instanceof URL || value instanceof URI) {
				attType = "anyURI";
			} else if (value.getClass().isArray() || value instanceof Collection) {
				attType = "liststring";
			}

			put(id, new GEXFAttribute(key, attType));
		}

		String getID(final String key, final Object value) {
			return String.format("%s@%s", key, value.getClass().getName());
		}

		void export(final XMLStreamWriter stream) throws XMLStreamException {
			if (size() == 0) { return; }

			startElement(stream, "attributes");
			stream.writeAttribute("class", type);

			for (final GEXFAttribute a : values()) {
				startElement(stream, "attribute");
				stream.writeAttribute("id", Integer.toString(a.index));
				stream.writeAttribute("title", a.key);
				stream.writeAttribute("type", a.type);
				endElement(stream, true);
			}

			endElement(stream, size() == 0);
		}

		void push(final XMLStreamWriter stream, final Element e, final String key) throws XMLStreamException {
			final String id = getID(key, e.getAttribute(key));
			final GEXFAttribute a = get(id);

			if (a == null) {
				// TODO
				return;
			}

			startElement(stream, "attvalue");
			stream.writeAttribute("for", Integer.toString(a.index));
			stream.writeAttribute("value", e.getAttribute(key).toString());
			endElement(stream, true);
		}

		void push(final XMLStreamWriter stream, final String elementId, final GraphSpells spells)
				throws XMLStreamException {
			CumulativeAttributes attr;

			if (type.equals("node")) {
				attr = spells.getNodeAttributes(elementId);
			} else {
				attr = spells.getEdgeAttributes(elementId);
			}

			for (final String key : attr.getAttributes()) {
				for (final Spell s : attr.getAttributeSpells(key)) {
					final Object value = s.getAttachedData();
					final String id = getID(key, value);
					final GEXFAttribute a = get(id);

					if (a == null) {
						// TODO
						return;
					}

					startElement(stream, "attvalue");
					stream.writeAttribute("for", Integer.toString(a.index));
					stream.writeAttribute("value", value.toString());
					putSpellAttributes(s);
					endElement(stream, true);
				}
			}
		}
	}
}
