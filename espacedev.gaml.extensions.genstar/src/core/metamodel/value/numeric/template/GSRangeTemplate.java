/*******************************************************************************************************
 *
 * GSRangeTemplate.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.metamodel.value.numeric.template;

import java.util.stream.Stream;

import core.util.data.GSDataParser.NumMatcher;
import core.util.exception.GenstarException;

/**
 * Define how a string should be parsed to formated range data string
 *
 * @author kevinchapuis
 *
 */
public class GSRangeTemplate {

	/** The top bound. */
	private final String bottomBound;
	
	/** The middle. */
	private final String middle;
	
	/** The top bound. */
	private final String topBound;

	/** The match. */
	private final String match;

	/** The num matcher. */
	private final NumMatcher numMatcher;

	/**
	 * Instantiates a new GS range template.
	 *
	 * @param lowerBound
	 *            the lower bound
	 * @param middle
	 *            the middle
	 * @param upperBound
	 *            the upper bound
	 * @param match
	 *            the match
	 * @param numMatcher
	 *            the num matcher
	 */
	public GSRangeTemplate(final String lowerBound, final String middle, final String upperBound, final String match,
			final NumMatcher numMatcher) {
		this.match = match;
		this.middle = middle;
		this.bottomBound = lowerBound;
		this.topBound = upperBound;
		this.numMatcher = numMatcher;
	}

	/**
	 * Process lower and upper numeric bounds to obtain a formated String representation
	 *
	 * @param low
	 * @param up
	 * @return
	 */
	public String getMiddleTemplate(final Number low, final Number up) {
		return middle.replaceFirst(match, low.toString()).replaceFirst(match, up.toString());
	}

	/**
	 * Process the lone bottom bound (smallest number) to obtain a formated String representation
	 *
	 * @param up
	 * @return
	 */
	public String getBottomTemplate(final Number bottom) {
		return bottomBound.replaceFirst(match, bottom.toString());
	}

	/**
	 * Process the lone top bound (largest number) to obtain a formated String representation
	 *
	 * @param low
	 * @return
	 */
	public String getTopTemplate(final Number top) {
		return topBound.replaceFirst(match, top.toString());
	}

	/**
	 * Process the input string {@code value} to determine if it complies with this template of range data
	 *
	 * @param value
	 * @return
	 */
	public boolean isValideRangeCandidate(final String value) {
		String valueTemplate = value.replaceAll(numMatcher.getMatch(), match);
		return Stream.of(bottomBound, middle, topBound).anyMatch(template -> template.equals(valueTemplate));
	}

	/**
	 * Enum type that define matcher for numerical value to parse in String
	 *
	 * @return
	 */
	public NumMatcher getNumberMatcher() { return numMatcher; }

	/**
	 * Get the minimum for this matcher
	 *
	 * @return
	 */
	public Number getTheoreticalMin() {
		return switch (numMatcher) {
			case DOUBLE_MATCH_ENG, DOUBLE_MATCH_FR -> Double.MIN_VALUE;
			case DOUBLE_POSITIF_MATCH_ENG, DOUBLE_POSITIF_MATCH_FR -> 0d;
			case INT_MATCH -> Integer.MIN_VALUE;
			case INT_POSITIF_MATCH -> 0;
			default -> throw new GenstarException();
		};
	}

	/**
	 * Get the maximum for this matcher
	 *
	 * @return
	 */
	public Number getTheoreticalMax() {
		return switch (numMatcher) {
			case DOUBLE_MATCH_ENG, DOUBLE_MATCH_FR, DOUBLE_POSITIF_MATCH_ENG, DOUBLE_POSITIF_MATCH_FR -> Double.MAX_VALUE;
			case INT_MATCH, INT_POSITIF_MATCH -> Integer.MAX_VALUE;
			default -> throw new GenstarException();
		};
	}

	@Override
	public String toString() {
		return "Template: {" + bottomBound + " ... " + middle + " ... " + topBound + "}";
	}

}
