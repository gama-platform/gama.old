/**
* Name: Convertion of CSV data to Matrix
* Author:  Patrick Taillandier
* Description: Model which shows how to initialize a matrix by using the content of a CSV File. The model load a CSV File, and write its content in the console. 
* Tags: csv, load_file
*/


model CSVfileloading

global {
	file my_csv_file <- csv_file("../includes/iris.csv",",");
	
	init {
		//convert the file into a matrix
		matrix data <- matrix(my_csv_file);
		//loop on the matrix rows (skip the first header line)
		loop i from: 1 to: data.rows -1{
			//loop on the matrix columns
			loop j from: 0 to: data.columns -1{
				write "data rows:"+ i +" colums:" + j + " = " + data[j,i];
			}	
		}		
	}
}

experiment main type: gui;
