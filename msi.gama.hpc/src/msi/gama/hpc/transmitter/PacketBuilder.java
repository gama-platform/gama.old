package msi.gama.hpc.transmitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class PacketBuilder {

	private String modelDirectory;
	private String inputDirectory;
	private ZipOutputStream dataToSend;
	
	public PacketBuilder()
	{
		
	}
	
	public void addModel(String directory)
	{
		this.modelDirectory = directory;

	}
	
	public void addexperiments(String directory)
	{
		this.inputDirectory = directory;

	}
	
	private void buildPath()
	{
		try 
		{ 
		    //create a ZipOutputStream to zip the data to 
		    ZipOutputStream zos = new 
		           ZipOutputStream(new FileOutputStream("/tmp/curDir.zip")); 
		     zipDir(this.modelDirectory, zos); 
		  
		    //close the stream 
		    zos.close(); 
		} 
		catch(Exception e) 
		{ 
		    //handle exception 
		} 
		//here is the code for the method 
	}
		public void zipDir(String dir2zip, ZipOutputStream zos) 
		{ 
		    try 
		   { 
		        //create a new File object based on the directory we 
		        //have to zip File    
		        File zipDir = new File(dir2zip); 
		        //get a listing of the directory content 
		        String[] dirList = zipDir.list(); 
		        byte[] readBuffer = new byte[2156]; 
		        int bytesIn = 0; 
		        //loop through dirList, and zip the files 
		        for(int i=0; i<dirList.length; i++) 
		        { 
		            File f = new File(zipDir, dirList[i]); 
			        if(f.isDirectory()) 
			        { 
			           String filePath = f.getPath(); 
			           zipDir(filePath, zos); 
			           continue; 
			        } 
		            //if we reached here, the File object f was not 
		        //    a directory 
		            //create a FileInputStream on top of f 
		            FileInputStream fis = new FileInputStream(f); 
		          //  create a new zip entry 
		            ZipEntry anEntry = new ZipEntry(f.getPath()); 
		            //place the zip entry in the ZipOutputStream object 
		            zos.putNextEntry(anEntry); 
		            //now write the content of the file to the ZipOutputStream 
		            while((bytesIn = fis.read(readBuffer)) != -1) 
		            { 
		                zos.write(readBuffer, 0, bytesIn); 
		            } 
		           //close the Stream 
		           fis.close(); 
		    } 
		} 
		catch(Exception e) 
		{ 
		    //handle exception 
		} 
	}
	
	
	/*public String getPathToFile() {
		return pathToFile;
	}

	public static void main(String [] arg)
	{
		new PacketBuilder("/tmp/").buildPath();
	}*/
	
}
