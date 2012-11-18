/**
 *  CallingR
 *  Author: Truong Xuna Viet
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
	list X <- [2, 3, 1];
	list Y <- [2, 12, 4]; 
	
	init{
		write corR(X, Y); // -> 0.755928946018454
		write meanR(X); // -> 2.0
		/* Copying the RCode.txt file into your folder, re-change the path, *.txt file must be ANSI text file */
		
		write R_compute("C:/RCode.txt"); // -> [a::[0.981980506061966]]
	}
}

