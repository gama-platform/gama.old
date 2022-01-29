/*******************************************************************************************************
 *
 * IOperator.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions.operators;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 22 ao�t 2010
 * 
 * @todo Description
 * 
 */
public interface IOperator extends IExpression, IBenchmarkable {

	/**
	 * The Interface IOperatorVisitor.
	 */
	@FunctionalInterface
	public static interface IOperatorVisitor {
		
		/**
		 * Visit.
		 *
		 * @param operator the operator
		 */
		void visit(IOperator operator);
	}

	/**
	 * Visit suboperators.
	 *
	 * @param visitor the visitor
	 */
	public abstract void visitSuboperators(IOperatorVisitor visitor);

	/**
	 * Arg.
	 *
	 * @param i the i
	 * @return the i expression
	 */
	public abstract IExpression arg(int i);

	/**
	 * Gets the prototype.
	 *
	 * @return the prototype
	 */
	public abstract OperatorProto getPrototype();

	/**
	 * Gets the name for benchmarks.
	 *
	 * @return the name for benchmarks
	 */
	@Override
	default String getNameForBenchmarks() {
		return serialize(true);
	}

}