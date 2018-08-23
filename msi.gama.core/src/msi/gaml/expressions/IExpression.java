/*********************************************************************************************
 *
 * 'IExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import java.util.function.Predicate;

import msi.gama.common.interfaces.IDisposable;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.ITyped;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.ICollector;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.types.IType;

/**
 * Interface IExpression. Represents the functional part of the facets in GAML, produced by compiling an
 * {@link IExpressionDescription}, which can be evaluated within a {@link IScope}.
 * 
 * @author A. Drogoul
 * @since 25 dec. 2010
 * @since August 2018, IExpression is a @FunctionalInterface
 * 
 */
@FunctionalInterface
public interface IExpression extends IGamlDescription, ITyped, IDisposable {

	/**
	 * Convenience method for obtaining the constant value without passing a scope. Should be invoked after testing the
	 * expression with {@link IExpression#isConst()}. All runtime exceptions are caught and the method returns null in
	 * case of exceptions. Typically useful in validation contexts
	 * 
	 * @return
	 */
	default Object getConstValue() {
		try {
			return value(null);
		} catch (final RuntimeException e) {
			return null;
		}
	}

	/**
	 * Returns the result of the evaluation of the expression within the scope passed in parameter.
	 * 
	 * @param scope
	 *            the current GAMA scope
	 * @return the result of the evaluation of the expression
	 * @throws GamaRuntimeException
	 *             if an error occurs
	 */
	public abstract Object value(final IScope scope) throws GamaRuntimeException;

	/**
	 * Whether the expression is considered as 'constant', meaning it does not need a scope to be evaluated and return a
	 * value
	 * 
	 * @return true if the expression is constant
	 */
	default boolean isConst() {
		// By default
		return false;
	}

	/**
	 * Returns the literal value of the expression. Depending on the subclasses, it can be its serialization or an
	 * arbitrary name (for example a variable's name)
	 * 
	 * @return the literal value of the expression
	 */
	default String literalValue() {
		return getName();
	}

	/**
	 * Returns an expression where all the temp variables belonging to the scope passed in parameter are replaced by
	 * constants representing their values
	 */
	default IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	/**
	 * Whether this expression should be parenthesized when serialized
	 * 
	 * @return true if the serialization of the expression needs to be parenthesized
	 */

	default boolean shouldBeParenthesized() {
		return true;
	}

	/**
	 * Collects the attributes defined in species that are being used in this expression
	 * 
	 * @param species
	 *            the description of a species
	 * @param result
	 *            a collector which can be fed with the description of the attributes defined in the species if they
	 *            happend to be used by this expression or one of its sub-expression
	 */
	default void collectUsedVarsOf(final SpeciesDescription species, final ICollector<VariableDescription> result) {
		// Nothing by default
	}

	/**
	 * Whether this expression depends on variables, attributes, or species belonging to a specific context or not
	 * 
	 * @return true if this expression does not use any attribute, variable or species
	 */
	default boolean isContextIndependant() {
		return true;
	}

	/**
	 * Returns, by default, the type of the expression (see {@link ITyped#getGamlType()}. Specialized in some cases (ie.
	 * {@link TypeExpression}) to return the type denoted by this expression
	 * 
	 * @return the type denoted by this expression
	 */
	public default IType<?> getDenotedType() {
		return getGamlType();
	}

	/**
	 * Whether this expression or one of its sub-expressions match the predicate passed in parameter
	 * 
	 * @param predicate
	 *            a predicate returning true or false
	 * @return true if this expression or one of its sub-expressions evaluate the predicate to true; false otherwise
	 */
	public default boolean findAny(final Predicate<IExpression> predicate) {
		return predicate.test(this);
	}

}