/**
 *  HowToImportRaster
 *  Author: Maroussia Vavasseur and Benoit Gaudou
 *  Description: Importation of a vectorial image
 */

model HowToImportVectorial



global {
	// Global variables related to the Management units	
	file ManagementUnitShape <- file('../images/ug/UGSelect.shp'); 
	
	//definition of the environment size from the shapefile. 
	//Note that is possible to define it from several files by using: geometry shape <- envelope(envelope(file1) + envelope(file2) + ...);
	geometry shape <- envelope(ManagementUnitShape);
	
	init {
		//Creation of managmentUnit agents from the shapefile (and reading of the shapefile attributes)
		create managementUnit from: ManagementUnitShape 
			with: [MUcode::int(read('Code_UG')), MULabel::string(read('Libelle_UG')), pgeSAGE::string(read('PGE_SAGE'))] ;
    }
}


	
species managementUnit{
	int MUcode;
	string MULabel;
	string pgeSAGE;
	
	aspect basic{
		draw shape color: #yellow;
	}
}	


experiment main type: gui {
	parameter 'Management unit' var: ManagementUnitShape category: 'MU' ;
		
	output {
		display HowToImportVectorial {
	   		species managementUnit aspect: basic; 
		}
	}
}


