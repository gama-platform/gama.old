/*********************************************************************************************
 *
 *
 * 'EcoreBasedExpressionDescription.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.descriptions.BasicExpressionDescription;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.factories.DescriptionFactory;

/**
 * The class EcoreBasedExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public class EcoreBasedExpressionDescription extends BasicExpressionDescription {

	protected EcoreBasedExpressionDescription(final EObject exp) {
		super(exp);
	}

	@Override
	public IExpressionDescription cleanCopy() {
		return new EcoreBasedExpressionDescription(target);
	}

	@Override
	public String toOwnString() {
		return EGaml.toString(target);
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		if (target == null) {
			return Collections.EMPTY_SET;
		}
		if (!(target instanceof Array)) {
			final String type = skills ? "skill" : "attribute";

			if (target instanceof VariableRef) {
				final String skillName = EGaml.getKeyOf(target);
				context.warning(
						type + "s should be provided as a list of identifiers, for instance [" + skillName + "]",
						IGamlIssue.AS_ARRAY, target, skillName);
				if (skills && !AbstractGamlAdditions.getSkillClasses().containsKey(skillName)) {
					context.error("Unknown " + type + " " + skillName, IGamlIssue.UNKNOWN_SKILL, target);
				}
				return new HashSet(Arrays.asList(skillName));
			}
			if (target instanceof Expression) {
				context.error("Impossible to recognize valid " + type + "s in " + EGaml.toString(target),
						skills ? IGamlIssue.UNKNOWN_SKILL : IGamlIssue.UNKNOWN_VAR, target);
			} else {
				context.error(type + "s should be provided as a list of identifiers.", IGamlIssue.UNKNOWN_SKILL,
						target);
			}
			return Collections.EMPTY_SET;
		}
		final Set<String> result = new HashSet();
		final Array array = (Array) target;
		for (final Expression expr : EGaml.getExprsOf(array.getExprs())) {
			final String type = skills ? "skill" : "attribute";

			final String name = EGaml.getKeyOf(expr);
			if (skills && !AbstractGamlAdditions.getSkillClasses().containsKey(name)) {
				context.error("Unknown " + type + " " + name, IGamlIssue.UNKNOWN_SKILL, expr);
			} else {
				result.add(name);
			}
		}
		return result;
	}

	static final class ExpressionBuilder extends GamlSwitch<IExpressionDescription> {

		private final Set<Diagnostic> currentErrors;

		ExpressionBuilder(final Set<Diagnostic> errors) {
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
			final IExpressionDescription ed = ConstantExpressionDescription
					.create(object.getOp().equals(IKeyword.TRUE));
			DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
			return ed;
		}

		@Override
		public IExpressionDescription defaultCase(final EObject object) {
			return new EcoreBasedExpressionDescription(object);
		}

	}

	public static IExpressionDescription create(final ISyntacticElement e, final Set<Diagnostic> errors) {
		final IExpressionDescription ed = new BlockExpressionDescription(e);
		return ed;
	}

	public static IExpressionDescription create(final EObject expr, final Set<Diagnostic> errors) {
		final IExpressionDescription result = new ExpressionBuilder(errors).doSwitch(expr);
		result.setTarget(expr);
		return result;
	}
}
