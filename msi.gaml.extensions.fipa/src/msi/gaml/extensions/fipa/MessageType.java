package msi.gaml.extensions.fipa;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

@type(name = Message.SPECIES_NAME, id = MessageType.CONV_ID, wraps = { Message.class }, kind = ISymbolKind.Variable.REGULAR)
public class MessageType extends GamaType<Message> {

	public final static short CONV_ID = 99;

	public MessageType() {}

	@Override
	public Message cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof Conversation ) { return ((Conversation) obj).last(); }
		if ( obj instanceof Message ) { return (Message) obj; }
		// ???
		return null;
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

}
