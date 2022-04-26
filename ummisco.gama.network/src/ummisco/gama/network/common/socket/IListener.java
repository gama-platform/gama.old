package ummisco.gama.network.common.socket;

import java.nio.ByteBuffer; 

public interface IListener {

	public void onOpen(AbstractProtocol conn);

	public void onClose(AbstractProtocol conn, int code, String reason, boolean remote);

	public void onMessage(AbstractProtocol conn, String message);

	public void onMessage(AbstractProtocol conn, ByteBuffer message);

	public void onError(AbstractProtocol conn, Exception ex);

	public void onStart();
}
