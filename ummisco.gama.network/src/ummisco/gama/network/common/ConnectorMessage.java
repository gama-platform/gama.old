package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.runtime.IScope;

public interface ConnectorMessage {
	public String getSender();
	public String getReceiver();
	public boolean isPlainMessage();
	public GamaMessage getContents(IScope scope);
}
