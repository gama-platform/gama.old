/*******************************************************************************************************
 *
 * msi.gama.extensions.messaging.GamaMessage.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.extensions.messaging;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class GamaMessageProxy.
 *
 * @author drogoul
 */

@vars({ @variable(name = GamaMessage.SENDER, type = IType.NONE, doc = {
		@doc("Returns the sender that has sent this message") }),
		@variable(name = GamaMessage.CONTENTS, type = IType.NONE, doc = {
				@doc("Returns the contents of this message, as a list of arbitrary objects") }),
		@variable(name = GamaMessage.UNREAD, type = IType.BOOL, init = IKeyword.TRUE, doc = {
				@doc("Returns whether this message is unread or not") }),
		@variable(name = GamaMessage.RECEPTION_TIMESTAMP, type = IType.INT, doc = {
				@doc("Returns the reception time stamp of this message (I.e. at what cycle it has been received)") }),
		@variable(name = GamaMessage.EMISSION_TIMESTAMP, type = IType.INT, doc = {
				@doc("Returns the emission time stamp of this message (I.e. at what cycle it has been emitted)") }) })
public class GamaMessage implements IValue {

	public final static String CONTENTS = "contents";
	public final static String UNREAD = "unread";
	public final static String EMISSION_TIMESTAMP = "emission_timestamp";
	public final static String RECEPTION_TIMESTAMP = "recention_timestamp";
	public final static String SENDER = "sender";
	public final static String RECEIVERS = "receivers";

	/** The unread. */
	private boolean unread;

	private Object sender;

	private Object receivers;

	protected Object contents;

	protected int emissionTimeStamp;

	// private int receptionTimeStamp;

	public GamaMessage(final IScope scope, final Object sender, final Object receivers, final Object content)
			throws GamaRuntimeException {
		emissionTimeStamp = scope.getClock().getCycle();
		unread = true;
		setSender(sender);
		setReceivers(receivers);
		setContents(content);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IGamaMessage#getSender()
	 */
	@getter(GamaMessage.SENDER)
	public Object getSender() {
		return sender;
	}

	/**
	 * Sets the sender.
	 *
	 * @param sender
	 *            the sender
	 */
	@setter(GamaMessage.SENDER)
	public void setSender(final Object sender) {
		this.sender = sender;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IGamaMessage#getSender()
	 */
	@getter(GamaMessage.RECEIVERS)
	public Object getReceivers() {
		return receivers;
	}

	/**
	 * Sets the receivers.
	 *
	 * @param sender
	 *            the receivers
	 */
	@setter(GamaMessage.RECEIVERS)
	public void setReceivers(final Object receivers) {
		this.receivers = receivers;
	}

	/**
	 * Gets the contents of the message.
	 *
	 * @return the contents
	 */
	@getter(GamaMessage.CONTENTS)
	public Object getContents(final IScope scope) {
		setUnread(false);
		return contents;
	}

	/**
	 * Sets the contents of the message.
	 *
	 * @param content
	 *            the content
	 */
	@setter(GamaMessage.CONTENTS)
	public void setContents(final Object content) {
		contents = content;
	}

	/**
	 * Checks if is unread.
	 *
	 * @return true, if is unread
	 */
	@getter(GamaMessage.UNREAD)
	public boolean isUnread() {
		return unread;
	}

	/**
	 * Sets the unread.
	 *
	 * @param unread
	 *            the new unread
	 */
	@setter(GamaMessage.UNREAD)
	public void setUnread(final boolean unread) {
		this.unread = unread;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.extensions.fipa.IGamaMessage#getTimestamp()
	 */
	@getter(GamaMessage.EMISSION_TIMESTAMP)
	public int getEmissionTimestamp() {
		return emissionTimeStamp;
	}

	@getter(GamaMessage.RECEPTION_TIMESTAMP)
	public int getReceptionTimestamp() {
		return emissionTimeStamp;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return StringUtils.toGaml(contents, includingBuiltIn);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "message[sender: " + getSender() + "; content: " + getContents(scope) + "; content" + "]";
	}

	@Override
	public GamaMessage copy(final IScope scope) throws GamaRuntimeException {
		return new GamaMessage(scope, getSender(), getReceivers(), getContents(scope));
	}

	/**
	 * Method getType()
	 * 
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType<?> getGamlType() {
		return Types.get(IType.MESSAGE);
	}

	public void hasBeenReceived(final IScope scope) {
		// receptionTimeStamp = scope.getClock().getCycle();

	}

}
