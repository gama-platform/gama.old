/**
* Name: Json
* Shows how to transform GAML values into JSON objects
* Author: A. Drogoul
* Tags: Serialization, JSON
*/


model Json

global {
	init {
		write "Simple values";
		write "-------------";
		write (to_json(1));
		write (to_json(1.24));
		write (to_json("a string"));
		write (to_json(date(0)));
		write (to_json(#blue));
		write (to_json(font("Helvetica")));
		write (to_json({20,10}));
		
		write "Lists";
		write "-------------";
		write(to_json([1,2,3,4,5]));
		write(to_json(["a",2,"aa",4,5.2]));
		
		write "Maps";
		write "-------------";
		write(to_json(["a"::2,"b"::4]));
		write(to_json([123::43,234::#green]));
		
		write "Other data structures";
		write "-------------";
		write(to_json(23::34));
		write(to_json([123::43,234::#green]));
		write(to_json({2,2} matrix_with 10));
		write(to_json(circle(10)));
		
		
	}
}

experiment "Run me";
