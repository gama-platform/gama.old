package msi.gama.headless.common;

import java.io.File;
import java.util.ArrayList;

import msi.gama.headless.util.WorkspaceManager;

public final class DirectoryAnalyser {
	public static ArrayList<String> readDirectory(String dir,String extension){
		ArrayList<String> listFiles = new ArrayList<String>();
		File rep = new File(dir);
		
		if(rep.isDirectory()){
			String t[] = rep.list();
			
			if(t!=null){
				for(String fName : t) {
					ArrayList<String> newList = readDirectory(rep.getAbsolutePath()+File.separator+fName,extension);
					listFiles.addAll(newList);
				}
			}
		} else {
			if(extension.equals(WorkspaceManager.getFileExtension(rep.getAbsolutePath()))){
				listFiles.add(rep.getAbsolutePath());				
			}
		}
		
		return listFiles;
	}
}
