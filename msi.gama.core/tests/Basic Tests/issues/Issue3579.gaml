/**
* Name: Issue3579
* Verifies that the fields are correctly loaded and gathered for GAML files 
* Author: A. Drogoul
* Tags: files,attributes,metadata
*/


model Issue3579

import "Issue3570.gaml"

global {
	init {
		gaml_file ff <- gaml_file("Issue3579.gaml");
		write ff.experiments; // Only the experiments defined in this file are listed, not the ones imported
		write ff.tags; // try adding new tags and run the model again
		write ff.uses; // if no file is used (resp. imported) these fields return an empty list
		write ff.imports; 
		write ff.valid; // whether the file is syntactically valid or not
	}
}

experiment "Test it";

