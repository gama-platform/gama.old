/**
* Name: mpi starter
* Author:
* Description: 
* Tags: inheritance
*/

model ParallelMPIExperimentControler

    reflex timeStep()
    {
    	
	controler.cleanOuterOLZ();
	controler.updateOuterOLZ();
	simutatedModel.step();
	controler.updateInnerOLZ();
    }
    		
/* Attention ici le nom de l'expe doit etre le meme que celui donne dans le xml */
experiment ParallelMPIStarter type: gui {

    Agent simutatedModel;
    Agent controler;

    init {

	/* Set model */
	/* Caution ! ici le nom de l'expe doit etre le meme que celui donne dans le xml */	
	simulatedModel <- load_sub_model("prey_predatorExp","/home/philippe/recherche/git/gama.experimental/femto.st.gama.mpi/models/parallelPredatorPrey/predatorPrey.gaml");
	
	controler <- createControler(simulatedModel, OLZSize);
    }




}
