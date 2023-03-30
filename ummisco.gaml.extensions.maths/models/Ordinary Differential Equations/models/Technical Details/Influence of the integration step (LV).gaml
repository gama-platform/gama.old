/***
* Name: Influence of the integration step
* Author: Tri, Nghi, Benoit
* Description:  The aim is to show the influence of the integration step on the result precision.
* 				The solutions of the Lotka-Volterra are periodic. When the integration step is not
* 				small enough, this periodicity is lost, as illustrated with h=1.
* 
* Tags: equation, math
***/

model LVInfluenceoftheIntegrationstep

global {
	init {
		create userLV with: [h::0.01,x::2.0,y::2.0];	
		create userLV with: [h::1.0,x::2.0,y::2.0];									
	}
}

species userLV {
	float t;
	float x ; 
	float y ; 
	float h;
	float alpha <- 0.8 ;
	float beta  <- 0.3 ;
	float gamma <- 0.2 ;
	float delta <- 0.85;
	
	equation eqLV { 
		diff(x,t) =   x * (alpha - beta * y);
		diff(y,t) = - y * (delta - gamma * x);
    }		
    
	reflex solving {
		solve eqLV method: #rk4 step_size: h;
	}
}

experiment examples type: gui {
	float minimum_cycle_duration <- 0.05#s;
	output {	
		layout horizontal([vertical([0::100,1::100])::100,2::100]) tabs: true;	
		display "h=0.01"  toolbar: false  type: 2d {
			chart 'Lotka-Voltera dynamics (time series)' type: series 
				background: rgb(47,47,47) color: #white
				y_label: "pop" x_tick_line_visible: false{
				data "x (h=0.01)" value: first(userLV).x[] color: rgb(52,152,219) marker: false thickness: 2;
				data "y (h=0.01)" value: first(userLV).y[] color: rgb(41,128,185) marker: false thickness: 2;
//				data "x (h=1)" value: last(userLV).x color: rgb(243,156,18) marker: false thickness: 2;
//				data "y (h=1)" value: last(userLV).y color: rgb(230,126,34) marker: false thickness: 2;				
			}			
		}
		display "h=1"   toolbar: false  type: 2d {
			chart 'Lotka-Voltera dynamics (time series)' type: series 
			background: rgb(47,47,47) color: #white
			y_label: "pop" x_tick_line_visible: false{
//				data "x (h=0.01)" value: first(userLV).x color: rgb(52,152,219) marker: false thickness: 2;
//				data "y (h=0.01)" value: first(userLV).y color: rgb(41,128,185) marker: false thickness: 2;
				data "x (h=1)" value: last(userLV).x[] color: rgb(243,156,18) marker: false thickness: 2;
				data "y (h=1)" value: last(userLV).y[] color: rgb(230,126,34) marker: false thickness: 2;				
			}			
		}		
		display "Phase Portrait"  toolbar: false  type: 2d  {
		chart 'Lotka-Voltera dynamics (phase portrait)' type: xy 
			background: rgb(47,47,47) color: #white y_label: "y" x_label: "x"
			x_range: {0,8} y_range: {0,5.3}{
				data "h=0.01" value: [first(userLV).x,first(userLV).y] color: rgb(52,152,219) marker: false;			
				data "h=1" value: [last(userLV).x,last(userLV).y] color: rgb(243,156,18) marker: false;			
			}		
		}						
	}
}