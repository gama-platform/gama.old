package msi.gama.doc;
import java.io.File;

public class Constants {
	public static String SRC_FOLDER = "files";
	
	public static String GEN_FOLDER = SRC_FOLDER + File.separator + "gen";
	public static String INPUT_FOLDER = SRC_FOLDER + File.separator + "input";
	public static String SVN_FOLDER = SRC_FOLDER + File.separator + "svn";
	
	public static String JAVA2XML_FOLDER = GEN_FOLDER + File.separator + "java2xml";
	public static String XML2WIKI_FOLDER = GEN_FOLDER + File.separator + "xml2wiki";
	public static String WIKI2WIKI_FOLDER = GEN_FOLDER + File.separator + "wiki2wiki";
	public static String WIKI2HTML_FOLDER = GEN_FOLDER + File.separator + "wiki2html";
	public static String HTML2XML_FOLDER = GEN_FOLDER + File.separator + "html2xml";
	public static String PDF_FOLDER = GEN_FOLDER + File.separator + "pdf";
	
	public static String XSL_XML2WIKI_FOLDER = INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2wiki";
	public static String TOC_FILE = INPUT_FOLDER + File.separator + "toc" + File.separator + "toc16.xml";
	public static String PYTHON_TEMPLATE_FOLDER = INPUT_FOLDER + File.separator + "templatePythonGeneration";
	public static String XSL_XML2PDF_FILE = INPUT_FOLDER + File.separator + "xsl" + File.separator + "xml2pdf" + File.separator + "xhtml-to-xslfo.xsl";
	
	public static String DOCGAMA_FILE = "gaml"+ File.separator + "docGAMA.xml";
	public static String DOCGAMA_GLOBAL_FILE = JAVA2XML_FOLDER + File.separator + "docGAMAglobal.xml";
	public static String DOCGAMA_FINAL = HTML2XML_FOLDER + File.separator + "docGamaFinal.xml";
	public static String DOCGAMA_PDF = PDF_FOLDER + File.separator + "docGAMAv16.pdf";
	
	public static String ADR_SVNGAMA = "http://gama-platform.googlecode.com/svn/wiki";
}
