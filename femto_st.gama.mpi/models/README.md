	GAMA-MPI MODELS
	---------------
	
# Run models:
- ./startMpiModel model.xml NB_PROC

# Directories

## logs:
- 1/rank.X : output of a simulation from processor X point of view
- console-outputs-0.txt : output from global species

## incldude (include directory)

## basic:
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
 

