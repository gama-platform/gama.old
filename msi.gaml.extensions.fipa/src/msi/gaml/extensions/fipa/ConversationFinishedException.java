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
package msi.gaml.extensions.fipa;

/**
 * ConversationFinishedException is thrown when a message is added to an already finished
 * conversation.
 */
public class ConversationFinishedException extends CommunicatingException {

	/** Constant field serialVersionUID. */
	private static final long serialVersionUID = -4582844650106730326L;

	/**
	 * Instantiates a new conversation finished exception.
	 * 
	 * @param message the message
	 */
	protected ConversationFinishedException(final String message) {
		super(message);
	}
}
