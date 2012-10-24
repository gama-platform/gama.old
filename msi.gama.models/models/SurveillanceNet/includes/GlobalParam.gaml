/**
 *  Insect Surveillance Network Model
 *  Author: Truong Xuan Viet
 *  Last Modified Date: 05-03-2012 
 */

model GlobalParam
//import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	// Sensor number:
	var no_of_sensor type: int init: 100 ;
	
	
	// Thresholds:
	var MAX_DENSITY type: float value: 1000000;
	var MAX_DENSITY type: float value: 0;
	
	// Disk graph radius:
	var DISK_RADIUS type: float value: 15000.0;
	var CORRELATION_THRESHOLD type: float value: 0.3;
	
	// IDW_RANGE:
	var IDW_RANGE type: float value: 40000.0;
	
	var HISTORICAL_DURATION type: int value: 365;
	
	// GLOBAL SCALE (APPLIED ONLY IF THE PROJECTION IS FAILED):
	//var GLOBAL_SCALE type: float value: 110556.6920356;
	
	// GRID VARIABLES:
	var COLUMNS_NO type: int value: 60;
	var ROWS_NO type: int value: 60;
	
	
	 
	
}
output ;
