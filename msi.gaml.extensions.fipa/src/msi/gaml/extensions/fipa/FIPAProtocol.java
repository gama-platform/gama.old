/*********************************************************************************************
 * 
 * 
 * 'FIPAProtocol.java', in plugin 'msi.gaml.extensions.fipa', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.extensions.fipa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.runtime.IScope;

/**
 * The Class FIPAProtocol.
 * 
 * @author drogoul
 */
abstract public class FIPAProtocol {

	/** Constant field INITIATOR. */
	protected static final Integer INITIATOR = 0;

	/** Constant field PARTICIPANT. */
	protected static final Integer PARTICIPANT = 1;

	/**
	 * A list of ProtocolNodes (performatives) that this protocol can begin
	 * with.
	 */
	private List<ProtocolNode> protocolRoots;

	// TODO UCdetector: Remove unused code:
	// /** The roots. */
	// public static Object[] roots;

	/** The protocols. */
	private final static Map<Integer, FIPAProtocol> protocols = new HashMap<Integer, FIPAProtocol>();

	static {
		protocols.put(FIPAConstants.Protocols.FIPA_BROKERING, new FIPABrokering());
		protocols.put(FIPAConstants.Protocols.FIPA_ITERATED_CONTRACT_NET, new FIPAIteratedContractNet());
		protocols.put(FIPAConstants.Protocols.FIPA_CONTRACT_NET, new FIPAContractNet());
		protocols.put(FIPAConstants.Protocols.FIPA_PROPOSE, new FIPAPropose());
		protocols.put(FIPAConstants.Protocols.FIPA_QUERY, new FIPAQuery());
		protocols.put(FIPAConstants.Protocols.FIPA_REQUEST, new FIPARequest());
		protocols.put(FIPAConstants.Protocols.FIPA_REQUEST_WHEN, new FIPARequestWhen());
		protocols.put(FIPAConstants.Protocols.FIPA_SUBSCRIBE, new FIPASubscribe());
		protocols.put(FIPAConstants.Protocols.NO_PROTOCOL, new NoProtocol());
		for (final FIPAProtocol p : protocols.values()) {
			p.computeProtocolRoots();
		}
	}

	/**
	 * Named.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the fIPA protocol
	 */
	protected static FIPAProtocol named(final Integer name) {
		return protocols.get(name);
	}

	/**
	 * Compute protocol roots.
	 */
	private void computeProtocolRoots() {
		// Create the protocol tree
		// protocolRoots = getProtocolTree(getRoots(), new TreeMap<Object[],
		// List<ProtocolNode>>(Comparators.OBJECT_COMPARE));
		// protocolRoots = getProtocolTree(getRoots(), new TreeMap<Object[],
		// List<ProtocolNode>>());
		protocolRoots = getProtocolTree(getRoots(), new HashMap<>());
	}

	/**
	 * Produces a ProtocolNode tree based upon the given Object[] array.
	 * 
	 * @param previousNodes
	 *            A map of previous nodes for loop detection
	 * @param root
	 *            the root
	 * 
	 * @return The List containing the top-level ConversationProtocol
	 */
	private List<ProtocolNode> getProtocolTree(final Object[] root,
			final Map<Object[], List<ProtocolNode>> previousNodes) {

		final List<ProtocolNode> tree = new ArrayList<ProtocolNode>();
		for (int i = 0; i < root.length / 4; i++) {
			final ProtocolNode node = new ProtocolNode();
			node.setPerformative((Integer) root[4 * i]);
			node.setConversationState(((Integer) root[4 * i + 1]).intValue());
			node.setSentByInitiator(((Integer) root[4 * i + 2]).equals(INITIATOR));

			if (root[4 * i + 3] != null) {
				// check for loop of the protocol.
				if (previousNodes.containsKey(root[4 * i + 3])) {
					node.setFollowingNodes(previousNodes.get(root[4 * i + 3]));
				} else {
					previousNodes.put(root, tree);
					final List<ProtocolNode> subTree = getProtocolTree((Object[]) root[4 * i + 3], previousNodes);
					node.setFollowingNodes(subTree);
				}
			} else {
				node.setFollowingNodes(new ArrayList<ProtocolNode>());
			}

			tree.add(node);
		}

		return tree;
	}

	/**
	 * Gets the roots.
	 * 
	 * @return the roots
	 */
	private Object[] getRoots() {
		try {
			return (Object[]) this.getClass().getField("roots").get(null);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final NoSuchFieldException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	abstract public int getIndex();

	abstract public String getName();

	/**
	 * Checks for protocol.
	 * 
	 * @return true if a protocol tree is defined, false otherwise.
	 */
	public boolean hasProtocol() {
		return !protocolRoots.isEmpty();
	}

	/**
	 * Gets the root node.
	 * 
	 * @param performative
	 *            the performative
	 * 
	 * @return the root node
	 */
	private ProtocolNode getRootNode(final int performative) {
		for (final ProtocolNode node : protocolRoots) {
			if (node.getPerformative() == performative) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Gets the node.
	 * 
	 * @param currentNode
	 *            the current node
	 * @param performative
	 *            the performative
	 * @param senderIsInitiator
	 *            the sender is initiator
	 * 
	 * @return the node
	 * 
	 * @throws ProtocolErrorException
	 *             the protocol error exception
	 */
	protected ProtocolNode getNode(final IScope scope, final FIPAMessage message, final ProtocolNode currentNode,
			final int performative, final boolean senderIsInitiator) throws ProtocolErrorException {
		if (currentNode == null) {
			return getRootNode(performative);
		}
		final List<ProtocolNode> followingNodes = currentNode.getFollowingNodes();

		if (followingNodes.size() == 0) {
			throw new ProtocolErrorException(scope, "Message received in conversation which has already ended!");
		}

		final List<ProtocolNode> potentialMatchingNodes = new ArrayList<ProtocolNode>();
		for (final ProtocolNode followingNode : followingNodes) {
			if (performative == followingNode.getPerformative()) {
				potentialMatchingNodes.add(followingNode);
			}
		}

		if (potentialMatchingNodes.isEmpty()) {
			throw new ProtocolErrorException(scope, "Protocol : " + this.getName()
					+ ". Unexpected message received of performative : " + message.getPerformativeName());
		}

		ProtocolNode matchingNode = null;
		for (final ProtocolNode potentialMatchingNode : potentialMatchingNodes) {
			// verify the sender of the message against the expected sender
			// defined in the protocol model.
			if (senderIsInitiator == potentialMatchingNode.isSentByInitiator()) {
				matchingNode = potentialMatchingNode;
				break;
			}
		}

		if (matchingNode == null) {
			throw new ProtocolErrorException(scope, "Couldn't match expected message types and participant");
		}
		return matchingNode;

	}
}
