/**
* Name: Json
* Shows how to transform GAML values into JSON objects
* Author: A. Drogoul
* Tags: Serialization, JSON
*/


model Json

global {
	init {
		write "-------------";			
		write "Simple values";
		write "------------- TO JSON";
		write sample(to_json(1));
		write sample(to_json(1.24));
		write sample(to_json("a string"));
		write sample(to_json(date(0)));
		write sample(to_json(#blue));
		write sample(to_json(font("Helvetica")));
		write sample(to_json({20,10}));
		write "------------- From JSON";
		write (from_json(to_json(1,true)));
		write (from_json(to_json(1.24,true)));
		write (from_json(to_json("a string",true)));
		write (from_json(to_json(date(0),true)));
		write (from_json(to_json(#blue,true)));
		write (from_json(to_json(font("Helvetica"),true)));
		write (from_json(to_json({20,10},true)));
		write "-------------";			
		write "Lists";
		write "------------- TO JSON";
		write(to_json([1,2,3,4,5],true));
		write(to_json(["a",2,"aa",4,5.2],true));
		write "------------- FROM JSON";
		write(from_json(to_json([1,2,3,4,5],true)));
		write(from_json(to_json(["a",2,"aa",4,5.2],true)));
		
		write "-------------";					
		write "Maps";
		write "------------- TO JSON";
		write(to_json(["a"::2,"b"::4],true));
		write(to_json([123::43,234::#green],true));
		write "------------- FROM JSON";
		write(from_json(to_json(["a"::2,"b"::4],true)));
		write(from_json(to_json([123::43,234::#green],true)));
		
		write "-------------";			
		write "Other data structures";
		write "------------- TO JSON";
		write(to_json(23::34,true));
		write(to_json({2,2} matrix_with 10,true));
		write "------------- FROM JSON";
		write(from_json(to_json(23::34,true)));
		write(from_json(to_json({2,2} matrix_with 10,true)));
		write "-------------";	
		write "Agents";
		write "-------------";	
		// We create 2 sp's	...
		create sp number: 2 ;
		// ... make them friends ...
		sp[0].friend <- sp[1];
		sp[1].friend <- sp[0];
		// ... serialise them ...
		string s <- (to_json(sp[0]));
		// ... then kill them ...
		ask sp {
			do die;
		}
		// ... and finally recreate them while asking to recreate an unknown value. 
		unknown t <- from_json(s);
		// We verify that t contains the former agent
		write t = sp(0);
		// ... and that all sp's have been recreated (as they were friends)
		write sp.population;
		write(to_json(sp.population,true));
//		save sp.population to: "../pop.json" rewrite: true;
	}
}

species sp 
{
	sp friend;
}

experiment "Run me";
