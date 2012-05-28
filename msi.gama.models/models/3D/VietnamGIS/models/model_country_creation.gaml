model Asian_GIS

global {
    file vietnam_shape_file<- '../includes/GIS/Vietnam/VNM_adm/VNM_adm0.shp' parameter: 'Shapefile for the vietnam:' category: 'GIS' ;
    file thailand_shape_file<- '../includes/GIS/Thailand/THA_adm/THA_adm0.shp' parameter: 'Shapefile for the thailand:' category: 'GIS' ;
    file laos_shape_file<- '../includes/GIS/Laos/LAO_adm/LAO_adm0.shp' parameter: 'Shapefile for the laos:' category: 'GIS' ;
    file cambodia_shape_file<- '../includes/GIS/Cambodia/KHM_adm/KHM_adm0.shp' parameter: 'Shapefile for the cambodia:' category: 'GIS' ;
    file malaysia_shape_file<- '../includes/GIS/Malaysia/MYS_adm/MYS_adm0.shp' parameter: 'Shapefile for the malaysia:' category: 'GIS' ;
    file myanmar_shape_file<- '../includes/GIS/Myanmar/MMR_adm/MMR_adm0.shp' parameter: 'Shapefile for the myanmar:' category: 'GIS' ;
 
       
    init {
        create country from : vietnam_shape_file{ 
        	set  color <- rgb("red");   
        }  
        create country from : thailand_shape_file{
        	set  color <- rgb("yellow");  
        }  
        
        create country from : laos_shape_file{
        	set  color <- rgb ([0, 125,125]); 
        }
        
        create country from : cambodia_shape_file{
        	set  color <-  rgb ([125, 125, 0]);   
        }
        
        create country from : malaysia_shape_file{
        	set  color <- rgb ([125, 0, 125]);  
        }
        
        create country from : myanmar_shape_file{
        	set  color <- rgb("black");  
        }  
   

    }   
}

entities {

    species country{
        rgb color;
        string name;
        aspect default{
            draw shape: geometry color:color empty:false ;
            }
    }
}

environment bounds: [vietnam_shape_file,thailand_shape_file,laos_shape_file,cambodia_shape_file,malaysia_shape_file,myanmar_shape_file] ;
output {
    display asian_display type:opengl   {
        species country aspect: default;
    }
}
