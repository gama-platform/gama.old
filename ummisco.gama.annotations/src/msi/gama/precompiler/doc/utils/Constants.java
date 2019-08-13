/*********************************************************************************************
 * 
 *
 * 'Constants.java', in plugin 'msi.gama.documentation', is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.precompiler.doc.utils;

import java.io.File;

public class Constants {

	//
	public static String RELEASE_APPLICATION = "ummisco.gama.product";
	public static String RELEASE_PRODUCT = "gama.runtime.product";

	// Repositories containing used files

	public static String BASE_FOLDER 	= "";
	public static String SRC_FOLDER 	= BASE_FOLDER + "files";

	public static String GEN_FOLDER 	= SRC_FOLDER + File.separator + "gen";
	public static String INPUT_FOLDER 	= SRC_FOLDER + File.separator + "input";
	public static String TEST_FOLDER	= SRC_FOLDER + File.separator + ".." + File.separator + ".." + File.separator
			+ "msi.gama.models" + File.separator + "models" + File.separator + "Tests";


	public static String WIKI_FOLDER = SRC_FOLDER + File.separator + ".." + File.separator 
			+ ".." + File.separator + ".." + File.separator + "gama.wiki";
//	public static String WIKI_FOLDER 	= "C:/git/gama.wiki";

	// Generation folders
	public static String WIKI_FOLDER_EXT 		= WIKI_FOLDER + File.separator + "References";	
	public static String WIKI_FOLDER_REF 		= WIKI_FOLDER + File.separator + "References" + File.separator + "GAMLReferences";
	public static String WIKI_FOLDER_WIKI_ONLY 	= WIKI_FOLDER + File.separator + "WikiOnly";
	public static String PATH_TO_KEYWORDS_XML 	= WIKI_FOLDER + File.separator + "keywords.xml";
	
	public static String WIKI_FOLDER_EXT_PLUGIN = WIKI_FOLDER_EXT + File.separator + "PluginDocumentation";
	public static String XML2WIKI_FOLDER 		= WIKI_FOLDER_REF;

	public static String JAVA2XML_FOLDER 			= GEN_FOLDER + File.separator + "java2xml";
	public static String PDF_FOLDER 				= GEN_FOLDER + File.separator + "pdf";
	public static String TOC_GEN_FOLDER 			= GEN_FOLDER + File.separator + "toc2pdf";
	public static String XML_KEYWORD_GEN_FOLDER 	= GEN_FOLDER + File.separator + "xmlKeywords";
	public static String CATALOG_GEN_FOLDER 		= GEN_FOLDER + File.separator + "catalog";
	

	// Inputs Folders
	public static String XSL_XML2WIKI_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2md";
	public static String XSL_XML2JSON_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2json";
	public static String XSL_XML2CATALOG_FOLDER 		= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2catalog";	
	public static String XSL_XML2TEST_FOLDER 			= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2test";
	public static String XSL_XML2KEYWORDS_XML_FOLDER 	= INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2keywordsXml";
	public static String PANDOC_FOLDER 					= INPUT_FOLDER + File.separator + "pandocPDF";

	public static String DOCGAMA_FILE 			= "target" + File.separator + "docGAMA.xml";
	public static String DOCGAMA_FILE_LOCAL 	= "gaml" + File.separator + "docGAMA.xml";
	public static String DOCGAMA_GLOBAL_FILE 	= JAVA2XML_FOLDER + File.separator + "docGAMAglobal.xml";

	public static String DOCGAMA_PDF 			= PDF_FOLDER + File.separator + "docGAMAv1.8.0.pdf";
	public static String TOC_FILE 				= INPUT_FOLDER + File.separator + "toc" + File.separator + "toc18.xml";
	public static String TOC_SIDEBAR_FILE		= WIKI_FOLDER + File.separator + "_Sidebar.md";

	// Tests
	public static String TEST_PLUGIN_FOLDER 	= "tests";
	public static String TEST_PLUGIN_GEN_FOLDER = TEST_PLUGIN_FOLDER + File.separator + "Generated";
	public static String TEST_PLUGIN_GEN_MODELS = TEST_PLUGIN_GEN_FOLDER + File.separator + "models";

	public static String TEST_OPERATORS_FOLDER 	= "Operators";
	public static String TEST_STATEMENTS_FOLDER = "Statements";

	public static String PROJECT_FILE = INPUT_FOLDER + File.separator + "project" + File.separator + ".project";

	// Commandes
	public static String CMD_PANDOC =
			OSUtils.isWindows() ? "C:/pandoc/pandoc.exe" : "/usr/local/bin/pandoc";
	public static String CMD_PDFLATEX = OSUtils.isWindows() ? "\"C:/MiKTeX 2.9/miktex/bin/x64/pdflatex.exe\""
			: "/usr/local/bin/pdflatex";
	// "/Library/TeX/Root/bin/universal-darwin/pdflatex";

	// Note : need to make change in mytemplate.tex to access the write style file ..... 
	
	// Path
	public static String PATH = "PATH=/usr/local/bin/:${PATH}";
}
