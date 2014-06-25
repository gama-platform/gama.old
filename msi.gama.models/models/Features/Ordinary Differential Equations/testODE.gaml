/**
 *  testODE
 *  Author: HUYNH
 *  Description: 
 */

model testODE
import "Built-In Equations.gaml" as BIE
import "Lotka-Volterra (Influence of Integration Step).gaml" as LVI
import "Lotka-Volterra (Simple).gaml" as LVS
import "SIR (ABM vs EBM).gaml" as SIR0
import "SIR (Built-In).gaml" as SIR1
import "SIR (Influence of Integration Step).gaml" as SIR2
import "SIR (Simple).gaml" as SIR3
import "SIR (Split in Agents, Multiple Strains).gaml" as SIR4
import "SIR (Split in Agents).gaml" as SIR5
import "SIR (Switch).gaml" as SIR6
global{
	init{
		create BIE.examples;
		create LVI.maths;
		create LVS.maths;
		create SIR0.Simulation;
		create SIR1.mysimulation;
		create SIR2.mysimulation1;
		create SIR3.maths;
		create SIR4.Simulation;
		create SIR5.Simulation;
		create SIR6.mysimulation;		
		
	}
	reflex doing{
		ask BIE.examples{ do _step_; }
		ask LVI.maths{ do _step_; }
		ask LVS.maths{ do _step_; }
		ask SIR0.Simulation{ do _step_; }
		ask SIR1.mysimulation{ do _step_; }
		ask SIR2.mysimulation1{ do _step_; }
		ask SIR3.maths{ do _step_; }
		ask SIR4.Simulation{ do _step_; }
		ask SIR5.Simulation{ do _step_; }
		ask SIR6.mysimulation{ do _step_; }
	}
}
/* Insert your model definition here */
experiment testODEexp type:gui{
	
}
