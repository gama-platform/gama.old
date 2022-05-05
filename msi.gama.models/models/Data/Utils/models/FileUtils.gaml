/**
* Name: Copyfile
* Example of file operators
* The delete_file operator is used to delete a file or a folder 
* The rename_file is used to rename a file or a folder
* The copy_file operator is used to copy a file or a folder
* Author: Patrick Taillandier and Tri Nguyen-Huu
* Tags: file
*/

model FileUtils

global {
	init {
		save "testA" to: "a_folder/fileA.txt";
		
		bool copy_file_ok <- copy_file("a_folder/fileA.txt","a_folder/fileB.txt");
		
		write "copy file is ok: " + copy_file_ok;
		
		bool delete_file_ok <- delete_file("a_folder/fileA.txt");
		
		write "delete file is ok: " + delete_file_ok;
		
		bool rename_file_ok <- rename_file("a_folder/fileB.txt","a_folder/fileA.txt");
		
		write "rename file is ok: " + rename_file_ok;
		
		bool delete_folder_ok <- delete_file("a_folder");
	
		write "delete folder is ok: " + delete_folder_ok;
			
	}
	
}

experiment fileUtils type: gui ;
