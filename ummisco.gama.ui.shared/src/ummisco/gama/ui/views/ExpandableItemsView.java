/*********************************************************************************************
 *
 * 'ExpandableItemsView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.ItemList;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;

public abstract class ExpandableItemsView<T> extends GamaViewPart implements ItemList<T> {

	private ParameterExpandBar viewer;

	protected boolean isOpen = true;

	public ParameterExpandBar getViewer() {
		return viewer;
	}

	public void createViewer(final Composite parent) {
		if (parent == null) { return; }
		if (viewer == null) {
			viewer = new ParameterExpandBar(parent, SWT.V_SCROLL, areItemsClosable(), areItemsPausable(), false, false,
					this);
			final Object layout = parent.getLayout();
			if (layout instanceof GridLayout) {
				final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
				viewer.setLayoutData(data);
			}
			viewer.computeSize(parent.getSize().x, SWT.DEFAULT);
			viewer.setSpacing(5);
		}
	}

	protected boolean areItemsClosable() {
		return false;
	}

	protected boolean areItemsPausable() {
		return false;
	}

	protected ParameterExpandItem createItem(final Composite parent, final T data, final Composite control,
			final boolean expanded, final GamaUIColor color) {
		return createItem(parent, getItemDisplayName(data, null), data, control, expanded, color);
	}

	protected ParameterExpandItem createItem(final Composite parent, final String name, final T data,
			final Composite control, final ParameterExpandBar bar, final boolean expanded, final GamaUIColor color) {
		final ParameterExpandItem i = buildConcreteItem(bar, data, color);
		if (name != null) {
			i.setText(name);
		}
		control.pack(true);
		control.layout();
		control.setBackground(bar.getBackground());
		i.setControl(control);
		i.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(expanded);
		parent.layout(true, true);
		return i;
	}

	protected ParameterExpandItem buildConcreteItem(final ParameterExpandBar bar, final T data,
			final GamaUIColor color) {
		return new ParameterExpandItem(bar, data, SWT.None, color);
	}

	protected ParameterExpandItem createItem(final Composite parent, final String name, final T data,
			final Composite control, final boolean expanded, final GamaUIColor color) {
		createViewer(parent);
		if (viewer == null) { return null; }
		return createItem(parent, name, data, control, viewer, expanded, color);
	}

	protected ParameterExpandItem createItem(final Composite parent, final T data, final boolean expanded,
			final GamaUIColor color) {
		createViewer(parent);
		if (viewer == null) { return null; }
		final Composite control = createItemContentsFor(data);
		if (control == null) { return null; }
		return createItem(parent, data, control, expanded, color);
	}

	protected abstract Composite createItemContentsFor(T data);

	protected void disposeViewer() {
		try {
			if (viewer != null) {
				WorkbenchHelper.run(() -> viewer.dispose());

				viewer = null;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		reset();
		isOpen = false;
		super.widgetDisposed(e);
	}

	@Override
	public void reset() {
		disposeViewer();
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
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
	public void makeItemVisible(final T obj, final boolean b) {}

	@Override
	public void makeItemSelectable(final T obj, final boolean b) {}

	@Override
	public String getItemDisplayName(final T obj, final String previousName) {
		return null;
	}

	@Override
	public GamaColor getItemDisplayColor(final T o) {
		return null;
	}

	public void displayItems() {
		final List<T> items = getItems();
		for (final T obj : items) {
			addItem(obj);
		}
	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return new GamaUIJob() {

			@Override
			protected UpdatePriority jobPriority() {
				return UpdatePriority.LOW;
			}

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				if (!isOpen) { return Status.CANCEL_STATUS; }
				if (getViewer() != null && !getViewer().isDisposed()) {
					getViewer().updateItemNames();
					getViewer().updateItemColors();
					updateItemValues();
				}
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	public abstract List<T> getItems();

	@Override
	public abstract void updateItemValues();

}
