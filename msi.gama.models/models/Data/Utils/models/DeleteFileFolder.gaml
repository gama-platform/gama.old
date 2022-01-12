/**
* Name: Deletefile
* Example of use of the delete_file operator to delete a file or a folder 
* Author: Patrick Taillandier
* Tags: file
*/

model deleteFile

global {
	init {
		save "testA" to: "a_folder/fileA.txt";
		save "testB" to: "fileB.txt";
		
		bool delete_file_ok <- delete_file("fileB.txt");
		
		write "delete file is ok: " + delete_file_ok;
		
		bool delete_folder_ok <- delete_file("a_folder");
	
		write "delete folder is ok: " + delete_folder_ok;
			
	}
	
}

experiment deleteFile type: gui ;
