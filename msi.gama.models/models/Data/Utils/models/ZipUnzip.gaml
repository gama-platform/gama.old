/**
* Name: ZipUnzip
* Example of use of the zip and unzip operators to compress/uncompress files 
* Author: Patrick Taillandier
* Tags: file
*/

model ZipUnzip

global {

	init {
		//zip fileA.txt and folderB into the archive.zip file
		bool zip_ok <- zip(["../includes/fileA.txt", "../includes/folderB"], "archive.zip");
		write "Zip operation is " + (zip_ok ? "ok" : "not ok");
		
		//unzip the archive.zip file into the results folder
		bool unzip_ok<- unzip( "archive.zip", "results");
		write "Unzip operation is " + (unzip_ok ? "ok" : "not ok");
	}
}

experiment ZipUnzip type: gui ;