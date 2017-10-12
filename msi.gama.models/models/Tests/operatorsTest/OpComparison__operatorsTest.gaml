/**
 *  OpOpComparison__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpComparison__operatorsTest.
 */

model OpComparison__operatorsTest

global {
	init {
		create testOpComparison__operatorsTest number: 1;
		ask testOpComparison__operatorsTest {do _step_;}
	}
}


	species testOpComparison__operatorsTest {

	
		test betweenOp {
			(date('2016-01-01') between(date('2000-01-01'), date('2020-02-02') -: true
			between(date('2000-01-01'), date('2020-02-02') // will return true if the current_date of the model is in_between the 2
			(date('2016-01-01') between(date('2000-01-01'), date('2020-02-02') -: true
			every #day between(date('2000-01-01'), date('2020-02-02') // will return true every new day between these two dates, taking the first one as the starting point
			bool var4 <- between(5, 1, 10); 	// var4 equals true
			assert var4 equals: true; 
			bool var5 <- between(5.0, 1.0, 10.0); 	// var5 equals true
			assert var5 equals: true; 

		}
	
		test DifferentOp {
			bool var0 <- 3.0 != 3.0; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- 4.0 != 4.7; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- 3.0 != 3; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- 4.7 != 4; 	// var3 equals true
			assert var3 equals: true; 
			#now != #now minus_hours 1 :- true
			bool var5 <- 3 != 3.0; 	// var5 equals false
			assert var5 equals: false; 
			bool var6 <- 4 != 4.7; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- [2,3] != [2,3]; 	// var7 equals false
			assert var7 equals: false; 
			bool var8 <- [2,4] != [2,3]; 	// var8 equals true
			assert var8 equals: true; 

		}
	
		test Different2Op {

		}
	
		test EqualsOp {
			bool var0 <- [2,3] = [2,3]; 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- 4 = 5; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 4.5 = 4.7; 	// var2 equals false
			assert var2 equals: false; 
			#now = #now minus_hours 1 :- false
			bool var4 <- 3 = 3.0; 	// var4 equals true
			assert var4 equals: true; 
			bool var5 <- 4 = 4.7; 	// var5 equals false
			assert var5 equals: false; 
			bool var6 <- 4.7 = 4; 	// var6 equals false
			assert var6 equals: false; 

		}
	
		test GEOp {
			bool var0 <- 'abc' >= 'aeb'; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- 'abc' >= 'abc'; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- {5,7} >= {4,6}; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- {5,7} >= {4,8}; 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- 3.5 >= 3.5; 	// var4 equals true
			assert var4 equals: true; 
			#now >= #now minus_hours 1 :- true
			bool var6 <- 3 >= 2.5; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3.5 >= 7; 	// var7 equals false
			assert var7 equals: false; 
			bool var8 <- 3 >= 7; 	// var8 equals false
			assert var8 equals: false; 

		}
	
		test GTOp {
			bool var0 <- {5,7} > {4,6}; 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- {5,7} > {4,8}; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 'abc' > 'aeb'; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- 3.5 > 7; 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- 3.5 > 7.6; 	// var4 equals false
			assert var4 equals: false; 
			#now > #now minus_hours 1 :- true
			bool var6 <- 3 > 2.5; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3 > 7; 	// var7 equals false
			assert var7 equals: false; 

		}
	
		test LEOp {
			bool var0 <- {5,7} <= {4,6}; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- {5,7} <= {4,8}; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 'abc' <= 'aeb'; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- 7.0 <= 7; 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- 3 <= 2.5; 	// var4 equals false
			assert var4 equals: false; 
			#now <= #now minus_hours 1 :- false
			bool var6 <- 3.5 <= 3.5; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3 <= 7; 	// var7 equals true
			assert var7 equals: true; 

		}
	
		test LTOp {
			bool var0 <- {5,7} < {4,6}; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- {5,7} < {4,8}; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 'abc' < 'aeb'; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- 3.5 < 7.6; 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <- 3.5 < 7; 	// var4 equals true
			assert var4 equals: true; 
			#now < #now minus_hours 1 :- false
			bool var6 <- 3 < 7; 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- 3 < 2.5; 	// var7 equals false
			assert var7 equals: false; 

		}
	
	}


experiment testOpComparison__operatorsTestExp type: gui {}	
	