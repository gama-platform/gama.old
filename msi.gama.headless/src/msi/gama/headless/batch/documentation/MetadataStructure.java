/*******************************************************************************************************
 *
 * MetadataStructure.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.headless.batch.documentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class MetadataStructure.
 */
public class MetadataStructure {

	/** The name regex. */
	private final String NAME_REGEX = "\\* Name: (.*)";
	
	/** The author regex. */
	private final String AUTHOR_REGEX = "\\* Author: (.*)";
	
	/** The description regex. */
	private final String DESCRIPTION_REGEX = "(?s)\\* Description: (.*)\\* Tags";
	
	/** The tags regex. */
	private final String TAGS_REGEX = "\\* Tags: (.*)";
	
	/** The tags separator. */
	private final String TAGS_SEPARATOR = ",";

	/** The m name. */
	private String m_name;
	
	/** The m author. */
	private String m_author;
	
	/** The m description. */
	private String m_description;
	
	/** The m tags. */
	private String[] m_tags;

	/**
	 * Instantiates a new metadata structure.
	 *
	 * @param name the name
	 * @param author the author
	 * @param description the description
	 * @param tags the tags
	 */
	public MetadataStructure(final String name, final String author, final String description, final String[] tags) {
		m_name = name;
		m_author = author;
		m_description = description;
		m_tags = tags;
	}

	/**
	 * Instantiates a new metadata structure.
	 *
	 * @param metadata the metadata
	 */
	public MetadataStructure(final String metadata) {
		try {
			computeMetadata(metadata);
		} catch (final IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Compute metadata.
	 *
	 * @param metadata the metadata
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private void computeMetadata(final String metadata) throws IllegalArgumentException, IllegalAccessException {
		final String name = findAndReturnRegex(metadata, NAME_REGEX);
		final String author = findAndReturnRegex(metadata, AUTHOR_REGEX);
		final String metadata_computed = metadata.replace("* \n", "\n");
		String description = findAndReturnRegex(metadata_computed, DESCRIPTION_REGEX);
		description = description.replaceAll("(\\n\\*(\\s|\\t)+)", ""); // replace
																		// "* "
																		// by ""
		String rawTags = findAndReturnRegex(metadata, TAGS_REGEX);
		// remove space character
		rawTags = rawTags.replace(" ", "");
		// split into tags
		String[] tags = null;
		if (!rawTags.isEmpty()) {
			rawTags = rawTags.toLowerCase();
			tags = rawTags.split(TAGS_SEPARATOR);
		}

		m_name = name;
		m_author = author;
		m_description = description;
		m_tags = tags;
	}

	/**
	 * Gets the md header.
	 *
	 * @return the md header
	 */
	public String getMdHeader() {
		String result = "";
		if (m_tags != null) {
			for (int tagIdx = 0; tagIdx < m_tags.length; tagIdx++) {
				// check if the concept exists in IConcept
				if (ConceptManager.conceptIsPossibleToAdd(m_tags[tagIdx])) {
					ConceptManager.addOccurrenceOfConcept(m_tags[tagIdx]);
					result += "[//]: # (keyword|concept_" + m_tags[tagIdx] + ")\n";
				} else {
					System.out.println("WARNING : the concept " + m_tags[tagIdx]
							+ " does not exist in the list predefined concept tags ! (in model " + m_name + ")");
				}
			}
		}
		if (m_name != null && !m_name.isEmpty())
			result += "# " + m_name + "\n\n\n";
		if (m_author != null && !m_author.isEmpty())
			result += "_Author : " + m_author + "_\n\n";
		if (m_description != null && !m_description.isEmpty())
			result += m_description + "\n\n";
		return result;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * Find and return regex.
	 *
	 * @param line the line
	 * @param regex the regex
	 * @param matchNumber the match number
	 * @return the string
	 */
	private String findAndReturnRegex(final String line, final String regex, final int matchNumber) {
		String str = "";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			str = matcher.group(matchNumber);
		}
		return str;
	}

	/**
	 * Find and return regex.
	 *
	 * @param line the line
	 * @param regex the regex
	 * @return the string
	 */
	private String findAndReturnRegex(final String line, final String regex) {
		return findAndReturnRegex(line, regex, 1);
	}
}
