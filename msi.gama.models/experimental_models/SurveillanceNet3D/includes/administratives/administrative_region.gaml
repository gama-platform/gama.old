model AdministrativeRegion

global {
	var SHAPE_ADMINISTRATIVE_PROVINCE type: string init: '../includes/gis/administratives/VNM_Province.shp' parameter: 'FILE OF PROVINCE AREAS:' category: 'ADMINISTRATIVE REGIONS' ;
	var SHAPE_ADMINISTRATIVE_DISTRICT type: string init: '../includes/gis/administratives/VNM_district.shp' parameter: 'FILE OF DISTRICT AREAS:' category: 'ADMINISTRATIVE REGIONS' ;
	var SHAPE_ADMINISTRATIVE_SMALLTOWN type: string init: '../includes/gis/administratives/VNM_smalltown.shp' parameter: 'FILE OF SMALLTOWN AREAS:' category: 'ADMINISTRATIVE REGIONS' ;
	var SHAPE_ADMINISTRATIVE_THREE_PROVINCES type: string init: '../includes/gis/administratives/VNM_Province_3_Provinces.shp' parameter: 'FILE OF PROVINCE AREAS (BL, ST, HG):' category: 'ADMINISTRATIVE REGIONS' ;
	var SHAPE_SEA_REGION type: string init: '../includes/gis/naturals/SeaRegion.shp' parameter: 'SEA REGION:' category: 'SEA REGIONS' ;
	
}

entities {
	species province_region {
		var id_1 type: string ;
		var region_name type: string ;
		var id_2 type: string ;
		var province_name type: string ;
		
		// RICE AGE:
		var rice_age type: float init: 0.0;
		
		var color type: rgb init: rgb('white') ;
		//aspect
		//{
			//draw text: province_name color: rgb('white') size: 100 at: {0, 0};
			//draw text: state color: rgb('white') size: 1 at:my location + {1,1};
		//}
	}
	
	species db skills:[SQLSKILL]{
		
	}
	species district_region  {
		var id_1 type: string ;
		var region_name type: string ;
		var id_2 type: string ;
		var province_name type: string ;
		var id_3 type: string ;
		var district_name type: string ;
		var transplantation_index type: float init: 0.0 ;
		var number_of_BPHs type: float init: 0.0 ;
		
		var color type: rgb init: rgb('white') ;
		var lighttrap_density_min type: float init: 0.0 ;
		var lighttrap_density_mean type: float init: 0.0 ;
		var lighttrap_density_max type: float init: 0.0 ;
		var propagated_density_min type: float init: 0.0 ;
		var propagated_density_mean type: float init: 0.0 ;
		var propagated_density_max type: float init: 0.0 ;
		var estimated_density_min type: float init: 0.0 ;
		var estimated_density_mean type: float init: 0.0 ;
		var estimated_density_max type: float init: 0.0 ;
	}

	species smalltown_region  {
		var id_1 type: string ;
		var region_name type: string ;
		var id_2 type: string ;
		var province_name type: string ;
		var id_3 type: string ;
		var district_name type: string ;
		var id_4 type: string ;
		var smalltown_name type: string ;
		var color type: rgb init: rgb('white') ;
		init {
			set color value: rgb((id_2 = '38253') ? 'blue' : 'white') ;
		}
	}
	species administrative_region  {
		var id_1 type: string ;
		var region_name type: string ;
		var id_2 type: string ;
		var province_name type: string ;
		var id_3 type: string ;
		var district_name type: string ;
		var id_4 type: string ;
		var smalltown_name type: string ;
		var color type: rgb init: rgb('white') ;
		init {
			set color value: rgb((id_2 = '38253') ? 'blue' : 'white') ;
		}
	}
	
	species sea_region{
		var description type: string ;
		var color type: rgb init: rgb('blue') ;
	}
}
output {}
