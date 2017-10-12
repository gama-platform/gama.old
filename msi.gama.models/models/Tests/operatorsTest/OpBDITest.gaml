/**
 *  OpOpBDITest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpBDITest.
 */

model OpBDITest

global {
	init {
		create testOpBDITest number: 1;
		ask testOpBDITest {do _step_;}
	}
}

	species testOpBDITest {

	
		test andOp {
			predicate1 and predicate2

		}
	
		test eval_whenOp {
			eval_when(plan1)

		}
	
		test get_aboutOp {
			get_about(emotion)

		}
	
		test get_agentOp {
			get_agent(social_link1)

		}
	
		test get_agent_causeOp {
			get_agent_cause(emotion)

		}
	
		test get_decayOp {
			get_decay(emotion)

		}
	
		test get_dominanceOp {
			get_dominance(social_link1)

		}
	
		test get_familiarityOp {
			get_familiarity(social_link1)

		}
	
		test get_intensityOp {
			emotion set_intensity 12

		}
	
		test get_lifetimeOp {
			get_lifetime(mental_state1)

		}
	
		test get_likingOp {
			get_liking(social_link1)

		}
	
		test get_modalityOp {
			get_modality(mental_state1)

		}
	
		test get_plan_nameOp {
			get_plan_name(agent.current_plan)

		}
	
		test get_predicateOp {
			get_predicate(mental_state1)

		}
	
		test get_solidarityOp {
			get_solidarity(social_link1)

		}
	
		test get_strengthOp {
			get_strength(mental_state1)

		}
	
		test get_super_intentionOp {

		}
	
		test get_truthOp {

		}
	
		test new_emotionOp {
			emotion("joy",eatFood)
			emotion("joy")
			emotion("joy",12.3,eatFood,4)
			emotion("joy",12.3,eatFood,4)
			emotion("joy",12.3,eatFood,4)
			emotion("joy",12.3,eatFood)
			emotion("joy",12.3)
			emotion("joy",12.3,eatFood,4)
			emotion("joy",12.3,eatFood,4)
			emotion("joy",12.3,4)
			emotion("joy",12.3,eatFood,4)
			emotion("joy",12.3,eatFood,4)

		}
	
		test new_mental_stateOp {
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)
			new_mental-state(belief)

		}
	
		test new_predicateOp {
			predicate("people to meet", people1 )
			predicate("people to meet", ["time"::10], true)
			predicate("people to meet")
			predicate("people to meet", ["time"::10], true)
			predicate("people to meet", ["time"::10], 10, agentA)
			predicate("people to meet", ["time"::10], 10,true)
			predicate("people to meet", ["time"::10], true)
			predicate("hasWater", true)
			predicate("people to meet", ["time"::10], true, agentA)
			predicate("people to meet", ["time"::10], 10, true, agentA)
			predicate("people to meet", ["time"::10], agentA)
			predicate("hasWater", 10 

		}
	
		test new_social_linkOp {
			new_social_link(agentA)
			new_social_link(agentA,0.0,-0.1,0.2,0.1)

		}
	
		test orOp {
			predicate1 or predicate2

		}
	
		test set_aboutOp {
			emotion set_about predicate1

		}
	
		test set_agentOp {
			social_link set_agent agentA

		}
	
		test set_agent_causeOp {
			emotion set_agent_cause agentA
			predicate set_agent_cause agentA

		}
	
		test set_decayOp {
			emotion set_decay 12

		}
	
		test set_dominanceOp {
			social_link set_dominance 0.4

		}
	
		test set_familiarityOp {
			social_link set_familiarity 0.4

		}
	
		test set_intensityOp {
			emotion set_intensity 12

		}
	
		test set_lifetimeOp {
			mental state set_lifetime 1

		}
	
		test set_likingOp {
			social_link set_liking 0.4

		}
	
		test set_modalityOp {
			mental state set_modality belief

		}
	
		test set_predicateOp {
			mental state set_predicate pred1

		}
	
		test set_solidarityOp {
			social_link set_solidarity 0.4

		}
	
		test set_strengthOp {
			mental state set_strength 1.0

		}
	
		test set_truthOp {
			predicate set_truth false

		}
	
		test with_lifetimeOp {
			predicate with_lifetime 10

		}
	
		test with_valuesOp {
			predicate with_values ["time"::10]

		}
	
	}


experiment testOpBDITestExp type: gui {}	
	