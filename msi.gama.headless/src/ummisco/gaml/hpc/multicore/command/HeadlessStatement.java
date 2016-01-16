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
import msi.gama.common.util.GuiUtils;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.headless.openmole.IMoleExperiment;
import msi.gama.headless.openmole.MoleSimulationLoader;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = IKeywords.STARTSIMULATION, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SINGLE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = {
		@facet(name = IKeywords.MODEL, type = IType.STRING, optional = false),
		@facet(name = IKeywords.EXPERIMENT, type = IType.STRING, optional = false),
		@facet(name = IKeywords.END, type = IType.INT, optional = true),
		@facet(name = IKeywords.CORE, type = IType.INT, optional = true),
		@facet(name = IKeywords.OUT, type = IType.STRING, optional = true),
		@facet(name = IKeywords.WITHOUTPUTS, type = IType.MAP, optional = true),
		@facet(name = IKeywords.WITHPARAMS, type = IType.MAP, optional = true)}, omissible = IKeywords.EXPERIMENT)

public class HeadlessStatement extends AbstractStatement {
	
	public HeadlessStatement(IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	
	private void initialize(final IScope scope) {
	
	}
	
	
	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {

		//GamaMap<String, Object> myInput =  Cast.asMap(scope, getFacetValue(scope,IKeywords.WITHPARAMS ));
		//GamaMap<String, Integer> myOutput =  Cast.asMap(scope, getFacetValue(scope,IKeywords.WITHOUTPUTS ));
		String expName =  Cast.asString(scope, getFacetValue(scope,IKeywords.EXPERIMENT ));
		String modelPath =  Cast.asString(scope, getFacetValue(scope,IKeywords.MODEL ));

		
		IModel mdl = MoleSimulationLoader.loadModel(new File("/tmp/headless/samples/predatorPrey/predatorPrey.gaml"));
		IMoleExperiment exp = MoleSimulationLoader.newExperiment(mdl);

		exp.setup("preyPred",123);
		exp.step();
		exp.step();
		exp.step();
		exp.step();
		System.out.println("coucocy "+ exp.getOutput("number_of_preys"));

		
		return null;
	}

}
