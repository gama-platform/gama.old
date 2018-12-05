/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.FIPAConstants.java, in plugin msi.gaml.extensions.fipa, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.extensions.fipa;

/**
 * The Interface FIPAConstants.
 */
public interface FIPAConstants {

	/**
	 * Constants identifying the FIPA performatives. These performatives will be employed to implement the interaction
	 * protocol in GAMA.
	 */
	public static String[] performativeNames = { "accept_proposal", "agree", "cancel", "cfp", "confirm", "disconfirm",
			"failure", "inform", "inform_if", "inform_ref", "not_understood", "propagate", "propose", "proxy", "query",
			"refuse", "reject_proposal", "request", "request_when", "request_whenever", "request_whomever", "subscribe",
			"end_conversation" };

	/**
	 * The Interface Performatives.
	 */
	public interface Performatives {

		/** Constant field ACCEPT_PROPOSAL. */
		public static final int ACCEPT_PROPOSAL = 0;

		/** Constant field AGREE. */
		public static final int AGREE = 1;

		/** Constant field CANCEL. */
		public static final int CANCEL = 2;

		/** Constant field CFP. */
		public static final int CFP = 3;

		// TODO UCdetector: Remove unused code:
		// /** Constant field CONFIRM. */
		// public static final int CONFIRM = 4;

		// TODO UCdetector: Remove unused code:
		// /** Constant field DISCONFIRM. */
		// public static final int DISCONFIRM = 5;

		/** Constant field FAILURE. */
		public static final int FAILURE = 6;

		/** Constant field INFORM. */
		public static final int INFORM = 7;

		// TODO UCdetector: Remove unused code:
		// /** Constant field INFORM_IF. */
		// public static final int INFORM_IF = 8;

		// TODO UCdetector: Remove unused code:
		// /** Constant field INFORM_REF. */
		// public static final int INFORM_REF = 9;

		/** Constant field NOT_UNDERSTOOD. */
		public static final int NOT_UNDERSTOOD = 10;

		// TODO UCdetector: Remove unused code:
		// /** Constant field PROPAGATE. */
		// public static final int PROPAGATE = 11;

		/** Constant field PROPOSE. */
		public static final int PROPOSE = 12;

		/** Constant field PROXY. */
		public static final int PROXY = 13;

		/** Constant field QUERY. */
		public static final int QUERY = 14;

		/** Constant field REFUSE. */
		public static final int REFUSE = 15;

		/** Constant field REJECT_PROPOSAL. */
		public static final int REJECT_PROPOSAL = 16;

		/** Constant field REQUEST. */
		public static final int REQUEST = 17;

		/** Constant field REQUEST_WHEN. */
		public static final int REQUEST_WHEN = 18;

		// TODO UCdetector: Remove unused code:
		// /** Constant field REQUEST_WHENEVER. */
		// public static final int REQUEST_WHENEVER = 19;

		// TODO UCdetector: Remove unused code:
		// /** Constant field REQUEST_WHOMEVER. */
		// public static final int REQUEST_WHOMEVER = 20;

		/** Constant field SUBSCRIBE. */
		public static final int SUBSCRIBE = 21;

		/** Constant field END_CONVERSATION. */
		public static final int END_CONVERSATION = 22;
	}

	/** The FIPA Interaction Protocols. */

	// public static String[] protocolNames = { "fipa-brokering", "fipa-contract-net", "fipa-iterated-contract-net",
	// "fipa-propose", "fipa-query", "fipa-request", "fipa-request-when", "fipa-subscribe", "no-protocol" };

	/**
	 * The Interface Protocols.
	 */
	public interface Protocols {

		/** Constant field FIPA_BROKERING. */
		// public static final int FIPA_BROKERING = 0;
		public static final String FIPA_BROKERING_STR = "fipa-brokering";

		/** Constant field FIPA_CONTRACT_NET. */
		// public static final int FIPA_CONTRACT_NET = 1;
		public static final String FIPA_CONTRACT_NET_STR = "fipa-contract-net";

		/** Constant field FIPA_ITERATED_CONTRACT_NET. */
		// public static final int FIPA_ITERATED_CONTRACT_NET = 2;
		public static final String FIPA_ITERATED_CONTRACT_NET_STR = "contract";

		/** Constant field FIPA_PROPOSE. */
		// public static final int FIPA_PROPOSE = 3;
		public static final String FIPA_PROPOSE_STR = "fipa-propose";

		/** Constant field FIPA_QUERY. */
		// public static final int FIPA_QUERY = 4;
		public static final String FIPA_QUERY_STR = "fipa-query";

		/** Constant field FIPA_REQUEST. */
		// public static final int FIPA_REQUEST = 5;
		public static final String FIPA_REQUEST_STR = "fipa-request";

		/** Constant field FIPA_REQUEST_WHEN. */
		// public static final int FIPA_REQUEST_WHEN = 6;
		public static final String FIPA_REQUEST_WHEN_STR = "fipa-request-when";

		/** Constant field FIPA_SUBSCRIBE. */
		// public static final int FIPA_SUBSCRIBE = 7;
		public static final String FIPA_SUBSCRIBE_STR = "fipa-subcribe";

		/** Constant field NO_PROTOCOL. */
		// public static final int NO_PROTOCOL = 8;
		public static final String NO_PROTOCOL_STR = "no-protocol";
	}

	/** Conversation has ended. */
	public static final int CONVERSATION_END = -1;

	/** Conversation requires no Agent interaction. */
	public static final int NO_AGENT_ACTION_REQ = -2;

	/** Conversation requires Agent interaction. */
	public static final int AGENT_ACTION_REQ = -3;

}
