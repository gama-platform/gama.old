/**
 *  CallingR
 *  Author: Truong Xuan Viet
 *  Description: First sample for R calling in GAMA

 */

model CallingR

/*
 * Configuration:
 * 1) In GAMA, select menu option: Edit\Preferences
 * 2) In "Config RScript's path", browse to your "RScript" file (R language installed in your system)
 * (*) Ensure that "install.packages("Runiversal")" is already applied in R environment.
 */


global {
	list X <- [2, 3, 5];
	
	list Y <- [2, 12, 4]; 
	list result;
	
	init{
		write string(corR(X, Y)); // -> 0.755928946018454
		write string(meanR(X)); // -> 2.0
		
		/* Copying the RCode.txt file into your folder, re-change the path, *.txt file must be ANSI text file */
		
		//result <- R_compute("D:/PSN-Simulation/RCaller/RGama/Correlation.R"); // -> result,[0.981980506061966];
		//write result at 0;
		
		//result <- R_compute("D:/PSN-Simulation/RCaller/RGama/RandomForest.R");
		//write result at 0;
		
		result <- list(R_compute_param("D:/PSN-Simulation/RCaller/RGama/AddParams.R", X)); // -> result,[0.981980506061966];
		write string(result at 0);

	}
}

