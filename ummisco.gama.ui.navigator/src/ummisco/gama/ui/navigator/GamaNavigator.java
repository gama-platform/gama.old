/*********************************************************************************************
 *
 * 'GamaNavigator.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.internal.navigator.CommonNavigatorActionGroup;
import org.eclipse.ui.internal.navigator.NavigatorSafeRunnable;
import org.eclipse.ui.internal.navigator.actions.LinkEditorAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonNavigatorManager;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.IDescriptionProvider;

import msi.gama.runtime.GAMA;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

public class GamaNavigator extends CommonNavigator implements IToolbarDecoratedView, ISelectionChangedListener {

	IResourceChangeListener listener;
	IAction link, collapse;
	ToolItem linkItem;
	protected Composite parent;
	protected GamaToolbar2 toolbar;
	private IDescriptionProvider commonDescriptionProvider;

	@Override
	protected CommonNavigatorManager createCommonManager() {
		final CommonNavigatorManager manager = new CommonNavigatorManager(this, memento);
		commonDescriptionProvider = anElement -> {
			if (anElement instanceof IStructuredSelection) {
				final IStructuredSelection selection = (IStructuredSelection) anElement;
				if (selection.isEmpty()) { return ""; }
				String message = null;
				if (selection.size() > 1) {
					message = "Multiple elements";
				} else if (selection.getFirstElement() instanceof VirtualContent) {
					message = ((VirtualContent) selection.getFirstElement()).getName();
				} else if (selection.getFirstElement() instanceof IResource) {
					message = ((IResource) selection.getFirstElement()).getName();
				}
				return message;
			}
			return "";
		};
		getCommonViewer().addPostSelectionChangedListener(this);

		return manager;
	}

	@Override
	public void createPartControl(final Composite compo) {
		this.parent = GamaToolbarFactory.createToolbars(this, compo);

		super.createPartControl(parent);
		final IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
		for (final IContributionItem item : toolbar.getItems()) {
			if (item instanceof ActionContributionItem) {
				final ActionContributionItem aci = (ActionContributionItem) item;
				final IAction action = aci.getAction();
				if (action instanceof LinkEditorAction) {
					link = action;
					toolbar.remove(aci);
				} else if (action instanceof org.eclipse.ui.internal.navigator.actions.CollapseAllAction) {
					collapse = action;
					toolbar.remove(aci);
				}

			}
		}
		// toolbar.removeAll();
		linkItem.setSelection(link.isChecked());
		try {
			final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
			mgr.setEnabled("msi.gama.application.date.decorator", false);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.PASTE.getId(), PasteAction.INSTANCE);
		getViewSite().getActionBars().updateActionBars();
	}

	@Override
	public CommonViewer createCommonViewer(final Composite parent) {
		final CommonViewer commonViewer = super.createCommonViewer(parent);
		final IResourceChangeListener resourceChangeListener = event -> {
			if (!PlatformUI.isWorkbenchRunning()) { return; }
			Display.getDefault().asyncExec(() -> {
				if (getCommonViewer() != null && getCommonViewer().getControl() != null
						&& !getCommonViewer().getControl().isDisposed()) {
					GAMA.getGui().updateDecorator("msi.gama.application.decorator");
					getCommonViewer().refresh();

				}
			});
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener,
				IResourceChangeEvent.POST_BUILD);

		return commonViewer;

	}

	@Override
	protected void initListeners(final TreeViewer viewer) {
		super.initListeners(viewer);

		listener = event -> {
			if (event.getType() == IResourceChangeEvent.PRE_BUILD || event.getType() == IResourceChangeEvent.PRE_CLOSE
					|| event.getType() == IResourceChangeEvent.PRE_DELETE) { return; }

			Display.getDefault().asyncExec(() -> {
				if (viewer == null || viewer.getControl() == null || viewer.getControl().isDisposed()) { return; }

				final IResourceDelta d = event.getDelta();
				if (d != null) {
					final IResourceDelta[] addedChildren = d.getAffectedChildren(IResourceDelta.ADDED);
					if (addedChildren.length > 0) {
						safeRefresh(d.getResource().getParent());
					}

				} else {
					safeRefresh(null);
				}
			});
		};
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);

	}

	@Override
	public void dispose() {
		super.dispose();
		if (listener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
			listener = null;
		}
	}

	@Override
	protected Object getInitialInput() {
		return new NavigatorRoot();
	}

	@Override
	protected void handleDoubleClick(final DoubleClickEvent anEvent) {
		final IStructuredSelection selection = (IStructuredSelection) anEvent.getSelection();
		final Object element = selection.getFirstElement();
		if (element instanceof IFile) {
			final TreeViewer viewer = getCommonViewer();
			if (viewer.isExpandable(element)) {
				viewer.setExpandedState(element, !viewer.getExpandedState(element));
			}
		}
		if (element instanceof VirtualContent && ((VirtualContent) element).handleDoubleClick()) {
			return;
		} else {
			super.handleDoubleClick(anEvent);
		}
	}

	@Override
	protected ActionGroup createCommonActionGroup() {
		return new CommonNavigatorActionGroup(this, getCommonViewer(), getLinkHelperService()) {

			@Override
			protected void fillViewMenu(final IMenuManager menu) {
				menu.removeAll();
			}

		};
	}

	/**
	 * Method createToolItem()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;

		// Menu: { IMPORT, NEW, SEP, SORT, COLLAPSE, LINK };
		tb.menu("navigator/navigator.import2", "", "Import...", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent trigger) {
				final GamaNavigatorImportMenu menu =
						new GamaNavigatorImportMenu((IStructuredSelection) getCommonViewer().getSelection());
				final ToolItem target = (ToolItem) trigger.widget;
				final ToolBar toolBar = target.getParent();
				menu.open(toolBar, trigger);

			}

		}, SWT.RIGHT);
		tb.menu("navigator/navigator.new2", "", "New...", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent trigger) {
				final GamaNavigatorNewMenu menu =
						new GamaNavigatorNewMenu((IStructuredSelection) getCommonViewer().getSelection());
				final ToolItem target = (ToolItem) trigger.widget;
				final ToolBar toolBar = target.getParent();
				menu.open(toolBar, trigger);

			}

		}, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.check("navigator/navigator.date2", "", "Sort by modification date", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent trigger) {
				final boolean enabled = ((ToolItem) trigger.widget).getSelection();

				try {
					final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
					mgr.setEnabled("msi.gama.application.date.decorator", enabled);
				} catch (final CoreException e) {
					e.printStackTrace();
				}
				safeRefresh(null);
				FileFolderSorter.BY_DATE = enabled;

			}

		}, SWT.RIGHT);
		tb.button("navigator/navigator.collapse2", "", "Collapse all items", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				collapse.run();
			}

		}, SWT.RIGHT);
		linkItem = tb.check("navigator/navigator.link2", "", "Stay in sync with the editor", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				link.run();
			}

		}, SWT.RIGHT);

	}

	/**
	 * Method selectionChanged()
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		String message = null;
		Image img = null;
		SelectionListener l;
		GamaUIColor color = null;
		if (selection == null || selection.isEmpty()) {
			toolbar.wipe(SWT.LEFT, true);
			return;
		} else if (selection.getFirstElement() instanceof TopLevelFolder && selection.size() == 1) {
			final TopLevelFolder folder = (TopLevelFolder) selection.getFirstElement();
			message = folder.getMessageForStatus();
			img = folder.getImageForStatus();
			color = folder.getColorForStatus();
			l = folder.getSelectionListenerForStatus();
		} else {
			message = commonDescriptionProvider.getDescription(selection);
			img = ((ILabelProvider) getCommonViewer().getLabelProvider()).getImage(selection.getFirstElement());
			color = IGamaColors.GRAY_LABEL;
			l = new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					final IAction action =
							getViewSite().getActionBars().getGlobalActionHandler(ActionFactory.PROPERTIES.getId());
					if (action != null) {
						action.run();
					}
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					widgetSelected(e);
				}
			};
		}
		toolbar.status(img, message, l, color, SWT.LEFT);
	}

	public Menu getSubMenu(final String text) {
		final Menu m = getCommonViewer().getTree().getMenu();
		for (final MenuItem mi : m.getItems()) {
			if (text.equals(mi.getText())) { return mi.getMenu(); }
		}
		return m;
	}
	//
	// @Override
	// public void setToogle(final Action toggle) {}

	public void safeRefresh(final IResource resource) {

		final CommonViewer localViewer = getCommonViewer();

		if (localViewer == null || localViewer.getControl().isDisposed()) { return; }
		final Display display = localViewer.getControl().getDisplay();
		if (display.isDisposed()) { return; }
		display.syncExec(() -> {
			if (localViewer.getControl().isDisposed()) { return; }
			final Object[] expanded = localViewer.getExpandedElements();
			SafeRunner.run(new NavigatorSafeRunnable() {

				@Override
				public void run() throws Exception {
					localViewer.getControl().setRedraw(false);
					if (resource == null) {
						localViewer.refresh();
					} else {
						localViewer.refresh(resource);
					}
				}
			});
			localViewer.getControl().setRedraw(true);
			getCommonViewer().setExpandedElements(expanded);
		});

	}

}
