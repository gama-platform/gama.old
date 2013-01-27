package ummisco.gaml.ext.maths.architecture;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IGamlAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IPath;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

//@vars({ @var(name = MathsController.PERSISTENCE_COEFFICIENT, type = IType.FLOAT_STR, init = "1.0"), 
//		@var(name = MathsController.DESIRE_BASE, type = IType.LIST_STR, init = "[]")})
//
//@skill(name = MathsController.MATHS_CONTROLLER)
public class MathsController extends AbstractArchitecture{
	public static final String MATHS_CONTROLLER = "maths_controller";
	public static final String EQUATION = "equation";
	public static final String PERSISTENCE_COEFFICIENT = "persistence_coefficient";
	private static final Double EPSILON = 0.0001;
	public static final String DESIRE_BASE = "desire_base";
	public static final String EQUATION_NAME = "equation_name";
	public static final String EQUATION_PARAMETERS = "equation_parameters";
	
	
	private IScope _consideringScope;

	private int _equationsNumber = 0;
	private int _perceiveNumber = 0;

	/* msi.gaml.architecture.reflex.ReflexArchitecture cannot be cast to msi.gaml.architecture.simplebdi.MathsController *
	@getter(PERSISTENCE_COEFFICIENT)
	public double getPersistenceCoefficient(final IAgent agent){
		return (Double) agent.getAttribute(PERSISTENCE_COEFFICIENT);
	}
	
	@setter(PERSISTENCE_COEFFICIENT)
	public void setPersistenceCoefficient(final IAgent agent, final double persistenceCoefficient){
		agent.setAttribute(PERSISTENCE_COEFFICIENT, persistenceCoefficient);
	}
	/**/
	
	
	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return executePlans(scope);
	}

	protected final Object executePlans(final IScope scope) {
		Object result = null;
//		if (_perceiveNumber > 0){
//			for (int i = 0; i < _perceiveNumber; i++){
//				result = _perceives.get(i).executeOn(scope);
//			}
//		}
		if (_equationsNumber > 0){
			IGamlAgent a = getCurrentAgent(scope);
			/**/
			Double persistenceCoefficient = scope.hasArg(PERSISTENCE_COEFFICIENT) ? scope.getFloatArg(PERSISTENCE_COEFFICIENT) : (Double)a.getAttribute(PERSISTENCE_COEFFICIENT);
			//Double persistenceCoefficient = (Double)a.getAttribute(PERSISTENCE_COEFFICIENT);
			if ((persistenceCoefficient != null) && (persistenceCoefficient > 0)){
				Boolean flipResult = msi.gaml.operators.Random.opFlip(scope, persistenceCoefficient);
				//System.out.println("Flip result of " + persistenceCoefficient + " is " + flipResult);
//				if (flipResult){
//					if (_persistentTask == null){
//						_persistentTask = selectExecutablePlanWithHighestPriority(scope);
//					}
//					if (_persistentTask != null){
//						if (!a.dead()){
//							result = _persistentTask.executeOn(scope);
//						}
//					}
//				}
//				else{
//					_persistentTask = selectExecutablePlanWithHighestPriority(scope);
//					if (_persistentTask != null){
//						if (!a.dead()){
//							result = _persistentTask.executeOn(scope);
//						}
//					}
//				}
				//System.out.println("chosen task: " + _persistentTask.getName());
			}
			/**/	
		}
		return result;
	}
//	
//	protected final SimpleEquation selectExecutablePlanWithHighestPriority(final IScope scope){
//		SimpleEquation resultStatement = null;
//		double highestPriority = Double.MIN_VALUE;
//		for (SimpleEquation statement: _equations){
//			boolean isContextConditionSatisfied = ( (statement.getContextExpression() == null) || Cast.asBool(scope, statement.getContextExpression().value(scope)) );
//			if (isContextConditionSatisfied){
//				double currentPriority = Cast.asFloat(scope, statement.getPriorityExpression().value(scope));
//				if (highestPriority < currentPriority){
//					highestPriority = currentPriority;
//					resultStatement = statement;
//				}
//			}
//		}
//		return resultStatement;
//	}
	
//	@action(name = "solve")
//	@args(names = { EQUATION_NAME, EQUATION_PARAMETERS})
	public Object primSolveEquation(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		String predicateName = scope.hasArg(EQUATION_NAME) ? scope.getStringArg(EQUATION_NAME) : (String)agent.getAttribute(EQUATION_NAME);
		GamaList<Object> predicateParameters = (GamaList<Object>) (scope.hasArg(EQUATION_PARAMETERS) ? scope.getListArg(EQUATION_PARAMETERS) : (GamaList<Object>)agent.getAttribute(EQUATION_PARAMETERS));
		if ( (predicateName == null) || (predicateName.length() == 0)){
			return null;
		}
		System.out.println(predicateName);
//		GamaList<Object> factBase = (GamaList<Object>) (scope.hasArg(BELIEF_BASE) ? scope.getListArg(BELIEF_BASE) : (GamaList<Object>)agent.getAttribute(BELIEF_BASE));
//		GamaList<Object> predicateItem = new GamaList<Object>();
//		predicateItem.add(predicateName);
//		if ( (predicateParameters != null) && (predicateParameters.size() > 0)){
//			predicateItem.add(predicateParameters);
//		}
//		else predicateItem.add(null);
//		int perceiveTime = SimulationClock.getCycle();
//		boolean isExistPredicate = false;
//		for (Object item: factBase){
//			try{
//				GamaList<Object> currentPredicate = (GamaList<Object>) item;
//				if (isEqualPredicates(predicateItem, currentPredicate)){
//					isExistPredicate = true;
//					if (currentPredicate.size() >= 4){
//						currentPredicate.set(3, perceiveTime);
//					}
//					break;
//				}
//			}
//			catch (Exception e1){
//				System.out.println(perceiveTime + ": " + e1.getMessage());
//			}
//		}
//		if (!isExistPredicate){
//			predicateItem.add(perceiveTime);
//			predicateItem.add(perceiveTime);
//			factBase.add(predicateItem);
//		}
//		agent.setAttribute(BELIEF_BASE, factBase);
		return null;
	}
	
	
	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		this._consideringScope = scope;
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
	}

	@Override
	public void dispose() {}
	
	private boolean isEqualPredicates(GamaList<Object> predicate1, GamaList<Object> predicate2){
		if ((predicate1.size() < 2) || (predicate2.size() < 0)) return false;
		Object currentElement; 
		currentElement = predicate1.get(0);
		if (!(currentElement instanceof String)) return false;
		String name1 = (String) currentElement;
		currentElement = predicate2.get(0);
		if (!(currentElement instanceof String)) return false;
		String name2 = (String) currentElement;
		if (!(name1.equals(name2))) return false;
		
		currentElement = predicate1.get(1);
		if (!(currentElement instanceof GamaList<?>)) return false;
		GamaList<?> parameter1 = (GamaList<?>) currentElement;
		currentElement = predicate2.get(1);
		if (!(currentElement instanceof GamaList<?>)) return false;
		GamaList<?> parameter2 = (GamaList<?>) currentElement;
		if (parameter1.size() != parameter2.size()) return false;
		for (int index = 0; index < parameter1.size(); index++){
			Object childElement1 = parameter1.get(index);
			Object childElement2 = parameter2.get(index);
			if (childElement1 instanceof String){
				if (!(childElement2 instanceof String)) return false;
				String value1 = (String) childElement1;
				String value2 = (String) childElement2;
				if (! value1.equals(value2)) return false;
			}
			else if (childElement1 instanceof Double){
				if (!(childElement2 instanceof Double)) return false;
				Double value1 = (Double) childElement1;
				Double value2 = (Double) childElement2;
				if (Maths.abs(value1 - value2) > EPSILON) return false;
			}
			else if (childElement1 instanceof Integer){
				if (!(childElement2 instanceof Integer)) return false;
				Integer value1 = (Integer) childElement1;
				Integer value2 = (Integer) childElement2;
				if ((value1 - value2) > 0) return false;
			}
			else{
				return false;
			}
		}
		return true;
	}

	@Override
	public void setChildren(List<? extends ISymbol> children) {
		// TODO Auto-generated method stub
		
	}
}
