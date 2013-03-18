/**
 *  Insect Surveillance Network Model
 *  Author: Truong Xuan Viet
 *  Last Modified Date: 11-12-2012 
 */

model GlobalParam
//import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	// Sensor number:
	var no_of_sensor type: int init: 100 ;
	
	// Simulation step:
	var SIMULATION_STEP type: int init: 0;
	
	// Thresholds:
	var MAX_DENSITY type: float init: 0.0;
	
	// Disk graph radius:
	var DISK_RADIUS type: float init: 15000.0;
	var LOWEST_RADIUS type: float init: 5000.0;
	var CORRELATION_THRESHOLD type: float init: 0.3;
	
	// IDW_RANGE:
	var IDW_RANGE type: float init: 40000.0;
	
	// Density data
	var HISTORICAL_DURATION type: int init: 365;
	
	// LIFE CYCLE OF BROWN PLANT HOPPER
	var BPH_LIFE_DURATION type: int <- 32;
	var ADULT_EGG_RATE type: float <- 250.0; // Long wing: 100; Short wing: 400
	var EGG_NYMPH_RATE type: float <- 0.3;
	var NYMPH_ADULT_RATE type: float <- 0.4;
	var NATURAL_MORTALITY_RATE type: float <- 0.05; //0.035;
	var EGG_DURATION type: float value: 7.0;
	var NYMPH_DURATION type: float value: 13.0;
	var ADULT_DURATION type: float value: 12.0;
	var ADULT_DURATION_GIVING_BIRTH_DURATION type: float value: 6.0;
	
	
		
	// GRID VARIABLES:
	var COLUMNS_NO type: int value: 60;
	var ROWS_NO type: int value: 60;
	
	// MANAGEMENT PARAMETERS
	var LIMIT_OF_NODES type: int value: 80;
	var NUMBER_OF_ADDED_NODES type: int value: 30;
	
	// TRANSPLANTATION (Used for the optimization model):
	var WINTER_SPRING_SEASON_COEF type: float value: 0.4;
	var SUMMER_AUTUMN_SEASON_COEF type: float value: 0.4;
	var BASED_SEASON_COEF type: float value: 0.2;
	
	// TRANSPLANTATION (Used for the migration model):
	var RICE_AGE type: float value: 60.0; // WINTER_SPRING: 11th month, DATA: 1rst month
	
	// RDBMS PARAMETERS
	var PARAMS type:map init: ['host'::'localhost','dbtype'::'sqlserver','database'::'SurveillanceDB','port'::'1433','user'::'sa','passwd'::'25111978*']; 
	
}
output ;
