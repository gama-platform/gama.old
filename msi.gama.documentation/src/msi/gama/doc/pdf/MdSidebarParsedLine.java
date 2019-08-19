package msi.gama.doc.pdf;

import ummisco.gama.dev.utils.DEBUG;

public class MdSidebarParsedLine {
	
	EntryLevel level;
	
	String entryName;
	String fileName;
	
	public enum EntryLevel {
		TITLE, OTHER
	}
	
	public MdSidebarParsedLine(String mdLine) {
		String remainingLine = mdLine;

		// ## []()
		remainingLine = parseEntryLevel(remainingLine);		

		// []()
		remainingLine = parseEntryName(remainingLine);	
		
		// ()		
		remainingLine = parseFileName(remainingLine);
	}
	
	public String getEntryName() {
		return entryName;
	}

	public String getFileName() {
		return fileName;
	}	
	
	public EntryLevel getLevel() {
		return level;
	}
	
	private String parseEntryLevel(String remainingLine) {
		int nbHashTag = 0;
		while (remainingLine.startsWith("#")) {
			nbHashTag++;
			remainingLine = remainingLine.substring(1);
		}
		if(nbHashTag > 0) {
			level = EntryLevel.TITLE;
		} else {
			level = EntryLevel.OTHER;
		}
		
		return remainingLine;
	}
	
	private String parseEntryName(String remainingLine) {
		final int openBracket = remainingLine.indexOf('[');
		final int closeBracket = remainingLine.indexOf(']');
		
		if (openBracket > -1 ) {
			entryName = remainingLine.substring(openBracket + 1, closeBracket);
		}			
		
		return remainingLine.substring(closeBracket + 1);
	}
	
	private String parseFileName(String remainingLine) {		
		final int openParenth = remainingLine.indexOf('(');
		final int closeParenth = remainingLine.lastIndexOf(')');
		
		if (openParenth > -1) {
			fileName = remainingLine.substring(openParenth + 1, closeParenth) + ".md";			
		}	
		return remainingLine.substring(closeParenth + 1);	
	}
	
	public String toString() {
		return "EntryLevel " + level + " -- EntryName: " + entryName + " -- FileName: " + fileName;
	}
	
	public static void main(String[] argc) {
		DEBUG.LOG(new MdSidebarParsedLine("## [My paragraph](Hello)"));
		DEBUG.LOG(new MdSidebarParsedLine(" 1. (Hello)"));
		
	}
}
