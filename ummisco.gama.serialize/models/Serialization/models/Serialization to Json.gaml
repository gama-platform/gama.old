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
		write "------------- TO JSON";
		write (to_json(1,true));
		write (to_json(1.24,true));
		write (to_json("a string",true));
		write (to_json(date(0),true));
		write (to_json(#blue,true));
		write (to_json(font("Helvetica"),true));
		write (to_json({20,10},true));
		write "------------- From JSON";
		write (from_json(to_json(1,true)));
		write (from_json(to_json(1.24,true)));
		write (from_json(to_json("a string",true)));
		write (from_json(to_json(date(0),true)));
		write (from_json(to_json(#blue,true)));
		write (from_json(to_json(font("Helvetica"),true)));
		write (from_json(to_json({20,10},true)));
		
		write "Lists";
		write "------------- TO JSON";
		write(to_json([1,2,3,4,5],true));
		write(to_json(["a",2,"aa",4,5.2],true));
		write "------------- FROM JSON";
		write(from_json(to_json([1,2,3,4,5],true)));
		write(from_json(to_json(["a",2,"aa",4,5.2],true)));
		
				
		write "Maps";
		write "------------- TO JSON";
		write(to_json(["a"::2,"b"::4],true));
		write(to_json([123::43,234::#green],true));
		write "------------- FROM JSON";
		write(from_json(to_json(["a"::2,"b"::4],true)));
		write(from_json(to_json([123::43,234::#green],true)));
		write "Other data structures";
		write "-------------";
		write(to_json(23::34,true));
		//write(to_json([123::43,234::#green],true));
		write(to_json({2,2} matrix_with 10,true));
		//write(to_json(circle(10),true));
		
		write "Agents";
		write "-------------";		
		create sp number: 2 ;
		sp[0].friend <- sp[1];
		sp[1].friend <- sp[0];
		write(to_json(sp.population,true));
	}
}

species sp 
{
	sp friend;
}

experiment "Run me";
