package msi.gama.doc.file.visitor;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import msi.gama.precompiler.doc.utils.Constants;

public class FilesByStartingFinder extends SimpleFileVisitor<Path> {
	String fileNameStart;
	List<File> l;

	public FilesByStartingFinder() {
		this("");
	}	
	
	public FilesByStartingFinder(String name) {
		fileNameStart = name;
		l = new ArrayList<>();		
	}
	
	public void setFileName(String name) {
		fileNameStart = name;
		l = new ArrayList<>();
	}
	
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
//    	System.out.println(file.getFileName());
//    	System.out.println(fileNameStart);
//    	
//    	System.out.println(file.getFileName().toString().startsWith(fileNameStart));
//    	
    	if(file.getFileName().toString().startsWith(fileNameStart)) {
    		l.add(file.toFile());
    	} 
        return CONTINUE;    		
    }   
    
    // To ignore the git folder...
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
    	if(dir.endsWith(".git")) {
    		return SKIP_SUBTREE;
    	} else {
    		return CONTINUE;
    	}
    }
    
    public List<File> getFiles() {
    	return l;
    }


    public static void main(String[] args) throws IOException {

    	FilesByStartingFinder files = new FilesByStartingFinder("IncrementalModel");
    	
//    	files.setFileName("Updating.md");
        Files.walkFileTree(Paths.get(Constants.WIKI_FOLDER), files);
//        System.out.println(files.getFiles().getCanonicalPath());

        files.getFiles().forEach(f -> {
			try {
				System.out.println(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
//
//        
//    	files.setFileName("NotExistingFile.md");
//        Files.walkFileTree(Paths.get(Constants.WIKI_FOLDER), files);
//        System.out.println(files.getFiles().getCanonicalPath());

        
        
    }

}
