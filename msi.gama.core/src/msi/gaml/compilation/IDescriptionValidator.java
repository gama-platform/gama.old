/*******************************************************************************************************
 *
 * IDescriptionValidator.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ImmutableSet;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.types.IType;
import msi.gaml.types.ITypesManager;
import msi.gaml.types.Types;

/**
 * Class IDescriptionValidator. This interface is intended to be used for individual validation of symbols. An instance
 * is typically known by a SymbolProto and called after the core of the validation has made its job.
 *
 * @author drogoul
 * @since 13 sept. 2013
 *
 */
@SuppressWarnings ({ "rawtypes" })
public interface IDescriptionValidator<T extends IDescription> extends IValidator {

	/** The reserved. */
	ImmutableSet<String> RESERVED = ImmutableSet
			.copyOf(new String[] { IKeyword.THE, IKeyword.FALSE, IKeyword.TRUE, IKeyword.NULL, IKeyword.MYSELF });

	/**
	 * Called at the end of the validation process. The enclosing description, the children and the facets of the
	 * description have all been already validated (and their expressions compiled), so everything is accessible here to
	 * make a finer validation with respect to the specificites of the symbol. This interface is not supposed to change
	 * the description unless it is absolutely necessary. It is supposed to attach warnings and errors to the
	 * description instead. Alternatively, developers may want to override validate(IDescription, EObject,
	 * IExpression[]), which allows to veto the validation by returning false
	 *
	 * @param description
	 */
	void validate(T description);

	/**
	 * In that particular implementation, arguments will always be empty. Returning false will veto the validation
	 * process
	 */
	@SuppressWarnings ("unchecked")
	@Override
	default boolean validate(final IDescription description, final EObject emfContext, final IExpression... arguments) {
		validate((T) description);
		return true;
	}

	/**
	 * Swap.
	 *
	 * @param desc
	 *            the desc
	 * @param oldFacet
	 *            the old
	 * @param newFacet
	 *            the next
	 */
	default void swap(final IDescription desc, final String oldFacet, final String newFacet) {
		if (desc.hasFacet(oldFacet)) {
			desc.setFacet(newFacet, desc.getFacet(oldFacet));
			desc.removeFacets(oldFacet);
		}
	}

	/**
	 * The Class Assert.
	 */
	public static class Assert {

		/**
		 * Types are compatible for assignment.
		 *
		 * @param facetName
		 *            the facet name
		 * @param context
		 *            the context
		 * @param receiverDescription
		 *            the receiver description
		 * @param receiverType
		 *            the receiver type
		 * @param assigned
		 *            the assigned
		 */
		public static void typesAreCompatibleForAssignment(final String facetName, final IDescription context,
				final String receiverDescription, final IType<?> receiverType, final IExpressionDescription assigned) {
			if (assigned == null) return;
			final IExpression value = assigned.getExpression();
			if (value == null) return;
			final IType assignedType = value.getGamlType();
			verifyIntFloatAndEmptyContainers(facetName, context, receiverDescription, receiverType, assigned, value,
					assignedType);
			// Contents Type
			if (receiverType.isContainer() && assignedType.isContainer()) {
				final IType receiverContentType = receiverType.getContentType();
				IType<?> contentType = assignedType.getContentType();
				// Special cases for the empty lists and maps
				if (Types.isEmptyContainerCase(receiverType, value) || speciesAreIncompatible(context, receiverType,
						assigned, value, receiverContentType, contentType))
					return;
				// Special case for maps and lists of pairs (Issue 846)
				if (receiverType.id() == IType.MAP && assignedType.id() == IType.LIST
						&& contentType.id() == IType.PAIR) {
					contentType = contentType.getContentType();
				}
				if (!contentType.isTranslatableInto(receiverContentType)
						|| Types.intFloatCase(receiverContentType, contentType)) {
					emitCastingWarning(facetName, context, receiverDescription, receiverType, assigned,
							receiverContentType, contentType);
				}
			}
		}

		private static void verifyIntFloatAndEmptyContainers(final String facetName, final IDescription context,
				final String receiverDescription, final IType<?> receiverType, final IExpressionDescription assigned,
				final IExpression value, final IType assignedType) {
			// AD: 6/9/13 special case for int and float (see Issue 590) and for
			// empty lists and maps
			if ((value != IExpressionFactory.NIL_EXPR
					&& !assignedType.getGamlType().isTranslatableInto(receiverType.getGamlType())
					|| Types.intFloatCase(receiverType, assignedType))
					&& !Types.isEmptyContainerCase(receiverType, value)) {
				final EObject target = assigned.getTarget();
				final String msg = receiverDescription + " of type " + receiverType.getGamlType()
						+ " is assigned a value of type " + assignedType.getGamlType() + ", which will be casted to "
						+ receiverType.getGamlType();
				if (target == null) {
					context.warning(msg, IGamlIssue.SHOULD_CAST, facetName, receiverType.toString());
				} else {
					context.warning(msg, IGamlIssue.SHOULD_CAST, target, receiverType.toString());
				}
			}
		}

		/**
		 * Emit casting warning.
		 *
		 * @param facetName
		 *            the facet name
		 * @param context
		 *            the context
		 * @param receiverDescription
		 *            the receiver description
		 * @param receiverType
		 *            the receiver type
		 * @param assigned
		 *            the assigned
		 * @param receiverContentType
		 *            the receiver content type
		 * @param contentType
		 *            the content type
		 */
		private static void emitCastingWarning(final String facetName, final IDescription context,
				final String receiverDescription, final IType<?> receiverType, final IExpressionDescription assigned,
				final IType receiverContentType, final IType<?> contentType) {
			final EObject target = assigned.getTarget();
			if (target == null) {
				context.warning("Elements of " + receiverDescription + " are of type " + receiverContentType
						+ " but are assigned elements of type " + contentType + ", which will be casted to "
						+ receiverContentType, IGamlIssue.SHOULD_CAST, facetName, receiverType.toString());
			} else {
				context.warning("Elements of " + receiverDescription + " are of type " + receiverContentType
						+ " but are assigned elements of type " + contentType + ", which will be casted to "
						+ receiverContentType, IGamlIssue.SHOULD_CAST, target, receiverType.toString());
			}
		}

		/**
		 * Species are incompatible.
		 *
		 * @param context
		 *            the context
		 * @param receiverType
		 *            the receiver type
		 * @param assigned
		 *            the assigned
		 * @param expr2
		 *            the expr 2
		 * @param receiverContentType
		 *            the receiver content type
		 * @param contentType
		 *            the content type
		 * @return true, if successful
		 */
		private static boolean speciesAreIncompatible(final IDescription context, final IType<?> receiverType,
				final IExpressionDescription assigned, final IExpression expr2, final IType receiverContentType,
				final IType<?> contentType) {
			// AD: 28/4/14 special case for variables of type species<xxx>
			if (expr2 != IExpressionFactory.NIL_EXPR && receiverType.getGamlType().id() == IType.SPECIES
					&& !contentType.isTranslatableInto(receiverContentType)) {
				context.error("Impossible assignment: " + contentType.getSpeciesName() + " is not a sub-species of "
						+ receiverContentType.getSpeciesName(), IGamlIssue.WRONG_TYPE, assigned.getTarget());
				return true;
			}
			return false;
		}

		/**
		 * Name is valid.
		 *
		 * @param cd
		 *            the cd
		 * @return true, if successful
		 */
		public static boolean nameIsValid(final IDescription cd) {
			final String name = cd.getName();
			if (name == null) {
				cd.error("The attribute 'name' is missing", IGamlIssue.MISSING_NAME);
				return false;
			}
			if (RESERVED.contains(name)) {
				final String type = "It cannot be used as a "
						+ (cd instanceof VariableDescription ? "variable" : cd.getKeyword()) + " name.";
				cd.error(name + " is a reserved keyword. " + type + " Reserved keywords are: " + RESERVED,
						IGamlIssue.IS_RESERVED, NAME, name);
				return false;
			}
			final ModelDescription md = cd.getModelDescription();
			final ITypesManager manager = md == null ? Types.builtInTypes : md.getTypesManager();
			final IType t = manager.get(name);
			if (t != Types.NO_TYPE) {
				final String type = "It cannot be used as a "
						+ (cd instanceof VariableDescription ? "variable" : cd.getKeyword()) + " name.";
				final String species = t.isAgentType() ? "species" : "type";
				cd.error(name + " is a " + species + " name. " + type, IGamlIssue.IS_A_TYPE, NAME, name);
				return false;
			}
			return true;
		}

	}

	/**
	 * The Class ValidNameValidator.
	 */
	public static class ValidNameValidator implements IDescriptionValidator {

		/**
		 * Verifies that the name is valid (non reserved, non type and non species)
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {
			Assert.nameIsValid(cd);
		}
	}

	/**
	 * The Class NullValidator.
	 */
	public static class NullValidator implements IDescriptionValidator {

		/**
		 * Verifies that the name is valid (non reserved, non type and non species)
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {}
	}

}
