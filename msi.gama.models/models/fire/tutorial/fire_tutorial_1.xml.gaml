model fire
// gen by Xml2Gaml


global {
	var width type: int parameter: 'Width of the environment (in meters):' init: 500 min: 10 max: 1000 category: 'Environment' ;
	var height type: int parameter: 'Height of the environment (in meters):' init: 500 min: 10 max: 1000 category: 'Environment' ;
	var trees_number type: int parameter: 'Number of trees:' init: 20000 min: 1 category: 'Trees' ;
	init {
		create species: tree number: trees_number ;
	}
}
environment width: width height: height torus: false ;
entities {
	species tree skills: [situated, visible] {
		const color type: rgb init: rgb('green') ;
		const size type: float init: 1 ;
		const location type: point init: {rnd(width), rnd(height)} ;
	}
}
output {
	display Forest {
		species tree ;
	}
}
