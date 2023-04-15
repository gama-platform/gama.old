/*******************************************************************************************************
 *
 * IUpdaterTarget.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

/**
 * The Interface IUpdaterTarget.
 *
 * @param <Message> the generic type
 */
public interface IUpdaterTarget<Message extends IUpdaterMessage> {

	/**
	 * Checks if is disposed.
	 *
	 * @return true, if is disposed
	 */
	public boolean isDisposed();

	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible();

	/**
	 * Checks if is busy.
	 *
	 * @return true, if is busy
	 */
	public boolean isBusy();

	/**
	 * Update with.
	 *
	 * @param m the m
	 */
	public void updateWith(Message m);

	/**
	 * Gets the current state.
	 *
	 * @return the current state
	 */
	public int getCurrentState();

	/**
	 * Resume.
	 */
	public void resume();

}