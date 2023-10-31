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
 * An immutable object that represents a location in the parsed text.
 */
public class Location {

	/**
	 * The absolute character index, starting at 0.
	 */
	public final int offset;

	/**
	 * The line number, starting at 1.
	 */
	public final int line;

	/**
	 * The column number, starting at 1.
	 */
	public final int column;

	/**
	 * Instantiates a new location.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param offset
	 *            the absolute character index, starting at 0.
	 * @param line
	 *            the line number, starting at 1.
	 * @param column
	 *            the column number, starting at 1.
	 * @date 29 oct. 2023
	 */
	Location(final int offset, final int line, final int column) {
		this.offset = offset;
		this.column = column;
		this.line = line;
	}

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
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		Location other = (Location) obj;
		return offset == other.offset && column == other.column && line == other.line;
	}

}