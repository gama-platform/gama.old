package msi.gama.extensions.messaging;

import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A specialized GamaList that holds messages
 * 
 * @author drogoul
 *
 */
public class GamaMailbox extends GamaList<GamaMessage> {

	public GamaMailbox() {
		this(100);
	}

	public GamaMailbox(final int capacity) {
		super(capacity, Types.get(IType.MESSAGE));
	}

	public void addMessage(final IScope scope, final GamaMessage message) {
		message.hasBeenReceived(scope);
		addValue(scope, message);
	}

}
