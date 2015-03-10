/*********************************************************************************************
 * 
 * 
 * 'ThreadedUpdater.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt;

import msi.gama.common.interfaces.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.progress.UIJob;

/**
 * Class ThreadedUpdater.
 * 
 * @author drogoul
 * @since 10 mars 2014
 * 
 */
public class ThreadedUpdater<Message extends IUpdaterMessage> extends UIJob implements IUpdaterTarget<Message> {

	Message message = null;
	private IUpdaterTarget<Message> control;

	public ThreadedUpdater(final String name) {
		super(SwtGui.getDisplay(), name);
	}

	@Override
	public boolean isDisposed() {
		return control.isDisposed();
	}

	@Override
	public void updateWith(final Message m) {
		message = m;
		if ( m == null || m.isEmpty() ) { return; }
		schedule();
	}

	@Override
	public int getCurrentState() {
		return control.getCurrentState();
	}

	public void setTarget(final IUpdaterTarget<Message> l) {
		control = l;
	}

	@Override
	public IStatus runInUIThread(final IProgressMonitor monitor) {
		if ( control.isDisposed() ) { return Status.CANCEL_STATUS; }
		control.updateWith(message);
		return Status.OK_STATUS;
	}
}
