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

import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.statements.IStatement;


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
//			computeEmotions(scope);
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
	
	public class UpdateEmotionsIntensity extends AbstractStatement {

		public UpdateEmotionsIntensity(IDescription desc) {
			super(desc);
		}

		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			updateEmotionsIntensity(scope);
			return null;
		}
		
	}
	
	public class UpdateLifeTimePredicates extends AbstractStatement {

		public UpdateLifeTimePredicates(IDescription desc) {
			super(desc);
		}

		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			updateLifeTimePredicates(scope);
			return null;
		}
		
	}
	public void preStep(final IScope scope, IPopulation<? extends IAgent> gamaPopulation){
		final IExpression schedule = gamaPopulation.getSpecies().getSchedule();
		final List<? extends IAgent> agents = schedule == null ? gamaPopulation : Cast.asList(scope, schedule.value(scope));
		
		GamaExecutorService.execute(scope, new UpdateLifeTimePredicates(null), agents,parallel) ;
		GamaExecutorService.execute(scope, new UpdateEmotionsIntensity(null), agents,parallel) ;
		
		if (_reflexes != null)
			for (final IStatement r : _reflexes) {
				if (!scope.interrupted()) {
					GamaExecutorService.execute(scope, r, agents,ConstantExpressionDescription.FALSE_EXPR_DESCRIPTION) ;
				}
			}
			
		if (_perceptionNumber > 0) {
			for (int i = 0; i < _perceptionNumber; i++) {
				if (!scope.interrupted()) {
					PerceiveStatement statement = _perceptions.get(i);
					IExpression par = statement.getParallel() == null ? parallel : statement.getParallel();
					GamaExecutorService.execute(scope, statement, agents,par) ;
				}
			}
		}
		if (_rulesNumber > 0) {
			for (int i = 0; i < _rulesNumber; i++) {
				RuleStatement statement = _rules.get(i);
				IExpression par = statement.getParallel() == null ? parallel : statement.getParallel();
				GamaExecutorService.execute(scope, statement, agents,par) ;
			}
		}
		
		GamaExecutorService.execute(scope, new UpdateEmotions(null), agents,parallel) ;
		GamaExecutorService.execute(scope, new UpdateSocialLinks(null), agents,parallel) ;
	}
	
	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executePlans(scope);
	}

}
