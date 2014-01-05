package msi.gaml.extensions.fipa;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

@type(name = ConversationType.CONVERSATION_STR, id = ConversationType.CONV_ID, wraps = { Conversation.class }, kind = ISymbolKind.Variable.CONTAINER)
public class ConversationType extends GamaContainerType<Conversation> {

	public final static String CONVERSATION_STR = "conversation";
	public final static short CONV_ID = 98;

	public ConversationType() {}

	@Override
	public Conversation cast(final IScope scope, final Object obj, final Object param, IType contentsType)
		throws GamaRuntimeException {
		if ( obj instanceof Conversation ) { return (Conversation) obj; }
		// if ( obj instanceof Message ) { return new
		// Conversation(FIPAConstants.Protocols.NO_PROTOCOL, (Message) obj); }
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(Message.class);
	}

	@Override
	public IType defaultKeyType() {
		return Types.get(IType.INT);
	}

	@operator(value = ConversationType.CONVERSATION_STR, can_be_const = true)
	@doc(value = "to be added", comment = "", special_cases = { "" }, examples = { "" })
	public static Conversation asMessage(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return ConversationType.staticCast(scope, val, null);
	}

	public static Conversation staticCast(IScope scope, Object val, Object object) {
		if ( val instanceof Conversation ) { return (Conversation) val; }
		if ( val instanceof Message ) { return ((Message) val).getConversation(); }
		// ???
		return null;
	}
}
