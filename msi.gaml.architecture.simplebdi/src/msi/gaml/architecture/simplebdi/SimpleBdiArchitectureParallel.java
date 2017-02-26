/*********************************************************************************************
 *
 *
 * 'SimpleBdiArchitecture.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;


@skill (
		name = "parallel_bdi",
		concept = { IConcept.BDI, IConcept.ARCHITECTURE })
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class SimpleBdiArchitectureParallel extends SimpleBdiArchitecture {

	IExpression parallel = ConstantExpressionDescription.TRUE_EXPR_DESCRIPTION;
	
	public class UpdateEmotions extends AbstractStatement {

		public UpdateEmotions(IDescription desc) {
			super(desc);
		}

		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			computeEmotions(scope);
			return null;
		}
		
	}
	
	public class UpdateSocialLinks extends AbstractStatement {

		public UpdateSocialLinks(IDescription desc) {
			super(desc);
		}

		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			updateSocialLinks(scope);
			return null;
		}
		
	}
	public void preStep(final IScope scope, IPopulation<? extends IAgent> gamaPopulation){
		if (_reflexes != null)
			for (final IStatement r : _reflexes) {
				if (!scope.interrupted()) {
					GamaExecutorService.execute(scope, r, gamaPopulation,parallel) ;
				}
			}
			
		if (_perceptionNumber > 0) {
			for (int i = 0; i < _perceptionNumber; i++) {
				if (!scope.interrupted()) {
					GamaExecutorService.execute(scope, _perceptions.get(i), gamaPopulation,parallel) ;
				}
			}
		}
		if (_rulesNumber > 0) {
			for (int i = 0; i < _rulesNumber; i++) {
				GamaExecutorService.execute(scope, _rules.get(i), gamaPopulation,parallel) ;
			}
		}
		
		GamaExecutorService.execute(scope, new UpdateEmotions(null), gamaPopulation,parallel) ;
		GamaExecutorService.execute(scope, new UpdateSocialLinks(null), gamaPopulation,parallel) ;
	}
	
	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executePlans(scope);
	}

}
