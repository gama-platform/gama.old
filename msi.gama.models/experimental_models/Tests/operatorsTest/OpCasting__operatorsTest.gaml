/**
 *  OpOpCasting__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpCasting__operatorsTest.
 */

model OpCasting__operatorsTest

global {
	init {
		create testOpCasting__operatorsTest number: 1;
		ask testOpCasting__operatorsTest {do _step_;}
	}
}


	species testOpCasting__operatorsTest {

	
		test asOp {
			int var0 <- 3.5 as int; 	// var0 equals int(3.5)
			assert var0 equals: int(3.5); 

		}
	
		test as_intOp {
			int var0 <- '20' as_int 10; 	// var0 equals 20
			assert var0 equals: 20; 
			int var1 <- '20' as_int 8; 	// var1 equals 16
			assert var1 equals: 16; 
			int var2 <- '20' as_int 16; 	// var2 equals 32
			assert var2 equals: 32; 
			int var3 <- '1F' as_int 16; 	// var3 equals 31
			assert var3 equals: 31; 
			int var4 <- 'hello' as_int 32; 	// var4 equals 18306744
			assert var4 equals: 18306744; 

		}
	
		test as_matrixOp {

		}
	
		test fontOp {
			font var0 <- font ('Helvetica Neue',12, #bold + #italic); 	// var0 equals a bold and italic face of the Helvetica Neue family

		}
	
		test isOp {
			bool var0 <- 0 is int; 	// var0 equals true
			assert var0 equals: true; 
			//bool var1 <- an_agent is node; 	// var1 equals true
			bool var2 <- 1 is float; 	// var2 equals false
			assert var2 equals: false; 

		}
	
		test is_skillOp {
			//bool var0 <- agentA is_skill 'moving'; 	// var0 equals true

		}
	
		test list_withOp {

		}
	
		test matrix_withOp {

		}
	
		test speciesOp {
			//species var0 <- species(self); 	// var0 equals the species of the current agent
			//species var1 <- species('node'); 	// var1 equals node
			//species var2 <- species([1,5,9,3]); 	// var2 equals nil
			//species var3 <- species(node1); 	// var3 equals node

		}
	
		test species_ofOp {

		}
	
		test to_gamlOp {
			string var0 <- to_gaml(0); 	// var0 equals '0'
			assert var0 equals: '0'; 
			string var1 <- to_gaml(3.78); 	// var1 equals '3.78'
			assert var1 equals: '3.78'; 
			string var2 <- to_gaml(true); 	// var2 equals 'true'
			assert var2 equals: 'true'; 
			string var3 <- to_gaml({23, 4.0}); 	// var3 equals '{23.0,4.0,0.0}'
			assert var3 equals: '{23.0,4.0,0.0}'; 
			string var4 <- to_gaml(5::34); 	// var4 equals '5::34'
			assert var4 equals: '5::34'; 
			string var5 <- to_gaml(rgb(255,0,125)); 	// var5 equals 'rgb (255, 0, 125,255)'
			assert var5 equals: 'rgb (255, 0, 125,255)'; 
			string var6 <- to_gaml('hello'); 	// var6 equals "'hello'"
			assert var6 equals: "'hello'"; 
			string var7 <- to_gaml([1,5,9,3]); 	// var7 equals '[1,5,9,3]'
			assert var7 equals: '[1,5,9,3]'; 
			string var8 <- to_gaml(['a'::345, 'b'::13, 'c'::12]); 	// var8 equals "(['a'::345,'b'::13,'c'::12] as map )"
			assert var8 equals: "(['a'::345,'b'::13,'c'::12] as map )"; 
			string var9 <- to_gaml([[3,5,7,9],[2,4,6,8]]); 	// var9 equals '[[3,5,7,9],[2,4,6,8]]'
			assert var9 equals: '[[3,5,7,9],[2,4,6,8]]'; 
			//string var10 <- to_gaml(a_graph); 	// var10 equals ([((1 as node)::(3 as node))::(5 as edge),((0 as node)::(3 as node))::(3 as edge),((1 as node)::(2 as node))::(1 as edge),((0 as node)::(2 as node))::(2 as edge),((0 as node)::(1 as node))::(0 as edge),((2 as node)::(3 as node))::(4 as edge)] as map ) as graph
			//string var11 <- to_gaml(node1); 	// var11 equals  1 as node

		}
	
		test topologyOp {
			topology var0 <- topology(0); 	// var0 equals nil
			assert var0 equals: nil; 
			//topology(a_graph)	--: Multiple topology in POLYGON ((24.712119771887785 7.867357373616512, 24.712119771887785 61.283226839310565, 82.4013676510046  7.867357373616512)) at location[53.556743711446195;34.57529210646354]

		}
	
	}


experiment testOpCasting__operatorsTestExp type: gui {}	
	