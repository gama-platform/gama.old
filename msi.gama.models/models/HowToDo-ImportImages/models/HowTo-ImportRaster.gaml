/**
 *  HowToImportRaster
 *  Author: Maroussia Vavasseur and Benoit Gaudou
 *  Description: Importation of a raster image
 */

model HowToImportRaster

global {	
	// Constants 
	const heightImg type: int init: 5587;
	const widthImg type: int init: 6201;	 
	 
	// Parameters
	var mntImageRaster type: file init: '../images/mnt/testAG.png' parameter: 'MNT file' category: 'MNT' ;
	var factorDiscret type: int init:30 parameter:'Discretization factor' category:'Environment';
	
	var nbIzard type: int init: 25 parameter: 'Nb of Izards' category: 'Izard';
	var izardShape type: file init:'../images/icons/izard.gif' parameter: 'Izard Shape' category: 'Izard';
	
	// Local variable
	// var mapColor type: matrix ;
			
	// Initialization of grid and creation of the izard agents
	// - we use the as_matrix operator to transform a image file into a matrix of colors 
	// (Note that as_matrix takes a Point as right operand, this point specifies the number of lines and columns of the matrix)
	// - we then set the color built-in attribute of the cell with the value of the corresponding matrix cell
	init {
		// set mapColor value: mntImageRaster as_matrix {widthImg/factorDiscret,heightImg/factorDiscret} ;
		ask target: cell as list {		
			set color value: mntImageRaster at {grid_x,grid_y} ;
		}
		create species: izard number: nbIzard; 
    }
}
 
// We create a grid as environment with the same dimensions as the matrix in which we want to store the image
// Note that the height (resp. the width) of the grid corresponds to the number of rows (resp. of columns) of the matrix:
// - in the creation of a matrix: ([...] as_matrix {widthImg/factorDiscret,heightImg/factorDiscret} ;)
// - in the creation of the grid: grid cellule width: widthImg/factorDiscret height: heightImg/factorDiscret;

environment {
	grid cell  width: widthImg/factorDiscret height: heightImg/factorDiscret;
}

// We create izard agents and locate them on one random 'cellule' among the list of cellules empty ('empty(each.agents)') 
// and with white color 'each.color = rgb('white')'

entities {
	species izard {	
		init{
			set location value: one_of(cell as list where (empty(each.agents) and each.color = rgb('green')) ) ;
		}		
		aspect basic{
    		draw shape: square color: rgb('orange') size: 1;
    	}
    	aspect image{
    		draw image: izardShape.path;
    	}
	}
}

// We display:
// - the grid
// - the original MNT image as background
// - izard agents
// We can thus compare the original MNT image and the discretized image in the grid.
// For cosmetic need, we can choose to not display the grid. 

output {
	display HowToImportRaster {
       grid cell;
       image name: 'Background' file: mntImageRaster.path;
       species izard aspect: image; 
    }   
    inspect name: 'Species' type: species refresh_every: 5;
}
