/*********************************************************************************************
 *
 * 'IUpdaterTarget.java, in plugin ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

public interface IUpdaterTarget<Message extends IUpdaterMessage> {

	public boolean isDisposed();

	public boolean isVisible();

	public boolean isBusy();

	public void updateWith(Message m);

	public int getCurrentState();

	public void resume();

}