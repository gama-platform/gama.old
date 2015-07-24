/**
 *  focus
 *  Author: drogoul
 *  Description: 
 */

model focus

/* Insert your model definition here */

global {
	file Rcode<-text_file("r.txt");
	init {
		
		
//		write "sss-";
//		
//		write Rcode;
		create RJava;
	}
}

species RJava skills:[RSkill] {
	init{
		loop s over:Rcode.contents{
			write s;			
			write R_eval(s);
		}
//		write R_eval("data(iris)");
//		write ""+R_eval("iris");
		
	}
}
experiment toto type:gui{
	output{
	}
}