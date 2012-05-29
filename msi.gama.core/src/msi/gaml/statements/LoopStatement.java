/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

// A group of commands that can be executed repeatedly.

@symbol(name = IKeyword.LOOP, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@facets(value = { @facet(name = IKeyword.FROM, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.TO, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.STEP, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.VAR, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.OVER, type = { IType.CONTAINER_STR, IType.POINT_STR }, optional = true),
	@facet(name = IKeyword.WHILE, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.TIMES, type = IType.INT_STR, optional = true) },

combinations = {

@combination({ IKeyword.FROM, IKeyword.TO, IKeyword.VAR, IKeyword.STEP }),
	@combination({ IKeyword.FROM, IKeyword.TO, IKeyword.VAR }),
	@combination({ IKeyword.OVER, IKeyword.VAR }),
	@combination({ IKeyword.OVER, IKeyword.VAR, IKeyword.STEP }), @combination({ IKeyword.TIMES }),
	@combination({ IKeyword.WHILE }) }, omissible = IKeyword.VAR)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
public class LoopStatement extends AbstractStatementSequence {

	private final LoopExecuter executer;

	public LoopStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		boolean isWhile = getFacet(IKeyword.WHILE) != null;
		boolean isList = getFacet(IKeyword.OVER) != null;
		boolean isBounded = getFacet(IKeyword.FROM) != null && getFacet(IKeyword.TO) != null;
		executer =
			isWhile ? new LoopWhileExecuter(this) : isList
				? getFacet(IKeyword.OVER).getType().id() == IType.POINT ? new LoopIntervalExecuter(
					this) : new LoopListExecuter(this) : isBounded ? new LoopBoundedExecuter(this)
					: new LoopTimesExecuter(this);

		// TODO Donner un nom
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		return executer.runIn(stack);
	}

	abstract class LoopExecuter {

		// protected final LoopCommand command;

		LoopExecuter(final LoopStatement com) {
			// command = com;
		}

		abstract Object runIn(final IScope scope) throws GamaRuntimeException;
	}

	class LoopBoundedExecuter extends LoopExecuter {

		private final IExpression from, to, step;
		private Integer constantFrom, constantTo, constantStep;
		private final String varName;

		LoopBoundedExecuter(final LoopStatement com) throws GamaRuntimeException {
			super(com);
			from = com.getFacet(IKeyword.FROM);
			if ( from.isConst() ) {
				constantFrom = Cast.asInt(null, from.value(GAMA.getDefaultScope()));
			}
			to = com.getFacet(IKeyword.TO);
			if ( to.isConst() ) {
				constantTo = Cast.asInt(null, to.value(GAMA.getDefaultScope()));
			}
			step = com.getFacet(IKeyword.STEP);
			if ( step == null ) {
				constantStep = 1;
			} else if ( step.isConst() ) {
				constantStep = Cast.asInt(null, step.value(GAMA.getDefaultScope()));
			}
			varName = com.getLiteral(IKeyword.VAR);
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			boolean allSkipped = true;
			int f = constantFrom == null ? Cast.asInt(scope, from.value(scope)) : constantFrom;
			int t = constantTo == null ? Cast.asInt(scope, to.value(scope)) : constantTo;
			int s = constantStep == null ? Cast.asInt(scope, step.value(scope)) : constantStep;
			if ( f > t ) {
				scope.setStatus(ExecutionStatus.skipped);
			}
			Object result = null;
			for ( int i = f, n = t + 1; i < n; i += s ) {
				scope.push(LoopStatement.this);
				scope.addVarWithValue(varName, i);
				result = LoopStatement.super.privateExecuteIn(scope);
				scope.pop(LoopStatement.this);
				final ExecutionStatus status = scope.getStatus();
				if ( status == ExecutionStatus.interrupt ) {
					return result;
				} else if ( status == ExecutionStatus._break ) {
					scope.setStatus(ExecutionStatus.success);
					return result;
				}
				allSkipped = allSkipped && status == ExecutionStatus.skipped;
			}
			scope.setStatus(allSkipped ? ExecutionStatus.skipped : ExecutionStatus.success);
			return result;
		}
	}

	class LoopIntervalExecuter extends LoopExecuter {

		private final IExpression over;
		ILocation constantOver;
		private final IExpression step;
		Integer constantStep;
		private final String varName;

		LoopIntervalExecuter(final LoopStatement com) throws GamaRuntimeException {
			super(com);
			over = com.getFacet(IKeyword.OVER);
			if ( over.isConst() ) {
				constantOver = Cast.asPoint(null, over.value(GAMA.getDefaultScope()));
			}
			step = com.getFacet(IKeyword.STEP);
			if ( step == null ) {
				constantStep = 1;
			} else if ( step.isConst() ) {
				constantStep = Cast.asInt(null, step.value(GAMA.getDefaultScope()));
			}
			varName = com.getLiteral(IKeyword.VAR);
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			ILocation interval =
				constantOver == null ? Cast.asPoint(scope, over.value(scope)) : constantOver;
			final int first = (int) interval.getX();
			final int last = (int) interval.getY();
			if ( first > last ) {
				scope.setStatus(ExecutionStatus.skipped);
			}
			final int step_ = constantStep == null ? Cast.asInt(scope, step.value(scope)) : 1;
			if ( step_ <= 0 || step_ > last - first ) {
				scope.setStatus(ExecutionStatus.skipped);
			}
			boolean allSkipped = true;
			Object result = null;
			for ( int i = first; i < last; i += step_ ) {
				scope.push(LoopStatement.this);
				scope.addVarWithValue(varName, i);
				result = LoopStatement.super.privateExecuteIn(scope);
				scope.pop(LoopStatement.this);
				final ExecutionStatus status = scope.getStatus();
				if ( status == ExecutionStatus.interrupt ) {
					return result;
				} else if ( status == ExecutionStatus._break ) {
					scope.setStatus(ExecutionStatus.success);
					return result;
				}
				allSkipped = allSkipped && status == ExecutionStatus.skipped;
			}
			scope.setStatus(allSkipped ? ExecutionStatus.skipped : ExecutionStatus.success);
			return result;
		}

	}

	class LoopListExecuter extends LoopExecuter {

		private final IExpression over;
		private final String varName;

		LoopListExecuter(final LoopStatement com) {
			super(com);
			over = com.getFacet(IKeyword.OVER);
			varName = com.getLiteral(IKeyword.VAR);
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			Object obj = over.value(scope);
			final IContainer list_ =
				!(obj instanceof IContainer) ? Cast.asList(scope, obj) : (IContainer) obj;
			boolean allSkipped = true;
			Object result = null;
			if ( list_ == null || list_.isEmpty() ) {
				scope.setStatus(ExecutionStatus.skipped);
				return null;
			}
			for ( Object each : list_ ) {
				scope.push(LoopStatement.this);
				scope.addVarWithValue(varName, each);
				result = LoopStatement.super.privateExecuteIn(scope);
				scope.pop(LoopStatement.this);
				final ExecutionStatus status = scope.getStatus();
				if ( status == ExecutionStatus.interrupt ) {
					return result;
				} else if ( status == ExecutionStatus._break ) {
					scope.setStatus(ExecutionStatus.success);
					return result;
				}
				allSkipped = allSkipped && status == ExecutionStatus.skipped;
			}
			scope.setStatus(allSkipped ? ExecutionStatus.skipped : ExecutionStatus.success);
			return result;
		}
	}

	class LoopTimesExecuter extends LoopExecuter {

		private final IExpression times;
		private Integer constantTimes;

		LoopTimesExecuter(final LoopStatement com) throws GamaRuntimeException {
			super(com);
			times = com.getFacet(IKeyword.TIMES);
			if ( times.isConst() ) {
				constantTimes = Cast.asInt(null, times.value(GAMA.getDefaultScope()));
			}
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final int max =
				constantTimes == null ? Cast.asInt(scope, times.value(scope)) : constantTimes;
			boolean allSkipped = true;
			if ( max <= 0 ) {
				scope.setStatus(ExecutionStatus.skipped);
			}
			Object result = null;
			for ( int i = 0; i < max; i++ ) {
				scope.push(LoopStatement.this);
				result = LoopStatement.super.privateExecuteIn(scope);
				scope.pop(LoopStatement.this);
				final ExecutionStatus status = scope.getStatus();
				if ( status == ExecutionStatus.interrupt ) {
					return result;
				} else if ( status == ExecutionStatus._break ) {
					scope.setStatus(ExecutionStatus.success);
					return result;
				}
				allSkipped = allSkipped && status == ExecutionStatus.skipped;
			}
			scope.setStatus(allSkipped ? ExecutionStatus.skipped : ExecutionStatus.success);
			return result;
		}

	}

	class LoopWhileExecuter extends LoopExecuter {

		private final IExpression cond;

		LoopWhileExecuter(final LoopStatement com) {
			super(com);
			cond = com.getFacet(IKeyword.WHILE);
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			boolean c = Cast.asBool(scope, cond.value(scope));
			boolean allSkipped = true;
			if ( !c ) {
				scope.setStatus(ExecutionStatus.skipped);
			}
			Object result = null;
			while (c) {
				scope.push(LoopStatement.this);
				result = LoopStatement.super.privateExecuteIn(scope);
				scope.pop(LoopStatement.this);
				final ExecutionStatus status = scope.getStatus();
				if ( status == ExecutionStatus.interrupt ) {
					return result;
				} else if ( status == ExecutionStatus._break ) {
					scope.setStatus(ExecutionStatus.success);
					return result;
				}
				allSkipped = allSkipped && status == ExecutionStatus.skipped;
				c = Cast.asBool(scope, cond.value(scope));
			}
			return allSkipped ? ExecutionStatus.skipped : ExecutionStatus.success;
		}
	}

}