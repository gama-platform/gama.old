/**
 *  TestFile2Mat
 *  Author: bgaudou
 *  Description: 
 */

model TestFile2Mat

global {
	string echelleTemperatueImageShape <- '../images/paletteCouleurTemperature-altern.png'; // image donnant la pallette de couleur pout la temperature

	init {
		let matriceEchelle type: matrix value: file(echelleTemperatueImageShape) as_matrix {70,1} ;
		write " mat : " + matriceEchelle ;
	}
}

entities {
	/** Insert here the definition of the species of agents */
}

experiment TestFile2Mat type: gui {
	/** Insert here the definition of the input and output of the model */
	output {
	}
}
