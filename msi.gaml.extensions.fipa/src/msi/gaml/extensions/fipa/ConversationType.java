/*********************************************************************************************
 *
 *
 * 'ConversationType.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.GamaContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@type (
		name = ConversationType.CONVERSATION_STR,
		id = ConversationType.CONV_ID,
		wraps = { Conversation.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.FIPA },
		doc = @doc ("Represents a list of the messages exchanged by agents"))
public class ConversationType extends GamaContainerType<Conversation> {

	public final static String CONVERSATION_STR = "conversation";
	public final static int CONV_ID = 98;

	public ConversationType() {}

	@Override
	public Conversation cast(final IScope scope, final Object obj, final Object param, final IType<?> keyType,
			final IType<?> contentType, final boolean copy) throws GamaRuntimeException {
		if (obj instanceof Conversation) { return (Conversation) obj; }
		// if ( obj instanceof Message ) { return new
		// Conversation(FIPAConstants.Protocols.NO_PROTOCOL, (Message) obj); }
		return null;
	}

	@Override
	public IType<FIPAMessage> getContentType() {
		return Types.get(FIPAMessage.class);
	}

	@operator (
			value = ConversationType.CONVERSATION_STR,
			can_be_const = true,
			category = { IOperatorCategory.FIPA },
			concept = { IConcept.FIPA },
			doc = @doc ("Converts the operand into a conversation (if applicable) or retrieves the conversation of the message passed. Otherwise returns nil"))
	// @doc(value = "to be added", comment = "", special_cases = { "" },
	// examples = { })
	@no_test
	public static Conversation asMessage(final IScope scope, final Object val) throws GamaRuntimeException {
		return ConversationType.staticCast(scope, val, null);
	}

	public static Conversation staticCast(final IScope scope, final Object val, final Object object) {
		if (val instanceof Conversation) { return (Conversation) val; }
		if (val instanceof FIPAMessage) { return ((FIPAMessage) val).getConversation(); }
		// ???
		return null;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}
}
