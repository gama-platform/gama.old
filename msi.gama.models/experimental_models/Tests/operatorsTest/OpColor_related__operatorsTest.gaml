/**
 *  OpOpColor_related__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpColor_related__operatorsTest.
 */

model OpColor_related__operatorsTest

global {
	init {
		create testOpColor_related__operatorsTest number: 1;
		ask testOpColor_related__operatorsTest {do _step_;}
	}
}


	species testOpColor_related__operatorsTest {

	
		test blendOp {
			rgb var0 <- blend(#red, #blue); 	// var0 equals rgb(127,0,127)
			assert var0 equals: rgb(127,0,127); 
			//rgb var1 <- blend(#red, #blue); 	// var1 equals to a color very close to the purple
			rgb var2 <- blend(#red, #blue, 0.3); 	// var2 equals rgb(76,0,178)
			assert var2 equals: rgb(76,0,178); 
			//rgb var3 <- blend(#red, #blue, 0.3); 	// var3 equals to a color between the purple and the blue

		}
	
		test brewer_colorsOp {
			//list<rgb> var0 <- list<rgb> colors <- brewer_colors("OrRd");; 	// var0 equals a list of 6 blue colors
			//list<rgb> var1 <- list<rgb> colors <- brewer_colors("Pastel1", 10);; 	// var1 equals a list of 10 sequential colors

		}
	
		test brewer_palettesOp {
			//list<string> var0 <- list<rgb> colors <- brewer_palettes(5,10);; 	// var0 equals a list of palettes that are composed of a min of 5 colors and a max of 10 colors
			//list<string> var1 <- list<rgb> colors <- brewer_palettes();; 	// var1 equals a list of palettes that are composed of a min of 5 colors

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
	
		test grayscaleOp {
			//rgb var0 <- grayscale (rgb(255,0,0)); 	// var0 equals to a dark grey
			rgb var1 <- grayscale (rgb(255,0,0)); 	// var1 equals rgb(76,76,76)
			assert var1 equals: rgb(76,76,76); 

		}
	
		test hsbOp {
			rgb var0 <- hsb (0.5,1.0,1.0,0.0); 	// var0 equals rgb("cyan",0)
			assert var0 equals: rgb("cyan",0); 
			rgb var1 <- hsb (0.0,1.0,1.0); 	// var1 equals rgb("red")
			assert var1 equals: rgb("red"); 

		}
	
		test meanOp {
			unknown var0 <- mean ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 5.125 
			assert var0 equals: 5.125 ; 

		}
	
		test medianOp {
			unknown var0 <- median ([4.5, 3.5, 5.5, 3.4, 7.0]); 	// var0 equals 5.0
			assert var0 equals: 5.0; 

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
	
		test rgbOp {
			rgb var0 <- rgb (255,0,0,125); 	// var0 equals a light red color
			int var1 <- rgb (255,0,0,125).alpha; 	// var1 equals 125
			assert var1 equals: 125; 
			rgb var2 <- rgb(rgb(255,0,0),125); 	// var2 equals a light red color
			rgb var3 <- rgb(rgb(255,0,0),0.5); 	// var3 equals a light red color
			rgb var4 <- rgb (255,0,0,0.5); 	// var4 equals a light red color
			rgb var5 <- rgb ("red"); 	// var5 equals rgb(255,0,0)
			assert var5 equals: rgb(255,0,0); 
			rgb var6 <- rgb (255,0,0); 	// var6 equals #red
			assert var6 equals: #red; 

		}
	
		test rnd_colorOp {
			rgb var0 <- rnd_color(255); 	// var0 equals a random color, equivalent to rgb(rnd(255),rnd(255),rnd(255))

		}
	
		test sumOp {
			int var0 <- sum ([12,10,3]); 	// var0 equals 25
			assert var0 equals: 25; 
			unknown var1 <- sum([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}]); 	// var1 equals {20.0,17.0}
			assert var1 equals: {20.0,17.0}; 

		}
	
	}


experiment testOpColor_related__operatorsTestExp type: gui {}	
	