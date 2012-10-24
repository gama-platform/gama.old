model Geographics
// gen by Xml2Gaml
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global ;
environment ;
entities {
	species GeographicalFunction skills: situated {
		var R type: float init: 6371 ;
		var dLon type: float init: 0 ;
		var dLat type: float init: 0 ;
		var a type: float value: 0 ;
		var c type: float value: 0 ;
		action calculate_distance {
			arg lonParam1 type: float ;
			arg latParam1 type: float ;
			arg lonParam2 type: float ;
			arg latParam2 type: float ;
			set dLon value: lonParam2 - lonParam1 ;
			set dLat value: latParam2 - latParam1 ;
			set a value: sin(dLat/2) * sin(dLat/2) + cos(latParam1) * cos(latParam2) *  sin(dLon/2) * sin(dLon/2) ;
			return value: R * c ;
		}
	}
}
output ;
