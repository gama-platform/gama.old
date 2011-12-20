model voters
// gen by Xml2Gaml


global {
	var width type: int init: 200 min: 10 parameter: 'Width:' category: 'Board' ;
	var height type: int init: 200 min: 10 parameter: 'Height:' category: 'Board' ;
	var torus type: bool init: true parameter: 'Torus?:' category: 'Board' ;
	var density type: int init: 50 min: 1 max: 99 parameter: 'Density of live cells:' category: 'Cells' ;
	var livingcolor type: rgb init: rgb('white') parameter: 'Color of live cells: category:' category: 'Colors' ;
	var deadcolor type: rgb init: rgb('black') parameter: 'Color of dead cells: category:' category: 'Colors' ;
	var random_choice type: bool init: true parameter: 'Random choice in case of tie ?' category: 'Cells' ;
}
environment width: width height: height {
	grid life_grid width: width height: height neighbours: 8 torus: torus {
		var living type: int value: ((self neighbours_at 1) of_species life_grid) count each.state ;
		var state type: bool init: (rnd(100)) < density value: (living = 4) ? (random_choice ? flip(0.5) : state): ((living > 4) ? true : false) ;
		var color type: rgb init: state ? livingcolor : deadcolor value: state ? livingcolor : deadcolor ;
	}
}
output {
	display Life {
		grid life_grid ;
	}
}
