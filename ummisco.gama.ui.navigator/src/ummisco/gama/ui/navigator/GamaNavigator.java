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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeListener;
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
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.internal.navigator.CommonNavigatorActionGroup;
import org.eclipse.ui.internal.navigator.NavigatorSafeRunnable;
import org.eclipse.ui.internal.navigator.actions.LinkEditorAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonNavigatorManager;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.IDescriptionProvider;

import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.navigator.contents.TopLevelFolder;
import ummisco.gama.ui.navigator.contents.VirtualContent;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.views.toolbar.GamaCommand;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;
import ummisco.gama.ui.views.toolbar.Selector;

public class GamaNavigator extends CommonNavigator implements IToolbarDecoratedView, ISelectionChangedListener {

	IResourceChangeListener listener;
	IAction link;
	ToolItem linkItem;
	protected Composite parent;
	protected GamaToolbar2 toolbar;
	private IDescriptionProvider commonDescriptionProvider;
	private PropertyDialogAction properties;
	private NavigatorSearchControl findControl;

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

	final GamaCommand collapseAll = new GamaCommand("action.toolbar.collapse2", "", "Collapse all folders",
			e -> getCommonViewer().collapseAll());

	final GamaCommand expandAll = new GamaCommand("action.toolbar.expand2", "", "Fully expand current folder(s)",
			e -> getCommonViewer().expandAll());

	@SuppressWarnings ("deprecation")
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
					toolbar.remove(aci);
				}

			}
		}
		linkItem.setSelection(link.isChecked());
		toolbar.update(true);
		// linkItem.setSelection(link.isChecked());
		// final Action a = linkCommand.toCheckAction();
		// a.setChecked(link.isChecked());
		// toolbar.insertBefore("toolbar.toggle", a);
		toolbar.insertBefore("toolbar.toggle", byDate.toCheckAction());
		toolbar.insertBefore("toolbar.toggle", expandAll.toAction());
		toolbar.insertBefore(expandAll.getId(), collapseAll.toAction());

		try {
			final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
			mgr.setEnabled("msi.gama.application.date.decorator", false);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		properties =
				new PropertyDialogAction(new SameShellProvider(getSite().getShell()), getSite().getSelectionProvider());
		findControl.initialize();
	}

	@Override
	public CommonViewer createCommonViewer(final Composite parent) {
		final CommonViewer commonViewer = super.createCommonViewer(parent);
		return commonViewer;

	}

	@Override
	protected CommonViewer createCommonViewerObject(final Composite aParent) {
		return new NavigatorCommonViewer(getViewSite().getId(), aParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	// @Override
	// protected void initListeners(final TreeViewer viewer) {
	// super.initListeners(viewer);
	// }

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

	// final GamaCommand importCommand = new GamaCommand("navigator/navigator.import2", "", "Import...", trigger -> {
	// final GamaNavigatorImportMenu menu =
	// new GamaNavigatorImportMenu((IStructuredSelection) getCommonViewer().getSelection());
	// final ToolItem target = (ToolItem) trigger.widget;
	// final ToolBar toolBar = target.getParent();
	// menu.open(toolBar, trigger);
	// });
	//
	// final GamaCommand newCommand = new GamaCommand("navigator/navigator.new2", "", "New...", trigger -> {
	// final GamaNavigatorNewMenu menu =
	// new GamaNavigatorNewMenu((IStructuredSelection) getCommonViewer().getSelection());
	// final ToolItem target = (ToolItem) trigger.widget;
	// final ToolBar toolBar = target.getParent();
	// menu.open(toolBar, trigger);
	// });

	final GamaCommand byDate = new GamaCommand("action.toolbar.sort2", "", "Sort by modification date", trigger -> {
		final boolean enabled = ((ToolItem) trigger.widget).getSelection();

		try {
			final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
			mgr.setEnabled("msi.gama.application.date.decorator", enabled);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		safeRefresh(null);
		FileFolderSorter.BY_DATE = enabled;

	});

	final GamaCommand linkCommand =
			new GamaCommand("navigator/navigator.link2", "", "Stay in sync with the editor", e -> link.run());

	/**
	 * Method createToolItem()
	 * 
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
		// tb.menu(importCommand, SWT.RIGHT);
		// tb.menu(newCommand, SWT.RIGHT);
		tb.sep(32, SWT.RIGHT);
		findControl = new NavigatorSearchControl(this).fill(toolbar.getToolbar(SWT.RIGHT));
		linkItem = tb.check(linkCommand, SWT.RIGHT);
	}

	/**
	 * Method selectionChanged()
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final IStructuredSelection currentSelection = (IStructuredSelection) event.getSelection();
		String message = null;
		String tooltip = null;
		Image img = null;
		Selector l = null;
		GamaUIColor color = null;
		if (currentSelection == null || currentSelection.isEmpty()) {
			message = NavigatorRoot.INSTANCE.getMessageForStatus();
			color = IGamaColors.NEUTRAL;
			tooltip = NavigatorRoot.INSTANCE.getTooltipForStatus();
		} else if (currentSelection.getFirstElement() instanceof TopLevelFolder && currentSelection.size() == 1) {
			final TopLevelFolder folder = (TopLevelFolder) currentSelection.getFirstElement();
			message = folder.getMessageForStatus();
			img = folder.getImageForStatus();
			color = folder.getColorForStatus();
			l = e -> properties.run();
		} else {
			message = commonDescriptionProvider.getDescription(currentSelection);
			img = ((ILabelProvider) getCommonViewer().getLabelProvider()).getImage(currentSelection.getFirstElement());
			color = IGamaColors.GRAY_LABEL;
			l = e -> properties.run();
		}
		final ToolItem t = toolbar.status(img, message, l, color, SWT.LEFT);
		if (tooltip != null)
			t.getControl().setToolTipText(tooltip);
	}

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
