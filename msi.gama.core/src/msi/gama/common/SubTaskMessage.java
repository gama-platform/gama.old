/**
 * Created by drogoul, 5 nov. 2014
 *
 */
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

	Double completion;
	String name;
	Boolean beginOrEnd;

	public SubTaskMessage(final String name, final boolean begin) {
		this.name = name;
		completion = null;
		this.beginOrEnd = begin;
	}

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

	public Double getCompletion() {
		return completion;
	}

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
