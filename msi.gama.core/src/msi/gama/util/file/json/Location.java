/*******************************************************************************************************
 *
 * Location.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

/**
 * An immutable object that represents a location in the parsed text.The absolute character index, starting at 0.The
 * line number, starting at 1.The column number, starting at 1.
 */
public record Location(int offset, int line, int column) {

	@Override
	public String toString() {
		return line + ":" + column;
	}

	@Override
	public int hashCode() {
		return offset;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		return obj instanceof Location other && offset == other.offset && column == other.column && line == other.line;
	}

}