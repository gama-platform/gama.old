/*******************************************************************************************************
 *
 * ExpandableItemsView.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * The Class ExpandableItemsView.
 *
 * @param <T>
 *            the generic type
 */
public abstract class ExpandableItemsView<T> extends GamaViewPart
		implements ItemList<T>, IToolbarDecoratedView.Expandable {

	/** The viewer. */
	private ParameterExpandBar viewer;

	/** The is open. */
	protected boolean isOpen = true;

	/**
	 * Gets the viewer.
	 *
	 * @return the viewer
	 */
	public ParameterExpandBar getViewer() { return viewer; }

	/**
	 * Creates the viewer.
	 *
	 * @param parent
	 *            the parent
	 */
	public void createViewer(final Composite parent) {
		if (parent == null) return;
		if (viewer == null) {
			viewer = new ParameterExpandBar(parent, SWT.V_SCROLL, areItemsClosable(), areItemsPausable(), false, false,
					this);
			final Object layout = parent.getLayout();
			if (layout instanceof GridLayout) {
				final var data = new GridData(SWT.FILL, SWT.FILL, true, true);
				viewer.setLayoutData(data);
			}
			// viewer.setBackground(!ThemeHelper.isDark() ? IGamaColors.WHITE.color() : IGamaColors.DARK_GRAY.darker());
			// viewer.computeSize(parent.getSize().x, SWT.DEFAULT);
			viewer.setSpacing(8);
		}
	}

	/**
	 * Are items closable.
	 *
	 * @return true, if successful
	 */
	protected boolean areItemsClosable() {
		return false;
	}

	/**
	 * Are items pausable.
	 *
	 * @return true, if successful
	 */
	protected boolean areItemsPausable() {
		return false;
	}

	/**
	 * Creates the item.
	 *
	 * @param parent
	 *            the parent
	 * @param data
	 *            the data
	 * @param control
	 *            the control
	 * @param expanded
	 *            the expanded
	 * @param color
	 *            the color
	 * @return the parameter expand item
	 */
	protected ParameterExpandItem createItem(final Composite parent, final T data, final Composite control,
			final boolean expanded, final GamaUIColor color) {
		return createItem(parent, getItemDisplayName(data, null), data, control, expanded, color);
	}

	/**
	 * Creates the item.
	 *
	 * @param parent
	 *            the parent
	 * @param name
	 *            the name
	 * @param data
	 *            the data
	 * @param control
	 *            the control
	 * @param bar
	 *            the bar
	 * @param expanded
	 *            the expanded
	 * @param color
	 *            the color
	 * @return the parameter expand item
	 */
	protected ParameterExpandItem createItem(final Composite parent, final String name, final T data,
			final Composite control, final ParameterExpandBar bar, final boolean expanded, final GamaUIColor color) {
		final var item = buildConcreteItem(bar, data, color);
		if (name != null) { item.setText(name); }
		control.pack(true);
		control.layout();
		item.setControl(control);
		item.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item.setExpanded(expanded);
		parent.layout(true, true);
		return item;
	}

	/**
	 * Builds the concrete item.
	 *
	 * @param bar
	 *            the bar
	 * @param data
	 *            the data
	 * @param color
	 *            the color
	 * @return the parameter expand item
	 */
	protected ParameterExpandItem buildConcreteItem(final ParameterExpandBar bar, final T data,
			final GamaUIColor color) {
		return new ParameterExpandItem(bar, data, SWT.None, color);
	}

	/**
	 * Creates the item.
	 *
	 * @param parent
	 *            the parent
	 * @param name
	 *            the name
	 * @param data
	 *            the data
	 * @param control
	 *            the control
	 * @param expanded
	 *            the expanded
	 * @param color
	 *            the color
	 * @return the parameter expand item
	 */
	protected ParameterExpandItem createItem(final Composite parent, final String name, final T data,
			final Composite control, final boolean expanded, final GamaUIColor color) {
		createViewer(parent);
		if (viewer == null) return null;
		return createItem(parent, name, data, control, viewer, expanded, color);
	}

	/**
	 * Creates the item.
	 *
	 * @param parent
	 *            the parent
	 * @param data
	 *            the data
	 * @param expanded
	 *            the expanded
	 * @param color
	 *            the color
	 * @return the parameter expand item
	 */
	protected ParameterExpandItem createItem(final Composite parent, final T data, final boolean expanded,
			final GamaUIColor color) {
		createViewer(parent);
		if (viewer == null) return null;
		final var control = createItemContentsFor(data);
		if (control == null) return null;
		return createItem(parent, data, control, expanded, color);
	}

	/**
	 * Creates the item contents for.
	 *
	 * @param data
	 *            the data
	 * @return the composite
	 */
	protected abstract Composite createItemContentsFor(T data);

	/**
	 * Dispose viewer.
	 */
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
		WorkbenchHelper.run(() -> {
			if (getParentComposite() == null) return;
			getParentComposite().setLayoutDeferred(true);
			disposeViewer();
			getParentComposite().setLayoutDeferred(false);
		});
	}

	@Override
	public void setFocus() {
		if (viewer != null && viewer.isVisible()) { viewer.setFocus(); }
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

	/**
	 * Display items.
	 */
	public void displayItems() {
		final var items = getItems();
		for (final T obj : items) { addItem(obj); }
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
				if (!isOpen) return Status.CANCEL_STATUS;
				if (getViewer() != null && !getViewer().isDisposed()) {
					getViewer().updateItemNames();
					getViewer().updateItemColors();
					updateItemValues(GAMA.isSynchronized());
				}
				return Status.OK_STATUS;
			}
		};
	}

	@Override
	public abstract List<T> getItems();

	@Override
	public abstract void updateItemValues(boolean synchronously);

	@Override
	public void collapseAll() {
		for (final ParameterExpandItem p : getViewer().getItems()) { p.setExpanded(false); }
	}

	@Override
	public void expandAll() {
		for (final ParameterExpandItem p : getViewer().getItems()) { p.setExpanded(true); }
	}

}
