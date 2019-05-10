/***
* Name: networktest
* Author: nicolas
* Description: 
* Tags: Tag1, Tag2, TagN
***/

model networktest

experiment "Tests for ummisco.gama.network" type: test skills:[network] {
	test "Action connect to the MQTT server" {
		try{ 
			do connect with_name:"Travis";
			 assert true;
		} catch {
			 assert false;
		} 
	}
}