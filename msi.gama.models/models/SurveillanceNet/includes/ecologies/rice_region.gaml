model RiceRegionLandUse

global {
	var SHAPE_LAND_USE type: string init: '../includes/gis/ecologies/LandUse_WS_Region.shp' parameter: 'Land Use File:' category: 'RICE_REGION' ;
	
}
entities {
	species rice_region{
		var id type: string ;
		var description type: string ;
		var color type: rgb init: rgb([64, 255, 64]);
	}
}
output ;
