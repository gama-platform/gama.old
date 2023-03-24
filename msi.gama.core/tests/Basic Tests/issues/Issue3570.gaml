/**
* Name: Issue3570
* Tests whether the two fixes provided in Issue #3570 are working on every combination of OS and hardware. 
* See https://github.com/gama-platform/gama/issues/3570
* Author: A. Drogoul
* Tags: chart, display, snapshot
*/
model Issue3570

global {}

experiment 'Verify me' {
	output synchronized: true { // should be synchronized by default
		display "One chart" autosave: {1000, 1000} every 100 #cycle type: 2d {
			// 1000x1000 images should be saved in the 'snapshot' folder every 100 cycles
			chart "One series"  {
				data "Data one" value: rnd(100);
				data "Data two" value: rnd(100);
			}
			
		}

	}

}
