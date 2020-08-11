/*********************************************************************************************
 * 
 *
 * 'Constants.java', in plugin 'msi.gama.documentation', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.precompiler.doc.utils;

import java.io.File;

public class Constants {
	
	public final static String GAMA_VERSION  = "1.8.1";

	// Commandes
	public final static String CMD_PANDOC =
			OSUtils.isWindows() ? "C:/pandoc/pandoc.exe" : "/usr/local/bin/pandoc";
	public final static String CMD_PDFLATEX = OSUtils.isWindows() ? "\"C:/MiKTeX 2.9/miktex/bin/x64/pdflatex.exe\""
			: "/Users/benoit/bin/pdflatex";
	// "/Library/TeX/Root/bin/universal-darwin/pdflatex";
	
	// Note : need to make change in mytemplate.tex to access the write style file ..... 
		
	
	//
	public final static String RELEASE_APPLICATION = "ummisco.gama.product";
	public final static String RELEASE_PRODUCT = "gama.runtime.product";

	// Repositories containing used files

	public final static String BASE_FOLDER 	= "";
	public final static String SRC_FOLDER 	= BASE_FOLDER + "files";

	public final static String GEN_FOLDER 		= SRC_FOLDER + File.separator + "gen";
	public final static String INPUT_FOLDER 	= SRC_FOLDER + File.separator + "input";
	public final static String TEST_FOLDER		= SRC_FOLDER + File.separator + ".." + File.separator + ".." + File.separator
			+ "msi.gama.models" + File.separator + "models" + File.separator + "Tests";


	public final static String WIKI_FOLDER = SRC_FOLDER + File.separator + ".." + File.separator 
			+ ".." + File.separator + ".." + File.separator + "gama.wiki";
//	public static String WIKI_FOLDER 	= "C:/git/gama.wiki";

	// Generation folders
	public final static String WIKI_FOLDER_EXT 			= WIKI_FOLDER + File.separator + "References";	
	public final static String WIKI_FOLDER_REF 			= WIKI_FOLDER + File.separator + "References" + File.separator + "GAMLReferences";
	public final static String WIKI_FOLDER_WIKI_ONLY 	= WIKI_FOLDER + File.separator + "WikiOnly";
	public final static String PATH_TO_KEYWORDS_XML 	= WIKI_FOLDER + File.separator + "keywords.xml";
	
	public final static String WIKI_FOLDER_EXT_PLUGIN 	= WIKI_FOLDER_EXT + File.separator + "PluginDocumentation";
	public final static String XML2WIKI_FOLDER 			= WIKI_FOLDER_REF;

	public final static String JAVA2XML_FOLDER 			= GEN_FOLDER + File.separator + "java2xml";
	public final static String PDF_FOLDER 				= GEN_FOLDER + File.separator + "pdf";
	public final static String TOC_GEN_FOLDER 			= GEN_FOLDER + File.separator + "toc2pdf";
	public final static String XML_KEYWORD_GEN_FOLDER 	= GEN_FOLDER + File.separator + "xmlKeywords";
	public final static String CATALOG_GEN_FOLDER 		= GEN_FOLDER + File.separator + "catalog";

	public final static String TOC_FILE_PATH  			= TOC_GEN_FOLDER + File.separatorChar +  "toc"+GAMA_VERSION+".xml";


	// Inputs Folders
	public final static String XSL_XML2WIKI_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2md";
	public final static String XSL_XML2JSON_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2json";
	public final static String XSL_XML2CATALOG_FOLDER 		= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2catalog";	
	public final static String XSL_XML2TEST_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2test";
	public final static String XSL_XML2KEYWORDS_XML_FOLDER 	= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2keywordsXml";
	public final static String PANDOC_FOLDER 					= INPUT_FOLDER + File.separator + "pandocPDF";

	public final static String DOCGAMA_FILE 		= "target" + File.separator + "docGAMA.xml";
	public final static String DOCGAMA_FILE_LOCAL 	= "gaml" + File.separator + "docGAMA.xml";
	public final static String DOCGAMA_GLOBAL_FILE 	= JAVA2XML_FOLDER + File.separator + "docGAMAglobal.xml";

	public final static String DOCGAMA_PDF 			= PDF_FOLDER + File.separator + "docGAMAv"+GAMA_VERSION+".pdf";
	public final static String TOC_FILE 			= INPUT_FOLDER + File.separator + "toc" + File.separator + "toc"+GAMA_VERSION+".xml";
	public final static String MD_BLANK_PAGE 		= "G__BlankPage.md"; // Blank page is directly in the wiki folder
	public final static String TOC_SIDEBAR_FILE		= WIKI_FOLDER + File.separator + "_Sidebar.md";

	// Tests
	public final static String TEST_PLUGIN_FOLDER 	= "tests";
	public final static String TEST_PLUGIN_GEN_FOLDER = TEST_PLUGIN_FOLDER + File.separator + "Generated";
	public final static String TEST_PLUGIN_GEN_MODELS = TEST_PLUGIN_GEN_FOLDER + File.separator + "models";

	public final static String TEST_OPERATORS_FOLDER 	= "Operators";
	public final static String TEST_STATEMENTS_FOLDER = "Statements";

	public final static String PROJECT_FILE = INPUT_FOLDER + File.separator + "project" + File.separator + ".project";

	// Path
	public final static String PATH = "PATH=/usr/local/bin/:${PATH}";
	
}
