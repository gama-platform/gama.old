model move   

global {
	int layerSize parameter: 'Layer size' min: 10 <- 50 ;
	file folder <- folder('images/graph/');
	file folder <- folder('/Users/macbookpro/Desktop/cover');

	int nb_layer<-0;

	init { 			
		 loop fileName over: folder {	 	
		 	if(string(fileName) contains '.png' or string(fileName) contains '.jpg'){
		 			
			 	create ImageAgent{
				  set location <-{0,0};
				  set image<-file(folder.path+"/"+fileName);
				  set label<-fileName;
				  set shape <- shape add_z(layerSize*nb_layer);			
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
	  
	  /*reflex move_Z{
	  	set shape <- shape add_z(layerSize*rnd(nb_layer));	
	  }*/	
	  
	  reflex changeImage{
	  	let filename<- one_of (folder);
	  	set image <- file(folder.path+"/" + filename);
	  }	
	  aspect image{
		draw image:image.path  size : layerSize;
	  }
	}
}
experiment display  type: gui {
	parameter 'Folder:' var:folder   category:'Folder';
	
	output {
		display Display refresh_every: 1 type:opengl{
			species ImageAgent aspect:image;
		}
	}
}
