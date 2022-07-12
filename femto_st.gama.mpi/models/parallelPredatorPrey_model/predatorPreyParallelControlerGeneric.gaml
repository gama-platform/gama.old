/**
* Name: mpi controler
* Author:
* Description: 
* Tags: inheritance
*/

model ParallelMPIExperimentControler

global skills:[MPI_Network] {

    /* Parallelization values must be part of the skill */
    int mpiRank <- 0;
    int mpiNetSize <- 0;
    
    /* lau 16/3/21 only one of  simulatedModel or exp ? */
    Agent simulatedModel;
    agent exp;
    
    int leftNeighbor;
    int rightNeighbor;

    /* Strip limits */
    int stripStart;
    int stripEnd;

    /* OverLap Zone (OLZ) values */
    int OLZSize <- 10;
    int leftOuterOLZBound;
    int leftInnerOLZBound;	
    int rightInnerOLZBound;
    int rightOuterOLZBound;

    init {
    
	// to be put in skill	
        do MPI_INIT; 
	/* Set Env position values */
	myRank <- MPI_RANK();
	write "mon rank est " + myRank ;
	mpiNetSize <- MPI_SIZE ();
	// end: to be put in skill

	/* Recup parameters from starter ? */

	// Circular model
    	leftNeighbor <- mpiRank - 1;
    	if (leftNeighbor = -1){ leftNeighbor <- mpiNetSize -1;	}
    	rightNeighbor <- mpiRank + 1;
    	if (rightNeighbor = mpiNetSize) { rightNeighbor <- 0; }

	int modelSize <- simulatedModel.shape.width;
	int modelStripSize <- modelSize / mpiNetSize;

	/* Defines strip position values */
	stripStart <- modelStripSize * mpiRank;
	stripEnd <- (modelStripSize * mpiRank) +1;
	
	/* Defines overlap bounds */
    	leftOuterOLZBound <- stripStart - OLZSize;
    	leftInnerOLZBound <- stripStart + OLZSize;	
    	rightInnerOLZBound <- stripEnd - OLZSize;
    	rightOuterOLZBound <- stripEnd + OLZSize;
    }

    action cleanOuterOLZ() {
    
    	/*  First: kill agents that are not on our environment part : left and right */
	int p:
	if (node != 0){ // Only nodes with a left part of the env 
	   p <- evaluate_sub_model(exp,"ask prey where (each.location.x < stripStart {remove self from: scheduled_preys; do die;}");
	   p <- evaluate_sub_model(exp,"ask predator where (each.location.x < stripStart {remove self from: scheduled_predators; do die;}");
	}
	if ( node != (mpiNetSize-1) ) { // Only nodes with a right part of the env
	   p <- evaluate_sub_model(exp,"ask prey where (each.location.x >= stripEnd {remove self from: scheduled_preys; do die;}");	
	   p <- evaluate_sub_model(exp,"ask predator where (each.location.x >= stripEnd {remove self from: scheduled_predators; do die;}");
	}
    
    }

    action updateOuterOLZ() {

    	/*
	 * Get agents in the overlap zone and send them to neighbors
	 */

	/* Left inner overlap zone */
	if ( node != 0 ) { // Only nodes with a left part of the env
	   // Should be: list<Agent> agentList <- getAgentList(stripStart, leftInnertOLZBound) + sendAgents(leftNdeighbor, agentList)
	   list<prey> leftPreyList <- evaluate_sub_model(exp,"(prey where (each.location.x >= stripStart and each.location.x < leftInnertOLZBound)) collect (each)"); 
	   list<predator> leftPredatorList <- evaluate_sub_model(exp,"(predator where (each.location.x >= stripStart and each.location.x < leftInnertOLZBound)) collect (each)"); 
	   do MPI_SEND mesg: leftPreyList dest: leftNeighbor stag: 50; 
	   do MPI_SEND mesg: leftPredatorList dest: leftNeighbor stag: 50;
	   write("*** " + node + " sends left overlap");
	}

	/* Right inner overlap zone */
	if ( node != mpiNetSize - 1){ // Only nodes with a right part of the env
	   list<prey> rightPreyList <- evaluate_sub_model(exp,"(prey where (each.location.x >= rightInnertOLZBound and each.location.x < stripEnd)) collect (each)");
	   list<predator> rightPredatorList <- evaluate_sub_model(exp,"(predator where (each.location.x >= rightInnertOLZBound and each.location.x < stripEnd)) collect (each)");
	   do MPI_SEND mesg: rightPreyList dest: rightNeighbor stag: 50;      
	   do MPI_SEND mesg: rightPredatorList dest: rightNeighbor stag: 50;
	   write("*** " + node + " sends right overlap");		
    	}
	   
	/*
	 * Receive overlap zones from neighbors
	 */
	 
	/* Left outer overlap zone */
	if ( node != 0 ){
	   list<prey> leftOuterOLZPreys <- self MPI_RECV [rcvsize:: 2, source:: leftNeighbor, rtag:: 50];
	   list<predator> leftOuterOLZPreds <- self MPI_RECV [rcvsize:: 2, source:: leftNeighbor, rtag:: 50];
	   write("*** 0 receive overlap. received = " + leftOuterOLZPreys + " loop " + leftOverlapPreds);
	        
           int nbCreatePrey <- evaluate_sub_model(exp,"create_preys(" + leftOuterOLZPreys + ", false)");
           int nbCreatePred <- evaluate_sub_model(exp,"create_predators(" + leftOuterOLZPreds + ", false)");
        }
	
	/* Right outer overlap zone */
        if ( node != (mpiNetSize-1) ) {
	   list<prey> rightOuterOLZPreys <- self MPI_RECV [rcvsize:: 2, source:: rightNeighbor, rtag:: 50];
	   list<predator> rightOuterOLZPreds <- self MPI_RECV [rcvsize:: 2, source:: rightNeighbor, rtag:: 50];
	   write("*** 0 receive overlap. received = " + rightOuterOLZPreys + " loop " + rightOuterOLZPreds);
	        
           int nbCreatePrey <- evaluate_sub_model(exp,"create_preys(" + rightOuterOLZPreys + ", false)");
       	   int nbCreatePred <- evaluate_sub_model(exp,"create_predators(" + rightOuterOLZPreds + ", false)");
        }
    }

    action updateInnerOLZ() {
    
    	/*
	 * Gather agents that have moved in the OLZ and send then to the neighbors */
	 */

	/* Left outer overlap zone */
	if ( node != 0 ) {
	   list<prey> leftOutcomePreyd <- evaluate_sub_model(exp,"(scheduled_preys where (each.location.x >= leftOuterOLZBound and each.location.x < stripStart)) collect (each)"); 
	   list<predator> leftOutcomePredators <- evaluate_sub_model(exp,"(scheduled_predators where (each.location.x >= leftOuterOLZBound and each.location.x < stripStart)) collect (each)"); 
	   do MPI_SEND mesg: leftOutcomePreys dest: leftNeighbor stag: 50;
	   do MPI_SEND mesg: leftOutcomePreys dest: leftNeighbor stag: 50;
    	}
	
    	/* Right outer overlap zone */
    	if ( node != (mpiNetSize-1) ) {
	   list<prey> rightOutcomePreys <- evaluate_sub_model(exp,"(scheduled_preys where (each.location.x >= stripEnd and each.location.x < rightOuterOLZBound)) collect (each)"); 
	   list<predator> rightOutcomePredators <- evaluate_sub_model(exp,"(scheduled_predators where (each.location.x >= stripEnd and each.location.x < rightOuterOLZBound))) collect (each)"); 
	   do MPI_SEND mesg: rightOutcomePreys dest: rightNeighbor stag: 50;
	   do MPI_SEND mesg: rightOutcomePredators dest: rightNeighbor stag: 50;
    	}
	write ("*** " + node + " after send outcomes" );
		
    	/*
	 * Receive incominig agents and create them in my part
	 */
	 
	/* Left outer overlap zone */
    	if (node != 0 ) {
	   list<prey> leftIncomePreys <- self MPI_RECV [rcvsize:: 2, source:: leftNeighbor, rtag:: 50];
	   list<predator> leftIncomePredators <- self MPI_RECV [rcvsize:: 2, source:: leftNeighbor, rtag:: 50];
	   // MERGE !!!
	   int nbCreatePrey <- evaluate_sub_model(exp,"create_preys("+ leftIncomePreys +", true)");
	   int nbCreatePred <- evaluate_sub_model(exp,"create_predators("+ leftIncomePredators +", true)");
	   write ("*** " + node + " after incomes" );	
	}

	/* Right outer overlap zone */
    	if ( node != (mpiNetSize-1) ) {
	   list<point> rightIncomePreys <- self MPI_RECV [rcvsize:: 2, source:: rightNeighbor, rtag:: 50];
	   list<point> rightIncomePredators <- self MPI_RECV [rcvsize:: 2, source:: rightNeighbor, rtag:: 50];
	   // MERGE !!!
	   int nbCreatePrey <- evaluate_sub_model(exp,"create_preys("+ rightIncomePreys +", true)");
	   int nbCreatePred <- evaluate_sub_model(exp,"create_predators("+ rightIncomePredators +", true)");
	   write ("*** " + node + " after incomes" );
	}
    }

    /* lau 16/3/21 ici ou dans starter ? */
    reflex timeStep()
    {
    	
	cleanOuterOLZ();
	updateOuterOLZ();
	simulatedModel.step();
	updateInnerOLZ();
	
    }
}
