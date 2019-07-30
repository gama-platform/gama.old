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

import java.util.ArrayList;
import java.util.Locale;

import msi.gama.ext.graphstream.AbstractElement.AttributeChangeEvent;
import msi.gama.ext.graphstream.SourceBase.ElementType;

public class PajekContext {
	FileSourcePajek pajek;
	String sourceId;

	protected boolean directed = false;

	protected String weightAttributeName = "weight";

	public PajekContext(final FileSourcePajek pajek) {
		this.pajek = pajek;
		this.sourceId = String.format("<Pajek stream %d>", System.currentTimeMillis());
	}

	protected void setDirected(final boolean on) {
		directed = on;
	}

	protected int addNodes(final Token nb) throws ParseException {
		final int n = getInt(nb);

		for (int i = 1; i <= n; ++i) {
			pajek.sendNodeAdded(sourceId, String.format("%d", i));
		}

		return n;
	}

	protected void addGraphAttribute(final String attr, final String value) {
		pajek.sendAttributeChangedEvent(sourceId, sourceId, ElementType.GRAPH, attr, AttributeChangeEvent.ADD, null,
				value);
	}

	protected void addNodeLabel(final String nb, final String label) {
		pajek.sendAttributeChangedEvent(sourceId, nb, ElementType.NODE, "ui.label", AttributeChangeEvent.ADD, null,
				label);
	}

	protected void addNodeGraphics(final String id, final NodeGraphics graphics) {
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.NODE, "ui.style", AttributeChangeEvent.ADD, null,
				graphics.getStyle());
	}

	protected void addNodePosition(final String id, final Token x, final Token y, final Token z) throws ParseException {
		final Object pos[] = new Object[3];
		pos[0] = getReal(x);
		pos[1] = getReal(y);
		pos[2] = z != null ? (Double) getReal(z) : 0;

		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.NODE, "xyz", AttributeChangeEvent.ADD, null, pos);
	}

	protected String addEdge(final String src, final String trg) {
		final String id =
				String.format("%s_%s_%d", src, trg, (long) (Math.random() * 100000) + System.currentTimeMillis());

		pajek.sendEdgeAdded(sourceId, id, src, trg, directed);

		return id;
	}

	protected void addEdges(final EdgeMatrix mat) {
		final int size = mat.size();
		int edgeid = 0;

		for (int line = 0; line < size; line++) {
			for (int col = 0; col < size; col++) {
				if (mat.hasEdge(line, col)) {
					final String id = String.format("%d_%d_%d", line + 1, col + 1, edgeid++);
					if (mat.hasEdge(col, line)) {
						pajek.sendEdgeAdded(sourceId, id, String.format("%d", line + 1), String.format("%d", col + 1),
								false);
						mat.set(col, line, false);
					} else {
						pajek.sendEdgeAdded(sourceId, id, String.format("%d", line + 1), String.format("%d", col + 1),
								true);
					}
				}
			}
		}
	}

	protected void addEdgeWeight(final String id, final Token nb) throws ParseException {
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.EDGE, weightAttributeName, AttributeChangeEvent.ADD,
				null, getReal(nb));
	}

	protected void addEdgeGraphics(final String id, final EdgeGraphics graphics) {
		pajek.sendAttributeChangedEvent(sourceId, id, ElementType.EDGE, "ui.style", AttributeChangeEvent.ADD, null,
				graphics.getStyle());
	}

	protected static int getInt(final Token nb) throws ParseException {
		try {
			return Integer.parseInt(nb.image);
		} catch (final Exception e) {
			throw new ParseException(String.format("%d:%d: %s not an integer", nb.beginLine, nb.beginColumn, nb.image));
		}
	}

	protected static double getReal(final Token nb) throws ParseException {
		try {
			return Double.parseDouble(nb.image);
		} catch (final Exception e) {
			throw new ParseException(String.format("%d:%d: %s not a real", nb.beginLine, nb.beginColumn, nb.image));
		}
	}

	public static String toColorValue(final Token R, final Token G, final Token B) throws ParseException {
		final double r = getReal(R);
		final double g = getReal(G);
		final double b = getReal(B);

		return String.format("rgb(%d, %d, %d)", (int) (r * 255), (int) (g * 255), (int) (b * 255));
	}
}

abstract class Graphics {
	protected StringBuffer graphics = new StringBuffer();

	public abstract void addKey(String key, String value, Token tk) throws ParseException;

	public String getStyle() {
		return graphics.toString();
	}

	protected double getReal(final String nb, final Token tk) throws ParseException {
		try {
			return Double.parseDouble(nb);
		} catch (final Exception e) {
			throw new ParseException(String.format("%d:%d: %s not a real", tk.beginLine, tk.beginColumn, nb));
		}
	}

	protected int getInt(final String nb, final Token tk) throws ParseException {
		try {
			return Integer.parseInt(nb);
		} catch (final Exception e) {
			throw new ParseException(String.format("%d:%d: %s not an integer", tk.beginLine, tk.beginColumn, nb));
		}
	}
}

class NodeGraphics extends Graphics {
	@Override
	public void addKey(final String key, final String value, final Token tk) throws ParseException {
		if (key.equals("shape")) {
			graphics.append(String.format("shape: %s;", value));
		} else if (key.equals("ic")) {
			graphics.append(String.format("fill-color: %s;", value));
		} else if (key.equals("bc")) {
			graphics.append(String.format("stroke-color: %s; stroke-mode: plain;", value));
		} else if (key.equals("bw")) {
			graphics.append(String.format(Locale.US, "stroke-width: %fpx;", getReal(value, tk)));
		} else if (key.equals("s_size")) {
			graphics.append(String.format(Locale.US, "size: %fpx;", getReal(value, tk)));
		} else if (key.equals("lc")) {
			graphics.append(String.format("text-color: %s;", value));
		} else if (key.equals("fos")) {
			graphics.append(String.format("text-size: %d;", getInt(value, tk)));
		} else if (key.equals("font")) {
			graphics.append(String.format("text-font: %s;", value));
		}
	}
}

class EdgeGraphics extends Graphics {
	@Override
	public void addKey(final String key, final String value, final Token tk) throws ParseException {
		if (key.equals("w")) {
			graphics.append(String.format(Locale.US, "size: %fpx;", getReal(value, tk)));
		} else if (key.equals("c")) {
			graphics.append(String.format("fill-color: %s;", value));
		} else if (key.equals("s")) {
			final double s = getReal(value, tk);
			graphics.append(String.format("arrow-size: %spx, %spx;", s * 5, s * 3));
		} else if (key.equals("l")) {
			// ?
		} else if (key.equals("p")) {
			// ?
		} else if (key.equals("lc")) {
			graphics.append(String.format("text-color: %s;", value));
		} else if (key.equals("fos")) {
			graphics.append(String.format("text-size: %d;", getInt(value, tk)));
		} else if (key.equals("font")) {
			graphics.append(String.format("text-font: %s;", value));
		}
	}
}

class EdgeMatrix {
	// Line first, col second.
	// Line = from node, col = to node.
	protected boolean mat[][];

	protected int curLine = 0;

	public EdgeMatrix(final int size) {
		mat = new boolean[size][size]; // Horror !
	}

	public int size() {
		return mat.length;
	}

	public boolean hasEdge(final int line, final int col) {
		return mat[line][col];
	}

	public void set(final int line, final int col, final boolean value) {
		mat[line][col] = value;
	}

	public void addLine(final ArrayList<String> line) {
		if (curLine < mat.length) {
			if (line.size() == mat.length) {
				for (int i = 0; i < mat.length; i++) {
					mat[curLine][i] = line.get(i).equals("1");
				}
				curLine++;
			} else if (line.size() == mat.length * mat.length) {
				final int n = mat.length * mat.length;
				curLine = -1;
				for (int i = 0; i < n; i++) {
					if (i % mat.length == 0) {
						curLine++;
					}
					mat[curLine][i - curLine * mat.length] = line.get(i).equals("1");
				}
			}
		}
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		for (final boolean[] element : mat) {
			for (int col = 0; col < mat.length; col++) {
				buffer.append(String.format("%s ", element[col] ? "1" : "0"));
			}
			buffer.append(String.format("%n"));
		}

		return buffer.toString();
	}
}
