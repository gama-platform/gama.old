/**
 *  new
 *  Author: drogoul
 *  Description: 
 */
model new

/* Insert your model definition here */
global {
	/*
	 * I am a comment written in the GAML file to give indications on liste1.
	 */
	list<int> liste1;


	list<int> l22 <- liste1;

	map<string, string> l2;

	
	action titi {
		arg a type: list<int>;
	}
	
	
	 init {
	 	
	write ' ' + 1.18 + ' ' + (1.18 with_precision 2);
		write ' ' + 1.19 + ' ' + (1.19 with_precision 2);		
		write ' ' + 1.18 + ' ' + (1.18 with_precision 3);
		write ' ' + 1.19 + ' ' + (1.19 with_precision 3);	 	
	 	
	let gg type: list<int> <- [];
	map<int, int> toto <- map([]);
	add 4 to: toto;
	map<int, int> toto2 <- map([]);
	add 4 to: container(toto2);
	liste1 <- [];
	loop i from: 0 to: 20 {
		/*
		 * And this is a stupid comment.
		 */
		liste1 << i;
		liste1 >> i; 
	}

	write string(liste1);
	liste1 >> 10 ; 
	write string(liste1);
	liste1[0] <- 100;
	write string(liste1);
	put 0 in: liste1 all: true;
	write string(liste1);
	add item: [1, 2, 3] to: liste1 all: true;
	write string(liste1);
	add all: [1, 2, 3] to: liste1 at: 0;
	write string(liste1);
	add all: [19, 10, 10] to: liste1;
	write string(liste1);
	remove index: 0 from: liste1;
	write string(liste1);
	remove all: [0, 2] from: liste1;
	write string(liste1);
	l2 <- map([]);
	loop i from: 0 to: 5 {
		l2[i] <- string(i + 10);
	}

	map<string, string> l3 <- (l2.pairs collect each);
	let l3bis <- l3;
	let l4 <- (agents) collect each.name;
	let l5 <- liste1 collect each + 1;
	write string(l2);
	add map(["a"::"b"]) to: l2 all: true;
	write string(l2);
	add "y"::3 to: l2;
	write string(l2);
	put [1, 2, 3, 4] in: l2 all: true;
	write string(l2);
	l2[1] <- 5;
	write string(l2);
}


experiment toto5{}

experiment titi{
	
}

experiment tutu{
	
}

experiment tata {
	
}

experiment dfgheh{
	
}

experiment fkkf{
	
}

experiment fdzas___djdjekekek {}

experiment fjfj{
	
}

experiment toto2{}

experiment titi2{
	
}

experiment tutu2{
	
}

experiment tata2 {
	
	
	



 } }
 
