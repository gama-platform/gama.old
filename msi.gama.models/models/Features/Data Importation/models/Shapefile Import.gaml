/**
* Name: Shapefiles Import
* Author: Maroussia Vavasseur and Benoit Gaudou
* Description: Model to show how to import data from a Shapefile, and create agents from a species from its shapefile, linking the  columns of 
*       the shapefile with the attributes of the species. The experiment display management units and shows their name coming from the 
* 	shapefile.
* Tag :  Import Files, GIS, Shapefiles
*/


model HowToImportVectorial



global {
	// Global variables related to the Management units	
	file ManagementUnitShape <- file('../images/ug/UGSelect.shp'); 
	
	//definition of the environment size from the shapefile. 
	//Note that is possible to define it from several files by using: geometry shape <- envelope(envelope(file1) + envelope(file2) + ...);
	geometry shape <- envelope(ManagementUnitShape);
	
	init {
		//Creation of managmentUnit agents from the shapefile: the MUcode, MULabel and pgeSAGE attributes of the agents are initialized according to the Code_UG, Libelle_UG and PGE_SAGE attributes of the shapefile
		create managementUnit from: ManagementUnitShape 
			with: [MUcode::int(read('Code_UG')), MULabel::string(get('Libelle_UG')), pgeSAGE::string(get('PGE_SAGE'))] ;
    }
}


	
species managementUnit{
	int MUcode;
	string MULabel;
	string pgeSAGE;
	
	aspect basic{
		draw shape color: #yellow;
		draw MULabel color:#black;
	}
}	


experiment main type: gui {
		
	output {
		display HowToImportVectorial {
	   		species managementUnit aspect: basic; 
		}
	}
}


