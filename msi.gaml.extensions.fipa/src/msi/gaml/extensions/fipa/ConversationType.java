package msi.gaml.extensions.fipa;

import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.*;

@type(name = Conversation.TYPE_NAME, id = ConversationType.CONV_ID, wraps = { Conversation.class }, kind = ISymbolKind.Variable.CONTAINER)
public class ConversationType extends GamaType<Conversation> {

	public final static short CONV_ID = 98;

	public ConversationType() {}

	@Override
	public Conversation cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		if ( obj instanceof Conversation ) { return (Conversation) obj; }
//		if ( obj instanceof Message ) { return new Conversation(FIPAConstants.Protocols.NO_PROTOCOL, (Message) obj); }
		return null;
	}

	@Override
	public Conversation getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.get(Message.class);
	}

	@Override
	protected boolean acceptNullInstances() {
		return true;
	}

}
