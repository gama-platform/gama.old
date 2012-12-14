model Statistics
// gen by Xml2Gaml
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	var output type: string init: '' ;
	var the_StatisticalAgent type: StatisticalAgent ;
	init {
		create species: StatisticalAgent number: 1 ;
	}
	reflex ;
}
environment {
	grid stupid_grid width: 100 height: 100 torus: true ;
}
entities {
	species StatisticalAgent skills: situated {
		var coefficience_b0 type: float init: 0 ;
		var coefficience_b1 type: float init: 0 ;
		var n type: int init: 0 ;
		matrix X size: 1000 ;
		matrix Y size: 1000 ;
		matrix XY size: 1000 ;
		matrix X_Square size: 1000 ;
		action SingleLinearRegresion {
			let mean_X type: float value: 0 ;
			let mean_Y type: float value: 0 ;
			let sum_X type: float value: 0 ;
			let sum_Y type: float value: 0 ;
			let sum_XY type: float value: 0 ;
			let sum_X_Square type: float value: 0 ;
			if condition: n != 0 {
				let i type: int;
				loop from: 1 to: n var: i {
					set var: XY at i value: (float (X at i)) * (float (Y at i)) ;
					set var: X_Square at i value: (float (X at i)) ^ 2 ;
					set var: sum_X value: sum_X + (float (X at i)) ;
					set var: sum_Y value: sum_Y + (float (Y at i)) ;
					set var: sum_XY value: sum_XY + (float (XY at i)) ;
					set var: sum_X_Square value: sum_X_Square + (float (X_Square at i)) ;
				}
				set var: mean_X value: sum_X/float (n) ;
				set var: mean_Y value: sum_Y/float (n) ;
				set coefficience_b1 value: mean_X ;
				set coefficience_b0 value: mean_Y ;
			}
			else {
				set coefficience_b1 value: 0 ;
				set coefficience_b0 value: 0 ;
			}
			
		}
	}
}
output {
	//monitor Number of light_traps value: output refresh_every: 1 ;
	file Output type: string data: output ;
}
