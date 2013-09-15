model move   

global {
	int layerSize parameter: 'Layer size' min: 10 <- 50 ;
	file folder <- folder('images/graph/');

	int nb_layer<-0;

	init { 			
		 loop fileName over: folder {	 	
		 	if(string(fileName) contains '.png' or string(fileName) contains '.jpg'){ 			
			 	create ImageAgent{
				  set location <-{0,0};
				  set image<-file(folder.path+"/"+fileName);
				  set label<-fileName;
				  set location <- {location.x,location.y,layerSize*nb_layer};
			    }
			    set nb_layer <- nb_layer+1;	
		 	} 	
		} 			
	}  
} 
 
 
environment;  
 
entities { 
	species ImageAgent skills: [moving]{
	  file image;
	  string label;	
	  
	  reflex changeImage{
	  	
	  	let filename<- one_of (folder);
	  	if (string(filename) contains '.png' or string(filename) contains '.jpg'){
	  		set image <- file(folder.path+"/" + filename);
	  	}
	  }	
	  aspect image{
		draw image  size : layerSize;
	  }
	}
}
experiment Display  type: gui {
	parameter 'Folder:' var:folder   category:'Folder';
	
	output {
		display Display refresh_every: 1 type:opengl{
			species ImageAgent aspect:image;
		}
	}
}
