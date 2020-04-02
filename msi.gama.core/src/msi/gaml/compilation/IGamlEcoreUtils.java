package msi.gaml.compilation;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

public interface IGamlEcoreUtils {

	/**
	 * Gets the name of a statement
	 *
	 * @param s
	 *            the s
	 * @return the name of
	 */
	String getNameOf(EObject s);

	/**
	 * Gets the exprs out of an expression list
	 *
	 * @param o
	 *            the o
	 * @return the exprs of
	 */
	List<? extends EObject> getExprsOf(EObject o);

	/**
	 * Gets the args out of the arguments of an action
	 *
	 * @param args
	 *            the args
	 * @return the args of
	 */
	List<? extends EObject> getArgsOf(EObject args);

	/**
	 * Gets the facets of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets of
	 */
	List<? extends EObject> getFacetsOf(EObject s);

	/**
	 * Gets the facets map of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets map of
	 */
	Map<String, ? extends EObject> getFacetsMapOf(EObject s);

	/**
	 * Get one particular facet of a statement
	 *
	 * @param s
	 * @return
	 */
	EObject getExpressionAtKey(EObject statement, String facetKey);

	/**
	 * Checks for children.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	boolean hasChildren(EObject obj);

	/**
	 * Gets the statements of a block
	 *
	 * @param block
	 *            the block
	 * @return the statements of
	 */
	List<? extends EObject> getStatementsOf(EObject block);

	/**
	 * Gets the equations of a systems of equations
	 *
	 * @param stm
	 *            the stm
	 * @return the equations of
	 */
	List<? extends EObject> getEquationsOf(EObject stm);

	/**
	 * Gets the key of an eObject
	 *
	 * @param f
	 *            the f
	 * @return the key of
	 */
	String getKeyOf(EObject f);

	/**
	 * Gets the key of an eObject in a given eClass
	 *
	 * @param object
	 *            the object
	 * @param clazz
	 *            the clazz
	 * @return the key of
	 */
	String getKeyOf(EObject object, EClass clazz);

	/**
	 * Gets the name of the ref represented by this eObject
	 *
	 * @param o
	 *            the o
	 * @return the name of ref
	 */
	String getNameOfRef(EObject o);

	/**
	 * Save an eObject into a string
	 *
	 * @param expr
	 *            the expr
	 * @return the string
	 */

	String toString(EObject expr);

	/**
	 * Gets the statement equal to or including this eObject
	 *
	 * @param o
	 *            the o
	 * @return the statement
	 */
	EObject getStatement(EObject o);

	/**
	 * Checks if this statement includes a batch definition
	 *
	 * @param e
	 *            the e
	 * @return true, if is batch
	 */
	boolean isBatch(EObject e);

	EObject getExprOf(EObject element);

	boolean hasFacet(EObject s, String facet);

}