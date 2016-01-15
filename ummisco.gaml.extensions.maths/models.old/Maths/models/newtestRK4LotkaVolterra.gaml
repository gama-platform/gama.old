/**
 *  comodel
 *  Author: Administrator
 *  Description: 
 */

model new

global {

	float alpha <- 0.8 min: 0.0 max: 1.0;
	float beta <- 0.3 min: 0.0 max: 1.0;
	float gamma <- 0.2 min: 0.0 max: 1.0;
	float delta <- 0.85 min: 0.0 max: 1.0;
	
	float xInit <- 2.0 min: 0.0;
	float yInit <- 2.0 min: 0.0;
	
	float hKR4 <- 0.01;
	float nbSteps <- 1.0;
	init{
		create my_maths number:1;
	}
}

environment  width: 10 height: 10;

entities {
species my_maths {
    float t;    
//    float S;
//    float I;
//    float R;
//    float mi<-0.0;
//    float up<-0.0;
//    float N<-0.0;
//    float f<-0.0;
//    float beta<-0.1;
//    float gamma<-0.0;
//	float y0<-0.0;
//	float y1<-1.0;
//	float c0<-1.0;
//	float c1<-1.0;
//	float omega<-0.1;
	float x <- xInit;
	float y <- yInit;
		
	
   
	equation SIRS{ 
			diff(x,t)=x * (alpha - beta * y);
			diff(y,t)=- y * (delta - gamma * x);
//			diff(y0,t)=omega*(c1-y1);
//			diff(y1,t)=omega*(y0-c0);
//			diff(S,t) =S;// (- beta * S * I) + mi * ( N - S) + (f * R);
//			diff(I,t) =I;// (beta * S * I) - (gamma * I) - (mi * I);
//			diff(R,t) =R;// (gamma * I) - (mi * R) - (f * R);		
        }
                init {
        solve SIRS method: "rk4" step:0.001{// with:[S::1500.0,I::1.0,R::0.0 ]{ 
        }}
        
}
}

experiment maths type: gui {
	output { 
		display LV refresh_every: 1 {
			chart "SIR" type: series background: rgb('white') {
				data 'x' value: first(list(my_maths)).x color: rgb('green') ;				
				data 'y' value: first(list(my_maths)).y color: rgb('red') ;
			}
		}
		display LVphase refresh_every: 1 {			
			chart "SIR" type: xy background: rgb('white') {
				data 'x' value: first(list(my_maths)).x color: rgb('green') ;				
				data 'y' value: first(list(my_maths)).y color: rgb('red') ;
			}
		}			
	}
}
