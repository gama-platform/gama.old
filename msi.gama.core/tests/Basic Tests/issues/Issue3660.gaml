/**
* Name: Issue3660
* Verifies that the absence of a format for a file to save does not prevent from saving simple text files
* See https://github.com/gama-platform/gama/issues/3660 
* Author: Alexis Drogoul
* Tags: save, data, file
*/


model Issue3660

global {
    init {
        string info <- ""  + 3 +"\n0.0\n0.0\n"+4+"\n"+"\n"+42;
//      save info to: "res/satellite.pgw"; // A warning should be emitted when uncommenting this line
        save info to: "res/satellite.pgw" format: "text"; // An info should be emitted
        save info to: "res/satellite.txt" format: "txt"; // Nothing should be emitted
    }
}
experiment name type: gui { }