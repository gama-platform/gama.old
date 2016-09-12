/*********************************************************************************************
 * 
 * 
 * 'IDescriptionValidator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class IDescriptionValidator. This interface is intended to be used for
 * individual validation of symbols. An instance is typically known by a
 * SymbolProto and called after the core of the validation has made its job.
 * 
 * @author drogoul
 * @since 13 sept. 2013
 * 
 */
public interface IDescriptionValidator<T extends IDescription> extends IKeyword {

	public static final ImmutableSet<String> RESERVED = ImmutableSet
			.copyOf(new String[] { IKeyword.THE, IKeyword.FALSE, IKeyword.TRUE, IKeyword.NULL, IKeyword.MYSELF });

	/**
	 * Called at the end of the validation process. The enclosing description,
	 * the children and the facets of the description have all been already
	 * validated (and their expressions compiled), so everything is accessible
	 * here to make a finer validation with respect to the specificites of the
	 * symbol. This interface is not supposed to change the description unless
	 * it is absolutely necessary. It is supposed to attach warnings and errors
	 * to the description instead.
	 * 
	 * @param description
	 */
	public void validate(T description);

	public static class Assert {

		public static void typesAreCompatibleForAssignment(final IDescription context, final String receiverDescription,
				final IType receiverType, final IExpressionDescription assigned) {
			if (assigned == null) {
				return;
			}
			// IExpression expr1 = receiver.getExpression();
			final IExpression expr2 = assigned.getExpression();
			if (expr2 == null) {
				return;
			}
			// IType receiverType = expr1.getType();
			final IType assignedType = expr2.getType();

			// AD: 6/9/13 special case for int and float (see Issue 590) and for
			// empty lists and maps
			if (expr2 != IExpressionFactory.NIL_EXPR
					&& !assignedType.getType().isTranslatableInto(receiverType.getType())
					|| Types.intFloatCase(receiverType, assignedType)) {
				if (!Types.mapListCase(receiverType, assignedType, expr2)) {
					context.warning(
							receiverDescription + " of type " + receiverType.getType() + " is assigned a value of type "
									+ assignedType.getType() + ", which will be casted to " + receiverType.getType(),
							IGamlIssue.SHOULD_CAST, assigned.getTarget(), receiverType.toString());
				}
			}
			// Contents Type
			if (receiverType.isContainer() && assignedType.isContainer()) {
				final IType receiverContentType = receiverType.getContentType();
				IType contentType = assignedType.getContentType();
				// Special cases for the empty lists and maps
				if (contentType == Types.NO_TYPE) {
					if (expr2.isConst() && (assignedType.id() == IType.LIST || assignedType.id() == IType.MAP)) {
						final IContainer c = (IContainer) expr2.value(null);
						if (c.isEmpty(null)) {
							return;
						}
					}
					// if ( expr2 instanceof ListExpression && ((ListExpression)
					// expr2).getElements().length == 0 ) {
					// return; }
					// if ( expr2 instanceof MapExpression && ((MapExpression)
					// expr2).keysArray().length == 0 ) {
					// return; }
				}
				// AD: 28/4/14 special case for variables of type species<xxx>
				if (expr2 != IExpressionFactory.NIL_EXPR && receiverType.getType().id() == IType.SPECIES) {
					if (!contentType.isTranslatableInto(receiverContentType)) {
						context.error(
								"Impossible assignment: " + contentType.getSpeciesName() + " is not a sub-species of "
										+ receiverContentType.getSpeciesName(),
								IGamlIssue.WRONG_TYPE, assigned.getTarget());
						return;
					}
				}

				// Special case for maps and lists of pairs (Issue 846)
				if (receiverType.id() == IType.MAP && assignedType.id() == IType.LIST
						&& contentType.id() == IType.PAIR) {
					contentType = contentType.getContentType();
				}
				if (!contentType.isTranslatableInto(receiverContentType)
						|| Types.intFloatCase(receiverContentType, contentType)) {
					context.warning(
							"Elements of " + receiverDescription + " are of type " + receiverContentType
									+ " but are assigned elements of type " + contentType + ", which will be casted to "
									+ receiverContentType,
							IGamlIssue.SHOULD_CAST, assigned.getTarget(), receiverType.toString());
				}
			}
		}

		public static boolean nameIsValid(final IDescription cd) {
			final String name = cd.getName();
			if (name == null) {
				cd.error("The attribute 'name' is missing", IGamlIssue.MISSING_NAME);
				return false;
			} else if (RESERVED.contains(name)) {
				final String type = "It cannot be used as a "
						+ (cd instanceof VariableDescription ? "variable" : cd.getKeyword()) + " name.";
				cd.error(name + " is a reserved keyword. " + type + " Reserved keywords are: " + RESERVED,
						IGamlIssue.IS_RESERVED, NAME, name);
				return false;
			} else {
				final IType t = cd.getModelDescription().getTypesManager().get(name);
				if (t != Types.NO_TYPE) {
					final String type = "It cannot be used as a "
							+ (cd instanceof VariableDescription ? "variable" : cd.getKeyword()) + " name.";
					final String species = t.isAgentType() ? "species" : "type";
					cd.error(name + " is a " + species + " name. " + type, IGamlIssue.IS_A_TYPE, NAME, name);
					return false;
				}
			}
			return true;
		}

	}

	public static class ValidNameValidator implements IDescriptionValidator {

		/**
		 * Verifies that the name is valid (non reserved, non type and non
		 * species)
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			Assert.nameIsValid(cd);
		}
	}

	public static class NullValidator implements IDescriptionValidator {

		/**
		 * Verifies that the name is valid (non reserved, non type and non
		 * species)
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
		}
	}

}
