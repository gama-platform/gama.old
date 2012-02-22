/**
 *  HowToImportRasterAndVectoriel
 *  Author: Maroussia Vavasseur and Benoit Gaudou
 *  Description: Importation of 1 raster and 2 vectorials data
 */

model HowToImportRasterAndVectoriel



global {
	// Constants
	const heightImg type: int init: 5587;
	const widthImg type: int init: 6201;	
	const boundsMNT type: file init: "../images/mnt/boundsMNT.shp"; 
	
	// Parameters related to the MNT
	var mntImageRaster type: file init: '../images/mnt/testAG.png' parameter: 'MNT file' category: 'MNT' ;
	var factorDiscret type: int init:20 parameter:'Discretization factor' category:'MNT';
	
	// Parameters related to the Management units	
	var ManagementUnitShape type: file init: '../images/ug/UGSelect.shp' parameter: 'Management unit:' category: 'MU' ;
	
	// Parameters related to the water network
	var waterShape type: file init: '../images/reseauHydro/reseauEau.shp' parameter: 'Rivers shapefile' category: 'Water';

	// Parameters related to izard agents
	var nbIzard type: int init: 25 parameter: 'Nb of Izards' category: 'Izard';
	var izardShape type: file init:'../images/icons/izard.gif' parameter: 'Izard Shape' category: 'Izard';


	// Local variable
	var mapColor type: matrix ; 
			
	// Initialization of grid and creation of the izard agents.
	// Creation of managmentUnit and rivers agents from the corresponding shapefile
	init {
		create species: managementUnit from: ManagementUnitShape.path 
				with: [MUcode::read('Code_UG'), MULabel::read('Libelle_UG'), pgeSAGE::read('PGE_SAGE')] ;
				
		create species: river from: waterShape.path;
				
		set mapColor value: mntImageRaster as_matrix {widthImg/factorDiscret,heightImg/factorDiscret} ;
		ask target: cell as list {		
			set color value: mapColor at {grid_x,grid_y} ;
		}
		create species: izard number: nbIzard; 			
    }
}


// We create a grid as environment with the same dimensions as the matrix in which we want to store the image
// The environment bounds are defined using the hand-made boundsMNT shapefile.
// This shapefile has been created as a georeferenced bounding box of the MNT raster image, using information of the .pgw file
environment bounds: boundsMNT.path {
	grid cell  width: widthImg/factorDiscret height: heightImg/factorDiscret;
}

entities {	
	species river {
		aspect basic{
			draw shape: geometry color: rgb('blue');
		}	
	}
	
	species managementUnit{
		var MUcode type: int;
		var MULabel type: string;
		var pgeSAGE type: string;
		
		aspect basic{
    		draw shape: geometry;
    	}
	}	
	species izard {	
		init{
			set location value: one_of(cell as list where (empty(each.agents) and each.color = rgb('green')) ) ;
		}		
		aspect basic{
    		draw shape: square color: rgb('orange') size: 5000;
    	}
    	aspect image{
    		draw image: izardShape.path size: 5000;
    	}
	}	
}

// We display:
// - the original MNT image as background
// - the grid representing the MNT
// - izard agents
// - the management unit shapefile
// - the river shapefile
// We can thus compare the original MNT image and the discretized image in the grid.
// For cosmetic need, we can choose to not display the grid. 

output {
	display HowToImportVectorial {
        image name: 'Background' file: mntImageRaster.path;  		
       	grid cell;
 		species managementUnit aspect: basic transparency: 0.5;
 		species river aspect: basic;
 		species izard aspect: image;  
	}
    inspect name: 'Species' type: species refresh_every: 5;
}


