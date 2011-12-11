/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.extensions.fipa_acl;

import java.util.List;

import msi.gama.util.GamaList;

/**
 * The Class ProtocolNode.
 */
public class ProtocolNode {

	/** Performative to be used for this branch. */
	private int performative;

	/** Initiator should send performative at this node?. */
	private boolean sentByInitiator;

	/** State of conversation at this node. */
	private int conversationState;

	/** Wait for response, if none is received end the conversation. */
	private boolean waitForResponse;

	/** Reference to following nodes. */
	private List<ProtocolNode> followingNodes;

	/**
	 * Instantiates a new protocol node.
	 */
	public ProtocolNode() {
		followingNodes = new GamaList<ProtocolNode>();
	}

	/**
	 * Gets the performative.
	 *
	 * @return the performative
	 */
	public int getPerformative() {
		return performative;
	}

	/**
	 * Sets the performative.
	 *
	 * @param performative
	 *            the performative to set
	 */
	public void setPerformative(final int performative) {
		this.performative = performative;
	}

	/**
	 * Checks if is sent by initiator.
	 *
	 * @return the sentByInitiator
	 */
	public boolean isSentByInitiator() {
		return sentByInitiator;
	}

	/**
	 * Sets the sent by initiator.
	 *
	 * @param sentByInitiator
	 *            the sentByInitiator to set
	 */
	public void setSentByInitiator(final boolean sentByInitiator) {
		this.sentByInitiator = sentByInitiator;
	}

	/**
	 * Gets the conversation state.
	 *
	 * @return the conversationState
	 */
	public int getConversationState() {
		return conversationState;
	}

	/**
	 * Sets the conversation state.
	 *
	 * @param conversationState
	 *            the conversationState to set
	 */
	public void setConversationState(final int conversationState) {
		this.conversationState = conversationState;
	}

	/**
	 * Checks if is wait for response.
	 *
	 * @return the waitForResponse
	 */
	public boolean isWaitForResponse() {
		return waitForResponse;
	}

	/**
	 * Sets the wait for response.
	 *
	 * @param waitForResponse
	 *            the waitForResponse to set
	 */
	public void setWaitForResponse(final boolean waitForResponse) {
		this.waitForResponse = waitForResponse;
	}

	/**
	 * Gets the following nodes.
	 *
	 * @return the followingNodes
	 */
	public List<ProtocolNode> getFollowingNodes() {
		return followingNodes;
	}

	/**
	 * Sets the following nodes.
	 *
	 * @param followingNodes
	 *            the followingNodes to set
	 */
	public void setFollowingNodes(final List<ProtocolNode> followingNodes) {
		this.followingNodes = followingNodes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer retVal = new StringBuffer();
		retVal.append("ProtocolNode : " + "\n\tperformative = " + performative
				+ "\n\t; sentByInitiator = " + sentByInitiator
				+ "\n\t; waitForResponse = " + waitForResponse
				+ "\n\t; followingNodes.size() = " + followingNodes.size()
				+ "\n\t; conversationState = " + conversationState);

		return retVal.toString();
	}

	/**
	 * Checks if is terminal.
	 *
	 * @return true, if is terminal
	 */
	public boolean isTerminal() {
		return followingNodes == null || followingNodes.isEmpty();
	}

}
