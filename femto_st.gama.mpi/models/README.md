	GAMA-MPI MODELS
	---------------
	
# Run models:
- ./startMpiModel model.xml NB_PROC
	-> GAML logs : filenameX.txt in the current directory
	-> JAVA logs : logX.txt in the directory of the model

# Directories

## include (include directory)

## basic: (basic MPI test)
- first_mpi.gaml: display MPI_RANK and MPI_SIZE
- communication.gaml: sending int and list of int through MPI_SEND and receiving MPI_RECV

# parallelPredatorPrey: (basic prey-predator model)
- predatorPrey.gaml: base model used by the controller, it implements the basic model + methods helping the controler to retrieve data from the model.
- predatorPreyControler.gaml: controler of predatorPrey.gaml, it implements all the MPI function.

# movingAgent: (agent moving inside 2D world)
- MovingAgent.gaml: base model used by the controller, it implements the basic model + methods helping the controler to retrieve data from the model.
- MovingAgentControler.gaml: controler of MovingAgent.gaml, it implements all the MPI function.  

# 3D: (agent moving inside 3D world)
- Cube.gaml: base model used by the controller, it implements the basic model + methods helping the controler to retrieve data from the model.
- CubeControler.gaml: controler of Cube.gaml, it implements all the MPI function.  

# shapefile : (agent moving inside 2D world defined with a shapefile)
- SHP.gaml: base model used by the controller, it implements the basic model + methods helping the controler to retrieve data from the model.
- SHPControler.gaml: controler of SHP.gaml, it implements all the MPI function.  

# plugin_models : (model to test the gama plugin)
- OLZ.gaml: agents moving in the world, the world is divided into area, each area is processeced by a different processor
- OLZ_Controler.gaml: controler of SHP.gaml, it implements all the MPI function. 
 

