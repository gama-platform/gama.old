/*********************************************************************************************
 * 
 *
 * 'IOutput.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;

/**
 * @author drogoul
 */
public interface IOutput extends ISymbol, IStepable {

	public String getId();

	public void pause();

	public void resume();

	public void open();

	public void close();

	public int getRefreshRate();

	public void setRefreshRate(int rate);

	public boolean isPaused();

	public boolean isOpen();

	// public void schedule() throws GamaRuntimeException;

	public void setNextTime(Integer i);

	public long getNextTime();

	public boolean isUserCreated();

	/*
	 * Called by the output thread to perform the actual "update" (of views, files, etc.)
	 */
	public void update() throws GamaRuntimeException;

	public IScope getScope();

	public void setUserCreated(boolean b);

}
