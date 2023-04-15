/*******************************************************************************************************
 *
 * SubTaskMessage.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStatusMessage;
import msi.gama.util.GamaColor;

/**
 * Class SubTaskMessage.
 *
 * @author drogoul
 * @since 5 nov. 2014
 *
 */
public class SubTaskMessage implements IStatusMessage {

	/** The completion. */
	Double completion;
	
	/** The name. */
	String name;
	
	/** The begin or end. */
	Boolean beginOrEnd;

	/**
	 * Instantiates a new sub task message.
	 *
	 * @param name the name
	 * @param begin the begin
	 */
	public SubTaskMessage(final String name, final boolean begin) {
		this.name = name;
		completion = null;
		this.beginOrEnd = begin;
	}

	/**
	 * Instantiates a new sub task message.
	 *
	 * @param completion the completion
	 */
	public SubTaskMessage(final Double completion) {
		this.completion = completion;
		this.beginOrEnd = null;
	}

	/**
	 * Method getText()
	 * 
	 * @see msi.gama.common.interfaces.IStatusMessage#getText()
	 */
	@Override
	public String getText() {
		return name;
	}

	/**
	 * Method getCode()
	 * 
	 * @see msi.gama.common.interfaces.IStatusMessage#getCode()
	 */
	@Override
	public int getCode() {
		return IGui.NEUTRAL;
	}

	/**
	 * Gets the completion.
	 *
	 * @return the completion
	 */
	public Double getCompletion() {
		return completion;
	}

	/**
	 * Gets the begin or end.
	 *
	 * @return the begin or end
	 */
	public Boolean getBeginOrEnd() {
		return beginOrEnd;
	}

	/**
	 * Method getColor()
	 * 
	 * @see msi.gama.common.interfaces.IStatusMessage#getColor()
	 */
	@Override
	public GamaColor getColor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.common.IStatusMessage#getIcon()
	 */
	@Override
	public String getIcon() {
		return null;
	}

}
