package msi.gama.doc.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TOCManager {

	String tocFile;
	
	public TOCManager(String toc){
		tocFile = toc;
	}
	
	public void createPartFiles() 
			throws ParserConfigurationException, SAXException, IOException{
		Document doc = XMLUtils.createDoc(tocFile);
		NodeList nl = doc.getElementsByTagName("part");		
		
		for(int i = 0; i<nl.getLength(); i++){
			String partName = ((Element)nl.item(i)).getAttribute("name");
			File partFile = new File(Constants.TOC_GEN_FOLDER + File.separator + partName.replaceAll(" ", "_") + ".md");

			FileWriter fw=new FileWriter(partFile);
			BufferedWriter partBw= new BufferedWriter(fw);
			
			partBw.newLine();
			partBw.write("\\part{"+partName+"}");		
			partBw.newLine();
			partBw.close();
		}
	}
	
	public void createSubpartFiles() 
			throws ParserConfigurationException, SAXException, IOException{
		Document doc = XMLUtils.createDoc(tocFile);
		NodeList nl = doc.getElementsByTagName("subpart");		
		
		for(int i = 0; i<nl.getLength(); i++){
			String subpartName = ((Element)nl.item(i)).getAttribute("name");
			File subpartFile = new File(Constants.TOC_GEN_FOLDER + File.separator + subpartName.replaceAll(" ", "_") + ".md");
			
			// copy the content of the wiki file in the new file.
			String wikiPagePath = Constants.WIKI_FOLDER+File.separatorChar+((Element)nl.item(i)).getAttribute("file")+".md";
			File wikiFile = new File(wikiPagePath);
			
			BufferedReader br = new BufferedReader(new FileReader(wikiFile));
			
			FileWriter fw=new FileWriter(subpartFile);
			BufferedWriter partBw= new BufferedWriter(fw);
			
			String line = null;
			boolean titleWritten=false;
			while ((line = br.readLine()) != null) {
				// change the title of the page (# Title) to the correct latex title
				if (line.startsWith("#") && !titleWritten) {
					// write latex content to make the content bigger.
					partBw.write("\\begingroup\n");
					partBw.write("\\newpage\n");
					partBw.write("\\fontsize{28}{34}\\selectfont\n");
					partBw.write("\\textbf{"+subpartName+"}\n");
					partBw.write("\\endgroup\n");
					partBw.write("\\vspace{20mm}\n");
					titleWritten = true;
				}
				else {
					partBw.write(line);
					partBw.newLine();
				}
			}
			
			br.close();			
			partBw.close();
		}
	}
	
	public List<String> getTocFilesList() 
			throws ParserConfigurationException, SAXException, IOException{
		List<String> lFile = new ArrayList<String>();
		Document doc = XMLUtils.createDoc(tocFile);
		
		NodeList nlPart = doc.getElementsByTagName("part");		
		for(int i = 0; i<nlPart.getLength(); i++){
			Element eltPart = (Element)nlPart.item(i);
			File fPart = new File(Constants.TOC_GEN_FOLDER + File.separator +eltPart.getAttribute("name").replaceAll(" ", "_") + ".md");
			lFile.add( fPart.getAbsolutePath() );			
			
			NodeList nlSubpart = eltPart.getElementsByTagName("subpart");
			for(int j = 0; j<nlSubpart.getLength(); j++){
				eltPart = (Element)nlSubpart.item(j);
				fPart = new File(Constants.TOC_GEN_FOLDER + File.separator +eltPart.getAttribute("name").replaceAll(" ", "_") + ".md");
				lFile.add(fPart.getAbsolutePath());
				NodeList chapterList = eltPart.getElementsByTagName("chapter");
				for(int k = 0; k<chapterList.getLength(); k++){
					File f = new File(Constants.WIKI_FOLDER + File.separator + ((Element)chapterList.item(k)).getAttribute("file") + ".md");
					lFile.add( f.getAbsolutePath());
				}	
			}		
		}

		return lFile;
	}	
	
	public String getTocFilesString() 
			throws ParserConfigurationException, SAXException, IOException {
		List<String> lf = getTocFilesList();
		File blankPage = new File(Constants.MD_BLANK_PAGE);
		String files = "";
		
		// the files have to be in relative, otherwise it does not work (for some obscure reason...)
		for(String f : lf){
			files = files + getRelativePathFromWiki(f)+" "+blankPage + " ";
		}
		return files;
	}
	
    public static String getRelativePathFromWiki(String targetPath/*, String basePath, String pathSeparator*/) {
    	
    	File tmp = new File(Constants.WIKI_FOLDER);
    	String basePath = tmp.getAbsolutePath();
    	String pathSeparator = "/";

        // Normalize the paths
        String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

        // Undo the changes to the separators made by normalization
        if (pathSeparator.equals("/")) {
            normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);

        } else if (pathSeparator.equals("\\")) {
            normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);

        } else {
            throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements. Store them as a string,
        // and also count how many of them there are.
        StringBuffer common = new StringBuffer();

        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex] + pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No single common path element. This most
            // likely indicates differing drive letters, like C: and D:.
            // These paths cannot be relativized.
            throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath
                    + "'");
        }   

        // The number of directories we have to backtrack depends on whether the base is a file or a dir
        // For example, the relative path from
        //
        // /foo/bar/baz/gg/ff to /foo/bar/baz
        // 
        // ".." if ff is a file
        // "../.." if ff is a directory
        //
        // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
        // the resource referred to by this path may not actually exist, but it's the best I can do
        boolean baseIsFile = true;

        File baseResource = new File(normalizedBasePath);

        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();

        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuffer relative = new StringBuffer();

        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relative.append(".." + pathSeparator);
            }
        }
        relative.append(normalizedTargetPath.substring(common.length()));
        return relative.toString();
    }


    static class PathResolutionException extends RuntimeException {
        PathResolutionException(String msg) {
            super(msg);
        }
    }
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		TOCManager t = new TOCManager(Constants.TOC_FILE);
		System.out.println(t.getTocFilesString());
		
		// t.createPartFiles();
		System.out.println(t.getTocFilesString());
	}

}
