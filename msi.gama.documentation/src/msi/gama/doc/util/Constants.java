/*********************************************************************************************
 * 
 *
 * 'Constants.java', in plugin 'msi.gama.documentation', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.doc.util;
import java.io.File;

public class Constants {
	public static boolean ONLINE = false;
	
	// 
	public static String RELEASE_APPLICATION = "msi.gama.application";
	public static String RELEASE_PRODUCT = "gama.product";
	
	// Repositories containing used files
	
	public static String BASE_FOLDER = "";
	public static String SRC_FOLDER = BASE_FOLDER + "files";
	
	public static String GEN_FOLDER = SRC_FOLDER + File.separator + "gen";
	public static String INPUT_FOLDER = SRC_FOLDER + File.separator + "input";
//	public static String SVN_FOLDER = SRC_FOLDER + File.separator + "svn";
	public static String TEST_FOLDER = SRC_FOLDER + File.separator + ".." + File.separator + ".." + File.separator + 
			"msi.gama.models" + File.separator + "models" + File.separator + "Tests";

	public static String WIKI_FOLDER = SRC_FOLDER + File.separator + ".." + File.separator + ".." + File.separator + ".." + File.separator + "GamaWiki";


	// Generation folders
	public static String WIKI_FOLDER_REF = WIKI_FOLDER + File.separator + "References" + File.separator + "GAMLReferences";
	public static String WIKI_FOLDER_WIKI_ONLY = WIKI_FOLDER + File.separator + "WikiOnly";
	public static String WIKI_FOLDER_EXT = WIKI_FOLDER + File.separator + "References";
	public static String WIKI_FOLDER_EXT_PLUGIN = WIKI_FOLDER_EXT + File.separator + "PluginDocumentation";
	
	public static String XML2WIKI_FOLDER = WIKI_FOLDER_REF ;	

	public static String JAVA2XML_FOLDER = GEN_FOLDER + File.separator + "java2xml";
//	public static String WIKI2WIKI_FOLDER = GEN_FOLDER + File.separator + "wiki2wiki";
	public static String WIKI2HTML_FOLDER = GEN_FOLDER + File.separator + "wiki2html";
//	public static String HTML2XML_FOLDER = GEN_FOLDER + File.separator + "html2xml";
	public static String PDF_FOLDER = GEN_FOLDER + File.separator + "pdf";
	public static String TOC_GEN_FOLDER = GEN_FOLDER + File.separator + "toc2pdf";
	public static String XML_KEYWORD_GEN_FOLDER = GEN_FOLDER + File.separator + "xmlKeywords";
	public static String PATH_TO_KEYWORDS_XML = Constants.WIKI_FOLDER+File.separator+"keywords.xml";
		
	
	// Inputs Folders
	public static String PYTHON_TEMPLATE_FOLDER = INPUT_FOLDER + File.separator + "templatePythonGeneration";
	public static String XSL_XML2WIKI_FOLDER = INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2md";
	public static String XSL_XML2TEST_FOLDER = INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2test";	
	public static String XSL_XML2PDF_FILE = INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2pdf" + File.separator + "xhtml-to-xslfo.xsl";
	public static String XSL_XML2KEYWORDS_XML_FOLDER = INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2keywordsXml";
	public static String PANDOC_FOLDER = INPUT_FOLDER + File.separator + "pandocPDF";
	
	
	
	public static String DOCGAMA_FILE = "gaml"+ File.separator + "docGAMA.xml";
	public static String DOCGAMA_GLOBAL_FILE = JAVA2XML_FOLDER + File.separator + "docGAMAglobal.xml";
//	public static String DOCGAMA_FINAL = HTML2XML_FOLDER + File.separator + "docGamaFinal.xml";
		
	public static String TEST_OPERATORS_FOLDER = "operatorsTest";
	public static String TEST_STATEMENTS_FOLDER = "statementsTest";
	
	public static String DOCGAMA_PDF = PDF_FOLDER + File.separator + "docGAMAv17.pdf";	
	public static String TOC_FILE = INPUT_FOLDER + File.separator + "toc" + File.separator + "toc17.xml";	
	public static String MD_BLANK_PAGE = "G__BlankPage.md"; // Blank page is directly in the wiki folder
	
//	public static String ADR_SVNGAMA = "http://gama-platform.googlecode.com/svn/wiki";
	
	// Commandes
	public static String CMD_PANDOC = "C:/Users/Julien/AppData/Local/Pandoc/pandoc";
	public static String CMD_PDFLATEX = "\"C:/Program Files/MiKTeX 2.9/miktex/bin/x64/xelatex\"";
	
	// Path
	public static String PATH = "PATH=/usr/local/bin/:${PATH}";
}
