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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
@facets(value = { @facet(name = IKeyword.FROM, type = IType.INT, optional = true),
	@facet(name = IKeyword.TO, type = IType.INT, optional = true),
	@facet(name = IKeyword.STEP, type = IType.INT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.NEW_TEMP_ID, optional = true),
	@facet(name = IKeyword.OVER, type = { IType.CONTAINER, IType.POINT }, optional = true),
	@facet(name = IKeyword.WHILE, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TIMES, type = IType.INT, optional = true) },

combinations = {

@combination({ IKeyword.FROM, IKeyword.TO, IKeyword.NAME, IKeyword.STEP }),
	@combination({ IKeyword.FROM, IKeyword.TO, IKeyword.NAME }), @combination({ IKeyword.OVER, IKeyword.NAME }),
	@combination({ IKeyword.OVER, IKeyword.NAME, IKeyword.STEP }), @combination({ IKeyword.TIMES }),
	@combination({ IKeyword.WHILE }) }, omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
public class LoopStatement extends AbstractStatementSequence {

	private final LoopExecuter executer;
	private final String varName;
	private final Object[] result = new Object[1];

	public LoopStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		final boolean isWhile = getFacet(IKeyword.WHILE) != null;
		final boolean isList = getFacet(IKeyword.OVER) != null;
		final boolean isPoint = isList && getFacet(IKeyword.OVER).getType().id() == IType.POINT;
		final boolean isBounded = getFacet(IKeyword.FROM) != null && getFacet(IKeyword.TO) != null;
		varName = getLiteral(IKeyword.NAME);
		executer =
			isWhile ? new While() : isList ? isPoint ? new Interval() : new Over() : isBounded ? new Bounded()
				: new Times();
	}

	@Override
	public void enterScope(final IScope scope) {
		super.enterScope(scope);
		if ( varName != null ) {
			scope.addVarWithValue(varName, null);
		}
	}

	@Override
	public void leaveScope(final IScope scope) {
		// Should clear any _loop_halted status present
		scope.popLoop();
		super.leaveScope(scope);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		return executer.runIn(scope);
	}

	protected boolean loopBody(final IScope scope, final Object var) {
		scope.push(this);
		if ( varName != null ) {
			scope.setVarValue(varName, var);
		}
		result[0] = super.privateExecuteIn(scope);
		scope.pop(this);
		return !scope.interrupted();
	}

	interface LoopExecuter {

		abstract Object runIn(final IScope scope);
	}

	class Bounded implements LoopExecuter {

		private final IExpression from = getFacet(IKeyword.FROM);
		private final IExpression to = getFacet(IKeyword.TO);
		private final IExpression step = getFacet(IKeyword.STEP);
		private Integer constantFrom, constantTo, constantStep;

		Bounded() throws GamaRuntimeException {
			final IScope scope = GAMA.obtainNewScope();
			if ( from.isConst() ) {
				constantFrom = Cast.asInt(scope, from.value(scope));
			}
			if ( to.isConst() ) {
				constantTo = Cast.asInt(scope, to.value(scope));
			}
			if ( step == null ) {
				constantStep = 1;
			} else if ( step.isConst() ) {
				constantStep = Cast.asInt(scope, step.value(scope));
			}
			GAMA.releaseScope(scope);
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final int f = constantFrom == null ? Cast.asInt(scope, from.value(scope)) : constantFrom;
			final int t = constantTo == null ? Cast.asInt(scope, to.value(scope)) : constantTo;
			final int s = constantStep == null ? Cast.asInt(scope, step.value(scope)) : constantStep;
			Object[] result = new Object[1];
			for ( int i = f, n = t + 1; i < n && loopBody(scope, i); i += s ) {}
			return result[0];
		}
	}

	class Interval implements LoopExecuter {

		private final IExpression over = getFacet(IKeyword.OVER);
		ILocation constantOver;
		private final IExpression step = getFacet(IKeyword.STEP);
		Integer constantStep = 1;

		Interval() throws GamaRuntimeException {
			final IScope scope = GAMA.obtainNewScope();
			if ( over.isConst() ) {
				constantOver = Cast.asPoint(scope, over.value(scope));
			}
			if ( step != null && step.isConst() ) {
				constantStep = Cast.asInt(scope, step.value(scope));
			}
			GAMA.releaseScope(scope);
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final ILocation interval = constantOver == null ? Cast.asPoint(scope, over.value(scope)) : constantOver;
			final int first = (int) interval.getX();
			final int last = (int) interval.getY();
			if ( first > last ) { return null; }
			final int step_ = constantStep == null ? Cast.asInt(scope, step.value(scope)) : 1;
			if ( step_ <= 0 || step_ > last - first ) { return null; }
			for ( int i = first; i < last && loopBody(scope, i); i += step_ ) {}
			return result[0];
		}

	}

	class Over implements LoopExecuter {

		private final IExpression over = getFacet(IKeyword.OVER);

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final Object obj = over.value(scope);
			final Iterable list_ =
				!(obj instanceof IContainer) ? Cast.asList(scope, obj) : ((IContainer) obj).iterable(scope);
			for ( final Object each : list_ ) {
				if ( !loopBody(scope, each) ) {
					break;
				}
			}
			return result[0];
		}
	}

	class Times implements LoopExecuter {

		private final IExpression times = getFacet(IKeyword.TIMES);
		private Integer constantTimes;

		Times() throws GamaRuntimeException {
			if ( times.isConst() ) {
				constantTimes = Cast.as(times, Integer.class);
			}
		}

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			final int max = constantTimes == null ? Cast.asInt(scope, times.value(scope)) : constantTimes;
			for ( int i = 0; i < max && loopBody(scope, null); i++ ) {}
			return result[0];
		}

	}

	class While implements LoopExecuter {

		private final IExpression cond = getFacet(IKeyword.WHILE);

		@Override
		public Object runIn(final IScope scope) throws GamaRuntimeException {
			while (Cast.asBool(scope, cond.value(scope)) && loopBody(scope, null)) {}
			return result[0];
		}
	}

}