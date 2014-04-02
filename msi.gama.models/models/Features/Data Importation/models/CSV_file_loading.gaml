/**
 *  CSVfileloading
 *  Author: administrateur
 *  Description: Shows how to import a CSV file and use it
 */

model CSVfileloading

global {
	file my_csv_file <- csv_file("../includes/iris.csv",",");
	
	init {
		matrix data <- matrix(my_csv_file);
		loop i from: 1 to: data.rows -1{
			loop j from: 0 to: data.columns -1{
				write "data rows:"+ i +" colums:" + j + " = " + data[j,i];
			}	
		}		
	}
}

experiment main type: gui{
}
