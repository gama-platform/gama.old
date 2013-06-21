model RiceRegionLandUse

global {
	var WS_SHAPE_LAND_USE type: string init: '../includes/gis/ecologies/LandUse_WS_Region.shp' parameter: 'Land Use File:' category: 'RICE_REGION' ;
	var SA_SHAPE_LAND_USE type: string init: '../includes/gis/ecologies/LandUse_SA_Region.shp' parameter: 'Land Use File:' category: 'RICE_REGION' ;
	
}
entities {
	species WS_rice_region{
		var id type: string ;
		var description type: string ;
		var color type: rgb init: rgb([64, 255, 64]);
	}
	
	species SA_rice_region{
		var id type: string ;
		var description type: string ;
		var color type: rgb init: rgb([180, 0, 0]);
	}
	
}
output ;
