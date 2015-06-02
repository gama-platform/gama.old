package gama_analyzer;


import java.util.LinkedList;
import java.util.List;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Cast; 
import msi.gaml.types.IType;
import weka.clusterers.*;
import weka.clusterers.forOPTICSAndDBScan.DataObjects.ManhattanDataObject;
import weka.core.*;

@species(name = "AgGroupAnalizer")
public class AgentGroupAnalizer extends ClusterBuilder  {
	
	//private LinkedList<GamlAgent> cluster;
	// private listeCluster;

	public AgentGroupAnalizer(final IPopulation s) throws GamaRuntimeException {
			super(s);
		}
	
	@action(name = "creation_cluster")
	public void creationCluster(final IScope scope) throws GamaRuntimeException  {
		int i;
		//for(i=1;i<)
		//scope.getSimulationScope().getSpecies().get(i);
	}
	
}
