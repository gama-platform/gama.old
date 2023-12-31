/*******************************************************************************************************
 *
 * ExpressionDescriptionBuilder.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.expression;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.Unary;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gaml.compilation.GAML;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IExpressionDescription;

/**
 * The Class ExpressionDescriptionBuilder.
 */
public class ExpressionDescriptionBuilder extends GamlSwitch<IExpressionDescription> {

	@Override
	public IExpressionDescription caseIntLiteral(final IntLiteral object) {
		IExpressionDescription ed = null;
		try {
			ed = ConstantExpressionDescription.create(Integer.parseInt(object.getOp()));
		} catch (final NumberFormatException e) {
			ed = ConstantExpressionDescription.create(0);
		}
		GamlResourceServices.getResourceDocumenter().setGamlDocumentation(object.eResource().getURI(), object,
				ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseDoubleLiteral(final DoubleLiteral object) {
		IExpressionDescription ed = null;
		try {
			ed = ConstantExpressionDescription.create(Double.parseDouble(object.getOp()));
		} catch (final NumberFormatException e) {
			ed = ConstantExpressionDescription.create(0d);
		}
		GamlResourceServices.getResourceDocumenter().setGamlDocumentation(object.eResource().getURI(), object,
				ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseStringLiteral(final StringLiteral object) {
		final IExpressionDescription ed = ConstantExpressionDescription.create(object.getOp());
		GamlResourceServices.getResourceDocumenter().setGamlDocumentation(object.eResource().getURI(), object,
				ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseBooleanLiteral(final BooleanLiteral object) {
		final IExpressionDescription ed = ConstantExpressionDescription.create(IKeyword.TRUE.equals(object.getOp()));
		GamlResourceServices.getResourceDocumenter().setGamlDocumentation(object.eResource().getURI(), object,
				ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseUnitName(final UnitName object) {
		final String s = EGaml.getInstance().getKeyOf(object);
		if (GAML.UNITS.containsKey(s)) return GAML.UNITS.get(s);
		return null;
	}

	@Override
	public IExpressionDescription caseUnary(final Unary object) {
		final String op = EGaml.getInstance().getKeyOf(object);
		if ("°".equals(op) || "#".equals(op)) return doSwitch(object.getRight());
		return null;
	}

	@Override
	public IExpressionDescription defaultCase(final EObject object) {
		return new EcoreBasedExpressionDescription(object);
	}

	/**
	 * Creates the.
	 *
	 * @param e
	 *            the e
	 * @param errors
	 *            the errors
	 * @return the i expression description
	 */
	public IExpressionDescription createBlock(final ISyntacticElement e) {
		return new BlockExpressionDescription(e);
	}

	/**
	 * Creates the.
	 *
	 * @param expr
	 *            the expr
	 * @return the i expression description
	 */
	public IExpressionDescription create(final EObject expr) {
		final IExpressionDescription result = doSwitch(expr);
		result.setTarget(expr);
		return result;
	}

}