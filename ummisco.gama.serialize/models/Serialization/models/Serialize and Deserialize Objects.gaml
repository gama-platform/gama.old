/**
* Name: SerializeandDeserializeObjects
* Shows how to serialize and deserialize arbitrary objects 
* Author: A. Drogoul
* Tags: serialization
*/


model SerializeandDeserializeObjects

global {
	init {
		
		list objects <- [[1,2,3,4], "fff",rgb(100,100,100)];
		
		loop o over: objects {
			string s <- serialize(o,'binary');
			write deserialize(s);
		}
		loop o over: objects {
			string s <- serialize(o,'json');
			write deserialize(s);
		}
		loop o over: objects {
			string s <- serialize(o,'xml');
			write deserialize(s);
		}
		list o2 <- deserialize(serialize(objects), list);
		write serialize(objects, 'json', false);
	}  
}

experiment "Run";

