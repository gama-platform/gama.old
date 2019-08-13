package msi.gama.doc.file.visitor;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.List;

import msi.gama.precompiler.doc.utils.Constants;

import static java.nio.file.FileVisitResult.*;

public class AllFilesFinder extends SimpleFileVisitor<Path> {	
	List<File> l;
	
	public AllFilesFinder() {
		l = new ArrayList<>();
	}
	
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        l.add(file.toFile());
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

    	AllFilesFinder files = new AllFilesFinder();
        Files.walkFileTree(Paths.get(Constants.WIKI_FOLDER), files);
        files.getFiles().forEach(f -> {
			try {
				System.out.println(f.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
    }
}