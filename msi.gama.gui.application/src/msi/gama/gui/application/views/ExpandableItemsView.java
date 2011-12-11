/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gama.gui.application.views;

import java.util.List;
import java.util.concurrent.Semaphore;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.IDisplayOutput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;

public abstract class ExpandableItemsView<T> extends GamaViewPart implements ItemList<T>, Runnable {

	private ParameterExpandBar viewer;

	boolean isOpen = true;
	Thread runThread;
	Semaphore semaphore = new Semaphore(1);

	protected ParameterExpandBar getViewer() {
		return viewer;
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
	}

	protected void createViewer() {
		if ( parent == null ) { return; }
		if ( viewer == null ) {
			viewer =
				new ParameterExpandBar(parent, SWT.V_SCROLL, areItemsClosable(),
					areItemsPausable(), this);
			viewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			viewer.computeSize(parent.getSize().x, SWT.DEFAULT);
			viewer.setSpacing(6);
		}
	}

	protected boolean areItemsClosable() {
		return false;
	}

	protected boolean areItemsPausable() {
		return false;
	}

	protected ParameterExpandItem createItem(final T data, final Composite control,
		final boolean expanded) {
		return createItem(getItemDisplayName(data, null), data, control, expanded);
	}

	protected ParameterExpandItem createItem(final String name, final T data,
		final Composite control, final ParameterExpandBar bar, final boolean expanded) {
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

	protected ParameterExpandItem createItem(final String name, final T data,
		final Composite control, final boolean expanded) {
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
		if ( viewer != null ) {
			viewer.dispose();
			viewer = null;
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
				GUI.run(new Runnable() {

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
