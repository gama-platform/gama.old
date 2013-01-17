package msi.gaml.extensions.fipa;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

@type(name = MessageType.MESSAGE_STR, id = MessageType.MESSAGE_ID, wraps = { Message.class }, kind = ISymbolKind.Variable.REGULAR)
public class MessageType extends GamaType<Message> {

	public static final String MESSAGE_STR = "message";
	public final static short MESSAGE_ID = 99;

	public MessageType() {}

	@Override
	public Message cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	@Override
	public Message getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.NO_TYPE;
	}

	@Override
	protected boolean acceptNullInstances() {
		return true;
	}

	@operator(value = MessageType.MESSAGE_STR, can_be_const = true)
	@doc(value = "to be added", comment = "", special_cases = { "" }, examples = { "" })
	public static Message asMessage(final IScope scope, final Object val)
		throws GamaRuntimeException {
		return MessageType.staticCast(scope, val, null);
	}

	private static Message staticCast(final IScope scope, final Object val, final Object object) {
		if ( val instanceof Conversation ) { return ((Conversation) val).last(scope); }
		if ( val instanceof Message ) { return (Message) val; }
		// ???
		return null;
	}
}
