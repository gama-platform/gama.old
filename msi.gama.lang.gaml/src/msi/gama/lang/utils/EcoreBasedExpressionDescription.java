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

import java.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.*;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;
import msi.gama.common.interfaces.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import msi.gaml.factories.DescriptionFactory;

/**
 * The class EcoreBasedExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public class EcoreBasedExpressionDescription extends BasicExpressionDescription {

	private EcoreBasedExpressionDescription(final EObject exp) {
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
		if ( target == null ) { return Collections.EMPTY_SET; }
		if ( !(target instanceof Array) ) {
			String type = skills ? "skill" : "attribute";

			if ( target instanceof VariableRef ) {
				String skillName = EGaml.getKey.caseVariableRef((VariableRef) target);
				context.warning(
					type + "s should be provided as a list of identifiers, for instance [" + skillName + "]",
					IGamlIssue.AS_ARRAY, target, skillName);
				if ( skills && !AbstractGamlAdditions.getSkillClasses().containsKey(skillName) ) {
					context.error("Unknown " + type + " " + skillName, IGamlIssue.UNKNOWN_SKILL, target);
				}
				return new HashSet(Arrays.asList(skillName));
			}
			if ( target instanceof Expression ) {
				context.error("Impossible to recognize valid " + type + "s in " + EGaml.toString(target),
					skills ? IGamlIssue.UNKNOWN_SKILL : IGamlIssue.UNKNOWN_VAR, target);
			} else {
				context.error(type + "s should be provided as a list of identifiers.", IGamlIssue.UNKNOWN_SKILL,
					target);
			}
			return Collections.EMPTY_SET;
		}
		Set<String> result = new HashSet();
		Array array = (Array) target;
		for ( Expression expr : EGaml.getExprsOf(array.getExprs()) ) {
			String type = skills ? "skill" : "attribute";

			String name = EGaml.getKeyOf(expr);
			if ( skills && !AbstractGamlAdditions.getSkillClasses().containsKey(name) ) {
				context.error("Unknown " + type + " " + name,
					skills ? IGamlIssue.UNKNOWN_SKILL : IGamlIssue.UNKNOWN_VAR, expr);
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
			} catch (NumberFormatException e) {
				Diagnostic d = new EObjectDiagnosticImpl(Severity.WARNING, "",
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
			} catch (NumberFormatException e) {
				Diagnostic d = new EObjectDiagnosticImpl(Severity.WARNING, "",
					"Impossible to parse this float value, automatically set to 0.0", object, null, 0, null);
				currentErrors.add(d);
				ed = ConstantExpressionDescription.create(0d);
			}
			DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
			return ed;
		}

		@Override
		public IExpressionDescription caseStringLiteral(final StringLiteral object) {
			IExpressionDescription ed = ConstantExpressionDescription.create(object.getOp());

			// AD: Change 14/11/14
			// IExpressionDescription ed = LabelExpressionDescription.create(object.getOp());
			DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
			return ed;
		}

		@Override
		public IExpressionDescription caseBooleanLiteral(final BooleanLiteral object) {
			IExpressionDescription ed = ConstantExpressionDescription.create(object.getOp().equals(IKeyword.TRUE));
			DescriptionFactory.setGamlDocumentation(object, ed.getExpression());
			return ed;
		}

		@Override
		public IExpressionDescription defaultCase(final EObject object) {
			return new EcoreBasedExpressionDescription(object);
		}

	}

	public static IExpressionDescription create(final EObject expr, final Set<Diagnostic> errors) {
		IExpressionDescription result = new ExpressionBuilder(errors).doSwitch(expr);

		result.setTarget(expr);
		return result;
	}
}
