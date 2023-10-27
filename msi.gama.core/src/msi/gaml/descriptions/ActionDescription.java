/*******************************************************************************************************
 *
 * ActionDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import msi.gaml.expressions.IExpression;
import msi.gaml.interfaces.IGamlIssue;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class ActionDescription.
 */
public class ActionDescription extends StatementWithChildrenDescription {

	/** The Constant NULL_ARGS. */
	public static final Arguments NULL_ARGS = new Arguments();

	/**
	 * Instantiates a new action description.
	 *
	 * @param keyword
	 *            the keyword
	 * @param superDesc
	 *            the super desc
	 * @param cp
	 *            the cp
	 * @param source
	 *            the source
	 * @param facets
	 *            the facets
	 */
	public ActionDescription(final String keyword, final IDescription superDesc, final Iterable<IDescription> cp,
			final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, true, source, facets, null);
		setIf(Flag.Abstract, TRUE.equals(getLitteral(VIRTUAL)));
		if (getName() != null && getName().startsWith(SYNTHETIC)) {
			set(Flag.Synthetic);
			unSet(Flag.BuiltIn);
		}
		removeFacets(VIRTUAL);
	}

	@Override
	public ActionDescription copy(final IDescription into) {
		final ActionDescription desc = new ActionDescription(getKeyword(), into, children, element, getFacetsCopy());
		desc.originName = getOriginName();
		return desc;
	}

	/**
	 * Checks if is abstract.
	 *
	 * @return true, if is abstract
	 */
	public boolean isAbstract() { return isSet(Flag.Abstract); }

	@Override
	protected boolean isSynthetic() { return isSet(Flag.Synthetic); }

	/**
	 * @return
	 */
	public List<String> getArgNames() { return Lists.newArrayList(Iterables.transform(getFormalArgs(), TO_NAME)); }

	/**
	 * Verify args.
	 *
	 * @param caller
	 *            the caller
	 * @param args
	 *            the args
	 * @return true, if successful
	 */
	@SuppressWarnings ("rawtypes")
	public boolean verifyArgs(final IDescription caller, final Arguments args) {
		final Arguments names = args == null ? NULL_ARGS : args;
		final Iterable<IDescription> formalArgs = getFormalArgs();
		final boolean noArgs = names.isEmpty();
		if (noArgs) {
			final Iterable<IDescription> formalArgsWithoutDefault =
					Iterables.filter(formalArgs, each -> !each.hasFacet(DEFAULT));
			if (Iterables.isEmpty(formalArgsWithoutDefault)) return true;
		}

		final List<String> allArgs = getArgNames();
		// If the names were not known at the time of the creation of the
		// caller, only the order
		if ((DO.equals(caller.getKeyword()) || INVOKE.equals(caller.getKeyword())) && names.containsKey("0")) {
			replaceNumberedArgs(names, allArgs);
		}

		// We compute the list of mandatory args

		if (formalArgs != null && !verifyMandatoryArgs(caller, names, formalArgs)) return false;

		for (final Facet arg : names.getFacets()) {
			// A null value indicates a previous compilation error in the
			// arguments
			if (arg != null) {
				final String the_name = arg.key;
				if (!allArgs.contains(the_name)) {
					caller.error("Unknown argument " + the_name + " in call to " + getName(),
							IGamlIssue.UNKNOWN_ARGUMENT, arg.value.getTarget(), arg.key);
					return false;
				}
				if (arg.value != null && arg.value.getExpression() != null) {
					final IDescription formalArg =
							Iterables.find(formalArgs, input -> input.getName().equals(the_name));
					if (formalArg.isID()) { continue; }
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

	/**
	 * Verify mandatory args.
	 *
	 * @param caller
	 *            the caller
	 * @param names
	 *            the names
	 * @param formalArgs
	 *            the formal args
	 * @return true, if successful
	 */
	private boolean verifyMandatoryArgs(final IDescription caller, final Arguments names,
			final Iterable<IDescription> formalArgs) {
		for (final IDescription c : formalArgs) {
			final String n = c.getName();
			if (c.hasFacet(DEFAULT)) {
				// AD: we compile the default (which is, otherwise, not
				// computed before validation
				c.getFacet(DEFAULT).compile(this);
				continue;
			}
			if ((c.hasFacet(OPTIONAL) && c.getFacet(OPTIONAL).equalsString(FALSE) || !c.hasFacet(OPTIONAL))
					&& !names.containsKey(n)) {
				caller.error("Missing argument " + n + " in call to " + getName() + ". Arguments passed are : " + names,
						IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(), n);
				return false;
			}
		}
		return true;
	}

	/**
	 * Replace numbered args.
	 *
	 * @param names
	 *            the names
	 * @param allArgs
	 *            the all args
	 */
	private void replaceNumberedArgs(final Arguments names, final List<String> allArgs) {
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

	/**
	 * Contains arg.
	 *
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
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
			if (ed != null) { e = ed.compile(superDesc); }
			ca.put(the_name, e);
		}
		return ca;

	}

	@Override
	public Doc getDocumentation() {
		Doc documentation = getShortDocumentation(false);

		if (getArgNames().size() > 0) {
			getFormalArgs().forEach(arg -> {
				final StringBuilder sb1 = new StringBuilder(100);
				String name = arg.getName();
				sb1.append(arg.getGamlType());
				if (arg.hasFacet(DEFAULT) && arg.getFacetExpr(DEFAULT) != null) {
					sb1.append(" <i>(default: ").append(arg.getFacetExpr(DEFAULT).serialize(false)).append(")</i>");
				}
				documentation.set("Arguments accepted: ", name, new ConstantDoc(sb1.toString()));
			});
		}
		return documentation;

	}

	/**
	 * Gets the short documentation.
	 *
	 * @return the short documentation
	 */
	public Doc getShortDocumentation(final boolean withArgs) {
		Doc result = new RegularDoc();
		Iterable<IDescription> args = getFormalArgs();
		if (withArgs && Iterables.size(args) > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			for (final IDescription desc : args) {
				sb.append(desc.getGamlType().toString()).append(" ").append(desc.getName()).append(", ");
			}
			if (sb.length() > 0) { sb.setLength(sb.length() - 2); }
			sb.append(")");
			result.append(sb.toString());
		} else {
			result.append("no arguments");
		}
		final String returns = getGamlType().equals(Types.NO_TYPE) ? ", no value returned"
				: ", returns a result of type " + getGamlType().getTitle();
		result.append(returns);
		final String doc = getBuiltInDoc();
		if (doc != null && !doc.isBlank()) { result.append(". ").append(doc); }
		result.append("<br/>");
		return result;
	}

	/**
	 * Gets the built in doc.
	 *
	 * @return the built in doc
	 */
	protected String getBuiltInDoc() { return null; }

}
