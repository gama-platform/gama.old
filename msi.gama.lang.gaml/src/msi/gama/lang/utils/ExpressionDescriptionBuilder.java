package msi.gama.lang.utils;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.Unary;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.IUnits;

public class ExpressionDescriptionBuilder extends GamlSwitch<IExpressionDescription> {

	private Set<Diagnostic> currentErrors;

	void setErrors(final Set<Diagnostic> errors) {
		currentErrors = errors;
	}

	@Override
	public IExpressionDescription caseIntLiteral(final IntLiteral object) {
		IExpressionDescription ed = null;
		try {
			ed = ConstantExpressionDescription.create(Integer.parseInt(object.getOp()));
		} catch (final NumberFormatException e) {
			final Diagnostic d = new EObjectDiagnosticImpl(Severity.WARNING, "",
					"Impossible to parse this int value, automatically set to 0", object, null, 0, null);
			currentErrors.add(d);
			ed = ConstantExpressionDescription.create(0);
		}
		DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseDoubleLiteral(final DoubleLiteral object) {
		IExpressionDescription ed = null;
		try {
			ed = ConstantExpressionDescription.create(Double.parseDouble(object.getOp()));
		} catch (final NumberFormatException e) {
			final Diagnostic d = new EObjectDiagnosticImpl(Severity.WARNING, "",
					"Impossible to parse this float value, automatically set to 0.0", object, null, 0, null);
			currentErrors.add(d);
			ed = ConstantExpressionDescription.create(0d);
		}
		DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseStringLiteral(final StringLiteral object) {
		final IExpressionDescription ed = ConstantExpressionDescription.create(object.getOp());

		// AD: Change 14/11/14
		// IExpressionDescription ed =
		// LabelExpressionDescription.create(object.getOp());
		DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
		return ed;
	}

	@Override
	public IExpressionDescription caseBooleanLiteral(final BooleanLiteral object) {
		final IExpressionDescription ed = ConstantExpressionDescription.create(object.getOp().equals(IKeyword.TRUE));
		DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
		return ed;
	}

	static int count;

	@Override
	public IExpressionDescription caseUnitName(final UnitName object) {
		final String s = EGaml.getKeyOf(object);
		if (IUnits.UNITS_EXPR.containsKey(s)) {
			// System.out.println("Saved " + ++count + " instance of units");
			return IUnits.UNITS_EXPR.get(s);
		}
		return null;
	}

	@Override
	public IExpressionDescription caseUnary(final Unary object) {
		final String op = EGaml.getKeyOf(object);
		if (op.equals("°") || op.equals("#")) {
			return doSwitch(object.getRight());
		}
		return null;
	}

	@Override
	public IExpressionDescription defaultCase(final EObject object) {
		return new EcoreBasedExpressionDescription(object);
	}

	public static IExpressionDescription create(final ISyntacticElement e, final Set<Diagnostic> errors) {
		final IExpressionDescription ed = new BlockExpressionDescription(e);
		return ed;
	}

	public static IExpressionDescription create(final EObject expr, final Set<Diagnostic> errors) {
		try {
			BUILDER.setErrors(errors);
			final IExpressionDescription result = BUILDER.doSwitch(expr);
			result.setTarget(expr);
			return result;
		} finally {
			BUILDER.setErrors(null);
		}
	}

	public static final ExpressionDescriptionBuilder BUILDER = new ExpressionDescriptionBuilder();

}