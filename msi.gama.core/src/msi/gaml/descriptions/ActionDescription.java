package msi.gaml.descriptions;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class ActionDescription extends StatementWithChildrenDescription {

	protected final boolean isAbstract;

	public ActionDescription(final String keyword, final IDescription superDesc, final ChildrenProvider cp,
			final EObject source, final Facets facets) {
		super(keyword, superDesc, cp, true, source, facets, null);
		isAbstract = TRUE.equals(getLitteral(VIRTUAL));
		removeFacets(VIRTUAL);
	}

	@Override
	public ActionDescription copy(final IDescription into) {
		final ActionDescription desc = new ActionDescription(getKeyword(), into,
				children != null ? new ChildrenProvider(children) : ChildrenProvider.NONE, element, getFacetsCopy());
		desc.originName = getOriginName();
		return desc;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * @return
	 */
	public List<String> getArgNames() {
		return Lists.newArrayList(Iterables.transform(getFormalArgs(), TO_NAME));
	}

	public boolean verifyArgs(final IDescription caller, final Arguments names) {
		final Iterable<IDescription> formalArgs = getFormalArgs();

		if (Iterables.isEmpty(formalArgs) && names.isEmpty()) {
			return true;
		}
		final List<String> allArgs = getArgNames();
		if (caller.getKeyword().equals(DO)) {
			// If the names were not known at the time of the creation of the
			// caller, only the order
			if (names.containsKey("0")) {
				int index = 0;
				for (final String name : allArgs) {
					final String key = String.valueOf(index++);
					final IExpressionDescription old = names.get(key);
					if (old != null) {
						names.put(name, old);
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
						caller.error(
								"Missing argument " + n + " in call to " + getName() + ". Arguments passed are : "
										+ names,
								IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(null), new String[] { n });
						return false;
					}

				}
			}
		}
		// If one is missing in the arguments passed, we raise an error
		// (except for primitives for the moment)
		// if ( !getKeyword().equals(PRIMITIVE) ) {
		// AD: Change in the policy regarding primitives; if it is not stated,
		// the arguments are now considered as
		// optional.
		// for (final String arg : mandatoryArgs) {
		// if (!names.containsKey(arg)) {
		// caller.error(
		// "Missing argument " + arg + " in call to " + getName() + ". Arguments
		// passed are : " + names,
		// IGamlIssue.MISSING_ARGUMENT, caller.getUnderlyingElement(null), new
		// String[] { arg });
		// return false;
		// }
		// }
		// }
		for (final Map.Entry<String, IExpressionDescription> arg : names.entrySet()) {
			// A null value indicates a previous compilation error in the
			// arguments
			if (arg != null) {
				final String name = arg.getKey();
				if (!allArgs.contains(name)) {
					caller.error("Unknown argument " + name + " in call to " + getName(), IGamlIssue.UNKNOWN_ARGUMENT,
							arg.getValue().getTarget(), new String[] { arg.getKey() });
					return false;
				} else if (arg.getValue() != null && arg.getValue().getExpression() != null) {
					final IDescription formalArg = Iterables.find(formalArgs, input -> input.getName().equals(name));
					final IType formalType = formalArg.getType();
					final IType callerType = arg.getValue().getExpression().getType();
					if (Types.intFloatCase(formalType, callerType)) {
						caller.warning("The argument " + name + " (of type " + callerType + ") will be casted to "
								+ formalType, IGamlIssue.WRONG_TYPE, arg.getValue().getTarget());
					} else {
						boolean accepted = formalType == Types.NO_TYPE || callerType.isTranslatableInto(formalType);
						accepted = accepted || callerType == Types.NO_TYPE && formalType.getDefault() == null;
						if (!accepted) {
							caller.error("The type of argument " + name + " should be " + formalType,
									IGamlIssue.WRONG_TYPE);
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
			final String name = sd.getName();
			IExpression e = null;
			final IDescription superDesc = getEnclosingDescription();
			final IExpressionDescription ed = sd.getFacet(VALUE, DEFAULT);
			if (ed != null) {
				e = ed.compile(superDesc);
			}
			ca.put(name, e);
		}
		return ca;

	}

}
