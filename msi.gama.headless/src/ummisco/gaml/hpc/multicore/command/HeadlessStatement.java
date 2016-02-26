/*********************************************************************************************
 * 
 *
 * 'HeadlessStatement.java', in plugin 'msi.gama.headless', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.hpc.multicore.command;

import java.io.File;
import java.util.Calendar;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.AbstractGui;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.openmole.IMoleExperiment;
import msi.gama.headless.openmole.MoleSimulationLoader;
import msi.gama.headless.runtime.LocalSimulationRuntime;
import msi.gama.headless.runtime.SimulationRuntime;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = IKeywords.STARTSIMULATION, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true,
concept = { IConcept.HEADLESS })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SINGLE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = {
		@facet(name = IKeywords.MODEL, type = IType.STRING, optional = false),
		@facet(name = IKeywords.EXPERIMENT, type = IType.STRING, optional = false),
		@facet(name = IKeywords.END, type = IType.INT, optional = true),
		@facet(name = IKeywords.CORE, type = IType.INT, optional = true),
		@facet(name = IKeywords.WITHSEED, type = IType.INT, optional = true),
		//@facet(name = IKeywords.OUT, type = IType.STRING, optional = true),
		@facet(name = IKeywords.WITHOUTPUTS, type = IType.MAP, optional = true),
		@facet(name = IKeywords.WITHPARAMS, type = IType.MAP, optional = true)}, omissible = IKeywords.EXPERIMENT)
public class HeadlessStatement extends AbstractStatement {
	private int numberOfThread = 4;
	private SimulationRuntime processorQueue;
	private int maxSimulationID=0;
	
	
	public String getSimulationId()
	{
		return (new Integer(maxSimulationID++)).toString();
	}

	public HeadlessStatement(IDescription desc) {
		super(desc);
		processorQueue = new LocalSimulationRuntime(this.numberOfThread);
	}

	
	private String retrieveModelFileAbsolutePath(IScope scope, String filename)
	{
		if(filename.charAt(0)=='/')
			return filename;
		return  new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath()+"/"+filename;
	}
	
	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {

		int seed = 0;
		String expName =  Cast.asString(scope, getFacetValue(scope,IKeywords.EXPERIMENT ));
		String modelPath =  Cast.asString(scope, getFacetValue(scope,IKeywords.MODEL ));
		if(modelPath !=null && !modelPath.isEmpty())
		{
			modelPath = retrieveModelFileAbsolutePath(scope, modelPath);
		}
		else
		{
			//no model specified, this caller model path is used.
			modelPath = scope.getModel().getFilePath();
		}
		
		GamaMap<String,?> outputs =  Cast.asMap(scope, getFacetValue(scope,IKeywords.WITHOUTPUTS ),false);

		
		if(this.hasFacet(IKeywords.WITHSEED))
			seed =  Cast.asInt(scope, getFacetValue(scope,IKeywords.WITHSEED ));
		
		long lseed = seed;
		
		System.out.println("chemin du fichier" + new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath());
		
		
		ExperimentJob sim=new ExperimentJob(this.getSimulationId(),modelPath, expName, 1000, lseed);
		
		this.processorQueue.pushSimulation(sim);


		
		return null;
	}

}
