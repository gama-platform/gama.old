package gama_analyzer;

import msi.gama.common.interfaces.IKeyword;	 
import msi.gama.common.util.GeometryUtils;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.variables.Variable;
import msi.gaml.types.Types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import autres.Analyse_statement;
import msi.gama.util.*;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Random;
import msi.gaml.operators.Maths;

import java.awt.Color;
import java.lang.Object;
import java.lang.Math;



import com.thoughtworks.xstream.*;

@vars({
	@var(name = "varmap", type = IType.MAP, doc = @doc("")),
	@var(name = "numvarmap", type = IType.MAP, doc = @doc("")),
	@var(name = "qualivarmap", type = IType.MAP, doc = @doc("")),
	@var(name = "metadatahistory", type = IType.MATRIX,doc = @doc("")),
	@var(name = "lastdetailedvarvalues", type = IType.MATRIX, doc = @doc("")),		
	@var(name = "averagehistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "stdevhistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "minhistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "maxhistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "distribhistoryparams", type = IType.MATRIX, doc = @doc("")),
	@var(name = "distribhistory", type = IType.MATRIX, doc = @doc("")),

	@var(name = "multi_metadatahistory", type = IType.MATRIX,doc = @doc("")),
	@var(name = "multi_lastdetailedvarvalues", type = IType.MATRIX, doc = @doc("")),		
	@var(name = "multi_averagehistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "multi_stdevhistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "multi_minhistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "multi_maxhistory", type = IType.MATRIX, doc = @doc("")),
	@var(name = "multi_distribhistoryparams", type = IType.MATRIX, doc = @doc("")),
	@var(name = "multi_distribhistory", type = IType.MATRIX, doc = @doc("")),

	@var(name = "color", type = IType.COLOR, doc = @doc("couleur de l'agent_group_follower")),
	@var(name = "dbscann", type = IType.INT, init = "3", doc = @doc("number of points for DBSCAN")),
	@var(name = "dbscane", type = IType.FLOAT, init = "25", doc = @doc("epsilon for DBSCAN")),
	@var(name = "display_mode", type = IType.STRING, doc = @doc("displaying global or SimGlobal")),
	@var(name = "clustering_mode", type = IType.STRING, doc = @doc("dbscan, none")),
	@var(name = "allSimShape", type = IType.LIST, doc = @doc("shape of all the simulation of the agent folllower")),
	@var(name = "colorList", type = IType.LIST, doc = @doc("color correponding to each simulation"))
})

@species(name = "agent_group_follower")
public class AgentGroupFollower extends ClusterBuilder //implements  MessageListener
{

	public long firsttime=0;
	
	public boolean ismastervar=true;
	public boolean doparallelsim=true;
	int lastreleasedcycle=0;

	GroupIdRule rule; 
	IList<IAgent> agentsCourants;

	List<IAgent> virtualAgents;
	HashMap<String,Boolean> parallelsims=new HashMap<String,Boolean>();

	
	//FIXME: This variable is a static one. Each data of each experiment is stored in this object.
	public static MultiSimManager manager = new MultiSimManager();

	public GroupIdRule getRule() { return rule; }
	public void setRule(GroupIdRule rule) { this.rule = rule; }

	public IList<IAgent> getAgentsCourants() { return agentsCourants; }
	public void setAgentsCourants(IList<IAgent> agentsCourants) { this.agentsCourants = agentsCourants; }

	public String getNom_espece() { return nom_espece; }
	public void setNom_espece(String nom_espece) { this.nom_espece = nom_espece; }

	public static final String ANALYSE_STATEMENT_VARIABLE = "species_to_analyse";
	public static final String ANALYSE_STATEMENT_CONSTRAINT = "with_constraint";

	public final static String VARMAP = "varmap";
	public final static String NUMVARMAP = "numvarmap";
	public final static String QUALIVARMAP = "qualivarmap";
	public final static String METADATAHISTORY = "metadatahistory";
	public final static String LASTDETAILDEDVARVALUES = "lastdetailedvarvalues";
	public final static String AVERAGEHISTORY = "averagehistory";
	public final static String STDEVHISTORY = "stdevhistory";
	public final static String MINHISTORY = "minhistory";
	public final static String MAXHISTORY = "maxhistory";
	public final static String DISTRIBHISTORYPARAMS = "distribhistoryparams";
	public final static String DISTRIBHISTORY = "distribhistory";

	public final static String MULTI_METADATAHISTORY = "multi_metadatahistory";
	public final static String MULTI_LASTDETAILDEDVARVALUES = "multi_lastdetailedvarvalues";
	public final static String MULTI_AVERAGEHISTORY = "multi_averagehistory";
	public final static String MULTI_STDEVHISTORY = "multi_stdevhistory";
	public final static String MULTI_MINHISTORY = "multi_minhistory";
	public final static String MULTI_MAXHISTORY = "multi_maxhistory";
	public final static String MULTI_DISTRIBHISTORYPARAMS = "multi_distribhistoryparams";
	public final static String MULTI_DISTRIBHISTORY = "multi_distribhistory";

	public final static String WITH_MATRIX = "with_matrix";
	public final static String WITH_VAR = "with_var";

	ArrayList<Number> intervalle = new ArrayList<Number>();
	ArrayList<Integer> repartition = new ArrayList<Integer>();

	boolean isregistered;
	public String mastername="unknown";	
	public ArrayList<String> slaveList = new ArrayList<String>();


	int k;
	int n;
	float deuxpuissancek;
	float newminInt;
	int preval;
	int postval;
	String analysedSpecies;
	
	IShape myShape; //geometry courante 
	List<IShape> allSimulationShape= new ArrayList<IShape>();

	
	List<IShape> curSimulationMutliPolygon= new ArrayList<IShape>(); //
	List<List<IShape>> allSimulationMultiPoly = new ArrayList(); //
	
	public StorableData mydata;
	public StorableData multidata;

	public ArrayList my_big_list = new ArrayList<Object>();

	@getter(VARMAP) public GamaMap getVarMap (IAgent agent) { return (GamaMap) ((AgentGroupFollower)agent).mydata.varmap; }
	@setter(VARMAP) public void setVarMap(final IAgent agent, final GamaMap latt) { ((AgentGroupFollower)agent).mydata.varmap = latt; }

	@getter(NUMVARMAP) public GamaMap getNumVarMap(final IAgent agent) { return (GamaMap) ((AgentGroupFollower)agent).mydata.numvarmap; }
	@setter(NUMVARMAP) public void setNumVarMap(final IAgent agent, final GamaMap ls) { ((AgentGroupFollower)agent).mydata.numvarmap = ls; }

	@getter(QUALIVARMAP) public GamaMap getQualiVarMap(final IAgent agent) { return (GamaMap) ((AgentGroupFollower)agent).mydata.qualivarmap; }
	@setter(QUALIVARMAP) public void setQualiVarMap(final IAgent agent, final GamaMap t) { ((AgentGroupFollower)agent).mydata.qualivarmap = t; } 

	@getter(METADATAHISTORY) public GamaMatrix getMetaDataHistory(final IAgent agent) { return (GamaMatrix) ((AgentGroupFollower)agent).mydata.metadatahistory; }
	@setter(METADATAHISTORY) public void setMetaDataHistory(final IAgent agent, final GamaObjectMatrix os) { ((AgentGroupFollower)agent).mydata.metadatahistory=os; }

	@getter(LASTDETAILDEDVARVALUES) public GamaMatrix getLastDetailedValues(final IAgent agent) { return (GamaMatrix) ((AgentGroupFollower)agent).mydata.lastdetailedvarvalues; }
	@setter(LASTDETAILDEDVARVALUES) public void setLastDetailedValues(final IAgent agent, final GamaObjectMatrix latt) { ((AgentGroupFollower)agent).mydata.lastdetailedvarvalues=latt; }

	@getter(AVERAGEHISTORY) public GamaFloatMatrix getAverageHistory(final IAgent agent) { return (GamaFloatMatrix) ((AgentGroupFollower)agent).mydata.averagehistory; }
	@setter(AVERAGEHISTORY) public void setAverageHistory(final IAgent agent, final GamaFloatMatrix ls) { ((AgentGroupFollower)agent).mydata.averagehistory=ls; }

	@getter(STDEVHISTORY) public GamaFloatMatrix getStDevHistory(final IAgent agent) { return (GamaFloatMatrix) ((AgentGroupFollower)agent).mydata.stdevhistory; }
	@setter(STDEVHISTORY) public void setStDevHistory(final IAgent agent, final GamaFloatMatrix t) { ((AgentGroupFollower)agent).mydata.stdevhistory= t; }

	@getter(MINHISTORY) public GamaFloatMatrix getMinHistory(final IAgent agent) { return (GamaFloatMatrix)((AgentGroupFollower)agent).mydata.minhistory; }
	@setter(MINHISTORY) public void setMinHistory(final IAgent agent, final GamaFloatMatrix os) { ((AgentGroupFollower)agent).mydata.minhistory=os;}

	@getter(MAXHISTORY) public GamaFloatMatrix getMaxHistory(final IAgent agent) { return (GamaFloatMatrix) ((AgentGroupFollower)agent).mydata.maxhistory; }
	@setter(MAXHISTORY) public void setMaxHistory(final IAgent agent, final GamaFloatMatrix t) { ((AgentGroupFollower)agent).mydata.maxhistory=t; }

	@getter(DISTRIBHISTORYPARAMS) public GamaObjectMatrix getDistribHistoryParams(final IAgent agent) { return (GamaObjectMatrix) ((AgentGroupFollower)agent).mydata.distribhistoryparams; }
	@setter(DISTRIBHISTORYPARAMS) public void setDistribHistoryParams(final IAgent agent, final GamaObjectMatrix os) { ((AgentGroupFollower)agent).mydata.distribhistoryparams=os; }

	@getter(DISTRIBHISTORY) public GamaObjectMatrix getDistribHistory(final IAgent agent) { return (GamaObjectMatrix) ((AgentGroupFollower)agent).mydata.distribhistory; }
	@setter(DISTRIBHISTORY) public void setDistribHistory(final IAgent agent, final GamaObjectMatrix os) { ((AgentGroupFollower)agent).mydata.distribhistory=os; }


	@getter(MULTI_METADATAHISTORY) public GamaObjectMatrix getMultiMetaDataHistory(final IAgent agent) { return (GamaObjectMatrix) ((AgentGroupFollower)agent).multidata.metadatahistory; }
	@setter(MULTI_METADATAHISTORY) public void setMultiMetaDataHistory(final IAgent agent, final GamaObjectMatrix os) { ((AgentGroupFollower)agent).multidata.metadatahistory=os; }

	@getter(MULTI_LASTDETAILDEDVARVALUES) public GamaObjectMatrix getMultiLastDetailedValues(final IAgent agent) { return (GamaObjectMatrix) ((AgentGroupFollower)agent).multidata.lastdetailedvarvalues; }
	@setter(MULTI_LASTDETAILDEDVARVALUES) public void setMultiLastDetailedValues(final IAgent agent, final GamaObjectMatrix latt) { ((AgentGroupFollower)agent).multidata.lastdetailedvarvalues=latt; }

	@getter(MULTI_AVERAGEHISTORY) public GamaFloatMatrix getMultiAverageHistory(final IAgent agent) { return (GamaFloatMatrix) ((AgentGroupFollower)agent).multidata.averagehistory; }
	@setter(MULTI_AVERAGEHISTORY) public void setMultiAverageHistory(final IAgent agent, final GamaFloatMatrix ls) { ((AgentGroupFollower)agent).multidata.averagehistory=ls; }

	@getter(MULTI_STDEVHISTORY) public GamaFloatMatrix getMultiStDevHistory(final IAgent agent) { return (GamaFloatMatrix) ((AgentGroupFollower)agent).multidata.stdevhistory; }
	@setter(MULTI_STDEVHISTORY) public void setMultiStDevHistory(final IAgent agent, final GamaFloatMatrix t) { ((AgentGroupFollower)agent).multidata.stdevhistory= t; }

	@getter(MULTI_MINHISTORY) public GamaFloatMatrix getMultiMinHistory(final IAgent agent) { return (GamaFloatMatrix)((AgentGroupFollower)agent).multidata.minhistory; }
	@setter(MULTI_MINHISTORY) public void setMultiMinHistory(final IAgent agent, final GamaFloatMatrix oss) { ((AgentGroupFollower)agent).multidata.minhistory=oss;}

	@getter(MULTI_MAXHISTORY) public GamaFloatMatrix getMultiMaxHistory(final IAgent agent) { return (GamaFloatMatrix) ((AgentGroupFollower)agent).multidata.maxhistory; }
	@setter(MULTI_MAXHISTORY) public void setMultiMaxHistory(final IAgent agent, final GamaFloatMatrix t) { ((AgentGroupFollower)agent).multidata.maxhistory=t; }

	@getter(MULTI_DISTRIBHISTORYPARAMS) public GamaObjectMatrix getMultiDistribHistoryParams(final IAgent agent) { return (GamaObjectMatrix) ((AgentGroupFollower)agent).multidata.distribhistoryparams; }
	@setter(MULTI_DISTRIBHISTORYPARAMS) public void setMultiDistribHistoryParams(final IAgent agent, final GamaObjectMatrix os) { ((AgentGroupFollower)agent).multidata.distribhistoryparams=os; }

	@getter(MULTI_DISTRIBHISTORY) public GamaObjectMatrix getMultiDistribHistory(final IAgent agent) { return (GamaObjectMatrix) ((AgentGroupFollower)agent).multidata.distribhistory; }
	@setter(MULTI_DISTRIBHISTORY) public void setMultiDistribHistory(final IAgent agent, final GamaObjectMatrix os) { ((AgentGroupFollower)agent).multidata.distribhistory=os; }

	@getter("dbscane") public double getDbscane(final IAgent agent) { return (Double) agent.getAttribute("dbscane"); }
	@setter("dbscane") public void setDbscane(final IAgent agent, final double os) { agent.setAttribute("dbscane",os); }

	@getter("dbscann") public int getDbscann(final IAgent agent) { return (Integer) agent.getAttribute("dbscann"); }
	@setter("dbscann") public void setDbscann(final IAgent agent, final int os) { agent.setAttribute("dbscann",os); }

	@getter("display_mode") public String getDisplay(final IAgent agent) { return (String) agent.getAttribute("display_mode"); }
	@setter("display_mode") public void setDisplay(final IAgent agent, final String os) { agent.setAttribute("display_mode",os); }
	
	@getter("clustering_mode") public String getClusteringMode(final IAgent agent) { return (String) agent.getAttribute("clustering_mode"); }
	@setter("clustering_mode") public void setClusteringMode(final IAgent agent, final String os) { agent.setAttribute("clustering_mode",os); }
	
	@getter("allSimShape") public List<IShape> getAllSimulationShape() {return allSimulationShape;}
	@setter("allSimShape") public void setAllSimulationShape(List<IShape> allSimulationMultiPoly) {this.allSimulationShape = allSimulationShape;}
	
	@getter("colorList") public IList<Color> getColorList() {return manager.simColorList;}
	//@setter("colorList") public void setColorList(IList<Color> colorList) {manager.simColorList = colorList;}

	
	
	

	public boolean init(final IScope scope)
	{
		boolean res=super.init(scope);
		firsttime=System.currentTimeMillis();
		this.setAttribute("display_mode", "simglobal");
		this.setAttribute("clustering_mode", "dbscan");
		messages= new HashMap<String, LinkedList<GamaMap<String,Object>>>();
	
	   	xstream = new XStream();
	   	xstream.registerConverter(new GamaAgentConverter());
	   	xstream.registerConverter(new GamaScopeConverter());
	   	xstream.registerConverter(new GamaSimulationAgentConverter());
	   	xstream.registerConverter(new GamaShapeConverter());
	   	 
		if (doparallelsim())
		{
			this.setAttribute("netAgtName",this.getNetName());
			this.connect("rmi://127.0.0.1:1099","sampleTopic",this.getNetName());
			System.out.println("connecté!! "+this.getNetName());
			if (ismaster())
			{
				Map<String,Object> message= new HashMap<String,Object>();
				message.put("type","mastername");
				message.put("follower",this.getName());
				message.put("value",this.getNetName());	
			}
		}
		return res;
	}
	
	public String getUniqueSimName(final IScope scope) {
		 return scope.getName().toString() + "_" + manager.hasard;
	}

	public void updateMetaDataHistory(final IScope scope, final int nrow){	
		GamaObjectMatrix maMatriceMETA = GamaObjectMatrix.from(9,nrow+1,mydata.metadatahistory);
		mydata.metadatahistory = maMatriceMETA;
		mydata.metadatahistory.set(scope, 0, nrow, scope.getSimulationScope());
		mydata.metadatahistory.set(scope, 1, nrow, scope.getClock().getCycle());
		mydata.metadatahistory.set(scope, 2, nrow, getUniqueSimName(scope)); 
		mydata.metadatahistory.set(scope, 3, nrow, rule);
		mydata.metadatahistory.set(scope, 4, nrow, scope.getAgentScope().getName()); //supgroupid, Useless for the moment but could be use if an agent_group_follower create a sub agent 
		mydata.metadatahistory.set(scope, 5, nrow, this.getName()); //supruleid, Useless for the moment  
		mydata.metadatahistory.set(scope, 6, nrow, this.agentsCourants.copy(scope));
		mydata.metadatahistory.set(scope, 7, nrow, this.agentsCourants.size());
		mydata.metadatahistory.set(scope, 8, nrow, this.getGeometry());
	}
	
	public void updatedata(final IScope scope)
	{
		
		System.out.println("updatedata cycle : " + scope.getSimulationScope().getClock().getCycle());
		if(mydata.getIsAgentCreated() == false ) {
			//GamaList varlist=(GamaList)this.getSpecies().getVarNames();
		}

		// METADATAHISTORY .........................................................................
		int nrow = mydata.metadatahistory.getRows(scope);
		updateMetaDataHistory(scope, nrow);
		


		// LASTDETAILEDVARVALUES & VARMAP & NUMVARMAP & QUALIVARMAP................................................
		GamaMap<Integer, Object> gmap1 = mydata.varmap.copy(scope);		
		GamaMap<Integer, Object> gmap2 = mydata.numvarmap.copy(scope);
		GamaMap<Integer, Object> gmap3 = mydata.qualivarmap.copy(scope);

		int nbAgents = this.agentsCourants.size();

		if (nbAgents>0)
		{		
			GamlAgent unagent = (GamlAgent)agentsCourants.get(0);
			List<String> attmap = new ArrayList(unagent.getSpecies().getVarNames());
			int nbAttributes = attmap.size();
			mydata.lastdetailedvarvalues = new GamaObjectMatrix(nbAttributes+1,nbAgents, msi.gaml.types.Types.NO_TYPE);

			int c=0;
			for(int j=0;j<nbAttributes;j++) {				
				String nvar = attmap.get(j);
				mydata.varmap.put(j, nvar);
				for(int i=0; i<nbAgents; i++) {
					mydata.lastdetailedvarvalues.set(scope, j, i,agentsCourants.get(i).getDirectVarValue(scope, nvar));				
				}
				if(agentsCourants.get(0).getAttribute(nvar) instanceof Number) {
					mydata.numvarmap.put(c, nvar);
					c++;
				}
				else {
					mydata.qualivarmap.put(j, nvar);
				}
			}


			mydata.varmap.put(nbAttributes, "step");
			mydata.qualivarmap.put(nbAttributes, "step");

			for(int i=0; i<nbAgents;i++) {
				mydata.lastdetailedvarvalues.set(scope, nbAttributes, i, scope.getClock().getCycle());
			}


			for(int i=0;i<mydata.varmap.length(scope);i++) {
				mydata.varmap_reverse = (GamaMap) mydata.varmap.reverse(scope).copy(scope);	
			}
			System.out.println("varmap reverse " + mydata.varmap_reverse.toString());



			// AVERAGEHISTORY ...........................................................................

			int nbVar = mydata.numvarmap.length(scope);
			int step = scope.getSimulationScope().getClock().getCycle();
			step=mydata.metadatahistory.numRows-1;

			GamaFloatMatrix maMatriceAV = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.averagehistory);
			mydata.averagehistory = maMatriceAV;

			GamaFloatMatrix maMatriceMIN = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.minhistory);
			mydata.minhistory = maMatriceMIN; 

			GamaFloatMatrix maMatriceMAX = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.maxhistory);
			mydata.maxhistory = maMatriceMAX; 

			float moyenne = 0;

			for(int j=0;j<nbVar;j++) {
				moyenne = 0;
				float minimum = Float.parseFloat(mydata.lastdetailedvarvalues.get(scope, (Integer)mydata.varmap_reverse.get(scope,mydata.numvarmap.getValues().get(j)),0).toString());
				LinkedList<String> agmin = new LinkedList<String>();
				agmin.add(mydata.lastdetailedvarvalues.get(scope, 0, 0).toString());

				float maximum = Float.parseFloat(mydata.lastdetailedvarvalues.get(scope, (Integer)mydata.varmap_reverse.get(scope,mydata.numvarmap.getValues().get(j)),0).toString());
				LinkedList<String> agmax = new LinkedList<String>();

				for(int k=0;k<nbAgents;k++) {

					float valeur = Float.parseFloat(mydata.lastdetailedvarvalues.get(scope, (Integer)mydata.varmap_reverse.get(scope,mydata.numvarmap.getValues().get(j)),k).toString());
					moyenne =  moyenne +  valeur;

					if(valeur < minimum) {
						minimum = valeur;
						agmin.clear();
						agmin.add(mydata.lastdetailedvarvalues.get(scope, j, k).toString());
					}
					else if(valeur == minimum) {
						agmin.add(mydata.lastdetailedvarvalues.get(scope, j, k).toString());
					}

					if(valeur > maximum) {
						maximum = valeur;
						agmax.clear();
						agmax.add(mydata.lastdetailedvarvalues.get(scope, j, k).toString());

					}
					else if(valeur == maximum) {
						agmax.add(mydata.lastdetailedvarvalues.get(scope, j, k).toString());
					} 
				}
				moyenne = moyenne / (float)nbAgents;

				mydata.averagehistory.set(scope, j, step, moyenne);
				mydata.minhistory.set(scope, j, step, minimum) ;
				mydata.maxhistory.set(scope, j, step, maximum) ;
			}

			// STDEVHISTORY...........................................................................

			GamaFloatMatrix maMatriceSTDEV = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.stdevhistory);
			mydata.stdevhistory = maMatriceSTDEV; 

			GamaObjectMatrix maMatriceDISTR = GamaObjectMatrix.from(nbVar,step+1,mydata.distribhistory);
			mydata.distribhistory = maMatriceDISTR;

			float ecartype = 0;

			for(int j=0; j<nbVar;j++) {
				ecartype = 0;

				for(int k=0; k<nbAgents;k++) {
					float valeur = Float.parseFloat(mydata.lastdetailedvarvalues.get(scope, (Integer)mydata.varmap_reverse.get(scope,mydata.numvarmap.getValues().get(j)),k).toString());
					ecartype = ecartype + (valeur-Float.parseFloat(mydata.averagehistory.get(scope, j, step).toString()))*(valeur-Float.parseFloat(mydata.averagehistory.get(scope, j, step).toString()));
				}

				ecartype = (float) Math.sqrt(ecartype/nbAgents);
				mydata.stdevhistory.set(scope, j, step, ecartype);
			}


			// DISTRIBHISTORYPARAMS & DISTRIBHISTORY...................................................................


			GamaObjectMatrix maMatriceDISTRP = GamaObjectMatrix.from(nbVar,step+1,mydata.distribhistoryparams);
			mydata.distribhistoryparams = maMatriceDISTRP;

			int nbRepartition = 0;
			int nbBarres = 10;

			preval = 0;
			postval = 0;

			for(int j=0;j<nbVar;j++) {

				repartition.clear();

				float min = Float.parseFloat(mydata.minhistory.get(scope, j, step).toString());
				float max = Float.parseFloat(mydata.maxhistory.get(scope, j, step).toString());

				if(min==max) {
					intervalle.clear();
					repartition.clear();
					intervalle.add(min);
					nbRepartition = nbAgents;
					repartition.add(nbRepartition);
					mydata.distribhistoryparams.set(scope, j, step, intervalle.clone());
					mydata.distribhistory.set(scope, j, step, repartition.clone());
					newminInt = (int) min;
				}

				else {

					intervalle.clear();

					int intermin = (int)min;
					if(min<0) { intermin = intermin -1; }
					int intermax = (int)max + 1;

					float minInt = (float)intermin;
					float maxInt = (float)intermax;


					if(maxInt-minInt==nbBarres) {
						newminInt = minInt;
						k = 0;
						n = (int)newminInt;
					}
					else {
						double N = Math.log10((maxInt-minInt)/(nbBarres-1))/Math.log10(2);

						if(maxInt-minInt<nbBarres) { k = (int)N; }
						else { k = (int)N + 1; }

						if(k<0) {
							float kprime = -k;
							deuxpuissancek = (float) (1/Math.pow(2, kprime));
						}
						else {
							deuxpuissancek = (float) Math.pow(2, k);
						}

						newminInt = 0;

						if(minInt<0) {
							if(minInt%deuxpuissancek!=0) {
								newminInt = (int)deuxpuissancek*(int)((minInt/deuxpuissancek)-1);
								n = (int)((minInt/deuxpuissancek)-1);
							}
							else {
								newminInt = minInt;
								n = (int) ((int)minInt/deuxpuissancek)-1;
							}
						}
						else {
							if(minInt%deuxpuissancek!=0) {
								newminInt = (int)deuxpuissancek*(int)((minInt/deuxpuissancek));
								n = (int)((minInt/deuxpuissancek));
							}
							else {
								newminInt = minInt;
								n = (int) ((int)minInt/deuxpuissancek);
							}
						}
					}

					intervalle.add(k);
					intervalle.add(n);

					mydata.distribhistoryparams.set(scope, j, step, intervalle.clone());
				}

				preval = (int)newminInt;

				if(min!=max) {
					for(int i=0;i<nbBarres;i++) {
						nbRepartition = 0;
						if (i!=0) {
							preval = (int) (preval + deuxpuissancek);
						}
						postval = (int) (preval + deuxpuissancek);	
						//System.out.println("PREVAL au step " + step + " et à la variable " + j + " et à la barre " + i + " est de " + preval);
						//System.out.println("POSTVAL au step " + step + " et à la variable " + j + " et à la barre " + i + " est de " + postval);
						for(int k=0;k<nbAgents;k++) {
							if(Float.parseFloat(mydata.lastdetailedvarvalues.get(scope, (Integer)mydata.varmap_reverse.get(scope,mydata.numvarmap.getValues().get(j)),k).toString())< postval & Float.parseFloat(mydata.lastdetailedvarvalues.get(scope, (Integer)mydata.varmap_reverse.get(scope,mydata.numvarmap.getValues().get(j)),k).toString())>= preval) {
								nbRepartition = nbRepartition + 1;
							}
						}

						//System.out.println("nbrepartition pour la variable " + j + " et pour la barre " + i + " est de: " + nbRepartition);
						repartition.add(nbRepartition);

						//System.out.println("repartition: " + repartition);
					}

					//System.out.println("repartition FINALE pour la variable " + j + "est de: " + repartition);

					mydata.distribhistory.set(scope, j, step, repartition.clone());
					//System.out.println( "lastdetailedvarvalues: "+ mydata.lastdetailedvarvalues.get(scope, j, 0));
				}
			}


			if(!manager.idSimList.contains(getUniqueSimName(scope))) {
				manager.idSimList.add(getUniqueSimName(scope));	
				if (manager.simColorList.size()+1<11)
					{
					int i=manager.simColorList.size();
						if (i==0) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.CYAN));
						if (i==1) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.RED));
						if (i==2) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.YELLOW));
						if (i==3) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.GREEN));
						if (i==4) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.BLUE));
						if (i==5) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.PINK));
						if (i==6) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.MAGENTA));
						if (i==7) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.ORANGE));
						if (i==8) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.LIGHT_GRAY));
						if (i==9) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.DARK_GRAY));
					}					
				else
					manager.simColorList.add((GamaColor)GamaColor.getInt(Random.opRnd(scope, 10000)));
			}

			if(!manager.agentGroupFollowerList.toString().contains(scope.getAgentScope().getName().toString())) {
				manager.agentGroupFollowerList.add((AgentGroupFollower)scope.getAgentScope());
			}


			if(mydata.metadatahistory.numRows==1) {
				GamaObjectMatrix matrice1 = GamaObjectMatrix.from(9,nrow+1,mydata.metadatahistory);
				GamaObjectMatrix matrice2 = GamaObjectMatrix.from(nbAttributes+1,nbAgents,mydata.lastdetailedvarvalues);
				GamaFloatMatrix matrice3 = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.minhistory);
				GamaFloatMatrix matrice4 = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.maxhistory);
				GamaFloatMatrix matrice5 = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.averagehistory);
				GamaFloatMatrix matrice6 = GamaFloatMatrix.from(scope,nbVar,step+1,mydata.stdevhistory);
				GamaObjectMatrix matrice7 = GamaObjectMatrix.from(nbVar,step+1,mydata.distribhistoryparams);
				GamaObjectMatrix matrice8 = GamaObjectMatrix.from(nbVar,step+1,mydata.distribhistory);
				multidata.metadatahistory = matrice1;
				multidata.lastdetailedvarvalues = matrice2;
				multidata.minhistory = matrice3;
				multidata.maxhistory = matrice4;
				multidata.averagehistory = matrice5;
				multidata.stdevhistory = matrice6;
				multidata.distribhistoryparams = matrice7;
				multidata.distribhistory = matrice8;

				if(manager.idSimList.length(scope)>1) {
					for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
						if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {

							manager.storableDataList.get(i).metadatahistory=(GamaObjectMatrix)manager.storableDataList.get(i).metadatahistory._opAppendVertically(scope, multidata.metadatahistory); //.opAppendVertically(scope, manager.storableDataList.get(i).metadatahistory, multidata.metadatahistory);						
							System.out.println("PARTIE 1 de la matrice: " + manager.storableDataList.get(i).minhistory);
							System.out.println("PARTIE 2 de la matrice: " + multidata.minhistory);
							manager.storableDataList.get(i).minhistory=(GamaFloatMatrix)manager.storableDataList.get(i).minhistory._opAppendVertically(scope, multidata.minhistory); //_opAppendVertically(scope, manager.storableDataList.get(i).minhistory, multidata.minhistory);						
							System.out.println("PARTIE CONCANTENEE de la minhistory de manager: " + manager.storableDataList.get(i).minhistory);
							manager.storableDataList.get(i).maxhistory=(GamaFloatMatrix)manager.storableDataList.get(i).maxhistory._opAppendVertically(scope, multidata.maxhistory); // .opAppendVertically(scope, manager.storableDataList.get(i).maxhistory, multidata.maxhistory);							
							manager.storableDataList.get(i).averagehistory=(GamaFloatMatrix)manager.storableDataList.get(i).averagehistory._opAppendVertically(scope, multidata.maxhistory); // .opAppendVertically(scope, manager.storableDataList.get(i).averagehistory, multidata.maxhistory);							
							manager.storableDataList.get(i).stdevhistory=(GamaFloatMatrix)manager.storableDataList.get(i).stdevhistory._opAppendVertically(scope, multidata.stdevhistory); // .opAppendVertically(scope, manager.storableDataList.get(i).stdevhistory, multidata.stdevhistory);							
							manager.storableDataList.get(i).distribhistoryparams=(GamaObjectMatrix)manager.storableDataList.get(i).distribhistoryparams._opAppendVertically(scope, multidata.distribhistoryparams); // .opAppendVertically(scope, manager.storableDataList.get(i).distribhistoryparams, multidata.distribhistoryparams);							
							manager.storableDataList.get(i).distribhistory=(GamaObjectMatrix)manager.storableDataList.get(i).distribhistory._opAppendVertically(scope, multidata.distribhistory); //.opAppendVertically(scope, manager.storableDataList.get(i).distribhistory, multidata.distribhistory);							
							multidata=manager.storableDataList.get(i);

						}
					}
				}
				else {
					for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
						if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
							manager.storableDataList.add(i,multidata);
							System.out.println("B- manager.storableDataList: " + manager.storableDataList);
						}
					}
				}

			} 
			else {				

				if (doparallelsim())
				{
					if (ismaster())
					{
						getdatafromslaves(scope);
						//UPDATA from slave

					}
					else
					{
						//SEND my data
					}
				}
				
				IList nl1=mydata.metadatahistory.getLine(scope, mydata.metadatahistory, mydata.metadatahistory.numRows-1);
				GamaObjectMatrix nm1=new GamaObjectMatrix(scope, nl1, new GamaPoint(mydata.metadatahistory.numCols,1), Types.NO_TYPE);
				multidata.metadatahistory=(GamaObjectMatrix)multidata.metadatahistory._opAppendVertically(scope, nm1); 

				GamaObjectMatrix matrice2 = GamaObjectMatrix.from(nbAttributes+1,nbAgents,mydata.lastdetailedvarvalues);
				multidata.lastdetailedvarvalues = matrice2;

				IList nl2=mydata.minhistory.getLine(scope, mydata.minhistory, mydata.minhistory.numRows-1);
				GamaFloatMatrix nm2=new GamaFloatMatrix(scope, nl2,new GamaPoint(mydata.minhistory.numCols,1));
				multidata.minhistory=(GamaFloatMatrix)multidata.minhistory._opAppendVertically(scope, nm2); 
			
				IList nl3=mydata.maxhistory.getLine(scope, mydata.maxhistory, mydata.maxhistory.numRows-1);
				GamaFloatMatrix nm3=new GamaFloatMatrix(scope,nl3,new GamaPoint(mydata.maxhistory.numCols,1));
				multidata.maxhistory=(GamaFloatMatrix)multidata.maxhistory._opAppendVertically(scope, nm3); 
			
				IList nl4=mydata.averagehistory.getLine(scope, mydata.averagehistory, mydata.averagehistory.numRows-1);
				GamaFloatMatrix nm4=new GamaFloatMatrix(scope,nl4,new GamaPoint(mydata.averagehistory.numCols,1));
				multidata.averagehistory=(GamaFloatMatrix)multidata.averagehistory._opAppendVertically(scope, nm4); 
			
				IList nl5=mydata.stdevhistory.getLine(scope, mydata.stdevhistory, mydata.stdevhistory.numRows-1);
				GamaFloatMatrix nm5=new GamaFloatMatrix(scope,nl5,new GamaPoint(mydata.stdevhistory.numCols,1));
				multidata.stdevhistory=(GamaFloatMatrix)multidata.stdevhistory._opAppendVertically(scope, nm5);
				
				IList nl6=mydata.distribhistoryparams.getLine(scope, mydata.distribhistoryparams, mydata.distribhistoryparams.numRows-1);
				GamaObjectMatrix nm6=new GamaObjectMatrix(scope,nl6,new GamaPoint(mydata.distribhistoryparams.numCols,1), Types.NO_TYPE);
				multidata.distribhistoryparams=(GamaObjectMatrix)multidata.distribhistoryparams._opAppendVertically(scope, nm6); 
			
				IList nl7=mydata.distribhistory.getLine(scope, mydata.distribhistory, mydata.distribhistory.numRows-1);
				GamaObjectMatrix nm7=new GamaObjectMatrix(scope,nl7,new GamaPoint(mydata.distribhistory.numCols,1), Types.NO_TYPE);
				multidata.distribhistory=(GamaObjectMatrix)multidata.distribhistory._opAppendVertically(scope, nm7); 
		
			}

			System.out.println("1) manager.idSimList: " + manager.idSimList);
			System.out.println("2) manager.agentGroupFollowerList: " + manager.agentGroupFollowerList);
			System.out.println("3) manager.storableDataList: " + manager.storableDataList);			
		}

	}




	public boolean step(final IScope scope) {

		if (doparallelsim())
		{
			if (ismaster())
			{
			}
			else
			{
				checkifreleased();
			}
		}

		System.out.println("agentsCourants dans step avant update: " + agentsCourants);
		agentsCourants = rule.update(scope,agentsCourants);
		System.out.println("agentsCourants dans step après update: " + agentsCourants);
		updatedata(scope);
		updateShape(scope);


		if (doparallelsim())
		{
			if (ismaster())
			{
				releaseslaves();

			}
			else
			{
				senddata();
			}
		}

		return super.step(scope);
	}


	public static void clearmatrices (final IScope scope) {
	  manager.getAgentGroupFollowerList().clear();
	  return;
	}

	public void updateShape(final IScope scope)
	{		

		List<IAgent> groupe = (List<IAgent>)this.agentsCourants;
		curSimulationMutliPolygon= new ArrayList<IShape>();

		
		allSimulationMultiPoly.clear();
		allSimulationShape.clear();
		
		//CREATE THE CURRENT SHAPE
		
		if(this.getAttribute("clustering_mode").equals("none"))
		{
			for (int i=0;i<groupe.size();i++) {
			  curSimulationMutliPolygon.add((IShape)groupe.get(i).getLocation());	
			}
			myShape=(IShape)new GamaShape(GamaGeometryType.buildPolygon(curSimulationMutliPolygon).getInnerGeometry().convexHull());
			this.setGeometry(((GamaShape)myShape));	
		}
		else{
			this.setAttribute("agents", agentsCourants);
			IList<String> listarg=GamaListFactory.create(Types.STRING);
			listarg.add("location.x");
			listarg.add("location.y");
			this.setAttribute("attributes", listarg);
			this.setAttribute("epsilon", (Double)this.getAttribute("dbscane"));
			this.setAttribute("min_points", (Integer)this.getAttribute("dbscann"));

			System.out.println("listarg: " + listarg);
			
			List<List<IAgent>> groupes = primClusteringDBScan(this.getScope());

			allSimulationMultiPoly.clear();

			if(groupes==null) {
				System.out.println("Pas de groupe.");
			}

			
			else if(groupes.size()>0) {
				for(int i=0;i<groupes.size();i++) {

					System.out.println("nombre de groupes: " + groupes.size());
					curSimulationMutliPolygon=new ArrayList<IShape>();
					for(int j=0;j<groupes.get(i).size();j++) {
						if(groupes.get(i).size()>1 || groupes.get(i).size()==0 ) {
							curSimulationMutliPolygon.add((IShape)groupes.get(i).get(j).getLocation());
						}
					}
					allSimulationMultiPoly.add(curSimulationMutliPolygon);
	
				}
				myShape=(IShape)GamaGeometryType.buildMultiPolygon(allSimulationMultiPoly);
				this.setGeometry(((GamaShape)myShape));
			}
			
		}
			
				
		if (this.getAttribute("display_mode").equals("global")) {
			allSimulationShape.add(myShape);
		}	
		

		if (this.getAttribute("display_mode").equals("simglobal")) {  //  à tester!!: --> chaque follower se fait son enveloppe
				allSimulationShape.add(myShape);
				
				if(manager.idSimList.length(scope)>1) {
						for (int j=0; j<multidata.metadatahistory.numRows; j++)
						{
							if ((Integer)multidata.metadatahistory.get(scope, 1, j) == this.getScope().getClock().getCycle())
								if (!scope.getSimulationScope().toString().equals(multidata.metadatahistory.get(scope, 0, j).toString()))
								{
									System.out.println("metadatahistory "+ multidata.metadatahistory.get(scope, 8,j)+" type "+multidata.metadatahistory.get(scope, 8,j).getClass());		
									allSimulationShape.add((GamaShape) multidata.metadatahistory.get(scope, 8,j));						
								}
						}
					}				
		}

		
		
		
		if (this.getAttribute("display_mode").equals("simglobalparal")) {  //  à tester!!: --> une grande enveloppe pour tous les agents de toutes les simulations
			System.out.println("manager.getAgentGroupFollowerList().length(scope)" + manager.getAgentGroupFollowerList().length(scope) );
			
			for (int i=0;i<manager.getAgentGroupFollowerList().length(scope);i++) {
					if (manager.getAgentGroupFollowerList().get(i).getName().equals(this.getName())) {
						for (int j=0;j<manager.getAgentGroupFollowerList().get(i).agentsCourants.length(scope);j++) {
							groupe.add(manager.getAgentGroupFollowerList().get(i).agentsCourants.get(j));
						}
						
					}
			}

			
			for (int k=0;k<groupe.size();k++) {
				curSimulationMutliPolygon.add((IShape)groupe.get(k).getLocation());	
			}
			myShape=(IShape)new GamaShape(GamaGeometryType.buildPolygon(curSimulationMutliPolygon).getInnerGeometry().convexHull());
				this.setGeometry(((GamaShape)myShape));
		}
		
		

		if ((!(this.getAttribute("display_mode").equals("global"))) & (!(this.getAttribute("display_mode").equals("simglobal")))) {  //  à tester!!: --> chaque follower se fait son enveloppe
			this.setAttribute("agents", agentsCourants);
			IList<String> listarg=GamaListFactory.create(Types.STRING);
			listarg.add("location.x");
			listarg.add("location.y");
			this.setAttribute("attributes", listarg);
			this.setAttribute("epsilon", (Double)this.getAttribute("dbscane"));
			this.setAttribute("min_points", (Integer)this.getAttribute("dbscann"));

			System.out.println("listarg: " + listarg);

			
			List<List<IAgent>> groupes = primClusteringDBScan(this.getScope());

			allSimulationMultiPoly.clear();

			if(groupes==null) {
				System.out.println("Pas de groupe.");
			}

			else if(groupes.size()>0) {
				for(int i=0;i<groupes.size();i++) {

					System.out.println("nombre de groupes: " + groupes.size());
					curSimulationMutliPolygon=new ArrayList<IShape>();
					for(int j=0;j<groupes.get(i).size();j++) {
						if(groupes.get(i).size()>1 || groupes.get(i).size()==0 ) {
							curSimulationMutliPolygon.add((IShape)groupes.get(i).get(j).getLocation());
						}
					}
					allSimulationMultiPoly.add(curSimulationMutliPolygon);
					//System.out.println("mespoly= " + mespoly);


					//			final IList<IShape> shapes = points.listValue(scope);
					//			final int size = polygone.length(scope);
					//			final IShape first = (IShape) polygone.first(scope);
					/*if ( !first.equals(polygone.last(scope)) ) {
					polygone.add(first);
				}*/

					//mageom= (IShape) GeometryUtils.buildGeometryJTS(utile);

					//this.setGeometry(((GamaShape)mageom).getExteriorRing().getGeometricEnvelope()); //rectangles pleins
					//this.setGeometry(((GamaShape)mageom).getGeometricEnvelope().getExteriorRing()); // rectangles vides
					//mageom.getGeometry().getInnerGeometry().convexHull(); // rien

					//mageom= GamaGeometryType.buildPolygon(polygone);
					//this.setInnerGeometry(((GamaShape)mageom).getInnerGeometry().convexHull()); // polygones pleins!!!

				}

				myShape=(IShape)GamaGeometryType.buildMultiPolygon(allSimulationMultiPoly);
				this.setGeometry(((GamaShape)myShape));
			}
		}
	}

	String nom_espece = Analyse_statement.getAnalyseStatementVariable(); 
	String nom_contrainte = Analyse_statement.getAnalyseStatementConstraint(); 

	public AgentGroupFollower(final IPopulation s) throws GamaRuntimeException {
		super(s);
		mydata = new StorableData();
		multidata = new StorableData();
		virtualAgents=new ArrayList<IAgent>();
	}

	public void analyseCluster(final IScope scope,String nomespece, String contrainte) throws GamaRuntimeException  { 

		GroupIdRuleList ru = new GroupIdRuleList();
		ru.nom=nomespece;
		rule=ru;
		System.out.println("this.getAgents : " + this.getAgents(scope)); // vide 
		agentsCourants = rule.update(scope,this.getAgents(scope));
		System.out.println("analyseCluster : " + agentsCourants);  //vide

	}

	@action(name = "analyse_cluster", args = {
			@arg(name = ANALYSE_STATEMENT_VARIABLE, type = IType.STRING, optional = false) 
	})
	public void analyseCluster(final IScope scope) throws GamaRuntimeException  {

		analysedSpecies = (String) scope.getStringArg(ANALYSE_STATEMENT_VARIABLE);
		this.setName(analysedSpecies.toString() + "_follower");

		System.out.println("AnalyseCluster: nom de l'espece à analyser : " + analysedSpecies); // OK: "mespeople"

		mydata.init(scope);
		multidata.init(scope);
		analyseCluster(scope,analysedSpecies, "");
	}	

	@action(name = "at_cycle", args = {
			@arg(name = WITH_MATRIX, type = IType.STRING, optional = false), 
			@arg(name = WITH_VAR, type = IType.STRING, optional = false)
	})
	public List at_cycle(final IScope scope) throws GamaRuntimeException  {

		String my_matrix = (String) scope.getStringArg(WITH_MATRIX);  
		String my_variable = (String) scope.getStringArg(WITH_VAR);

		System.out.println("chosen_matrix: " + my_matrix);
		System.out.println("chosen_variable: " + my_variable);

		int step = scope.getClock().getCycle()-2;

		List at_cycle_manager=new ArrayList<Double>();

		//Double valeur;
		Object objvaleur;

		if(my_matrix.equals("multi_minhistory")) {
			at_cycle_manager=new ArrayList<Double>();
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					//System.out.println("on est à l'agent group follower: " + manager.agentGroupFollowerList.get(i).toString());					
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())== step) {
							//System.out.println("valeur du step: " + manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j));
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).minhistory.getColumn(scope, k).get(j);
									//System.out.println("objvaleur: " + objvaleur);
									at_cycle_manager.add(objvaleur);
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_maxhistory")) {
			at_cycle_manager=new ArrayList<Double>();
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())== step) {
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).maxhistory.getColumn(scope, k).get(j);
									at_cycle_manager.add(objvaleur);
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_averagehistory")) {
			at_cycle_manager=new ArrayList<Double>();
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {				
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())== step) {
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).averagehistory.getColumn(scope, k).get(j);
									at_cycle_manager.add(objvaleur);
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_stdevhistory")) {
			at_cycle_manager=new ArrayList<Double>();
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())== step) {
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).stdevhistory.getColumn(scope, k).get(j);
									at_cycle_manager.add(objvaleur);
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_distribhistory")) {
			at_cycle_manager=new ArrayList<Object>();
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())== step) {
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).distribhistory.getColumn(scope, k).get(j);
									at_cycle_manager.add(objvaleur);
								}
							}
						}				
					}
				}
			}
		}		
		else {
			System.out.println("perdu");
		}
		System.out.println("at_cycle_manager: " + at_cycle_manager);

		return at_cycle_manager;		

	}

	@action(name = "at_var", args = {
			@arg(name = WITH_MATRIX, type = IType.STRING, optional = false), 
			@arg(name = WITH_VAR, type = IType.STRING, optional = false)
	})
	public List at_var(final IScope scope) throws GamaRuntimeException  {

		String my_matrix = (String) scope.getStringArg(WITH_MATRIX);
		String my_variable = (String) scope.getStringArg(WITH_VAR);

		System.out.println("my_matrix: " + my_matrix);
		System.out.println("my_variable: " + my_variable);

		int step = scope.getClock().getCycle()-1;
		System.out.println("STEP ICI: " + step);

		Object objvaleur;
		List sous_liste = new ArrayList<Object>();
		List at_var_manager=new ArrayList<Object>();
		List<ArrayList> my_very_big_list=new ArrayList<ArrayList>();

		int numSim = 0;

		if(my_matrix.equals("multi_minhistory")) {
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						int st=Integer.parseInt(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString());
						String idsim=manager.storableDataList.get(i).metadatahistory.getColumn(scope, 2).get(j).toString();
						for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
							if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
								objvaleur = (Object) manager.storableDataList.get(i).minhistory.getColumn(scope, k).get(j);
								if (my_very_big_list.size()<=manager.idSimList.indexOf(idsim))
								{
									for (int l=my_very_big_list.size(); l<=manager.idSimList.indexOf(idsim); l++)
									{
										List<Double> my_big_list2=new ArrayList<Double>();
										my_very_big_list.add((ArrayList) my_big_list2);									
									}
								}
								my_very_big_list.get(manager.idSimList.indexOf(idsim)).add(objvaleur);
								System.out.println("aj st" +st+" ids "+manager.idSimList.indexOf(idsim));
							}
						}			
					}
				}
			}
		}
		else if(my_matrix.equals("multi_maxhistory")) {
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						int st=Integer.parseInt(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString());
						String idsim=manager.storableDataList.get(i).metadatahistory.getColumn(scope, 2).get(j).toString();
						{
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).maxhistory.getColumn(scope, k).get(j);
									if (my_very_big_list.size()<=manager.idSimList.indexOf(idsim))
									{
										for (int l=my_very_big_list.size(); l<=manager.idSimList.indexOf(idsim); l++)
										{
											List<Double> my_big_list2=new ArrayList<Double>();
											my_very_big_list.add((ArrayList) my_big_list2);
										}
									}
									my_very_big_list.get(manager.idSimList.indexOf(idsim)).add(objvaleur);
									System.out.println("aj st" +st+" ids "+manager.idSimList.indexOf(idsim));
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_averagehistory")) {
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						int st=Integer.parseInt(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString());
						String idsim=manager.storableDataList.get(i).metadatahistory.getColumn(scope, 2).get(j).toString();
						{
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).averagehistory.getColumn(scope, k).get(j);
									if (my_very_big_list.size()<=manager.idSimList.indexOf(idsim))
									{
										for (int l=my_very_big_list.size(); l<=manager.idSimList.indexOf(idsim); l++)
										{
											List<Double> my_big_list2=new ArrayList<Double>();
											my_very_big_list.add((ArrayList) my_big_list2);
										}
									}
									my_very_big_list.get(manager.idSimList.indexOf(idsim)).add(objvaleur);
									System.out.println("aj st" +st+" ids "+manager.idSimList.indexOf(idsim));
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_stdevhistory")) {
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						int st=Integer.parseInt(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString());
						String idsim=manager.storableDataList.get(i).metadatahistory.getColumn(scope, 2).get(j).toString();
						//						if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())== step) 
						{
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).stdevhistory.getColumn(scope, k).get(j);
									if (my_very_big_list.size()<=manager.idSimList.indexOf(idsim))
									{
										for (int l=my_very_big_list.size(); l<=manager.idSimList.indexOf(idsim); l++)
										{
											List<Double> my_big_list2=new ArrayList<Double>();
											my_very_big_list.add((ArrayList) my_big_list2);

										}
									}
									my_very_big_list.get(manager.idSimList.indexOf(idsim)).add(objvaleur);
									System.out.println("aj st" +st+" ids "+manager.idSimList.indexOf(idsim));
								}
							}
						}				
					}
				}
			}
		}
		else if(my_matrix.equals("multi_distribhistory")) {
			for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
				if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
					for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
						int st=Integer.parseInt(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString());
						String idsim=manager.storableDataList.get(i).metadatahistory.getColumn(scope, 2).get(j).toString();
						{
							for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
								if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
									objvaleur = (Object) manager.storableDataList.get(i).distribhistory.getColumn(scope, k).get(j);
									if (my_very_big_list.size()<=manager.idSimList.indexOf(idsim))
									{
										for (int l=my_very_big_list.size(); l<=manager.idSimList.indexOf(idsim); l++)
										{
											List<Double> my_big_list2=new ArrayList<Double>();
											my_very_big_list.add((ArrayList) my_big_list2);

										}
									}
									my_very_big_list.get(manager.idSimList.indexOf(idsim)).add(objvaleur);
									System.out.println("aj st" +st+" ids "+manager.idSimList.indexOf(idsim));
								}
							}
						}				
					}
				}
			}
		}
		else {}

		at_var_manager = my_very_big_list;

		System.out.println(my_very_big_list);
		System.out.println("at_var_manager: " + at_var_manager);

		return at_var_manager;
	}
	
	@action(name = "distrib_legend", args = {
			//@arg(name = WITH_MATRIX, type = IType.STRING, optional = false), 
			@arg(name = WITH_VAR, type = IType.STRING, optional = false)//,
			//@arg(name = CYCLE, type = IType.INT, optional = false)
	})
	public List distrib_legend(final IScope scope) throws GamaRuntimeException  {

		List<String> legende_suivante = new ArrayList<String>();

		//String my_matrix = (String) scope.getStringArg(WITH_MATRIX);
		String my_variable = (String) scope.getStringArg(WITH_VAR);

		int step = scope.getClock().getCycle()-1;

		List<String> legende = new ArrayList<String>();
		//String intervalle;
		List obj = new ArrayList<Object>();
		double value;
		double single_value;


		for(int i=0;i<manager.agentGroupFollowerList.length(scope);i++) {
			if(manager.agentGroupFollowerList.get(i).toString().contains(scope.getAgentScope().getName().toString())) {
				for(int j=0;j<manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).length(scope);j++) {
					if(Float.parseFloat(manager.storableDataList.get(i).metadatahistory.getColumn(scope, 1).get(j).toString())==step) {
						for(int k=0; k<mydata.numvarmap.getValues().length(scope);k++) {
							if(mydata.numvarmap.getValues().get(k).equals(my_variable)) {
								if(legende.size()>=10) {
									legende.clear();
								}
								obj = (List)manager.storableDataList.get(i).distribhistoryparams.getColumn(scope, k).get(j);
								System.out.println("obj= " + obj);
								if(obj.size()==1) {
									single_value = (Float) obj.get(0);
									legende.add(String.valueOf(single_value));
									for(int l=1; l<10;l++) {
										legende.add("0");
									}
								}
								else {
									double ecart;
									if(Float.parseFloat(obj.get(0).toString())>=0) {
										ecart = Maths.pow(2, Integer.parseInt(obj.get(0).toString()));
									}
									else { 
										float temp = (-1)*Float.parseFloat(obj.get(0).toString());
										System.out.println("temp= " + temp);
										double temppow = Maths.pow(2, (int)temp);
										System.out.println("tempow= " + String.valueOf(temppow));
										ecart = (float) (1/temppow); 
										System.out.println("ecart= " + ecart);
									}

									value = ecart*Float.parseFloat(obj.get(1).toString());
									for(int l=0;l<10; l++) {
										double temp = value+ecart;
										legende.add(new String(String.valueOf(value) + "-" + String.valueOf(temp)));
										value = temp;
									}									
								}
							}
						}
					}				
				}
			}
		}
		// }
		System.out.println("LEGENDE: les valeurs de la variable: " + my_variable + " sont réparties sur les intervalles suivants: " + legende);
		legende_suivante = legende;

		return legende;
	}

	public void senddata()
	{
		Map<String,Object> message=new HashMap<String,Object>();
		message.put("type","newdata");
		message.put("follower",this.getName());
		multidata.lastdetailedvarvalues=mydata.lastdetailedvarvalues;
		mydata.lastdetailedvarvalues=null;
		message.put("value",mydata);
		mydata.lastdetailedvarvalues=multidata.lastdetailedvarvalues;

	}
    //For parrallel simulation
	public void getdatafromslaves(IScope scope)
	{
		for (String source:parallelsims.keySet())
		{
			parallelsims.put(source, false);
		}
		boolean receivedall=false;
		while (!receivedall)
		{
			try {
				Thread t=Thread.currentThread();
				synchronized (t)
				{
					t.wait(100);
					System.out.print("still empty...");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (!this.emptyMessage())
			{
				GamaMap<String,Object> message=this.fetchMessage();
				String source=(String)message.get("from");
				GamaMap<String,Object> content=(GamaMap<String,Object>)message.get("content");
				if (content.get("follower").equals(this.getName()))
				{
					if (!source.equals(getNetName()))
						if (!this.parallelsims.containsKey(source))
						{
							parallelsims.put(source, false);
						}
					if (content.get("type").equals("newdata"))
					{
						parallelsims.put(source, true);

						StorableData newdata=(StorableData)content.get("value");

						System.out.println("new data "+this.getNetName()+" is "+newdata);
						
						
						IList nl1=newdata.metadatahistory.getLine(scope, newdata.metadatahistory, newdata.metadatahistory.numRows-1);
						GamaObjectMatrix nm1=new GamaObjectMatrix(scope, nl1, new GamaPoint(mydata.metadatahistory.numCols,1), Types.NO_TYPE);
						multidata.metadatahistory=(GamaObjectMatrix)multidata.metadatahistory._opAppendVertically(scope, nm1);

						GamaObjectMatrix matrice2 = newdata.lastdetailedvarvalues;
						if (matrice2!=null)
						{
						multidata.lastdetailedvarvalues=(GamaObjectMatrix)multidata.lastdetailedvarvalues.opAppendVertically(scope,  matrice2);
						multidata.lastdetailedvarvalues = matrice2;
						}

						IList nl2=mydata.minhistory.getLine(scope, mydata.minhistory, mydata.minhistory.numRows-1);
						GamaFloatMatrix nm2=new GamaFloatMatrix(scope, nl2,new GamaPoint(mydata.minhistory.numCols,1));
						multidata.minhistory=(GamaFloatMatrix)multidata.minhistory._opAppendVertically(scope, nm2);

						IList nl3=mydata.maxhistory.getLine(scope, mydata.maxhistory, mydata.maxhistory.numRows-1);
						GamaFloatMatrix nm3=new GamaFloatMatrix(scope,nl3,new GamaPoint(mydata.maxhistory.numCols,1));
						multidata.maxhistory=(GamaFloatMatrix)multidata.maxhistory._opAppendVertically(scope, nm3); // .opAppendVertically(scope, multidata.maxhistory, nm3);
					

						IList nl4=mydata.averagehistory.getLine(scope, mydata.averagehistory, mydata.averagehistory.numRows-1);
						GamaFloatMatrix nm4=new GamaFloatMatrix(scope,nl4,new GamaPoint(mydata.averagehistory.numCols,1));
						multidata.averagehistory=(GamaFloatMatrix)multidata.averagehistory._opAppendHorizontally(scope, nm4); // .opAppendVertically(scope, multidata.averagehistory, nm4);
						

						IList nl5=mydata.stdevhistory.getLine(scope, mydata.stdevhistory, mydata.stdevhistory.numRows-1);
						GamaFloatMatrix nm5=new GamaFloatMatrix(scope,nl5,new GamaPoint(mydata.stdevhistory.numCols,1));
						multidata.stdevhistory=(GamaFloatMatrix)multidata.stdevhistory._opAppendVertically(scope, nm5); //.opAppendVertically(scope, multidata.stdevhistory, nm5);

						IList nl6=mydata.distribhistoryparams.getLine(scope, mydata.distribhistoryparams, mydata.distribhistoryparams.numRows-1);
						GamaObjectMatrix nm6=new GamaObjectMatrix(scope,nl6,new GamaPoint(mydata.distribhistoryparams.numCols,1), Types.NO_TYPE);
						multidata.distribhistoryparams=(GamaObjectMatrix)multidata.distribhistoryparams._opAppendVertically(scope, nm6); // .opAppendVertically(scope, multidata.distribhistoryparams, nm6);

						IList nl7=mydata.distribhistory.getLine(scope, mydata.distribhistory, mydata.distribhistory.numRows-1);
						GamaObjectMatrix nm7=new GamaObjectMatrix(scope,nl7,new GamaPoint(mydata.distribhistory.numCols,1), Types.NO_TYPE);
						multidata.distribhistory=(GamaObjectMatrix)multidata.distribhistory._opAppendHorizontally(scope, nm7); //.opAppendVertically(scope, multidata.distribhistory, nm7);
						

						String idsim=(String)newdata.metadatahistory.get(scope, 2,0);
						
						if(!manager.idSimList.contains(idsim)) {
							manager.idSimList.add(idsim);	
							if (manager.simColorList.size()+1<11)
							{
								int i=manager.simColorList.size();
									if (i==0) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.CYAN));
									if (i==1) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.RED));
									if (i==2) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.YELLOW));
									if (i==3) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.GREEN));
									if (i==4) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.BLUE));
									if (i==5) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.PINK));
									if (i==6) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.MAGENTA));
									if (i==7) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.ORANGE));
									if (i==8) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.LIGHT_GRAY));
									if (i==9) manager.simColorList.add((GamaColor)Cast.asColor(scope,GamaColor.DARK_GRAY));
								}					
//								manager.simColorList.add((GamaColor)GamaColor.int_colors.values().toArray()[manager.simColorList.size()+1]);
							else
								manager.simColorList.add((GamaColor)GamaColor.getInt(Random.opRnd(scope, 10000)));

							int i=manager.idSimList.size()-1;
								ISpecies sp=this.getPopulationFor("agent").getSpecies();
								if (!sp.hasVar("color"))
								{
									Variable nc=new Variable(this.getSpecies().getVar("color").getDescription());
									List<Variable> nv=new ArrayList<Variable>();
									nv.add(nc);
									sp.setChildren(nv);
								}
								GamlAgent nagent=new GamlAgent(this.getPopulationFor("agent"));
								this.getPopulationFor("agent").add(nagent);

								nagent._init_(scope);
								nagent.setName("parallel"+manager.idSimList.get(i));
								nagent.setDirectVarValue(scope,"color", manager.simColorList.get(i));
								virtualAgents.add(nagent);
								System.out.println("new virtual "+nagent.getName());

						}
						
					}

				}

			}
			receivedall=true;
			for (String source:parallelsims.keySet())
			{
				if (parallelsims.get(source)==false)
				{
					System.out.println("waiting for "+source);
					receivedall=false;
				}
			}



		}

	}

	public boolean doparallelsim()
	{
		return doparallelsim;
	}

	public boolean ismaster()
	{
		return ismastervar;
	}

	public void checkifreleased()
	{
		while (mastername.equals("unknown"))
		{
			try {
				Thread t=Thread.currentThread();
				synchronized (t)
				{
					t.wait(100);
					System.out.print("still no master...");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (!this.emptyMessage())
			{
				GamaMap<String,Object> message=this.fetchMessage();
				GamaMap<String,Object> content=(GamaMap<String,Object>)message.get("content");

				if (content.get("type").equals("mastername"))
				{
					if (content.get("follower").equals(this.getName()))
					{
						mastername=(String)content.get("value");
						System.out.println("the master of "+this.getNetName()+" is "+mastername);
					}
				}
			}

		}
		while (lastreleasedcycle<this.getScope().getClock().getCycle())
		{
			try {
				Thread t=Thread.currentThread();
				synchronized (t)
				{
					t.wait(100);
					System.out.print("still waiting for "+mastername+"...");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!this.emptyMessage())
			{
				GamaMap<String,Object> message=this.fetchMessage();
				GamaMap<String,Object> content=(GamaMap<String,Object>)message.get("content");

				if (content.get("type").equals("release"))
				{
					if (content.get("follower").equals(this.getName()))
					{
						lastreleasedcycle=(Integer)content.get("value");
						System.out.println("released for "+this.getNetName()+" is "+lastreleasedcycle);
					}

				}				
				if (content.get("type").equals("mastername"))
				{
					if (content.get("follower").equals(this.getName()))
					{
						mastername=(String)content.get("value");
						System.out.println("the master of "+this.getNetName()+" CHANGED, it is now "+mastername);
					}

				}
			}		
		}		
	}

	public void checknewslaves()
	{
		if (!this.emptyMessage())
		{

		}

	}

	public void releaseslaves()
	{
		GamaMap<String,Object> message = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
		message.put("type","release");
		message.put("follower",this.getName());
		message.put("value",this.getScope().getClock().getCycle());
	}



	public String getNetName()
	{
		return this.getName()+this.getUniqueSimName(this.getScope())+ismastervar;
	}

	 HashMap<String,LinkedList<GamaMap<String,Object>>> messages;
	 XStream xstream ;

	public void connect(String serverUrl, String mytopic, String name)
	{
/*		final IAgent agent = this;
		//		String serverUrl = (String) scope.getArg("topic", IType.STRING);
		//		String mytopic = (String) scope.getArg("to", IType.STRING);
		//		String name = (String) scope.getArg("withName", IType.STRING);

		long ctime=0;

		agent.setAttribute("topicName", mytopic);
		agent.setAttribute("serverURL", serverUrl);
		agent.setAttribute("netAgtName", name);

		if(!messages.containsKey(name))
		{
			messages.put(name, new LinkedList<GamaMap<String,Object>>());
		}

		if(this.producer==null)
		{
			try
			{
				this.listenTopic(serverUrl, mytopic);
				this.connectToTopic(serverUrl, mytopic);
			}catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}

//	public void sendMessage(String to, String tmpName, GamaMap<String,Object> message) throws GamaRuntimeException {
//	public void sendMessage(String to, String tmpName, GamaMap<String,String> message) throws GamaRuntimeException {
	public void sendMessage(String to, String tmpName, Map<String,Object> message) throws GamaRuntimeException {
		try
		{
			final IAgent agent = this;
			//	    	 String to = (String) scope.getArg("to", IType.STRING);
			//	    	 String tmpName =(String) agent.getAttribute("netAgtName");
			//	    	 GamaMap<String, Object> agentMap=(GamaMap<String, Object>)	scope.getArg("message", IType.MAP);
//			GamaMap<String, Object> agentMap=message;
			Map<String, Object> agentMap=message;

			MapMessage msg = session.createMapMessage();


//	    	 ObjectOutputStream out = xstream.createObjectOutputStream(System.out);
	    	for(String key:agentMap.keySet())
	    	{
//	    		System.out.println("message " + key + " " +agentMap.get(key).getClass().getCanonicalName() );
//	    		out.writeObject(agentMap.get(key));
		    	 msg.setObject(key, xstream.toXML(agentMap.get(key)));
	    	}
			msg.setStringProperty("to", to);
			msg.setStringProperty("follower", this.getName());
			msg.setStringProperty("from",tmpName );
			producer.send(msg);
			System.out.println("message from "+tmpName+" to "+to);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

*/	}

	public GamaMap<String, Object> fetchMessage()
	{
		final IAgent agent = this;
		String tmpName =(String) agent.getAttribute("netAgtName");
		LinkedList<GamaMap<String,Object>> mList=this.messages.get(tmpName);
		if(mList.isEmpty())
			return null;
		GamaMap<String, Object> mess=this.messages.get(tmpName).pollFirst();
		return mess;
	}

	public boolean emptyMessage()
	{
		final IAgent agent =this;
		String tmpName =(String) agent.getAttribute("netAgtName");
		LinkedList<GamaMap<String,Object>> mList=this.messages.get(tmpName);
		if (mList==null) return true;
/*		for (int i=this.messages.get(tmpName).size()-1; i>=0; i--)
		{
		GamaMap<String, Object> mess=this.messages.get(tmpName).get(i);
		if ((Long)mess.get("date")<firsttime) this.messages.get(tmpName).remove(i);
		}
		return (this.messages.get(tmpName).isEmpty());*/
		return mList.isEmpty();
	}




	
	/*
	private InitialContext initialiseContext(String url) throws NamingException
	{
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.objectweb.carol.jndi.spi.MultiOrbInitialContextFactory");
		props.put( "java.naming.provider.url", url ); 
		return new InitialContext(props);

	}

	private void listenTopic(String serverUrl, String mytopic) throws NamingException, JMSException
	{
		InitialContext jndi = initialiseContext(serverUrl);
		TopicConnectionFactory conFactory =  (TopicConnectionFactory)jndi.lookup("JTCF");
		// Create a JMS connection
		TopicConnection connection = conFactory.createTopicConnection();
		connection.start( );
		TopicSession subSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		// Look up a JMS topic
		Topic chatTopic = (Topic)jndi.lookup(mytopic);
		// Create a JMS publisher and subscriber
		MessageProducer publ = subSession.createProducer(chatTopic);
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);
		// Set a JMS message listener
		subscriber.setMessageListener(this);
		
		// Start the JMS connection; allows messages to be delivered	
	}

	public void connectToTopic(String serverUrl,String mytopic) throws NamingException, JMSException
	{
		InitialContext jndi = initialiseContext(serverUrl);
		TopicConnectionFactory conFactory =  (TopicConnectionFactory)jndi.lookup("JTCF");
		TopicConnection connection = conFactory.createTopicConnection();
		connection.start( );
		this.session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE); // false=NotTransacted
		Topic topic = (Topic)jndi.lookup(mytopic);
		this.producer = session.createProducer(topic);
		this.producer.setTimeToLive(10000);
		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);	

	}


	//	public void connectToTopic(IScope scope) throws NamingException, JMSException
	//	{
	//		String serverUrl = (String) scope.getArg("topic", IType.STRING);
	//		String mytopic = (String) scope.getArg("to", IType.STRING);
	//		InitialContext jndi = initialiseContext(serverUrl);
	//		TopicConnectionFactory conFactory =  (TopicConnectionFactory)jndi.lookup("JTCF");
	//		TopicConnection connection = conFactory.createTopicConnection();
	//		connection.start( );
	//		this.session = connection.createSession(false,
	//		Session.AUTO_ACKNOWLEDGE); // false=NotTransacted
	//		Topic topic = (Topic)jndi.lookup(mytopic);
	//		this.producer = session.createProducer(topic);
	//		this.producer.setTimeToLive(10000);
	//		this.producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);	
	//	}
	//


	@Override
	public void onMessage(Message msg) {

		try {
			String to = msg.getStringProperty("to");
			String from = msg.getStringProperty("from");
			if(!(msg instanceof MapMessage) || (to == null) || (!(this.messages.containsKey(to)||to.equals("all"))))
				return;
			if (msg.getJMSTimestamp()<firsttime) return;
			MapMessage mapMsg = (MapMessage)msg;
			if(to.equals("all"))
				pushMessage(to,to,from,mapMsg);
			else
			{
				for(String agtName:this.messages.keySet())
				{
					pushMessage(agtName,to,from,mapMsg);
				}
			}

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void pushMessage(String netAgentName, String to, String from, MapMessage mapMsg ) throws JMSException
	{
		LinkedList<GamaMap<String, Object>>	myMsgBox = this.messages.get(netAgentName);
		Map<String, Object> content=  new HashMap<String, Object>();

		Enumeration<String> contentNames = mapMsg.getMapNames();
		String key = null;
		while(contentNames.hasMoreElements())
		{
			key = contentNames.nextElement();
				content.put(key, xstream.fromXML((String)mapMsg.getObject(key)));
		}

		Map<String, Object> agentMsg=  new HashMap<String, Object>();
		agentMsg.put("from", from);
		agentMsg.put("to", to);
		agentMsg.put("date", mapMsg.getJMSTimestamp());
		agentMsg.put("content",content);
//		this.messages.get(netAgentName).addLast(agentMsg);
//		if (to.equals("all"))
			this.messages.get((String) this.getAttribute("netAgtName")).addLast((GamaMap<String, Object>) agentMsg);


	}*/

}
