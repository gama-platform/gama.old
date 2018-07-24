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

import static msi.gama.common.preferences.GamaPreferences.create;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static ummisco.gama.ui.navigator.contents.NavigatorRoot.getInstance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.internal.navigator.CommonNavigatorActionGroup;
import org.eclipse.ui.internal.navigator.actions.LinkEditorAction;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonNavigatorManager;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.IDescriptionProvider;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gaml.types.IType;
import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.navigator.contents.TopLevelFolder;
import ummisco.gama.ui.navigator.contents.VirtualContent;
import ummisco.gama.ui.navigator.contents.WrappedContainer;
import ummisco.gama.ui.navigator.contents.WrappedFile;
import ummisco.gama.ui.navigator.contents.WrappedResource;
import ummisco.gama.ui.navigator.contents.WrappedSyntacticContent;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.views.toolbar.GamaCommand;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;
import ummisco.gama.ui.views.toolbar.Selector;

public class GamaNavigator extends CommonNavigator implements IToolbarDecoratedView, ISelectionChangedListener {
	//
	// static Pref<String> NAVIGATOR_EXPANDED_STATE =
	// create("pref_navigator_state", "Navigator", "", IType.STRING).hidden();

	static Pref<Boolean> KEEP_NAVIGATOR_STATE =
			create("pref_keep_navigator_state", "Maintain the state of the navigator across sessions", true, IType.BOOL)
					.in(GamaPreferences.Interface.NAME, GamaPreferences.Interface.STARTUP);

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
					message = ((VirtualContent<?>) selection.getFirstElement()).getName();
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
		restoreState();
		final IToolBarManager tb = getViewSite().getActionBars().getToolBarManager();
		for (final IContributionItem item : tb.getItems()) {
			if (item instanceof ActionContributionItem) {
				final ActionContributionItem aci = (ActionContributionItem) item;
				final IAction action = aci.getAction();
				if (action instanceof LinkEditorAction) {
					link = action;
					tb.remove(aci);
				} else if (action instanceof org.eclipse.ui.internal.navigator.actions.CollapseAllAction) {
					tb.remove(aci);
				}

			}
		}
		linkItem.setSelection(link.isChecked());
		tb.update(true);
		// linkItem.setSelection(link.isChecked());
		// final Action a = linkCommand.toCheckAction();
		// a.setChecked(link.isChecked());
		// toolbar.insertBefore("toolbar.toggle", a);
		tb.insertBefore("toolbar.toggle", byDate.toCheckAction());
		tb.insertBefore("toolbar.toggle", expandAll.toAction());
		tb.insertBefore(expandAll.getId(), collapseAll.toAction());

		try {
			final IDecoratorManager mgr = PlatformUI.getWorkbench().getDecoratorManager();
			mgr.setEnabled("msi.gama.application.date.decorator", false);
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		properties =
				new PropertyDialogAction(new SameShellProvider(getSite().getShell()), getSite().getSelectionProvider());
		findControl.initialize();
		// getCommonViewer().getControl().addDisposeListener(e -> saveState());
	}

	@Override
	public void saveState(final IMemento newMemento) {
		if (KEEP_NAVIGATOR_STATE.getValue()) {
			final StringBuilder sb = new StringBuilder();
			for (final Object o : getCommonViewer().getExpandedElements()) {
				final String name =
						o instanceof WrappedContainer ? ((WrappedContainer<?>) o).getResource().getFullPath().toString()
								: o instanceof TopLevelFolder ? ((TopLevelFolder) o).getName() : null;
				if (name != null) {
					sb.append(name);
					sb.append("@@");
				}
			}
			if (sb.length() > 2) {
				sb.setLength(sb.length() - 2);
			}
			newMemento.putString("EXPANDED_STATE", sb.toString());
		}
		super.saveState(newMemento);
	}

	private void restoreState() {
		final String saved = memento.getString("EXPANDED_STATE");
		if (saved == null) { return; }
		if (KEEP_NAVIGATOR_STATE.getValue()) {
			final List<VirtualContent<?>> contents = new ArrayList<>();
			final String[] names = saved.split("@@");
			for (final String s : names) {
				if (s.startsWith("/")) {
					final WrappedResource<?, ?> resource = getInstance().getManager()
							.findWrappedInstanceOf(getWorkspace().getRoot().findMember(new Path(s)));
					if (resource != null) {
						contents.add(resource);
					}
				} else {
					final TopLevelFolder folder = getInstance().getFolder(s);
					if (folder != null) {
						contents.add(folder);
					}
				}
			}

			getCommonViewer().setExpandedElements(contents.toArray(new VirtualContent[0]));
		}
	}

	@Override
	public CommonViewer createCommonViewer(final Composite p) {
		final CommonViewer commonViewer = super.createCommonViewer(p);
		return commonViewer;

	}

	@Override
	public void selectReveal(final ISelection selection) {
		VirtualContent<?> current;
		final Object o1 = getCommonViewer().getStructuredSelection().getFirstElement();
		if (o1 instanceof IResource) {
			current = NavigatorRoot.getInstance().getManager().findWrappedInstanceOf(o1);
		} else {
			current = (VirtualContent<?>) getCommonViewer().getStructuredSelection().getFirstElement();
		}

		StructuredSelection newSelection = new StructuredSelection();
		if (selection instanceof StructuredSelection) {
			newSelection = (StructuredSelection) selection;
			Object o = ((StructuredSelection) selection).getFirstElement();
			if (o instanceof IResource) {
				o = NavigatorRoot.getInstance().getManager().findWrappedInstanceOf(o);
				if (o != null) {
					newSelection = new StructuredSelection(o);
				}
			}
		}

		if (current instanceof WrappedSyntacticContent) {
			final Object o = newSelection.getFirstElement();
			if (o instanceof WrappedFile) {
				if (((VirtualContent<?>) current).isContainedIn((VirtualContent<?>) o)) {
					getCommonViewer().setSelection(new StructuredSelection(current));
				}
				return;
			}
		}
		if (!newSelection.isEmpty()) {
			super.selectReveal(newSelection);
		}
	}

	@Override
	protected CommonViewer createCommonViewerObject(final Composite aParent) {
		return new NavigatorCommonViewer(getViewSite().getId(), aParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	@Override
	protected Object getInitialInput() {
		return NavigatorRoot.getInstance();
	}

	@Override
	protected void handleDoubleClick(final DoubleClickEvent anEvent) {
		final IStructuredSelection selection = (IStructuredSelection) anEvent.getSelection();
		final Object element = selection.getFirstElement();
		if (element instanceof VirtualContent && ((VirtualContent<?>) element).handleDoubleClick()) {
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

		getCommonViewer().refresh();
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
		// tb.sep(24, SWT.RIGHT);
		if (PlatformHelper.isWindows()) {
			tb.sep(24, SWT.RIGHT);
			findControl = new NavigatorSearchControl(this).fill(toolbar.getToolbar(SWT.RIGHT));
			linkItem = tb.check(linkCommand, SWT.RIGHT);

		} else {
			findControl = new NavigatorSearchControl(this).fill(toolbar.getToolbar(SWT.RIGHT));
			tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
			linkItem = tb.check(linkCommand, SWT.RIGHT);
		}
	}

	/**
	 * Method selectionChanged()
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final IStructuredSelection currentSelection = (IStructuredSelection) event.getSelection();
		VirtualContent<?> element;
		if (currentSelection == null || currentSelection.isEmpty()) {
			element = NavigatorRoot.getInstance();
		} else {
			element = (VirtualContent<?>) currentSelection.getFirstElement();
		}
		element.handleSingleClick();
		showStatus(element);
	}

	private void showStatus(final VirtualContent<?> element) {
		final String message = element.getStatusMessage();
		final String tooltip = element.getStatusTooltip();
		final Image image = element.getStatusImage();
		final GamaUIColor color = element.getStatusColor();
		final Selector l = e -> properties.run();

		final ToolItem t = toolbar.status(image, message, l, color, SWT.LEFT);
		t.getControl().setToolTipText(tooltip == null ? message : tooltip);
	}

}
