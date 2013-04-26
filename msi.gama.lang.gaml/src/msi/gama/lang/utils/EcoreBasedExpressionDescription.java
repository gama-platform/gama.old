/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import org.eclipse.emf.ecore.EObject;

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
	public String toString() {
		return expression == null ? EGaml.toString(target) : super.toString();
	}

	@Override
	public Set<String> getStrings(IDescription context, boolean skills) {
		String type = skills ? "skill" : "attribute";
		if ( target == null ) { return Collections.EMPTY_SET; }
		if ( !(target instanceof Array) ) {
			if ( target instanceof VariableRef ) {
				String skillName = EGaml.getKey.caseVariableRef((VariableRef) target);
				context.warning(type + "s should be provided as a list of identifiers, for instance [" + skillName +
					"]", IGamlIssue.AS_ARRAY, target, skillName);
				if ( skills && !AbstractGamlAdditions.getSkillClasses().containsKey(skillName) ) {
					context.error("Unknown " + type + " " + skillName, IGamlIssue.UNKNOWN_SKILL, target);
				}
				return new HashSet(Arrays.asList(skillName));
			}
			if ( target instanceof Expression ) {
				context.error("Impossible to recognize valid " + type + "s in " + EGaml.toString(target), skills
					? IGamlIssue.UNKNOWN_SKILL : IGamlIssue.UNKNOWN_VAR, target);
			} else {
				context
					.error(type + "s should be provided as a list of identifiers.", IGamlIssue.UNKNOWN_SKILL, target);
			}
			return Collections.EMPTY_SET;
		}
		Set<String> result = new HashSet();
		Array array = (Array) target;
		for ( Expression expr : EGaml.getExprsOf(array.getExprs()) ) {
			String name = EGaml.getKeyOf(expr);
			if ( skills && !AbstractGamlAdditions.getSkillClasses().containsKey(name) ) {
				context.error("Unknown " + type + " " + name, skills ? IGamlIssue.UNKNOWN_SKILL
					: IGamlIssue.UNKNOWN_VAR, expr);
			} else {
				result.add(name);
			}
		}
		return result;
	}

	static final GamlSwitch<IExpressionDescription> getExpr = new GamlSwitch() {

		@Override
		public IExpressionDescription caseIntLiteral(IntLiteral object) {
			return ConstantExpressionDescription.create(Integer.parseInt(object.getOp()));
		}

		@Override
		public IExpressionDescription caseDoubleLiteral(DoubleLiteral object) {
			return ConstantExpressionDescription.create(Double.parseDouble(object.getOp()));
		}

		@Override
		public IExpressionDescription caseStringLiteral(StringLiteral object) {
			return LabelExpressionDescription.create(object.getOp());
		}

		@Override
		public IExpressionDescription caseBooleanLiteral(BooleanLiteral object) {
			return ConstantExpressionDescription.create(object.getOp().equals(IKeyword.TRUE));
		}

		@Override
		public IExpressionDescription defaultCase(EObject object) {
			return new EcoreBasedExpressionDescription(object);
		}

	};

	public static IExpressionDescription create(EObject expr) {
		IExpressionDescription result = getExpr.doSwitch(expr);
		result.setTarget(expr);
		return result;
	}
}
