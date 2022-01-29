/*******************************************************************************************************
 *
 * Constants.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler.doc.utils;

import java.io.File;

/**
 * The Class Constants.
 */
public class Constants {
	
	/** The Constant GAMA_VERSION. */
	public final static String GAMA_VERSION  = "1.8.1";

	/** The Constant CMD_PANDOC. */
	// Commandes
	public final static String CMD_PANDOC =
			OSUtils.isWindows() ? "C:/pandoc/pandoc.exe" : "/usr/local/bin/pandoc";
	
	/** The Constant CMD_PDFLATEX. */
	public final static String CMD_PDFLATEX = OSUtils.isWindows() ? "\"C:/MiKTeX 2.9/miktex/bin/x64/pdflatex.exe\""
			: "/Users/benoit/bin/pdflatex";
	// "/Library/TeX/Root/bin/universal-darwin/pdflatex";
	
	// Note : need to make change in mytemplate.tex to access the write style file ..... 
		
	
	/** The Constant RELEASE_APPLICATION. */
	//
	public final static String RELEASE_APPLICATION = "ummisco.gama.product";
	
	/** The Constant RELEASE_PRODUCT. */
	public final static String RELEASE_PRODUCT = "gama.runtime.product";

	// Repositories containing used files

	/** The Constant BASE_FOLDER. */
	public final static String BASE_FOLDER 	= "";
	
	/** The Constant SRC_FOLDER. */
	public final static String SRC_FOLDER 	= BASE_FOLDER + "files";

	/** The Constant GEN_FOLDER. */
	public final static String GEN_FOLDER 		= SRC_FOLDER + File.separator + "gen";
	
	/** The Constant INPUT_FOLDER. */
	public final static String INPUT_FOLDER 	= SRC_FOLDER + File.separator + "input";
	
	/** The Constant TEST_FOLDER. */
	public final static String TEST_FOLDER		= SRC_FOLDER + File.separator + ".." + File.separator + ".." + File.separator
			+ "msi.gama.models" + File.separator + "models" + File.separator + "Tests";


	/** The Constant WIKI_FOLDER. */
	public final static String WIKI_FOLDER = SRC_FOLDER + File.separator + ".." + File.separator 
			+ ".." + File.separator + ".." + File.separator + "gama.wiki";
//	public static String WIKI_FOLDER 	= "C:/git/gama.wiki";

	/** The Constant WIKI_FOLDER_EXT. */
// Generation folders
	public final static String WIKI_FOLDER_EXT 			= WIKI_FOLDER + File.separator + "References";	
	
	/** The Constant WIKI_FOLDER_REF. */
	public final static String WIKI_FOLDER_REF 			= WIKI_FOLDER + File.separator + "References" + File.separator + "GAMLReferences";
	
	/** The Constant WIKI_FOLDER_WIKI_ONLY. */
	public final static String WIKI_FOLDER_WIKI_ONLY 	= WIKI_FOLDER + File.separator + "WikiOnly";
	
	/** The Constant PATH_TO_KEYWORDS_XML. */
	public final static String PATH_TO_KEYWORDS_XML 	= WIKI_FOLDER + File.separator + "keywords.xml";
	
	/** The Constant WIKI_FOLDER_EXT_PLUGIN. */
	public final static String WIKI_FOLDER_EXT_PLUGIN 	= WIKI_FOLDER_EXT + File.separator + "PluginDocumentation";
	
	/** The Constant XML2WIKI_FOLDER. */
	public final static String XML2WIKI_FOLDER 			= WIKI_FOLDER_REF;

	/** The Constant JAVA2XML_FOLDER. */
	public final static String JAVA2XML_FOLDER 			= GEN_FOLDER + File.separator + "java2xml";
	
	/** The Constant PDF_FOLDER. */
	public final static String PDF_FOLDER 				= GEN_FOLDER + File.separator + "pdf";
	
	/** The Constant TOC_GEN_FOLDER. */
	public final static String TOC_GEN_FOLDER 			= GEN_FOLDER + File.separator + "toc2pdf";
	
	/** The Constant XML_KEYWORD_GEN_FOLDER. */
	public final static String XML_KEYWORD_GEN_FOLDER 	= GEN_FOLDER + File.separator + "xmlKeywords";
	
	/** The Constant CATALOG_GEN_FOLDER. */
	public final static String CATALOG_GEN_FOLDER 		= GEN_FOLDER + File.separator + "catalog";

	/** The Constant TOC_FILE_PATH. */
	public final static String TOC_FILE_PATH  			= TOC_GEN_FOLDER + File.separatorChar +  "toc"+GAMA_VERSION+".xml";


	/** The Constant XSL_XML2WIKI_FOLDER. */
	// Inputs Folders
	public final static String XSL_XML2WIKI_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2md";
	
	/** The Constant XSL_XML2JSON_FOLDER. */
	public final static String XSL_XML2JSON_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2json";
	
	/** The Constant XSL_XML2CATALOG_FOLDER. */
	public final static String XSL_XML2CATALOG_FOLDER 		= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2catalog";	
	
	/** The Constant XSL_XML2TEST_FOLDER. */
	public final static String XSL_XML2TEST_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2test";
	
	/** The Constant XSL_XML2KEYWORDS_XML_FOLDER. */
	public final static String XSL_XML2KEYWORDS_XML_FOLDER 	= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2keywordsXml";
	
	/** The Constant PANDOC_FOLDER. */
	public final static String PANDOC_FOLDER 					= INPUT_FOLDER + File.separator + "pandocPDF";

	/** The Constant DOCGAMA_FILE. */
	public final static String DOCGAMA_FILE 		= "target" + File.separator + "docGAMA.xml";
	
	/** The Constant DOCGAMA_FILE_LOCAL. */
	public final static String DOCGAMA_FILE_LOCAL 	= "gaml" + File.separator + "docGAMA.xml";
	
	/** The Constant DOCGAMA_GLOBAL_FILE. */
	public final static String DOCGAMA_GLOBAL_FILE 	= JAVA2XML_FOLDER + File.separator + "docGAMAglobal.xml";

	/** The Constant DOCGAMA_PDF. */
	public final static String DOCGAMA_PDF 			= PDF_FOLDER + File.separator + "docGAMAv"+GAMA_VERSION+".pdf";
	
	/** The Constant TOC_FILE. */
	public final static String TOC_FILE 			= INPUT_FOLDER + File.separator + "toc" + File.separator + "toc"+GAMA_VERSION+".xml";
	
	/** The Constant MD_BLANK_PAGE. */
	public final static String MD_BLANK_PAGE 		= "G__BlankPage.md"; // Blank page is directly in the wiki folder
	
	/** The Constant TOC_SIDEBAR_FILE. */
	public final static String TOC_SIDEBAR_FILE		= WIKI_FOLDER + File.separator + "_Sidebar.md";

	/** The Constant TEST_PLUGIN_FOLDER. */
	// Tests
	public final static String TEST_PLUGIN_FOLDER 	= "tests";
	
	/** The Constant TEST_PLUGIN_GEN_FOLDER. */
	public final static String TEST_PLUGIN_GEN_FOLDER = TEST_PLUGIN_FOLDER + File.separator + "Generated";
	
	/** The Constant TEST_PLUGIN_GEN_MODELS. */
	public final static String TEST_PLUGIN_GEN_MODELS = TEST_PLUGIN_GEN_FOLDER + File.separator + "models";

	/** The Constant TEST_OPERATORS_FOLDER. */
	public final static String TEST_OPERATORS_FOLDER 	= "Operators";
	
	/** The Constant TEST_STATEMENTS_FOLDER. */
	public final static String TEST_STATEMENTS_FOLDER = "Statements";

	/** The Constant PROJECT_FILE. */
	public final static String PROJECT_FILE = INPUT_FOLDER + File.separator + "project" + File.separator + ".project";

	/** The Constant PATH. */
	// Path
	public final static String PATH = "PATH=/usr/local/bin/:${PATH}";
	
}
