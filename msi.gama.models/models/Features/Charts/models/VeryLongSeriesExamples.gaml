/**
 *  newtest
 *  Author: HPhi
 *  Description: 
 */

model newtest

global
{
					int serie_length<-1000;
					
			 		list<float> xlist<-[];
			 		list<float> coslist<-[];
			 		list<float> sinlist<-[];
			 		float base;
			 		init
			 		{
			 		loop i from: 0 to: serie_length 
			 		{
			 			base<-float(i);
			 			add base to:xlist;
			 			add cos(base/1000) to:coslist;
			 			add sin(base/1000) to:sinlist;
			 		}
			 			
			 		}
			 		reflex update_sinchart
			 		{
//			 		xlist<-[];
//			 		coslist<-[];
//			 		sinlist<-[];
			 		loop i from: 0 to: serie_length 
			 		{
				 		base<-float(serie_length*cycle+i);
			 			add base to:xlist;
			 			add cos(base/1000) to:coslist;
			 			add sin(base/1000) to:sinlist;
			 		}
			 			
			 		}

}

experiment my_experiment type: gui {
	output {
		display "long_series" type:java2D {
			chart "Long series values" type:series 
			x_label:"#points to draw at each step"
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