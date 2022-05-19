package femto_st.gama.mpi;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.types.IType;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.root.PlatformAgent;




/**
 * Point Alexis :
 * 
 * 
 * Surcharge de méthode dans gama par l'user --> rédéfinition de l'action
 * 
 * Type java pour grid -> grid pour le découpage de la simulation sur les procs
 * 				-> grid topology (GamaSpatialMatrix)
 * 
 * check gama GamaQuadTree
 * 
 * Fonction de création d'OLZ automatique selon une shape
 * 				-> récupérer un des côtés d'une shape
 * 
 * 		->				Geometry : base de toute les formes de gama
 * 		Java Topological Suite (JTS check)
 * 
 * Ishape -> getInnerGeometry() -> 
 * 
 * Modèle template pour la distrib 
 * 
 * Développement de la distrib le plus proche possible du gaml pour redéfinition par l'user
 * 
 * MPISkill dans master et dans slave
 * 
 * Classe universel pour les agents ? 
 * 				-> récupération du vrai type depuis un container
 */

/**
 * 
 * todo check : paramétrage du garbage collector dans les procs
 * todo check : exactitude des méthode de controle de la mémoire et des procs
 * todo check : getNumberOfProccessorAvailable() sur cluster avec nombre de noeuds dynamique
 * todo check : Communication de groupe MPI == Broadcast (utile pour le controlerMPI)
 * 
 * todo define : lib utils pour la réception et envoie d'agent entre proc (envoie/reception + création centralisé d'individu)
 * 			-> attribution de seed par procs
 * 			-> initialisation de l'experiment (suivant le total d'agent à initialiser -> distribution de ceux-ci entre les procs pour les init)
 * todo define : modèle de distribution par défault selon le nombre de proc dispo (distrib spatial pour commencer)
 * 			-> tous les X cycles changé de distrib vers une distrib avec +1 proc (1->2->3....)
 * 
 * todo define : CPU and thread monitor : https://docs.oracle.com/javase/7/docs/api/java/lang/management/ThreadMXBean.html
 * 
 * todo define : grid de distribution (setter possible + grid de base en fonction du nombre de proc)
 * 					-> X ou Y ou XY   X = (-)  Y =(|)  XY = (|-|-|)
 * 					-> grid à 4 voisins, 6 voisins, 8 voisins
 * 					-> User input process -> gestion des erreurs si ne correspond pas aux ressources
 * 
 * todo discuss : centralisation de la création d'agent à l'init + au runtime (garder une cohérence entre le nom des individus des espèces)
 * todo discuss : redistribution centralisé ou chaque processus distribue sa charge sur la nouvelle distrib ? 
 * 					-> centralisation des infos des envoies/réception -> ordre de traitement + syncho
 * todo discuss : distribution des agents avec des shapes chevauchant 2 procs 
 * todo discuss : AOI (area of interest <> data from step-1) / OLZ (overlapping zone <> data from step + process at end of step) 
 * todo discuss : synchro ghost (division du modèle en grille et limitation de l'éxécution du step n d'une cellule si les voisins n'ont pas finis le step n-1)
 * 						-> implémentation via AOI ou OLZ
 * 
 * todo discuss : synchro neighbors (système de grille et envoie de copie seulement aux voisins) + découpage de la zone géré par les proc en 4
 * 					pour éxécution simultanés (0 puis 1 puis 2 puis 3).  -> limite à des models 2D
 * 
 * todo discuss : syncho mode Lecture -> ghost
 * 				  synchro mode Ecriture unique -> non ghost 
 * 				  synchro mode Ecriture concurente ->  non ghost + synchro agent individuel
 * 
 * todo discuss : mode de syncho
 * 					-> pas de synchro en dehors des zones de perception (tronqué au limite de proc)
 * 					-> OLZ / AOI -> pas de report de modif des agents copié
 * 					-> Ecriture Asynchrone (EA) -> modif du valeur sans acquitement (prise en compte dans le step courant)
 * 					-> Synchronisation Stricte (SS) -> modif de valeur avec acquitement bloquant
 * 					-> synchronisation stricte décalée (lazy load) (SSD) -> mise en pause de l'agent jusqu'a la fin du step puis traitement
 * 
 * ------------------------------------------------------------------
 * Synchro Ghost: 
 * 
 * Accès direct aux données du step n-1 pour les agents partagées
 * 		
 * 		Problème : Besoin d'accèder au données de la simulation au step n-1
 * 		Solution : 
 * 			-> Lib d'accès au noeud XML de la simualtion n-1 serialisé (complexe pour trouver l'agent/données dans le xml)
 * 			-> Dédoublement de l'experiment sur le proc (simple mais peut-être couteux en ressource?)
 * 			-> Copie locale des agents de la zone fantome
 * 				-> Si propagation des changements sur les copies -> coûteux + gestion des conflits nécéssaire
 * 				-> Sinon, perte d'intégrité du modèle (rapide)
 * 			-> Centralisation de la simu n-1 sur le proc master (beaucoup de message à envisager)
 * 				-> regroupement des requêtes et du traitement ? 

 * 
 * Loop (Ghost) until end of simulation 
 * {  
 * 		Process du cycle sur les données du cycle N-1
 * 		Destruction du buffer/agents ghost du cycle N-1
 * 
 *		Synchro des procs
 * 		Récupération/Envoie des buffers/agents ghost du cycle N aux voisins
 * 		Synchro des procs
 * }		
 * 
 *  ------------------------------------------------------------------
 *   * Synchro non Ghost: 
 * 
 * Accès direct au donnée du step courant pour les agents partagées
 * 		
 * 		Problème : Besoin d'accèder aux données des agents dans le buffer au step courant
 * 		Solution : 
 * 			-> Exécution des sous-zones de proc en 4 temps 
 * 				-> les zones inter procs sont donc éxécutés à des temps différents
 * 			-> Update des buffers à la fin de l'éxécution des sous-zones
 * 				-> les données des agents sont à jour et sans accès concurrent entre les procs
 * 
 * 					Distribution sur les axes (X,Y)
 * 			_________________________________
 			|		|		|		|		|
 			|	0	|	1	|	0	|	1	|
 	P1		|-------|-------|-------|-------|     P2
 			|	2	|	3   |	2	|	3	|
 			|_______|_______|_______|_______|
 			|		|		|		|		|
 			|	0	|	1	|	0	|	1	|
 	P3		|-------|-------|-------|-------|	  P4
 			|	2	|	3   |	2	|	3	|		 			 			 			 			 			
 			|_______|_______|_______|_______|
 			
 			
 			
					Distribution sur l'axe (X)
 			
 					P1				P2				P3				P4	
			_________________________________________________________________
 			|		|		|		|		|		|		|		|		|			
 			|	0	|	1	|	0	|	1	|	0	|	1	|	0	|	1	|
 			|-------|-------|-------|-------|-------|-------|-------|-------| 
 			|	2	|	3   |	2   |	3   |	2   |	3   |	2	|	3	|
 			|_______|_______|_______|_______|_______|_______|_______|_______|
 * 
 * 
 * Loop (non Ghost) until end of simulation 
 * {
 * 		Process de la zone (0)
 * 		Update des buffers  
 * 		Synchro des procs
 * 
 * 		Process de la zone (1)
 * 		Update des buffers 
 * 		Synchro des procs 
 * 
 * 		Process de la zone (2)
 * 		Update des buffers
 * 		Synchro des procs  
 * 
 * 		Process de la zone (3)
 * 		Update des buffers
 * 		Synchro des procs  
 * }		
 * 
 *  ------------------------------------------------------------------
 * Synchro Github 
 * 
 * Merge des simulations de chaque proc en 1 seule nouvelle simulation via la sérialisation de simulation
 * 
 * 		-> Serialisation/désérialisation des simulation OK    (serialize_agent)
 * 		-> Remplacement d'un simulation par une autre OK   (unSerializeSimulationFromXML)
 * 
 * 		Problème : détection des conflits
 * 		Solution : ??? 
 * 
 * 		Problème : Synchronisation entre les processus :
 * 
 * 		Solution : 2 stratégies 
 * 
 * 		-> Stratégie optimiste (merge tous les X cycles)
 * 			-> Conflit plus dur à gérer 
 * 			-> Rollback à un cycle donné complexe (nessécite de garder la sérialisation de la simulation de tous les cycles)
 * 			-> Destruction des agents en conflit (simple)
 * 			-> Synchro tous les X cycles
 * 
 * 		-> Stratégie pessimiste (merge à tous les cycles)
 * 			-> Conflit moins dur à gérer 
 * 			-> Rollback de l'éxécution de la simulation sur un proc possible
 * 			-> Destruction des agents en conflit (simple)
 * 			-> Synchro nécéssaire à chaque cycle
 * 		
 * 		-> Nécéssite une communication socket entre master et slave pour la synchro
 * 
 * 
 * Loop (Github) until end of simulation 
 * {
 * 
 * 		Envoie des simu seria via MPI
 * 		Création de simu à partie de seria  -> ReverseOperator  (unSerializeSimulationFromXML)
 *		Triage des données propre au processeur
 * 		Process des cycles sur les slaves
 * 		Renvoie de la simulation au master
 * 		Détection de conflit sur les simulations sérialisées
 * 		Merge 
 * 
 * }
  *  ------------------------------------------------------------------
  *  
 * 
 * 
 * 
 */

@species(name = "ControlerMPI", skills={IMPISkill.MPI_NETWORK})
@doc ("ControlerMPI to distribute model on cluster/multi-thread")
@vars({
	@variable (
			name = ControlerMPI.MEMORY_AVAILABLE, 
			type = IType.INT, 
			constant = true,
			init = "0",
			doc = { @doc ("Current total memory available")}),
	
	@variable (
			name = ControlerMPI.MEMORY_USAGE, 
			type = IType.INT, 
			constant = true,
			init = "0",
			doc = { @doc ("Current memory used")}),
	@variable (
			name = ControlerMPI.MAX_MEMORY_AVAILABLE, 
			type = IType.INT, 
			constant = true,
			init = "0",
			doc = { @doc ("Maximum memory available")}),
	
	@variable (
			name = ControlerMPI.PROC_USAGE,
			type = IType.INT, 
			constant = true,
			init = "1",
			doc = { @doc ("Current number of processus used")}),
	
	@variable (
			name = ControlerMPI.PROC_AVAILABLE,
			type = IType.INT,
			constant = true,
			init = "1",
			doc = { @doc ("Current number of processus available")}),
	
	@variable (
			name = ControlerMPI.NUMBER_OF_AGENT_BY_PROC, 
			type = IType.MAP, 
			constant = true,
			init = "[]",
			doc = { @doc ("Map containing the count of agent for all processus")}),
	
	@variable (
			name = ControlerMPI.PROC_NUMBER_WITH_HIGHEST_TIME_PROCESSING_STEP,
			type = IType.INT, 
			constant = true,
			init = "0",
			doc = { @doc ("Number of the processus that had the highest time processing the last step")}),
	

	@variable (
			name = ControlerMPI.PROC_NUMBER_WITH_LOWEST_TIME_PROCESSING_STEP,
			type = IType.INT, 
			constant = true,
			init = "0",
			doc = { @doc ("Number of the processus that had the lowest time processing the last step")}),

	@variable (
			name = ControlerMPI.DIFFERENCE_HIGHEST_LOWEST_STEP_PROCESS_TIME,
			type = IType.FLOAT, 
			constant = true,
			init = "0.0",
			doc = { @doc ("Difference highest lowest time of step processing")}),

	@variable (
			name = ControlerMPI.PROCESSING_TIME_OF_STEP_OF_EACH_PROC,
			type = IType.MAP,
			constant = true,
			init = "[]",
			doc = { @doc ("Time processing the last step of each processus")}),

	@variable (
			name = ControlerMPI.AVERAGE_PROCESSING_TIME_OF_STEP,
			type = IType.FLOAT,
			constant = true,
			init = "0.0",
			doc = { @doc ("Average time processing the last step")}),

	@variable (
			name = ControlerMPI.PERCENT_OF_AGENT_LOAD_ON_EACH_PROC, 
			type = IType.MAP,
			constant = true,
			init = "[]",
			doc = { @doc ("Percent of total agent on each proc")}),

	@variable (
			name = ControlerMPI.HIGHEST_AGENT_LOAD_PERCENT_ON_ONE_PROC, 
			type = IType.FLOAT,
			constant = true,
			init = "0.0",
			doc = { @doc ("highest_agent_load_percent_on_one_proc")}),

	@variable (
			name = ControlerMPI.LOWEST_AGENT_LOAD_PERCENT_ON_ONE_PROC, 
			type = IType.FLOAT,
			constant = true,
			init = "0.0",
			doc = { @doc ("lowest_agent_load_percent_on_one_proc")}),
	
	@variable (
			name = ControlerMPI.EXPERIMENT_TOTAL_TIME,
			type = IType.FLOAT, 
			constant = true,
			init = "0.0",
			doc = { @doc ("Total time took to run the experiment")}),
	
	@variable (
			name = ControlerMPI.NUMBER_OF_AGENT_EXCHANGE_BETWEEN_PROC,
			type = IType.INT, 
			constant = true,
			init = "-1",
			doc = { @doc ("Number of agent exchange between proc for the last step")}),
	
	@variable (
			name = ControlerMPI.NUMBER_OF_EXCHANGE_BETWEEN_AGENT_ON_DIFFERENT_PROC,
			type = IType.INT, 
			constant = true,
			init = "-1",
			doc = { @doc ("Number of exchange between agent on different proc for the last step")}),
	
	@variable (
			name = ControlerMPI.AVERAGE_AGENT_PROCESS_TIME,
			type = IType.FLOAT, 
			constant = true,
			init = "0.0",
			doc = { @doc ("Average time took to process one agent")}),
	
	@variable (
			name = ControlerMPI.HIGHEST_AGENT_PROCESS_TIME,
			type = IType.FLOAT, 
			constant = true,
			init = "0.0",
			doc = { @doc ("Highest time took to process one agent")}),
	
	@variable (
			name = ControlerMPI.LOWEST_AGENT_PROCESS_TIME,
			type = IType.FLOAT, 
			constant = true,
			init = "0.0",
			doc = { @doc ("Lowest time took to process one agent")}),
	
	
	@variable (
			name = ControlerMPI.AGENT_PROCESS_TIME_ON_EACH_PROC,
			type = IType.MAP, 
			constant = true,
			init = "[]",
			doc = { @doc ("Time took by each proc to process his agents")}),
	
	@variable (
			name = ControlerMPI.DIFFERENCE_HIGHEST_LOWEST_AGENT_PROCESS_TIME,
			type = IType.FLOAT, 
			constant = true,
			init = "0.0",
			doc = { @doc ("Difference between highest and lowest agent process time")}),
	
	@variable (
			name = ControlerMPI.NUMBER_OF_REALLOCATION,
			type = IType.INT, 
			constant = true,
			init = "0",
			doc = { @doc ("Number of time we did a reallocation on the experiment")}),
	
	@variable (
			name = ControlerMPI.MAX_NUMBER_OF_REALLOCATION,
			type = IType.INT, 
			init = "-1",
			doc = { @doc ("Maximum number of time we can reallocate the experiment")}),
	
	@variable (
			name = ControlerMPI.MAX_PROC_TO_USE, 
			type = IType.INT, 
			init = "100",
			doc = { @doc ("Maximum number of procs to use to run the experiment")}),
	
	@variable (
			name = ControlerMPI.MIN_PROC_TO_USE, 
			type = IType.INT, 
			init = "1",
			doc = { @doc ("Minimum number of procs to use to run the experiment")})

})
public class ControlerMPI extends GamlAgent {
	

	public static final String EXPERIMENT_TOTAL_TIME = "total_time_of_experiment";
	

	public static final String MAX_MEMORY_AVAILABLE = "max_memory_available";
	public static final String MEMORY_AVAILABLE = "memory_available";
	public static final String MEMORY_USAGE = "memory_usage";
	
	public static final String PROC_USAGE = "number_of_processor_used";
	public static final String PROC_AVAILABLE = "number_of_processor_available";
	
	public static final String MIN_PROC_TO_USE = "min_number_of_proc_to_use";
	public static final String MAX_PROC_TO_USE = "max_number_of_proc_to_use";
	
	public static final String MAX_NUMBER_OF_REALLOCATION = "max_number_of_reallocation";
	public static final String NUMBER_OF_REALLOCATION = "current_number_of_reallocation";
	
	public static final String NUMBER_OF_AGENT_BY_PROC = "number_of_agent_by_processus";
	
	public static final String AGENT_PROCESS_TIME_ON_EACH_PROC = "time_of_agent_process_for_each_proc";
	public static final String AVERAGE_AGENT_PROCESS_TIME = "average_time_to_process_agent";
	public static final String HIGHEST_AGENT_PROCESS_TIME = "lowest_time_to_process_agent";
	public static final String LOWEST_AGENT_PROCESS_TIME = "highest_time_to_process_agent";
	public static final String DIFFERENCE_HIGHEST_LOWEST_AGENT_PROCESS_TIME = "difference_highest_lowest_agent_process_time";
	
	public static final String NUMBER_OF_EXCHANGE_BETWEEN_AGENT_ON_DIFFERENT_PROC = "number_of_exchange_between_agent_on_different_proc";
	public static final String NUMBER_OF_AGENT_EXCHANGE_BETWEEN_PROC = "number_of_exchange_of_agent_between_proc_last_step";
	
	public static final String PERCENT_OF_AGENT_LOAD_ON_EACH_PROC = "percent_of_agent_load_on_each_proc";
	public static final String AVERAGE_AGENT_LOAD_PERCENT_ON_ONE_PROC = "average_agent_load_percent"; ////// todo 
	public static final String HIGHEST_AGENT_LOAD_PERCENT_ON_ONE_PROC = "highest_agent_load_percent";
	public static final String LOWEST_AGENT_LOAD_PERCENT_ON_ONE_PROC = "lowest_agent_load_percent";
	public static final String DIFFERENCE_HIGHEST_LOWEST_AGENT_LOAD_ON_ONE_PROC = "difference_highest_lowest_agent_load_percent"; ///// todo
	
	public static final String PROCESSING_TIME_OF_STEP_OF_EACH_PROC = "processing_time_of_step_of_each_proc";
	public static final String AVERAGE_PROCESSING_TIME_OF_STEP = "average_time_processing_step";
	public static final String PROC_NUMBER_WITH_HIGHEST_TIME_PROCESSING_STEP = "proc_number_with_highest_time_processing_step";
	public static final String PROC_NUMBER_WITH_LOWEST_TIME_PROCESSING_STEP = "proc_number_with_lowest_time_processing_step";
	public static final String DIFFERENCE_HIGHEST_LOWEST_STEP_PROCESS_TIME = "difference_highest_lowest_step_process_time";
	
	PlatformAgent platform;
	ExperimentAgent experiment;
    Runtime runtime;
    
    // https://github.com/oshi/oshi -> os control + monitor
    int to_megabyte = 1024 * 1024;
    
	Map<Integer,ArrayList<String>> agentAttribution; // proc number::list of agent on this proc
	Map<Integer,ArrayList<String>> nextAgentAttribution; // proc number::list of agent on this proc
	
	ArrayList<Integer> procList = new ArrayList<Integer>(); // todo intialize with value
	
	public ControlerMPI(IPopulation<? extends IAgent> s, int index) 
	{
		super(s, index);
		runtime = Runtime.getRuntime();
		experiment = (ExperimentAgent) GAMA.getRuntimeScope().getExperiment();
		platform = GAMA.getPlatformAgent();
	}
	
	public double getExperimentTime()
	{
		return experiment.getClock().getTotalDuration();
	}
	
	public void sendAgent(int source, int dest, String species, int agentID)
	{
		
		// get agent from source.
		// Object agent = proc.get(source).getAgent(species+agentID);
		// proc.get(dest).add(agent);	
		
		agentAttribution.get(source).remove(species+agentID);
		agentAttribution.get(dest).add(species+agentID);
		
	}
	
	public ArrayList<String> getAgentsFromProc(int proc_number)
	{
		return agentAttribution.get(proc_number);
	}
	
	public int getPositionOfAgent(String species, int agent_id)
	{
		for(var proc : agentAttribution.entrySet())
		{
			if(proc.getValue().contains(species+agent_id)) 
			{
				return proc.getKey();
			}
		}
		return -1;
	}
	
	
	public void initializeExperiment()
	{
		// random seed
		// dispatch all species on random proc to be created
		
		// create utilsMPI species in slave proc ? 
		
	}
	
	public void do_step()
	{
		// synchronize
		// do step of the experiment
		// update data
		// synchronize
	}
	
	// wait all proc before stepping into next action
	public void synchronize()
	{
		// request of sync to all procs
		// when every procs as ack the controler then proceed
	}
	
	// reflex after/before each step
	public void udpate()
	{
		// update all trigger data after/before each step
		// if trigger activated -> reallocate
	}
	
	// Action
	public String serialize()
	{
		// check if synchro is done
		// write all trigger values
		// write the position of each agent + associated proc
		// write experiment info (time + cycle + agent?)
		
		return "";
	}
	
	public void reallocate()
	{
		// synchronize all proc to current step
		// serialize the current allocation
		// process all reallocation possibilities
		// score them
		// take the best
		// define area from the best reallocation
		// associate area to a proc (take care at reduce the number of exchange between procs)
		// get agent in these area by looking at them in the current reallocation
		// move them from old area/old proc to new area/new proc
		
		// update trigger data
		// do the next step of the experiment
	}
	
	public void moveAgents()
	{
		for(var proc : agentAttribution.entrySet())
		{
			moveAgentTo(proc.getKey());
		}
	}
	
	public void moveAgentTo(int procToMoveOn)
	{
		ArrayList<String> agentToGet = nextAgentAttribution.get(procToMoveOn);
		
		for(var proc : agentAttribution.entrySet())
		{
			if(proc.getKey() != procToMoveOn)
			{
				ArrayList<String> agentToMove = (ArrayList<String>) CollectionUtils.intersection(proc.getValue(), agentToGet);
				agentToGet.removeAll(agentToMove);
				
				// proc.getKey().sendTo(procToMoveOn,agentToMove);
				// procToMoveOn.receiveFrom(procToMoveOn,agentToMove);
				
				// proc.unleash(agentToMove);
				// proc.leash(agentToMove);
			}
		}
	}
	
	
	// check for dynamical change of number of proc available
	@action(name="getNumberOfProccessorAvailable")
	@doc ("Return the number of core available on JVM startup")
	@getter(PROC_AVAILABLE)
	public int getNumberOfProccessor(final IScope scope)
	{
        return runtime.availableProcessors();
	}

	@action(name="getMaximalMemoryAvailable")
	@doc ("Return the maximum memory available in megabyte")
	@getter(MAX_MEMORY_AVAILABLE)
	public float getMaximalMemoryAvailable(final IScope scope)
	{
        return runtime.totalMemory() / to_megabyte;
    }
	
	@action(name="getFreeMemoryAvailable")
	@doc ("Return memory usage in megabyte")
	@getter(MEMORY_AVAILABLE)
    public float getFreeMemoryAvailable(final IScope scope)
	{
        return runtime.freeMemory() / to_megabyte;
    }

	@action(name="getMemoryUsage")
	@doc ("Return memory usage in megabyte")
	@getter(MEMORY_USAGE)
    public float getMemoryUsage(final IScope scope)
	{
        return ((runtime.totalMemory() - runtime.freeMemory()) / to_megabyte);
    }

	@action(name="cpu")
	public void xxx(final IScope scope)
	{

	    System.out.println("HELLO ");
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
			
		for(Long threadID : threadMXBean.getAllThreadIds()) {
		    ThreadInfo info = threadMXBean.getThreadInfo(threadID);
		    System.out.println("Thread name: " + info.getThreadName());
		    System.out.println("Thread State: " + info.getThreadState());
		    System.out.println(String.format("CPU time: %s ns", 
		      threadMXBean.getThreadCpuTime(threadID)));
		  }
	}
	
	@setter(MAX_NUMBER_OF_REALLOCATION)
	public void setMax_number_of_reallocation(final int number) {
		if(number < 0)
		{
			setAttribute(MAX_NUMBER_OF_REALLOCATION, 1);
		}else
		{
			setAttribute(MAX_NUMBER_OF_REALLOCATION, number);
		}
	}
	
	@setter(MAX_PROC_TO_USE)
	public void setMax_number_of_proc_to_use(final int number)
	{
		if(number < 0)
		{
			setAttribute(MAX_PROC_TO_USE, 1);
		}else
		{
			setAttribute(MAX_PROC_TO_USE, number);
		}
	}
	
	@setter(MIN_PROC_TO_USE)
	public void setMin_number_of_proc_to_use(final int number)
	{
		if(number < 0)
		{
			setAttribute(MIN_PROC_TO_USE, 1);
		}else
		{
			setAttribute(MIN_PROC_TO_USE, number);
		}
	}
}



