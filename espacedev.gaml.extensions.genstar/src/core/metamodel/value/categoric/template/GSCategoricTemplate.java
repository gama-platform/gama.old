/*******************************************************************************************************
 *
 * GSCategoricTemplate.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value.categoric.template;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Objects;

/**
 * extends {@link Format} method to fit java interface requirement
 *
 * @author kevinchapuis
 *
 */
public class GSCategoricTemplate extends Format {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** The case sensitive. */
	private final boolean caseSensitive;

	/** The regex. */
	private String[] regex;

	/**
	 * Instantiates a new GS categoric template.
	 */
	public GSCategoricTemplate() {
		this(true);
	}

	/**
	 * Instantiates a new GS categoric template.
	 *
	 * @param caseSensitive
	 *            the case sensitive
	 * @param regex
	 *            the regex
	 */
	public GSCategoricTemplate(final boolean caseSensitive, final String... regex) {
		this.caseSensitive = caseSensitive;
		this.regex = regex;
	}

	/**
	 * Change parameter String value to desired nominal space format. For example if the string should be case sensitive
	 * or not, or reflect a date
	 *
	 * @param value
	 * @return
	 */
	public String getFormatedString(String string) {
		string = string.trim();
		String formatedString = caseSensitive ? string : string.toLowerCase();
		Arrays.asList(regex).stream().forEach(r -> formatedString.replaceAll(caseSensitive ? r : r.toLowerCase(), ""));
		return this.format(formatedString);
	}

	/**
	 * Sets the regex.
	 *
	 * @param regex
	 *            the new regex
	 */
	public void setRegex(final String[] regex) { this.regex = regex; }

	/**
	 * Gets the regex.
	 *
	 * @return the regex
	 */
	public String[] getRegex() { return this.regex; }

	@Override
	public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
		return toAppendTo.append(obj.toString());
	}

	@Override
	public Object parseObject(final String source, final ParsePosition pos) {
		return source;
	}

	@Override
	public int hashCode() {
		return Objects.hash(caseSensitive, Arrays.hashCode(regex));
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		GSCategoricTemplate other = (GSCategoricTemplate) obj;
		if (caseSensitive != other.caseSensitive) return false;
		return Arrays.equals(regex, other.regex);

	}

}
