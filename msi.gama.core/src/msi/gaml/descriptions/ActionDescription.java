/*******************************************************************************************************
 *
 * msi.gaml.descriptions.ActionDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class ActionDescription extends StatementWithChildrenDescription {

	protected final boolean isAbstract;
	protected final boolean isSynthetic;
	public static Arguments NULL_ARGS = new Arguments();

	public ActionDescription(final String keyword, final IDescription superDesc, final Iterable<IDescription> cp,
			final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, true, source, facets, null);
		isAbstract = TRUE.equals(getLitteral(VIRTUAL));
		isSynthetic = getName() != null && getName().startsWith(SYNTHETIC);
		removeFacets(VIRTUAL);
	}

	@Override
	public ActionDescription copy(final IDescription into) {
		final ActionDescription desc = new ActionDescription(getKeyword(), into, children, element, getFacetsCopy());
		desc.originName = getOriginName();
		return desc;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean isBuiltIn() {
		return super.isBuiltIn() && !isSynthetic;
	}

	@Override
	protected boolean isSynthetic() {
		return isSynthetic;
	}

	/**
	 * @return
	 */
	public List<String> getArgNames() {
		return Lists.newArrayList(Iterables.transform(getFormalArgs(), TO_NAME));
	}

	@SuppressWarnings ("rawtypes")
	public boolean verifyArgs(final IDescription caller, final Arguments args) {
		final Arguments names = args == null ? NULL_ARGS : args;
		final Iterable<IDescription> formalArgs = getFormalArgs();
		final boolean noArgs = names.isEmpty();
		if (noArgs) {
			final Iterable<IDescription> formalArgsWithoutDefault =
					Iterables.filter(formalArgs, each -> !each.hasFacet(DEFAULT));
			if (Iterables.isEmpty(formalArgsWithoutDefault)) { return true; }
		}

		final List<String> allArgs = getArgNames();
		if (caller.getKeyword().equals(DO) || caller.getKeyword().equals(INVOKE)) {
			// If the names were not known at the time of the creation of the
			// caller, only the order
			if (names.containsKey("0")) {
				int index = 0;
				for (final String the_name : allArgs) {
					final String key = String.valueOf(index++);
					final IExpressionDescription old = names.get(key);
					if (old != null) {
						names.put(the_name, old);
						names.remove(key);
					}
				}
			}
		}

		// We compute the list of mandatory args

		if (formalArgs != null) {
			for (final IDescription c : formalArgs) {
				final String n = c.getName();
				if (c.hasFacet(DEFAULT)) {
					// AD: we compile the default (which is, otherwise, not
					// computed before validation
					c.getFacet(DEFAULT).compile(this);
					continue;
				}
				if (c.hasFacet(OPTIONAL) && c.getFacet(OPTIONAL).equalsString(FALSE) || !c.hasFacet(OPTIONAL)) {
					if (!names.containsKey(n)) {
						caller.error("Missing argument " + n + " in call to " + getName() + ". Arguments passed are : "
								+ names, IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(), n);
						return false;
					}

				}
			}
		}

		for (final Facet arg : names.getFacets()) {
			// A null value indicates a previous compilation error in the
			// arguments
			if (arg != null) {
				final String the_name = arg.key;
				if (!allArgs.contains(the_name)) {
					caller.error("Unknown argument " + the_name + " in call to " + getName(),
							IGamlIssue.UNKNOWN_ARGUMENT, arg.value.getTarget(), arg.key);
					return false;
				} else if (arg.value != null && arg.value.getExpression() != null) {
					final IDescription formalArg =
							Iterables.find(formalArgs, input -> input.getName().equals(the_name));
					final IType<?> formalType = formalArg.getGamlType();
					final IType<?> callerType = arg.value.getExpression().getGamlType();
					if (Types.intFloatCase(formalType, callerType)) {
						caller.warning("The argument " + the_name + " (of type " + callerType + ") will be casted to "
								+ formalType, IGamlIssue.WRONG_TYPE, arg.value.getTarget());
					} else {
						boolean accepted = formalType == Types.NO_TYPE || callerType.isTranslatableInto(formalType);
						accepted = accepted || callerType == Types.NO_TYPE && formalType.getDefault() == null;
						if (!accepted) {
							caller.error("The type of argument " + the_name + " should be " + formalType,
									IGamlIssue.WRONG_TYPE, arg.value.getTarget());
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public boolean containsArg(final String s) {
		final IDescription formalArg = Iterables.find(getFormalArgs(), input -> input.getName().equals(s));
		return formalArg != null;
	}

	@Override
	public Arguments createCompiledArgs() {
		final Arguments ca = new Arguments();
		for (final IDescription sd : getFormalArgs()) {
			final String the_name = sd.getName();
			IExpression e = null;
			final IDescription superDesc = getEnclosingDescription();
			final IExpressionDescription ed = sd.getFacet(VALUE, DEFAULT);
			if (ed != null) {
				e = ed.compile(superDesc);
			}
			ca.put(the_name, e);
		}
		return ca;

	}

	@Override
	public String getDocumentation() {
		return getArgDocumentation() + super.getDocumentation();
	}

	public String getArgDocumentation() {
		final StringBuilder sb = new StringBuilder(200);

		if (getArgNames().size() > 0) {
			final List<String> args = ImmutableList.copyOf(Iterables.transform(getFormalArgs(), desc -> {
				final StringBuilder sb1 = new StringBuilder(100);
				sb1.append("<li><b>").append(Strings.TAB).append(desc.getName()).append("</b>, type ")
						.append(desc.getGamlType());
				if (desc.hasFacet(IKeyword.DEFAULT)) {
					sb1.append(" <i>(default: ").append(desc.getFacetExpr(IKeyword.DEFAULT).serialize(false))
							.append(")</i>");
				}
				sb1.append("</li>");

				return sb1.toString();
			}));
			sb.append("Arguments accepted: ").append("<br/><ul>");
			for (final String a : args) {
				sb.append(a);
			}
			sb.append("</ul><br/>");
		}
		return sb.toString();
	}

	// @Override
	// public TypeDescription getEnclosingDescription() {
	// return (TypeDescription) super.getEnclosingDescription();
	// }

	@Override
	public String getTitle() {
		return super.getTitle() + getShortDescription();
	}

	public String getShortDescription() {
		final String returns = getGamlType().equals(Types.NO_TYPE) ? ", no value returned"
				: ", returns a result of type " + getGamlType().getTitle();
		final StringBuilder args = new StringBuilder();
		for (final IDescription desc : getFormalArgs()) {
			args.append(desc.getGamlType()).append(" ").append(desc.getName()).append(", ");
		}
		if (args.length() > 0) {
			args.setLength(args.length() - 2);
		}

		return "(" + args.toString() + ")" + returns;

	}

}
