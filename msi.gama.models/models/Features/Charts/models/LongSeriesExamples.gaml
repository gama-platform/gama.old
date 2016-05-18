/**
 *  newtest
 *  Author: HPhi
 *  Description: 
 */

model newtest

global
{
					int serie_length<-50000;
					
			 		list<float> xlist<-[];
			 		list<float> coslist<-[];
			 		list<float> sinlist<-[];
			 		float base;
			 		init
			 		{
			 		loop i from: 0 to: serie_length 
			 		{
			 			base<-cycle+i*500/serie_length;
			 			add base to:xlist;
			 			add cos(base) to:coslist;
			 			add sin(base) to:sinlist;
			 		}
			 			
			 		}
			 		reflex update_sinchart
			 		{
			 		xlist<-[];
			 		coslist<-[];
			 		sinlist<-[];
			 		loop i from: 0 to: serie_length 
			 		{
				 		base<-cycle+i*(500)/serie_length;
			 			add base to:xlist;
			 			add cos(base) to:coslist;
			 			add sin(base) to:sinlist;
			 		}
			 			
			 		}

}

experiment my_experiment type: gui {
	output {
		display "long_series" type:java2D {
			chart "Long series values" type:series 
//			x_serie:xlist
			 	{ 
				data "Cosinus" value:coslist
					color:#blue
					marker:false
					style:line;
				data "Sinus" value:sinlist
					color:#red
					marker:false
					style:line;
			}
		} 

	}
}