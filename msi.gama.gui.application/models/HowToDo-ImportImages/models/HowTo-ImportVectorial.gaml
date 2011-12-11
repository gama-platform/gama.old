/**
 *  HowToImportRaster
 *  Author: Maroussia Vavasseur and Benoit Gaudou
 *  Description: Importation of a vectorial image
 */

model HowToImportVectorial



global {
	var ManagementUnitShape type: file init: '../images/ug/UGSelect.shp' parameter: 'Management unit:' category: 'GIS' ;
	
	init {
		create species: managementUnit from: ManagementUnitShape.path 
			with: [MUcode::read('Code_UG'), MULabel::read('Libelle_UG'), pgeSAGE::read('PGE_SAGE')] ;
    }
}

environment bounds: ManagementUnitShape.path {}

entities {	
	species managementUnit{
		var MUcode type: int;
		var MULabel type: string;
		var pgeSAGE type: string;
		
		aspect basic{
    		draw shape: geometry;
    	}
	}	
}

output {
	display HowToImportVectorial {
   		image name: 'GISBackground' gis: ManagementUnitShape.path color: rgb('blue');
   		species managementUnit aspect: basic; 
	}
    inspect name: 'Species' type: species refresh_every: 5;
}


