/**
 *  OpOpStrings_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpStrings_related__operatorsTest.
 */

model OpStrings_related__operatorsTest

global {
	init {
		create testOpStrings_related__operatorsTest number: 1;
		ask testOpStrings_related__operatorsTest {do _step_;}
	}
}


	species testOpStrings_related__operatorsTest {

	
		test atOp {
			int var0 <- [1, 2, 3] at 2; 	// var0 equals 3
			assert var0 equals: 3; 
			point var1 <- [{1,2}, {3,4}, {5,6}] at 0; 	// var1 equals {1.0,2.0}
			assert var1 equals: {1.0,2.0}; 
			string var2 <- 'abcdef' at 0; 	// var2 equals 'a'
			assert var2 equals: 'a'; 

		}
	
		test charOp {
			string var0 <- char (34); 	// var0 equals '"'
			assert var0 equals: '"'; 

		}
	
		test containsOp {
			bool var0 <- [1, 2, 3] contains 2; 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- [{1,2}, {3,4}, {5,6}] contains {3,4}; 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- 'abcded' contains 'bc'; 	// var2 equals true
			assert var2 equals: true; 

		}
	
		test contains_allOp {
			bool var0 <- "abcabcabc" contains_all ["ca","xy"]; 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- [1,2,3,4,5,6] contains_all [2,4]; 	// var1 equals true 
			assert var1 equals: true ; 
			bool var2 <- [1,2,3,4,5,6] contains_all [2,8]; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- [1::2, 3::4, 5::6] contains_all [1,3]; 	// var3 equals false 
			assert var3 equals: false ; 
			bool var4 <- [1::2, 3::4, 5::6] contains_all [2,4]; 	// var4 equals true
			assert var4 equals: true; 

		}
	
		test contains_anyOp {
			bool var0 <- "abcabcabc" contains_any ["ca","xy"]; 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- [1,2,3,4,5,6] contains_any [2,4]; 	// var1 equals true 
			assert var1 equals: true ; 
			bool var2 <- [1,2,3,4,5,6] contains_any [2,8]; 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- [1::2, 3::4, 5::6] contains_any [1,3]; 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- [1::2, 3::4, 5::6] contains_any [2,4]; 	// var4 equals true
			assert var4 equals: true; 

		}
	
		test copy_betweenOp {
			string var0 <- copy_between("abcabcabc", 2,6); 	// var0 equals "cabc"
			assert var0 equals: "cabc"; 
			container var1 <-  copy_between ([4, 1, 6, 9 ,7], 1, 3); 	// var1 equals [1, 6]
			assert var1 equals: [1, 6]; 

		}
	
		test dateOp {
			date("1999-12-30", 'yyyy-MM-dd')

		}
	
		test emptyOp {
			bool var0 <- empty([]); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- empty ('abced'); 	// var1 equals false
			assert var1 equals: false; 

		}
	
		test firstOp {
			int var0 <- first ([1, 2, 3]); 	// var0 equals 1
			assert var0 equals: 1; 
			string var1 <- first ('abce'); 	// var1 equals 'a'
			assert var1 equals: 'a'; 

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
	
		test inOp {
			bool var0 <- 2 in [1,2,3,4,5,6]; 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- 7 in [1,2,3,4,5,6]; 	// var1 equals false
			assert var1 equals: false; 
			bool var2 <- 3 in [1::2, 3::4, 5::6]; 	// var2 equals false
			assert var2 equals: false; 
			bool var3 <- 6 in [1::2, 3::4, 5::6]; 	// var3 equals true
			assert var3 equals: true; 
			bool var4 <-  'bc' in 'abcded'; 	// var4 equals true
			assert var4 equals: true; 

		}
	
		test indented_byOp {

		}
	
		test index_ofOp {
			int var1 <-  "abcabcabc" index_of "ca"; 	// var1 equals 2
			assert var1 equals: 2; 
			point var2 <- matrix([[1,2,3],[4,5,6]]) index_of 4; 	// var2 equals {1.0,0.0}
			assert var2 equals: {1.0,0.0}; 
			int var3 <- [1,2,3,4,5,6] index_of 4; 	// var3 equals 3
			assert var3 equals: 3; 
			int var4 <- [4,2,3,4,5,4] index_of 4; 	// var4 equals 0
			assert var4 equals: 0; 
			unknown var0 <- [1::2, 3::4, 5::6] index_of 4; 	// var0 equals 3
			assert var0 equals: 3; 

		}
	
		test is_numberOp {
			bool var0 <- is_number("test"); 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- is_number("123.56"); 	// var1 equals true
			assert var1 equals: true; 
			bool var2 <- is_number("-1.2e5"); 	// var2 equals true
			assert var2 equals: true; 
			bool var3 <- is_number("1,2"); 	// var3 equals false
			assert var3 equals: false; 
			bool var4 <- is_number("#12FA"); 	// var4 equals true
			assert var4 equals: true; 
			bool var5 <- is_number(4.66); 	// var5 equals true
			assert var5 equals: true; 
			bool var6 <- is_number(#infinity); 	// var6 equals true
			assert var6 equals: true; 
			bool var7 <- is_number(#nan); 	// var7 equals false
			assert var7 equals: false; 

		}
	
		test lastOp {
			int var0 <- last ([1, 2, 3]); 	// var0 equals 3
			assert var0 equals: 3; 
			string var1 <- last ('abce'); 	// var1 equals 'e'
			assert var1 equals: 'e'; 

		}
	
		test last_index_ofOp {
			int var0 <- "abcabcabc" last_index_of "ca"; 	// var0 equals 5
			assert var0 equals: 5; 
			point var1 <- matrix([[1,2,3],[4,5,4]]) last_index_of 4; 	// var1 equals {1.0,2.0}
			assert var1 equals: {1.0,2.0}; 
			int var2 <- [1,2,3,4,5,6] last_index_of 4; 	// var2 equals 3
			assert var2 equals: 3; 
			int var3 <- [4,2,3,4,5,4] last_index_of 4; 	// var3 equals 5
			assert var3 equals: 5; 
			unknown var4 <- [1::2, 3::4, 5::4] last_index_of 4; 	// var4 equals 5
			assert var4 equals: 5; 

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
	
		test lengthOp {
			int var0 <- length([12,13]); 	// var0 equals 2
			assert var0 equals: 2; 
			int var1 <- length([]); 	// var1 equals 0
			assert var1 equals: 0; 
			int var2 <- length(matrix([["c11","c12","c13"],["c21","c22","c23"]])); 	// var2 equals 6
			assert var2 equals: 6; 
			int var3 <- length ('I am an agent'); 	// var3 equals 13
			assert var3 equals: 13; 

		}
	
		test lower_caseOp {
			string var0 <- lower_case("Abc"); 	// var0 equals 'abc'
			assert var0 equals: 'abc'; 

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
	
		test replaceOp {
			string var0 <- replace('to be or not to be,that is the question','to', 'do'); 	// var0 equals 'do be or not do be,that is the question'
			assert var0 equals: 'do be or not do be,that is the question'; 

		}
	
		test replace_regexOp {
			string var0 <- replace_regex("colour, color", "colou?r", "col"); 	// var0 equals 'col, col'
			assert var0 equals: 'col, col'; 

		}
	
		test reverseOp {
			msi.gama.util.IContainer<?,?> var0 <- reverse ([10,12,14]); 	// var0 equals [14, 12, 10]
			assert var0 equals: [14, 12, 10]; 
			msi.gama.util.IContainer<?,?> var1 <- reverse (['k1'::44, 'k2'::32, 'k3'::12]); 	// var1 equals [12::'k3',  32::'k2', 44::'k1']
			assert var1 equals: [12::'k3',  32::'k2', 44::'k1']; 
			msi.gama.util.IContainer<?,?> var2 <- reverse(matrix([["c11","c12","c13"],["c21","c22","c23"]])); 	// var2 equals matrix([["c11","c21"],["c12","c22"],["c13","c23"]])
			assert var2 equals: matrix([["c11","c21"],["c12","c22"],["c13","c23"]]); 
			string var3 <- reverse ('abcd'); 	// var3 equals 'dcba'
			assert var3 equals: 'dcba'; 

		}
	
		test sampleOp {
			container var0 <- sample([2,10,1],2,false,[0.1,0.7,0.2]); 	// var0 equals [10,2]
			container var1 <- sample([2,10,1],2,false); 	// var1 equals [1,2]

		}
	
		test shuffleOp {
			matrix var0 <- shuffle (matrix([["c11","c12","c13"],["c21","c22","c23"]])); 	// var0 equals matrix([["c12","c21","c11"],["c13","c22","c23"]]) (for example)
			container var1 <- shuffle ([12, 13, 14]); 	// var1 equals [14,12,13] (for example)
			string var2 <- shuffle ('abc'); 	// var2 equals 'bac' (for example)

		}
	
		test split_withOp {
			container var0 <- 'to be or not to be,that is the question' split_with ' ,'; 	// var0 equals ['to','be','or','not','to','be','that','is','the','question']
			assert var0 equals: ['to','be','or','not','to','be','that','is','the','question']; 

		}
	
		test stringOp {
			format(#now, 'yyyy-MM-dd')

		}
	
		test tokenizeOp {

		}
	
		test upper_caseOp {
			string var0 <- upper_case("Abc"); 	// var0 equals 'ABC'
			assert var0 equals: 'ABC'; 

		}
	
	}


experiment testOpStrings_related__operatorsTestExp type: gui {}	
	