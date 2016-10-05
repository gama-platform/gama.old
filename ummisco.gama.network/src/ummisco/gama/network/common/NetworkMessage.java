package ummisco.gama.network.common;

import msi.gama.extensions.messaging.GamaMessage;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;

public class NetworkMessage implements ConnectorMessage {
	private static final byte[] keyChain = { 3, 5, 8, 13 };
	private static final String UNDEFINED = "undefined";
	private static final int MAX_HEADER_SIZE = 1024;
	private final String from;
	private final String to;
	private final String content;
	private boolean isPlainMessage = false;

	public NetworkMessage(final String from, final String data) {
		this.content = data;
		this.from = from;
		this.to = UNDEFINED;
		isPlainMessage = true;
	}

	public NetworkMessage(final String from, final String to, final String data) {
		this.from = from;
		this.to = to;
		this.content = data;
		isPlainMessage = false;
	}

	@Override
	public String getSender() {
		return from;
	}

	@Override
	public String getReceiver() {
		return to;
	}

	@Override
	public boolean isPlainMessage() {
		return isPlainMessage;
	}

	@Override
	public GamaMessage getContents(final IScope scope) {
		return isPlainMessage ? getPlainContent(scope) : getCompositeContent(scope);
	}

	public GamaMessage getPlainContent(final IScope scope) {
		final GamaMessage message = new GamaMessage(scope, from, to, content);
		message.hasBeenReceived(scope);
		return message;
	}

	public GamaMessage getCompositeContent(final IScope scope) {
		final Object messageContent = StreamConverter.convertStreamToObject(scope, content);
		GamaMessage message = null;
		if (messageContent instanceof CompositeGamaMessage)
			message = (GamaMessage) messageContent;
		else
			message = new GamaMessage(scope, from, to, messageContent);
		message.hasBeenReceived(scope);
		return message;
	}

	public static NetworkMessage unPackMessage(final String sender, final String data) {
		final String key = new String(keyChain);
		if (data.substring(0, keyChain.length - 1).equals(key))
			return new NetworkMessage(sender, data);

		final int size = MAX_HEADER_SIZE < data.length() ? MAX_HEADER_SIZE : data.length();
		final String header = data.substring(0, size);
		final String headSplit[] = header.split(key);
		final String from = headSplit[1];
		final String to = headSplit[2];
		final String content = data.substring(from.length() + to.length() + 3 * key.length());
		return new NetworkMessage(from, to, content);
	}

	public static String packMessage(final NetworkMessage msg) {
		final String mKey = new String(keyChain);
		return mKey + msg.from + mKey + msg.to + mKey + msg.content;
	}

}
