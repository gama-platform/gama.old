/**
 *  OpOpDate_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpDate_related__operatorsTest.
 */

model OpDate_related__operatorsTest

global {
	init {
		create testOpDate_related__operatorsTest number: 1;
		ask testOpDate_related__operatorsTest {do _step_;}
	}
}

	species testOpDate_related__operatorsTest {

	
		test add_daysOp {

		}
	
		test add_hoursOp {

		}
	
		test add_minutesOp {

		}
	
		test add_monthsOp {

		}
	
		test add_msOp {

		}
	
		test add_secondsOp {

		}
	
		test add_weeksOp {

		}
	
		test add_yearsOp {

		}
	
		test afterOp {
			reflex when: after(starting_date) {} -: will always be run after the first step
			reflex when: false after(starting date + #10days) {} -: will not be run after this date. Better to use 'until' or 'before' in that case
			every(2#days) after (starting_date + 1#day) // the computation will return true every two days (using the starting_date of the model as the starting point) only for the dates strictly after this starting_date + 1#day

		}
	
		test beforeOp {
			reflex when: before(starting_date) {} -: will never be run

		}
	
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
	
		test everyOp {
			reflex when: every(2#days) since date('2000-01-01') { .. }
			state a { transition to: b when: every(2#mn);} state b { transition to: a when: every(30#s);} // This oscillatory behavior will use the starting_date of the model as its starting point in time
			if every(2) {write "the cycle number is even";}
				     else {write "the cycle number is odd";}
			(date('2000-01-01') to date('2010-01-01')) every (#month) // builds an interval between these two dates which contains all the monthly dates starting from the beginning of the interval

		}
	
		test fromOp {

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
	
		test milliseconds_betweenOp {
			milliseconds_between(d1, d2) -: 10 

		}
	
		test MinusOp {
			rgb var7 <- rgb([255, 128, 32]) - 3; 	// var7 equals rgb([252,125,29])
			assert var7 equals: rgb([252,125,29]); 
			rgb var8 <- rgb([255, 128, 32]) - rgb('red'); 	// var8 equals rgb([0,128,32])
			assert var8 equals: rgb([0,128,32]); 
			int var9 <- 1 - 1; 	// var9 equals 0
			assert var9 equals: 0; 
			int var10 <- 1.0 - 1; 	// var10 equals 0.0
			assert var10 equals: 0.0; 
			int var11 <- 3.7 - 1.2; 	// var11 equals 2.5
			assert var11 equals: 2.5; 
			int var12 <- 3 - 1.2; 	// var12 equals 1.8
			assert var12 equals: 1.8; 
			point var13 <- {1, 2} - {4, 5}; 	// var13 equals {-3.0, -3.0}
			assert var13 equals: {-3.0, -3.0}; 
			list<int> var14 <- [1,2,3,4,5,6] - [2,4,9]; 	// var14 equals [1,3,5,6]
			assert var14 equals: [1,3,5,6]; 
			list<int> var15 <- [1,2,3,4,5,6] - [0,8]; 	// var15 equals [1,2,3,4,5,6]
			assert var15 equals: [1,2,3,4,5,6]; 
			matrix var16 <- 3.5 - matrix([[2,5],[3,4]]); 	// var16 equals matrix([[1.5,-1.5],[0.5,-0.5]])
			assert var16 equals: matrix([[1.5,-1.5],[0.5,-0.5]]); 
			date1 - 200
			geometry var18 <- shape - 5; 	// var18 equals a geometry corresponding to the geometry of the agent applying the operator reduced by a distance of 5
			point var19 <- {1, 2} - 4.5; 	// var19 equals {-3.5, -2.5, -4.5}
			assert var19 equals: {-3.5, -2.5, -4.5}; 
			point var20 <- {1, 2} - 4; 	// var20 equals {-3.0,-2.0,-4.0}
			assert var20 equals: {-3.0,-2.0,-4.0}; 
			float var21 <- date1 - date2; 	// var21 equals 598
			assert var21 equals: 598; 
			list<int> var22 <- [1,2,3,4,5,6] - 2; 	// var22 equals [1,3,4,5,6]
			assert var22 equals: [1,3,4,5,6]; 
			list<int> var23 <- [1,2,3,4,5,6] - 0; 	// var23 equals [1,2,3,4,5,6]
			assert var23 equals: [1,2,3,4,5,6]; 
			geometry var24 <- rectangle(10,10) - [circle(2), square(2)]; 	// var24 equals rectangle(10,10) - (circle(2) + square(2))
			assert var24 equals: rectangle(10,10) - (circle(2) + square(2)); 
			//geometry var25 <- geom1 - geom2; 	// var25 equals a geometry corresponding to difference between geom1 and geom2
			map var0 <- ['a'::1,'b'::2] - ['b'::2]; 	// var0 equals ['a'::1]
			assert var0 equals: ['a'::1]; 
			map var1 <- ['a'::1,'b'::2] - ['b'::2,'c'::3]; 	// var1 equals ['a'::1]
			assert var1 equals: ['a'::1]; 
			map var2 <- ['a'::1,'b'::2] - ('b'::2); 	// var2 equals ['a'::1]
			assert var2 equals: ['a'::1]; 
			map var3 <- ['a'::1,'b'::2] - ('c'::3); 	// var3 equals ['a'::1,'b'::2]
			assert var3 equals: ['a'::1,'b'::2]; 
			point var4 <- -{3.0,5.0}; 	// var4 equals {-3.0,-5.0}
			assert var4 equals: {-3.0,-5.0}; 
			point var5 <- -{1.0,6.0,7.0}; 	// var5 equals {-1.0,-6.0,-7.0}
			assert var5 equals: {-1.0,-6.0,-7.0}; 
			int var6 <- - (-56); 	// var6 equals 56
			assert var6 equals: 56; 

		}
	
		test minus_daysOp {
			date1 minus_days 20

		}
	
		test minus_hoursOp {
			date1 minus_hours 15 // equivalent to date1 - 15 #h

		}
	
		test minus_minutesOp {
			date1 minus_minutes 5 // equivalent to date1 - 5#mn

		}
	
		test minus_monthsOp {
			date1 minus_months 5

		}
	
		test minus_msOp {
			date1 minus_ms 15 // equivalent to date1 - 15 #ms

		}
	
		test minus_secondsOp {

		}
	
		test minus_weeksOp {
			date1 minus_weeks 15

		}
	
		test minus_yearsOp {
			date1 minus_years 3

		}
	
		test months_betweenOp {
			months_between(d1, d2) -: 10 

		}
	
		test PlusOp {
			point var4 <- {1, 2} + 4; 	// var4 equals {5.0, 6.0,4.0}
			assert var4 equals: {5.0, 6.0,4.0}; 
			point var5 <- {1, 2} + 4.5; 	// var5 equals {5.5, 6.5,4.5}
			assert var5 equals: {5.5, 6.5,4.5}; 
			string var6 <- "hello " + 12; 	// var6 equals "hello 12"
			assert var6 equals: "hello 12"; 
			list<int> var7 <- [1,2,3,4,5,6] + 2; 	// var7 equals [1,2,3,4,5,6,2]
			assert var7 equals: [1,2,3,4,5,6,2]; 
			list<int> var8 <- [1,2,3,4,5,6] + 0; 	// var8 equals [1,2,3,4,5,6,0]
			assert var8 equals: [1,2,3,4,5,6,0]; 
			geometry var9 <- circle(5) + (5,32); 	// var9 equals circle(10)
			assert var9 equals: circle(10); 
			//geometry var10 <- geom1 + geom2; 	// var10 equals a geometry corresponding to union between geom1 and geom2
			date1 + 200
			matrix var12 <- 3.5 + matrix([[2,5],[3,4]]); 	// var12 equals matrix([[5.5,8.5],[6.5,7.5]])
			assert var12 equals: matrix([[5.5,8.5],[6.5,7.5]]); 
			point var13 <- {1, 2} + {4, 5}; 	// var13 equals {5.0, 7.0}
			assert var13 equals: {5.0, 7.0}; 
			rgb var14 <- rgb([255, 128, 32]) + 3; 	// var14 equals rgb([255,131,35])
			assert var14 equals: rgb([255,131,35]); 
			int var15 <- 1 + 1; 	// var15 equals 2
			assert var15 equals: 2; 
			int var16 <- 1.0 + 1; 	// var16 equals 2.0
			assert var16 equals: 2.0; 
			int var17 <- 1.0 + 2.5; 	// var17 equals 3.5
			assert var17 equals: 3.5; 
			geometry var18 <- circle(5) + 5; 	// var18 equals circle(10)
			assert var18 equals: circle(10); 
			geometry var19 <- circle(5) + (5,32,#round); 	// var19 equals circle(10)
			assert var19 equals: circle(10); 
			list<int> var20 <- [1,2,3,4,5,6] + [2,4,9]; 	// var20 equals [1,2,3,4,5,6,2,4,9]
			assert var20 equals: [1,2,3,4,5,6,2,4,9]; 
			list<int> var21 <- [1,2,3,4,5,6] + [0,8]; 	// var21 equals [1,2,3,4,5,6,0,8]
			assert var21 equals: [1,2,3,4,5,6,0,8]; 
			rgb var22 <- rgb([255, 128, 32]) + rgb('red'); 	// var22 equals rgb([255,128,32])
			assert var22 equals: rgb([255,128,32]); 
			map var0 <- ['a'::1,'b'::2] + ['c'::3]; 	// var0 equals ['a'::1,'b'::2,'c'::3]
			assert var0 equals: ['a'::1,'b'::2,'c'::3]; 
			map var1 <- ['a'::1,'b'::2] + [5::3.0]; 	// var1 equals ['a'::1.0,'b'::2.0,5::3.0]
			assert var1 equals: ['a'::1.0,'b'::2.0,5::3.0]; 
			map var2 <- ['a'::1,'b'::2] + ('c'::3); 	// var2 equals ['a'::1,'b'::2,'c'::3]
			assert var2 equals: ['a'::1,'b'::2,'c'::3]; 
			map var3 <- ['a'::1,'b'::2] + ('c'::3); 	// var3 equals ['a'::1,'b'::2,'c'::3]
			assert var3 equals: ['a'::1,'b'::2,'c'::3]; 

		}
	
		test plus_daysOp {
			date1 plus_days 20

		}
	
		test plus_hoursOp {
			date1 plus_hours 15 // equivalent to date1 + 15 #h

		}
	
		test plus_minutesOp {
			date1 plus_minutes 5 // equivalent to date1 + 5 #mn

		}
	
		test plus_monthsOp {
			date1 plus_months 5

		}
	
		test plus_msOp {
			date1 plus_ms 15 // equivalent to date1 + 15 #ms

		}
	
		test plus_secondsOp {

		}
	
		test plus_weeksOp {
			date1 plus_weeks 15

		}
	
		test plus_yearsOp {
			date1 plus_years 3

		}
	
		test sinceOp {
			reflex when: since(starting_date) {} -: will always be run
			every(2#days) since (starting_date + 1#day) // the computation will return true 1 day after the starting date and every two days after this reference date

		}
	
		test subtract_daysOp {

		}
	
		test subtract_hoursOp {

		}
	
		test subtract_minutesOp {

		}
	
		test subtract_monthsOp {

		}
	
		test subtract_msOp {

		}
	
		test subtract_secondsOp {

		}
	
		test subtract_weeksOp {

		}
	
		test subtract_yearsOp {

		}
	
		test toOp {
			date('2000-01-01') to date('2010-01-01') // builds an interval between these two dates
			(date('2000-01-01') to date('2010-01-01')) every (#month) // builds an interval between these two dates which contains all the monthly dates starting from the beginning of the interval

		}
	
		test untilOp {
			reflex when: until(starting_date) {} -: will be run only once at the beginning of the simulation

		}
	
		test years_betweenOp {
			years_between(d1, d2) -: 10 

		}
	
	}


experiment testOpDate_related__operatorsTestExp type: gui {}	
	