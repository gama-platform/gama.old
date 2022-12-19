/*******************************************************************************************************
 *
 * GamaMessageType.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class GamaMessageType.
 */
@type (
		name = GamaMessageType.MESSAGE_STR,
		id = IType.MESSAGE,
		wraps = { GamaMessage.class },
		kind = ISymbolKind.Variable.REGULAR,
		doc = @doc ("Represents the messages exchanged between agents"))
public class GamaMessageType extends GamaType<GamaMessage> {

	/** The Constant MESSAGE_STR. */
	public static final String MESSAGE_STR = "message";

	/**
	 * Instantiates a new gama message type.
	 */
	public GamaMessageType() {}

	@Override
	public GamaMessage getDefault() { return null; }

	@Override
	protected boolean acceptNullInstances() {
		return true;
	}

	/**
	 * As message.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @return the gama message
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@operator (
			value = GamaMessageType.MESSAGE_STR,
			can_be_const = true,
			category = { IOperatorCategory.FIPA },
			concept = { IConcept.FIPA })
	@doc (
			value = "defines a new message that will be exchanged between agents.")
	@no_test
	public static GamaMessage asMessage(final IScope scope, final Object val) throws GamaRuntimeException {
		return GamaMessageType.staticCast(scope, val, null);
	}

	/**
	 * Static cast.
	 *
	 * @param scope
	 *            the scope
	 * @param val
	 *            the val
	 * @param object
	 *            the object
	 * @return the gama message
	 */
	private static GamaMessage staticCast(final IScope scope, final Object val, final Object object) {

		if (val instanceof GamaMessage) return (GamaMessage) val;
		// TODO AD ??? ??? Demander au skill la classe de message à produire !
		return new GamaMessage(scope, scope.getAgent(), null, val);
	}

	@Override
	@doc ("Returns a message built from the argument. If the argument is already a message returns it, otherwise returns a message with the current agent as the sender and the argument as the contents ")
	public GamaMessage cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}
}
