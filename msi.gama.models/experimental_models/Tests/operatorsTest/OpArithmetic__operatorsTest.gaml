/**
 *  OpOpArithmetic__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpArithmetic__operatorsTest.
 */

model OpArithmetic__operatorsTest

global {
	init {
		create testOpArithmetic__operatorsTest number: 1;
		ask testOpArithmetic__operatorsTest {do _step_;}
	}
}


	species testOpArithmetic__operatorsTest {

	
		test absOp {
			int var0 <- abs (-10); 	// var0 equals 10
			assert var0 equals: 10; 
			int var1 <- abs (10); 	// var1 equals 10
			assert var1 equals: 10; 
			int var2 <- abs (-0); 	// var2 equals 0
			assert var2 equals: 0; 
			float var3 <- abs (200 * -1 + 0.5); 	// var3 equals 199.5
			assert var3 equals: 199.5; 

		}
	
		test acosOp {
			float var0 <- acos (0); 	// var0 equals 90.0
			assert var0 equals: 90.0; 

		}
	
		test asinOp {
			float var0 <- asin (0); 	// var0 equals 0.0
			assert var0 equals: 0.0; 
			float var1 <- asin (90); 	// var1 equals #nan
			assert var1 equals: #nan; 

		}
	
		test atanOp {
			float var0 <- atan (1); 	// var0 equals 45.0
			assert var0 equals: 45.0; 

		}
	
		test atan2Op {
			float var0 <- atan2 (0,0); 	// var0 equals 0.0
			assert var0 equals: 0.0; 

		}
	
		test ceilOp {
			float var0 <- ceil(3); 	// var0 equals 3.0
			assert var0 equals: 3.0; 
			float var1 <- ceil(3.5); 	// var1 equals 4.0
			assert var1 equals: 4.0; 
			float var2 <- ceil(-4.7); 	// var2 equals -4.0
			assert var2 equals: -4.0; 

		}
	
		test cosOp {
			float var0 <- cos (0); 	// var0 equals 1.0
			assert var0 equals: 1.0; 
			float var1 <- cos(360); 	// var1 equals 1.0
			assert var1 equals: 1.0; 
			float var2 <- cos(-720); 	// var2 equals 1.0
			assert var2 equals: 1.0; 

		}
	
		test cos_radOp {

		}
	
		test divOp {
			int var0 <- 40.5 div 3; 	// var0 equals 13
			assert var0 equals: 13; 
			int var1 <- 40.1 div 4.5; 	// var1 equals 8
			assert var1 equals: 8; 
			int var2 <- 40 div 3; 	// var2 equals 13
			assert var2 equals: 13; 
			int var3 <- 40 div 4.1; 	// var3 equals 9
			assert var3 equals: 9; 

		}
	
		test DivideOp {
			rgb var0 <- rgb([255, 128, 32]) / 2; 	// var0 equals rgb([127,64,16])
			assert var0 equals: rgb([127,64,16]); 
			rgb var1 <- rgb([255, 128, 32]) / 2.5; 	// var1 equals rgb([102,51,13])
			assert var1 equals: rgb([102,51,13]); 
			float var2 <- 3 / 5.0; 	// var2 equals 0.6
			assert var2 equals: 0.6; 
			point var3 <- {5, 7.5} / 2.5; 	// var3 equals {2, 3}
			assert var3 equals: {2, 3}; 
			point var4 <- {2,5} / 4; 	// var4 equals {0.5,1.25}
			assert var4 equals: {0.5,1.25}; 

		}
	
		test evenOp {
			bool var0 <- even (3); 	// var0 equals false
			assert var0 equals: false; 
			bool var1 <- even(-12); 	// var1 equals true
			assert var1 equals: true; 

		}
	
		test expOp {
			float var0 <- exp (0); 	// var0 equals 1.0
			assert var0 equals: 1.0; 

		}
	
		test factOp {
			float var0 <- fact(4); 	// var0 equals 24
			assert var0 equals: 24; 

		}
	
		test floorOp {
			float var0 <- floor(3); 	// var0 equals 3.0
			assert var0 equals: 3.0; 
			float var1 <- floor(3.5); 	// var1 equals 3.0
			assert var1 equals: 3.0; 
			float var2 <- floor(-4.7); 	// var2 equals -5.0
			assert var2 equals: -5.0; 

		}
	
		test hypotOp {
			float var0 <- hypot(0,1,0,1); 	// var0 equals sqrt(2)
			assert var0 equals: sqrt(2); 

		}
	
		test is_finiteOp {
			bool var0 <- is_finite(4.66); 	// var0 equals true
			assert var0 equals: true; 
			bool var1 <- is_finite(#infinity); 	// var1 equals false
			assert var1 equals: false; 

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
	
		test lnOp {
			float var0 <- ln(1); 	// var0 equals 0.0
			assert var0 equals: 0.0; 
			float var1 <- ln(exp(1)); 	// var1 equals 1.0
			assert var1 equals: 1.0; 

		}
	
		test logOp {
			float var0 <- log(10); 	// var0 equals 1.0
			assert var0 equals: 1.0; 
			float var1 <- log(1); 	// var1 equals 0.0
			assert var1 equals: 0.0; 

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
	
		test modOp {
			int var0 <- 40 mod 3; 	// var0 equals 1
			assert var0 equals: 1; 

		}
	
		test MultiplyOp {
			point var1 <- {2,5} * 4; 	// var1 equals {8.0, 20.0}
			assert var1 equals: {8.0, 20.0}; 
			point var2 <- {2, 4} * 2.5; 	// var2 equals {5.0, 10.0}
			assert var2 equals: {5.0, 10.0}; 
			rgb var3 <- rgb([255, 128, 32]) * 2; 	// var3 equals rgb([255,255,64])
			assert var3 equals: rgb([255,255,64]); 
			matrix<float> m <- (3.5 * matrix([[2,5],[3,4]]));	//m equals matrix([[7.0,17.5],[10.5,14]])
			geometry var5 <- shape * {0.5,0.5,2}; 	// var5 equals a geometry corresponding to the geometry of the agent applying the operator scaled by a coefficient of 0.5 in x, 0.5 in y and 2 in z
			int var6 <- 1 * 1; 	// var6 equals 1
			assert var6 equals: 1; 
			float var7 <- {2,5} * {4.5, 5}; 	// var7 equals 34.0
			assert var7 equals: 34.0; 
			geometry var8 <- circle(10) * 2; 	// var8 equals circle(20)
			assert var8 equals: circle(20); 
			float var0 <- 2.5 * 2; 	// var0 equals 5.0
			assert var0 equals: 5.0; 

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
	
		test PowerOp {
			float var0 <- 2 ^ 3; 	// var0 equals 8.0
			assert var0 equals: 8.0; 
			float var1 <- 4.0^2; 	// var1 equals 16.0
			assert var1 equals: 16.0; 
			float var2 <- 4.0^0.5; 	// var2 equals 2.0
			assert var2 equals: 2.0; 
			float var3 <- 8^0; 	// var3 equals 1.0
			assert var3 equals: 1.0; 
			float var4 <- 8.0^0; 	// var4 equals 1.0
			assert var4 equals: 1.0; 
			float var5 <- 8^1; 	// var5 equals 8.0
			assert var5 equals: 8.0; 
			float var6 <- 8.0^1; 	// var6 equals 8.0
			assert var6 equals: 8.0; 
			float var7 <- 8^1.0; 	// var7 equals 8.0
			assert var7 equals: 8.0; 
			float var8 <- 8.0^1.0; 	// var8 equals 8.0
			assert var8 equals: 8.0; 
			float var9 <- 2^0.5; 	// var9 equals sqrt(2)
			assert var9 equals: sqrt(2); 
			float var10 <- 16.81^0.5; 	// var10 equals sqrt(16.81)
			assert var10 equals: sqrt(16.81); 
			assert (10^(-9) = 0) equals: false;
			float var12 <- 4.84 ^ 0.5; 	// var12 equals 2.2
			assert var12 equals: 2.2; 

		}
	
		test roundOp {
			point var0 <- {12345.78943,  12345.78943, 12345.78943} with_precision 2; 	// var0 equals {12345.79,12345.79,12345.79}
			assert var0 equals: {12345.79,12345.79,12345.79}; 
			int var1 <- round (0.51); 	// var1 equals 1
			assert var1 equals: 1; 
			int var2 <- round (100.2); 	// var2 equals 100
			assert var2 equals: 100; 
			int var3 <- round(-0.51); 	// var3 equals -1
			assert var3 equals: -1; 

		}
	
		test signumOp {
			int var0 <- signum(-12); 	// var0 equals -1
			assert var0 equals: -1; 
			int var1 <- signum(14); 	// var1 equals 1
			assert var1 equals: 1; 
			int var2 <- signum(0); 	// var2 equals 0
			assert var2 equals: 0; 

		}
	
		test sinOp {
			float var0 <- sin (0); 	// var0 equals 0.0
			assert var0 equals: 0.0; 
			float var1 <- sin(360); 	// var1 equals 0.0
			assert var1 equals: 0.0; 

		}
	
		test sin_radOp {
			float var0 <- sin(360); 	// var0 equals 0.0
			assert var0 equals: 0.0; 

		}
	
		test sqrtOp {
			float var0 <- sqrt(4); 	// var0 equals 2.0
			assert var0 equals: 2.0; 
			float var1 <- sqrt(4); 	// var1 equals 2.0
			assert var1 equals: 2.0; 

		}
	
		test tanOp {
			float var0 <- tan (0); 	// var0 equals 0.0
			assert var0 equals: 0.0; 
			float var1 <- tan(90); 	// var1 equals 1.633123935319537E16
			assert var1 equals: 1.633123935319537E16; 

		}
	
		test tan_radOp {

		}
	
		test tanhOp {
			float var0 <- tanh(0); 	// var0 equals 0.0
			assert var0 equals: 0.0; 
			float var1 <- tanh(100); 	// var1 equals 1.0
			assert var1 equals: 1.0; 

		}
	
		test with_precisionOp {
			geometry var0 <- self with_precision 2; 	// var0 equals the geometry resulting from the rounding of points of the geometry with a precision of 0.1.
			float var1 <- 12345.78943 with_precision 2; 	// var1 equals 12345.79
			assert var1 equals: 12345.79; 
			float var2 <- 123 with_precision 2; 	// var2 equals 123.00
			assert var2 equals: 123.00; 
			point var3 <- {12345.78943, 12345.78943, 12345.78943} with_precision 2 ; 	// var3 equals {12345.79, 12345.79, 12345.79}
			assert var3 equals: {12345.79, 12345.79, 12345.79}; 

		}
	
	}


experiment testOpArithmetic__operatorsTestExp type: gui {}	
	