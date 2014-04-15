/*********************************************************************************************
 * 
 *
 * 'ExpandableItemsView.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import java.util.List;
import java.util.concurrent.Semaphore;
import msi.gama.common.interfaces.ItemList;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.controls.*;
import msi.gama.outputs.IDisplayOutput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;

public abstract class ExpandableItemsView<T> extends GamaViewPart implements ItemList<T>, Runnable {

	private ParameterExpandBar viewer;

	protected boolean isOpen = true;
	Thread runThread;
	private final Semaphore semaphore = new Semaphore(1);

	protected ParameterExpandBar getViewer() {
		return viewer;
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		FillLayout fl = new FillLayout();
		// fl.marginHeight = 10;
		parent.setLayout(fl);
	}

	protected void createViewer() {
		if ( parent == null ) { return; }
		if ( viewer == null ) {
			viewer = new ParameterExpandBar(parent, SWT.V_SCROLL, areItemsClosable(), areItemsPausable(), this);
			GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
			viewer.setLayoutData(data);
			viewer.computeSize(parent.getSize().x, SWT.DEFAULT);
			viewer.setSpacing(1);
		}
	}

	protected boolean areItemsClosable() {
		return false;
	}

	protected boolean areItemsPausable() {
		return false;
	}

	protected ParameterExpandItem createItem(final T data, final Composite control, final boolean expanded) {
		return createItem(getItemDisplayName(data, null), data, control, expanded);
	}

	protected ParameterExpandItem createItem(final String name, final T data, final Composite control,
		final ParameterExpandBar bar, final boolean expanded) {
		ParameterExpandItem i = new ParameterExpandItem(bar, data, SWT.None);
		if ( name != null ) {
			i.setText(name);
		}
		control.pack(true);
		control.layout();
		i.setControl(control);
		i.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(expanded);
		parent.layout();
		return i;
	}

	protected ParameterExpandItem createItem(final String name, final T data, final Composite control,
		final boolean expanded) {
		createViewer();
		if ( viewer == null ) { return null; }
		return createItem(name, data, control, viewer, expanded);
	}

	protected ParameterExpandItem createItem(final T data, final boolean expanded) {
		createViewer();
		if ( viewer == null ) { return null; }
		Composite control = createItemContentsFor(data);
		if ( control == null ) { return null; }
		return createItem(data, control, expanded);
	}

	protected abstract Composite createItemContentsFor(T data);

	protected void disposeViewer() {
		try {
			if ( viewer != null ) {
				viewer.dispose();
				viewer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {
		reset();
		isOpen = false;
		super.dispose();
	}

	public void reset() {
		disposeViewer();
	}

	@Override
	public void setFocus() {
		if ( viewer != null ) {
			viewer.setFocus();
		}
	}

	@Override
	public void removeItem(final T obj) {}

	@Override
	public void pauseItem(final T obj) {}

	@Override
	public void resumeItem(final T obj) {}

	@Override
	public void focusItem(final T obj) {}

	@Override
	public String getItemDisplayName(final T obj, final String previousName) {
		return null;
	}

	protected void displayItems() {
		List<T> items = getItems();
		for ( T obj : items ) {
			addItem(obj);
		}
	}

	@Override
	public void run() {
		while (isOpen) {

			try {
				semaphore.acquire();
				GuiUtils.run(new Runnable() {

					@Override
					public void run() {
						if ( !isOpen ) { return; }
						if ( getViewer() != null && !getViewer().isDisposed() ) {
							getViewer().updateItemNames();
							updateItemValues();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public void update(final IDisplayOutput output) {
		// TODO Attention : un release pour CHAQUE output ! Ne marche pas pour MonitorView
		semaphore.release();
		if ( runThread == null ) {
			runThread = new Thread(this, getClass().getSimpleName());
			runThread.start();
		}
	}

	@Override
	public abstract List<T> getItems();

	@Override
	public abstract void updateItemValues();

}
