/*******************************************************************************************************
 *
 * msi.gaml.expressions.IOperator.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.interfaces.IBenchmarkable;
import msi.gaml.descriptions.OperatorProto;

/**
 * Written by drogoul Modified on 22 ao�t 2010
 * 
 * @todo Description
 * 
 */
public interface IOperator extends IExpression, IBenchmarkable {

	@FunctionalInterface
	public static interface IOperatorVisitor {
		void visit(IOperator operator);
	}

	public abstract void visitSuboperators(IOperatorVisitor visitor);

	public abstract IExpression arg(int i);

	public abstract OperatorProto getPrototype();

	@Override
	default String getNameForBenchmarks() {
		return serialize(true);
	}

}