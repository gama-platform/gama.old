package msi.gama.doc.pdf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import msi.gama.doc.file.visitor.AllFilesFinder;
import msi.gama.doc.file.visitor.FilesByStartingFinder;
import msi.gama.doc.pdf.MdSidebarParsedLine.EntryLevel;
import msi.gama.precompiler.doc.utils.Constants;
import ummisco.gama.dev.utils.DEBUG;

public class TOCManager {

	String toc;
	Path basePath;
	List<File> docFiles;
	List<String> deadLinks;
	
	public static final String TUTORIALS_FILE = "Tutorials.md";
	public static final String OPERATORS = "Operators";
	boolean inTutorials = false;
	boolean operatorsAdded = false;

	public TOCManager(final String _toc, final String base) throws IOException {
		toc = _toc;
		basePath = Paths.get((new File(base)).getCanonicalPath());
		docFiles = new ArrayList<>();
		deadLinks = new ArrayList<>();
		
		parseBar();
	}
		
	public void parseBar() throws IOException {
		List<File> allFiles = getAllFilesFromWikiFolder();
		
		try (BufferedReader br = new BufferedReader(new FileReader(toc))) {
			while (br.ready()) {
				final String line = br.readLine();
				MdSidebarParsedLine l = new MdSidebarParsedLine(line);

				if(l.getFileName() != null) {
					final Optional<File> fifi = allFiles.stream()
							.filter(elt -> l.getFileName().equals(elt.getName()))
							.findFirst();
					
					if(fifi.isPresent()) {
						File fileToAdd = fifi.get();
						if(l.getLevel().equals(EntryLevel.TITLE)) {
							File partFile = createPartFile(l.getEntryName());
							docFiles.add(partFile);
							
							inTutorials = fileToAdd.getName().equals(TUTORIALS_FILE);				
						}
						
						// For Tutorials, get all the files with   						
						if(inTutorials) {
							docFiles.addAll(getTutorialFiles(fileToAdd));
						} else if(fileToAdd.getName().startsWith(OPERATORS)) {
							if(!operatorsAdded) {
								File fileOperator = allFiles.stream()
										.filter(elt -> elt.getName().equals(OPERATORS + ".md"))
										.findFirst()
										.orElseThrow(() -> new FileNotFoundException(OPERATORS + ".md file not found."));
								docFiles.add(fileOperator);
								operatorsAdded = true;
							} // else: the whole file has already been added and there is no need to add another one.
						} else {
							docFiles.add(fileToAdd);
						}
					} else {
						deadLinks.add(l.getFileName());
					}
				}
			}
		}			
	}
	
	private List<File> getTutorialFiles(File titlePage) throws IOException {
		String fileNameWithoutExt = titlePage.getName().substring(0,titlePage.getName().lastIndexOf('.'));			
		FilesByStartingFinder filesVisitor = new FilesByStartingFinder(fileNameWithoutExt);	
		
        Files.walkFileTree(Paths.get(Constants.WIKI_FOLDER), filesVisitor);
        
        return filesVisitor.getFiles().stream()
        		.sorted( (a,b) -> {
        			if(a.getName().length() == b.getName().length()) return a.getName().compareTo(b.getName());
        			else return a.getName().length() - b.getName().length();
        		})
        		.map(f -> {
					try {
						return f.getCanonicalFile();
					} catch (IOException e) {
						DEBUG.ERR("Error in getting tutorial file.",e);
					}
					return null;
				})
        		.collect(Collectors.toList());        
	}
	
	private List<File> getAllFilesFromWikiFolder() throws IOException {
    	AllFilesFinder files = new AllFilesFinder();
        Files.walkFileTree(basePath, files);
        return files.getFiles();	
	}
	
	public List<File> getFiles() {
		return docFiles;
	}
	
	public List<String> getDeadLinks() {
		return deadLinks;
	}	

	
	public String getTocFilesString() {
		return getFiles().stream()
				.map(f -> basePath.relativize(f.toPath().toAbsolutePath()).toString())
				.reduce("", (a,b) -> a + " " + b.replace("(", "\\(").replace(")", "\\)"));
	}

	
	public File createPartFile(String partName) throws IOException  {

		final File partFile =
				new File(Constants.TOC_GEN_FOLDER + File.separator + partName.replaceAll(" ", "_") + ".md");

		try (final FileWriter fw = new FileWriter(partFile);
			 final BufferedWriter partBw = new BufferedWriter(fw);) {

			partBw.newLine();
			partBw.write("\\part{" + partName + "}");
			partBw.newLine();
		}	
		
		return partFile;
	}		

	public static void main(final String[] args) throws IOException {
		
		final TOCManager t = new TOCManager(Constants.TOC_SIDEBAR_FILE, Constants.WIKI_FOLDER);	
		
		t.getFiles().forEach(f -> {
			try {
				DEBUG.LOG(f.getCanonicalPath());
				DEBUG.LOG(f.getPath());
				
			} catch (IOException e) {
				DEBUG.ERR("Error in min of TOCManager.", e);
			}
		});

		DEBUG.LOG("*********************");
		DEBUG.LOG("Broken Links:");
		DEBUG.LOG("*********************");
		
		t.getDeadLinks().forEach(DEBUG::LOG);
	}
}
