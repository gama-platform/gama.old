/*******************************************************************************************************
 *
 * PrettyPrint.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * Enables human readable JSON output by inserting whitespace between values.after commas and colons. Example:
 *
 * <pre>
 * jsonValue.writeTo(writer, PrettyPrint.singleLine());
 * </pre>
 */
public class PrettyPrint extends WriterConfig {

	/** The indent chars. */
	private final char[] indentChars;

	/**
	 * Instantiates a new pretty print.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param indentChars
	 *            the indent chars
	 * @date 29 oct. 2023
	 */
	protected PrettyPrint(final char[] indentChars) {
		this.indentChars = indentChars;
	}

	/**
	 * Print every value on a separate line. Use tabs (<code>\t</code>) for indentation.
	 *
	 * @return A PrettyPrint instance for wrapped mode with tab indentation
	 */
	public static PrettyPrint singleLine() {
		return new PrettyPrint(null);
	}

	/**
	 * Print every value on a separate line. Use the given number of spaces for indentation.
	 *
	 * @param number
	 *            the number of spaces to use
	 * @return A PrettyPrint instance for wrapped mode with spaces indentation
	 */
	public static PrettyPrint indentWithSpaces(final int number) {
		if (number < 0) throw new IllegalArgumentException("number is negative");
		char[] chars = new char[number];
		Arrays.fill(chars, ' ');
		return new PrettyPrint(chars);
	}

	/**
	 * Do not break lines, but still insert whitespace between values.
	 *
	 * @return A PrettyPrint instance for single-line mode
	 */
	public static PrettyPrint indentWithTabs() {
		return new PrettyPrint(new char[] { '\t' });
	}

	@Override
	protected JsonWriter createWriter(final Writer writer) {
		return new PrettyPrintWriter(writer, indentChars);
	}

	/**
	 * The Class PrettyPrintWriter.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 29 oct. 2023
	 */
	private static class PrettyPrintWriter extends JsonWriter {

		/** The indent chars. */
		private final char[] indentChars;

		/** The indent. */
		private int indent;

		/**
		 * Instantiates a new pretty print writer.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param writer
		 *            the writer
		 * @param indentChars
		 *            the indent chars
		 * @date 29 oct. 2023
		 */
		private PrettyPrintWriter(final Writer writer, final char[] indentChars) {
			super(writer);
			this.indentChars = indentChars;
		}

		@Override
		protected void writeArrayOpen() throws IOException {
			indent++;
			writer.write('[');
			writeNewLine();
		}

		@Override
		protected void writeArrayClose() throws IOException {
			indent--;
			writeNewLine();
			writer.write(']');
		}

		@Override
		protected void writeArraySeparator() throws IOException {
			writer.write(',');
			if (!writeNewLine()) { writer.write(' '); }
		}

		@Override
		protected void writeObjectOpen() throws IOException {
			indent++;
			writer.write('{');
			writeNewLine();
		}

		@Override
		protected void writeObjectClose() throws IOException {
			indent--;
			writeNewLine();
			writer.write('}');
		}

		@Override
		protected void writeMemberSeparator() throws IOException {
			writer.write(':');
			writer.write(' ');
		}

		@Override
		protected void writeObjectSeparator() throws IOException {
			writer.write(',');
			if (!writeNewLine()) { writer.write(' '); }
		}

		/**
		 * Write new line.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @return true, if successful
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @date 29 oct. 2023
		 */
		private boolean writeNewLine() throws IOException {
			if (indentChars == null) return false;
			writer.write('\n');
			for (int i = 0; i < indent; i++) { writer.write(indentChars); }
			return true;
		}

	}

}
