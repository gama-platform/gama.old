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
		
		bool folder_exist_ok <- folder_exists("..");
		
		write "folder exists ok: " + folder_exist_ok;
		
		file current_folder <- folder(".");
		
		write "folder the model is in: "+current_folder;
		
		list folder_contents <- current_folder.contents;
		
		write "list of file inside the model folder: "+ folder_contents;
			
	}
	
}

experiment fileUtils type: gui ;
