package msi.ummisco.modelLibraryGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MetadataStructure {
	
	private String NAME_REGEX = "\\* Name: (.*)";
	private String AUTHOR_REGEX = "\\* Author: (.*)";
	private String DESCRIPTION_REGEX = "(?s)\\* Description: (.*)\\* Tags";
	private String TAGS_REGEX = "\\* Tags: (.*)";
	private String TAGS_SEPARATOR = ",";
	
	private String m_name;
	private String m_author;
	private String m_description;
	private String[] m_tags;
	
	public MetadataStructure(String name, String author, String description, String[] tags) {
		m_name = name;
		m_author = author;
		m_description = description;
		m_tags = tags;
	}
	
	public MetadataStructure(String metadata) {
		try {
			computeMetadata(metadata);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void computeMetadata(String metadata) throws IllegalArgumentException, IllegalAccessException {
		String name = findAndReturnRegex(metadata,NAME_REGEX);
		String author = findAndReturnRegex(metadata,AUTHOR_REGEX);
		String metadata_computed = metadata.replace("* \n", "\n");
		String description = findAndReturnRegex(metadata_computed,DESCRIPTION_REGEX);
		description = description.replaceAll("(\\n\\*(\\s|\\t)+)", ""); // replace "* " by ""
		String rawTags = findAndReturnRegex(metadata,TAGS_REGEX);
		// remove space character
		rawTags = rawTags.replace(" ", "");
		// split into tags
		String[] tags = null;
		if (rawTags != "")
		{
			rawTags = rawTags.toLowerCase();
			tags = rawTags.split(TAGS_SEPARATOR);
		}
		
		m_name = name;
		m_author = author;
		m_description = description;
		m_tags = tags;
	}
	
	public String getMdHeader() {
		String result = "";
		if (m_tags != null)
		{
			for (int tagIdx = 0 ; tagIdx < m_tags.length ; tagIdx++) {
				// check if the concept exists in IConcept
				if (ConceptManager.conceptIsPossibleToAdd(m_tags[tagIdx])) {
					ConceptManager.addOccurrenceOfConcept(m_tags[tagIdx]);
					result += "[//]: # (keyword|concept_"+m_tags[tagIdx]+")\n";
				}
				else {
					System.out.println("WARNING : the concept "+m_tags[tagIdx]+" does not exist in the list predefined concept tags ! (in model "+m_name+")");
				}
			}
		}
		if (m_name != "")
			result += "# "+m_name+"\n\n\n";
		if (m_author != "")
			result += "_Author : "+m_author+"_\n\n";
		if (m_description != "")
			result += m_description+"\n\n";
		return result;
	}
	
	public String getName() {
		return m_name;
	}
	
	private String findAndReturnRegex(String line, String regex, int matchNumber) {
		String str = "";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			str = matcher.group(matchNumber);
		}
		return str;
	}
	
	private String findAndReturnRegex(String line, String regex)
	{
		return findAndReturnRegex(line,regex,1);
	}
}
