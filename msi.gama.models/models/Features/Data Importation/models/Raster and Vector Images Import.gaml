/**
 *  HowToImportRasterAndVectoriel
 *  Author: Maroussia Vavasseur and Benoit Gaudou
 *  Description: Importation of 1 raster and 2 vectorials data
 */

model HowToImportRasterAndVectoriel


 
global {
	// Constants
	const heightImg type: int <- 5587;
	const widthImg type: int <- 6201;	
	const boundsMNT type: file <- file("../images/mnt/boundsMNT.shp"); 
	
	
// The environment bounds are defined using the hand-made boundsMNT shapefile.
// This shapefile has been created as a georeferenced bounding box of the MNT raster image, using information of the .pgw file
	geometry shape <- envelope(boundsMNT);
	
	// Global variables related to the MNT
	file mntImageRaster <- image_file('../images/mnt/testAG.jpg') ;
	int factorDiscret  <- 20;
	
	// Global variables  related to the Management units	
	file ManagementUnitShape <- file('../images/ug/UGSelect.shp');
	
	// Global variables  related to the water network
	file waterShape <- file('../images/reseauHydro/reseauEau.shp');

	// Global variables  related to izard agents
	int nbIzard <- 25 ;
	file izardShape <-file('../images/icons/izard.gif') ;
		
	// Initialization of grid and creation of the izard agents.
	// Creation of managmentUnit and rivers agents from the corresponding shapefile
	init {
		create managementUnit from: ManagementUnitShape 
				with: [MUcode::int(read('Code_UG')), MULabel::string(read('Libelle_UG')), pgeSAGE::string(read('PGE_SAGE'))] ;
				
		create river from: waterShape.path;
				
		matrix<int> mapColor <- matrix<int>(mntImageRaster as_matrix {widthImg/factorDiscret,heightImg/factorDiscret}) ;
		ask cell {		
			color <- rgb( mapColor at {grid_x,grid_y} );
		}
		create izard number: nbIzard; 			
    }
}



entities {	
	species river {
		aspect basic{
			draw  shape color: rgb('blue');
		}	
	}
	
	species managementUnit{
		int MUcode;
		string MULabel;
		string pgeSAGE;
		
		aspect basic{
    		draw shape;
    	}
	}	
	species izard {	
		init{
			set location <- (shuffle(cell) first_with ((each.color != rgb('white')) and (empty(agents overlapping each)))).location ;
		}	
		aspect basic{
    		draw square(5000) color: rgb('orange');
    	}
    	aspect image{
    		draw izardShape size: 5000;
    	}
	}	
	
// We create a grid as environment with the same dimensions as the matrix in which we want to store the image

	grid cell  width: widthImg/factorDiscret height: heightImg/factorDiscret;
	
}

experiment main type: gui {
	// Parameters
	parameter 'MNT file' var: mntImageRaster category: 'MNT' ;
	parameter'Discretization factor' var: factorDiscret category:'Environment';
	
	parameter 'Nb of Izards' var: nbIzard category: 'Izard'; 
	parameter 'Izard Shape' var: izardShape category: 'Izard';
	
	parameter 'Management unit' var: ManagementUnitShape category: 'MU' ;
	
	parameter 'Rivers shapefile' var: waterShape category: 'Water';
	
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
	}
}


