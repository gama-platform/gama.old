/**
 *  bdimodel
 *  Author: carole
 *  Description: 
 */

model bdimodel

/* Insert your model definition here */


global {

	
}


entities {
	
	// a type to represent emotions
	species emotion {
		
		// emotion attributes
		string type;
		predicate about;
		float intensity;
		
		// emotion parameters
		float decayRate <- 1.2;
		float dieThreshold <- 0.1;
		
		string name update: dead(self)?' ':(type+' about '+about.name+' deg '+intensity);
		
		// TODO: logical definition?
		
		reflex decay {
			intensity <- intensity/decayRate;
		}
		
		reflex disappear when: intensity<dieThreshold {
			do die;
		}
		
	}
	


	// BDI + emotional + inference agent
	species bdiagent control: simple_bdi {  //control: fsm {

		// FIXME waiting for new version of simple_bdi
		//list<emotion> emotion_base;
		
		// emotion of max intensity
		string max_emotion <- 'neutral';
		
		string new_emotion update: empty(emotion_base)?'neutral':last(emotion_base).name;
		
		// list of available actions to reason about them? use plans from simple_bdi instead
		// (action name, conditions, effects)
		// list<list<string>> action_base;
		// [name::"fight',condition::"emotion=fear",effect::""]
		// list<map<string,string>> action_base;


		// remove dead emotion agents from the base
 		reflex remove_deademo{
			loop e over: (emotion_base where dead(each as emotion)) {
				//write 'remove '+each+' from emobase';
				remove each from: emotion_base;
			}		
		}


		/***************
		 * *** DEBUG ***
		 ***************/

		action display_bdie {
			write "B:" + length(belief_base) + ":" + (belief_base collect (each as predicate).name); 
			write "D:" + length(desire_base) + ":" + (desire_base  collect (each as predicate).name); 
			write "I:" + length(intention_base) + ":" + (intention_base collect (each as predicate).name); 
			write "curIntention:" + get_current_intention() ;
		
			write "E: " + length(emotion_base) + ' : '+ (emotion_base collect each.name);
			/*emotion e;
			loop e over: (emotion_base where !dead(each as emotion)) {
				write 'emo '+(e as emotion).type+' about '+(e as emotion).about+' deg '+(e as emotion).intensity;	
			}*/
		}



		/****************************
		 * ******* EMOTIONS ******* *
		 * **************************/


		action create_emotion(string etype, predicate eabout, float eintensity) {
			create emotion  {
				// set attributes of emotion
				type <- etype;
				about <- eabout;
				intensity <- eintensity;  // p.priority;
				
				// add emotion to emotion_base
				add self to: myself.emotion_base;
			}
		}

		
		/****************************
		 * ******* REFLEXES ******* *
		 ****************************/
		
		reflex emotion_trigger {
			do joy_pred;
			do hope_trigger;
		}
		
		// reflex to compute emotions from BDI predicates
		action joy_pred {
			predicate p;
			loop p over:desire_base {
				write 'desire that '+p;
				bool ok <- p in belief_base;
				write 'check '+p+' in beliefs = '+ok;
				if ok {
					if (empty(emotion where (each.type='joy' and each.about=p))) {
						do create_emotion('joy',p,(p as predicate).priority);
					}
					else {
						// reset intensity of existing emotion to max (1)
						any(emotion where (each.type='joy' and each.about=p)).intensity <- 1.0;
					}
				}
				else {
					if (empty(emotion where (each.type='sadness' and each.about=p))) {
						do create_emotion('sadness',p,(p as predicate).priority);
					}
					else {
						// reset existing sadness to max intensity (=1)
						any(emotion where (each.type='sadness' and each.about=p)).intensity <- 1.0;
					}
				}
			}
		}
		
		
		// trigger hope or fear about effects of plans for current intention
		action hope_trigger {
			
			predicate afterp;
			loop afterp over: beliefs_of_type('after') {
				string the_action <- (afterp.values at 'action') as string ;
				predicate effect <- (afterp.values at 'after') as predicate ;
				float proba <- (afterp.values at 'proba') as float ;
				
				write 'action = '+the_action;
				write 'effect = '+effect;
				write 'proba = '+proba;
				
				// TODO check the_action.when = current_intention
				
				// check if effect is in desires
				if (has_desire(effect)) {
					write 'has desire OK';
					// TODO degree depends on proba AND desirability
					do create_emotion('hope',effect,proba);
					write 'hope created';
				}
				// FIXME for fear, needs to have a desire that predicate is false
			} 			
			
			
		}


		// trigger emotions by analysing plans
		action plans_emotion {
			list<BDIPlan> myplans<-get_plans() as list<BDIPlan>;
			BDIPlan plan;
			loop plan over: myplans {
				// si j'ai un plan pour mon intention courante : hope
				bool applicable <- eval_gaml(plan.todo) as bool;
				write 'plan = '+plan;
				write 'applicable? '+applicable;
				if applicable {
					// hope to satisfy current intention
					do create_emotion('hope',get_current_intention(),1.0);
				}
			} 
		}
		
		action trigger_fear {
			// get plans for current intention
			list<BDIPlan> applic_plans <- applicable_plans;
			BDIPlan a_plan;
			
			loop a_plan over: applic_plans {
				// loop on effects
				list<predicate> effects <- get_effects(a_plan.name);
				predicate an_effect;
				loop an_effect over: effects {
					// if effect is desirable then hope
					if (has_desire(an_effect)) {
						do create_emotion('hope',an_effect,get_effect_proba(a_plan.name,an_effect));
					}	
					// if desire effect false then fear
					
					
				}
				
			}
		}
	
	// one reflex per emotion type ?
	// TODO ajouter les defs des autres emotions
	
	
	
	// update top emotion by finding the max intensity in emo base
	reflex update_topemo {
		emotion emomax <- any(emotion where !dead(each));
		if (emomax = nil) {
			max_emotion <- 'neutral';
		}
		else {
			loop e over: (emotion where !dead(each)) {
				if e.intensity > emomax.intensity {
					emomax <- e;
				}
			}
			max_emotion <- ' '+emomax.type + ' about '+emomax.about.name;		
		}
	}
	
	// expression show top emotion
	// TODO: condition intensity > expression threshold 		
	reflex show_emotion when: max_emotion!='neutral' { // finished_when: true {
		write 'emotion MAX = '+max_emotion;
		write 'emotion NEW = '+new_emotion;
	}
	
	
	
	
	
	
	/*********************************
	 * *******   BDI PLANS   ******* *
	 *********************************/

/* 	
	reflex hope_trigger {
		loop varname over:desires.keys {
			bool desireOK <- eval_gaml(varname+desires[varname]) as bool;
			if ( !desireOK ) {
				//loop myplan over:plans;
			}
		}
	}
*/	
	
	
	//  (belief_base where (((each as predicate).values at "type") = ptype);

	// negation of a predicate
 	predicate negation(predicate pred) {
		
		predicate neg <- new_predicate(pred.name,pred.values,!pred.is_true);
		
		return neg;
	}
	
	
	// action / function
	list<predicate> beliefs_of_type(string ptype)  {
		list<predicate> rez;
		rez <- (   (belief_base as list<predicate>) where (  (each as predicate).values at "type" = ptype  ) );
		
		return rez;
	}
	
	
	// is a plan applicable / matching the current intention
	bool is_applicable(BDIPlan a_plan) {
		return eval_gaml(a_plan.todo) as bool;
	}
	
	// return list of plans for current intention
	list<BDIPlan> applicable_plans {
		list<BDIPlan> myplans<-get_plans() as list<BDIPlan>;
		BDIPlan plan;
		loop plan over: myplans {
			if is_applicable(plan) {
				add plan to: myplans;
			}
		}
		return myplans;
	}
	
	// get list of effects (predicates) of a given plan
	list<predicate> get_effects(string planName) {
		list<predicate> afterRules <- beliefs_of_type("after");
		list<predicate> effects <- [];
		loop rule over: afterRules {
			// if the rule is an effect of that plan
			if ((rule as predicate).values at 'action') = planName {
				predicate an_effect <- (rule as predicate).values at 'after' as predicate;
				add an_effect to: effects;
			}
		}
		return effects;
	}
	
	// get probability of given effect of a given plan
	float get_effect_proba(string planName,predicate effect) {
		list<predicate> afterRules <- beliefs_of_type("after");
		return any(afterRules where (each.values at "action"=planName and each.values at "after"=effect)).values at "proba" as float;
	}

	/***********************************
	 * *******   BDI ACTIONS   ******* *
	 ***********************************/

	// returns boolean = finished = nothing new
	action update_kb {
		write 'updating kb';
		bool finished <- true;
		
		// find all rules, ie find all beliefs on predicates of type "implication"
		predicate rule;
		loop rule over: beliefs_of_type("implication") { 
					//(belief_base where (((each as predicate).values at "type") = "implication")) {
			predicate premise <- (((rule as predicate).values at "premise") as predicate);
			predicate conclu <- (((rule as predicate).values at "conclu") as predicate);

			if (has_belief(premise)) {
				write 'deduce conclu '+conclu.name+' from premise '+premise.name;
				do add_belief(conclu);
				finished <- false;
			}
		}
			
		return finished;
	}


	// add a new rule
	action add_rule(string rulename, predicate premise, predicate conclu) {
		predicate rule <- new_predicate(rulename,["type"::"implication","premise"::premise,"conclu"::conclu]);
		do add_belief(rule);
	}


	// TODO temporal predicates can be generated automatically from plans
	// when simple_bdi provides a type plan with conditions (aspect when) and effects (aspect finished_when)

	// temporal predicates for consequences of plans/actions
	// after(action,csq) with proba (so several consequences can be expected with various probabilities (non deterministic)
	action add_after(string rname, string planName, predicate csq, float proba) {
		// TODO the action field should be of type plan (when simple_bdi adds a type plan)
		predicate after <- new_predicate(rname,["type"::"after","action"::planName,"after"::csq,"proba"::proba]);
		do add_belief(after);
	}
	
	// idem for conditions of plans/actions
	// before(action,condition) no probability, conditions are necessary
	action add_before(string rname, string planName, predicate cond) {
		predicate before <- new_predicate(rname, ['type'::"before","action"::planName,"before"::cond]);
		do add_belief(before);
	}



	}
}


/* THE END */











/* ------- DRAFTS ------- */


	// to be implemented by sub-species: abstract actions
//	action test virtual:true;

	// TODO
	// action qui execute une action de la sub-specy
	// action qui verifie les conditions d'une action de la sub-specy et termine cette action si faux
	// action qui ? effets ? 

		
		/**************************
		 * ******* STATES ******* *
		 **************************/
		 // to emulate the perception-decision-action loop of BDI agents
		 
/*		 state perception initial:true {
		 	write 'agent '+self+' in perception mode';
		 	
		 	// perceive relevant properties, declared in an attribute?
		 	
		 	transition to: update ;	
		 }
		 
		 state update {
		 	write 'agent '+self+' in update mode';
		 	bool finished <- update_kb as bool; // returns: finished;
		 	
		 	// TODO need inference rules to apply
		 	
		 	transition to: appraisal when: finished;
		 }

		// trigger emotions
		state appraisal {
			write 'agent '+self+' in appraisal mode';
		}
		
		// plan action to reach current intention + schedule next action
		state planning {
			write 'agent '+self+' in planning mode';
			
		}

		// perform scheduled action
		state performing {
			write 'agent '+self+' in performing mode';
		}
*/



	/*****************
	 * *** ASPECTS ***
	 *****************/
	

