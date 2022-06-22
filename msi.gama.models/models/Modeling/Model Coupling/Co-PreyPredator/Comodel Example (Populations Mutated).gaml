
/**
* Name: comodeling_example_populations_mutating
* Author: Arthur Brugiere & HUYNH Quang Nghi
* Description: This simple comodel serves to demonstrate the importation and instantiation of micro-model.
* Here triangle predators can eat any prey, square can eat square and round prey, and round predators can only eat round prey
* Tags: comodel
*/ 

model comodeling_example_populations_mutating

import "Prey Predator Adapter.gaml" as Organism

global
{
    // set the shape of world as a rectangle 200 x 100
    geometry shape <- square(100);
    init
    {
        //instantiate three instant of micro-model PreyPredator
        create Organism.Simple with: [shape::square(100), preyinit::10, predatorinit::1] number: 3;
    
        //explicitly save the orginal population of predator and original population of prey of each micro-model

        //the predator population of experiment 0 saved into the list lstpredator0  
        list<agent> lstpredator0 <- Organism.Simple[0].get_predator(); //PT1
        //the prey population of experiment 0 saved into the list lstprey0
        list<agent> lstprey0 <- Organism.Simple[0].get_prey(); //PR1
        
        //the predator population of experiment 1 saved into the list lstpredator1
        list<agent> lstpredator1 <-Organism.Simple[1].get_predator(); //PT2
        //the prey population of experiment 1 saved into the list lstprey1
        list<agent> lstprey1 <- Organism.Simple[1].get_prey(); //PR2

        //the predator population of experiment 2 saved into the list lstpredator2
        list<agent> lstpredator2 <- Organism.Simple[2].get_predator(); //PT3
        //the prey population of experiment 2 saved into the list lstprey2
        list<agent> lstprey2 <- Organism.Simple[2].get_prey(); //PR3
        

        //mutate the popuplation of micro-model by assigning the list above to  the population of micro-models
        (Organism.Simple[0].simulation).lstPredator <- lstpredator0;
        (Organism.Simple[1].simulation).lstPredator <- lstpredator0 + lstpredator1;
        (Organism.Simple[2].simulation).lstPredator <- lstpredator0 + lstpredator1 + lstpredator2;
    

        (Organism.Simple[0].simulation).lstPrey <- lstprey0;
        (Organism.Simple[1].simulation).lstPrey <- lstprey1;
        (Organism.Simple[2].simulation).lstPrey <- lstprey2;

        //change the shape correspond with the new role of agent in the new populations

        ask (Organism.Simple[2].simulation.lstPredator + Organism.Simple[2].simulation.lstPrey)
        {
            shape <- circle(1);
        }
        ask (Organism.Simple[1].simulation.lstPredator + Organism.Simple[1].simulation.lstPrey)
        {
            shape <- square(1);
        }
        ask (Organism.Simple[0].simulation.lstPredator + Organism.Simple[0].simulation.lstPrey)
        {
            shape <- triangle(1);


        }


    }

    reflex simulate_micro_models
    {
        // ask all simulation do their job
        ask (Organism.Simple collect each.simulation)
        {
            do _step_;
        }
    }

}

experiment main type: gui
{
    output synchronized:true
    {
        //a mixing display of all agents from all populations
        display "Comodel display"
        {
            agents "agentprey" value: (Organism.Simple accumulate each.get_prey());

            graphics "Prey of prey" {
                loop pr over:Organism.Simple accumulate each.simulation.lstPrey {
                    if(!dead(pr)){
	                    string n <- "PR"+int(pr.host.host);	                    	
	                    write n;
	                    draw "PREY" at:pr.location+{1,1,1} color:#green;
                    }
                }
            }

            agents "agentpredator" value: (Organism.Simple accumulate each.get_predator());

            graphics "Prey of prey" {
                loop pr over:Organism.Simple accumulate each.simulation.lstPredator {
                    draw "PDRT" at:pr.location+{-1,-1,1} color:#red;
                }
            }
        }
    }
}