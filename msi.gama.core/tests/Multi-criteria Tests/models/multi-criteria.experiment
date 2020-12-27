/**
* Name: multicriteria
* Author: Patrick Taillandier
* Description: Tests multi-criteria decision making operators
* Tags: multi_criteria, test
*/

model multicriteriaTest

experiment TopologyTests type: test {
	list<list<float>> candidates <- [[1.0,1.0,1.0],[2.0,0.0,1.0],[0.0,0.0,0.0],[1.0,2.0,0.0],[0.0,0.5,3.0]];
	
	test "weighted_means_DM"{
		list criteria_WM <- [["name"::"C1", "weight" :: 1.0],["name"::"C2", "weight" :: 2.0],["name"::"C3", "weight" ::3.0]]; 
		int choice <- weighted_means_DM(candidates,criteria_WM);
		assert choice = 4;
	}	
	
	test "electre_DM"{
		list criteria_WM <- [["name"::"C1", "weight" :: 1.0, "p"::2.0, "q"::0.0, "v"::2.0, "maximize" :: true],["name"::"C2", "weight" :: 2.0, "p"::2.0, "q"::0.0, "v"::2.0,  "maximize" :: true],["name"::"C3", "weight" ::3.0, "p"::2.0, "q"::0.0, "v"::2.0,  "maximize" :: true]]; 
		int choice <- electre_DM(candidates,criteria_WM, 0.7);
		assert choice = 4;
	}
	
	test "promethee_DM"{
		list criteria_Promethee <- [["name"::"C1", "weight" :: 1.0, "p"::2.0, "q"::0.0, "s"::3.0, "maximize" :: true],["name"::"C2", "weight" :: 2.0, "p"::2.0, "q"::0.0, "s"::3.0,  "maximize" :: true],["name"::"C3", "weight" ::1.0, "p"::2.0, "q"::0.0, "s"::3.0,  "maximize" :: true]];  
		int choice <- promethee_DM(candidates,criteria_Promethee);
		assert choice = 3;
	}	
	
	test "evidence_theory_DM"{
		list criteria_ET <- [["name"::"C1", "s1"::0.0, "s2"::3.0, "v1p"::0.0, "v2p"::1.0, "v1c"::0.0, "v2c"::0.0,"maximize" :: true],["name"::"C2", "s1"::0.0, "s2"::3.0, "v1p"::0.0, "v2p"::2.0, "v1c"::0.0, "v2c"::0.0,"maximize" :: true],["name"::"C3", "s1"::0.0, "s2"::3.0, "v1p"::0.0, "v2p"::3.0, "v1c"::0.0, "v2c"::0.0,"maximize" :: true]];
		int choice <- evidence_theory_DM(candidates,criteria_ET, true);
		assert choice = 4;
		
		choice <- evidence_theory_DM(candidates,criteria_ET, false);
		assert choice = 0;
	}	
	
	test "fuzzy_choquet_DM"{
		map<list<string>,float> criteria_FC <- ([["C1"]::1.0, ["C2"]::2.0, ["C3"]:: 3.0,["C1","C2"]::1.0]); 
		int choice <- fuzzy_choquet_DM(candidates,["C1", "C2", "C3"],criteria_FC);
		assert choice = 4;
	}		
}


