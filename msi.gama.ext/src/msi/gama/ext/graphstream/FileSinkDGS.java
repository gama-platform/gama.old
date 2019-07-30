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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * File output for the DGS (Dynamic Graph Stream) file format.
 */
public class FileSinkDGS extends FileSinkBase {
	// Attribute

	/**
	 * A shortcut to the output.
	 */
	protected PrintWriter out;

	protected String graphName = "";

	// Command

	@Override
	protected void outputHeader() throws IOException {
		out = (PrintWriter) output;

		out.printf("DGS004%n");

		if (graphName.length() <= 0) {
			out.printf("null 0 0%n");
		} else {
			out.printf("\"%s\" 0 0%n", FileSinkDGSUtility.formatStringForQuoting(graphName));
		}
	}

	@Override
	protected void outputEndOfFile() throws IOException {
		// NOP
	}

	@Override
	public void edgeAttributeAdded(final String graphId, final long timeId, final String edgeId, final String attribute,
			final Object value) {
		edgeAttributeChanged(graphId, timeId, edgeId, attribute, null, value);
	}

	@Override
	public void edgeAttributeChanged(final String graphId, final long timeId, final String edgeId,
			final String attribute, final Object oldValue, final Object newValue) {
		out.printf("ce \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(edgeId),
				FileSinkDGSUtility.attributeString(attribute, newValue, false));
	}

	@Override
	public void edgeAttributeRemoved(final String graphId, final long timeId, final String edgeId,
			final String attribute) {
		out.printf("ce \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(edgeId),
				FileSinkDGSUtility.attributeString(attribute, null, true));
	}

	@Override
	public void graphAttributeAdded(final String graphId, final long timeId, final String attribute,
			final Object value) {
		graphAttributeChanged(graphId, timeId, attribute, null, value);
	}

	@Override
	public void graphAttributeChanged(final String graphId, final long timeId, final String attribute,
			final Object oldValue, final Object newValue) {
		out.printf("cg %s%n", FileSinkDGSUtility.attributeString(attribute, newValue, false));
	}

	@Override
	public void graphAttributeRemoved(final String graphId, final long timeId, final String attribute) {
		out.printf("cg %s%n", FileSinkDGSUtility.attributeString(attribute, null, true));
	}

	@Override
	public void nodeAttributeAdded(final String graphId, final long timeId, final String nodeId, final String attribute,
			final Object value) {
		nodeAttributeChanged(graphId, timeId, nodeId, attribute, null, value);
	}

	@Override
	public void nodeAttributeChanged(final String graphId, final long timeId, final String nodeId,
			final String attribute, final Object oldValue, final Object newValue) {
		out.printf("cn \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(nodeId),
				FileSinkDGSUtility.attributeString(attribute, newValue, false));
	}

	@Override
	public void nodeAttributeRemoved(final String graphId, final long timeId, final String nodeId,
			final String attribute) {
		out.printf("cn \"%s\" %s%n", FileSinkDGSUtility.formatStringForQuoting(nodeId),
				FileSinkDGSUtility.attributeString(attribute, null, true));
	}

	@Override
	public void edgeAdded(final String graphId, final long timeId, final String edgeIdOriginal,
			final String fromNodeIdOriginal, final String toNodeIdOriginal, final boolean directed) {
		final String edgeId = FileSinkDGSUtility.formatStringForQuoting(edgeIdOriginal);
		final String fromNodeId = FileSinkDGSUtility.formatStringForQuoting(fromNodeIdOriginal);
		final String toNodeId = FileSinkDGSUtility.formatStringForQuoting(toNodeIdOriginal);

		out.printf("ae \"%s\" \"%s\" %s \"%s\"%n", edgeId, fromNodeId, directed ? ">" : "", toNodeId);
	}

	@Override
	public void edgeRemoved(final String graphId, final long timeId, final String edgeId) {
		out.printf("de \"%s\"%n", FileSinkDGSUtility.formatStringForQuoting(edgeId));
	}

	@Override
	public void graphCleared(final String graphId, final long timeId) {
		out.printf("cl%n");
	}

	@Override
	public void nodeAdded(final String graphId, final long timeId, final String nodeId) {
		out.printf("an \"%s\"%n", FileSinkDGSUtility.formatStringForQuoting(nodeId));
	}

	@Override
	public void nodeRemoved(final String graphId, final long timeId, final String nodeId) {
		out.printf("dn \"%s\"%n", FileSinkDGSUtility.formatStringForQuoting(nodeId));
	}

	@Override
	public void stepBegins(final String graphId, final long timeId, final double step) {
		out.printf(Locale.US, "st %f%n", step);
	}
}