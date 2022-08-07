/*******************************************************************************************************
 *
 * GamaToolbarFactory.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import static msi.gama.application.workbench.ThemeHelper.isDark;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchSite;

import msi.gama.common.interfaces.IGamaView;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.controls.ITooltipDisplayer;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The class GamaToolbarFactory.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class GamaToolbarFactory {

	static {
		DEBUG.ON();
	}

	// public static final Pref<Boolean> REDUCED_VIEW_TOOLBAR_HEIGHT = GamaPreferences
	// .create("pref_view_toolbar_height", "Reduce the height of views' toolbars", false, IType.BOOL, false)
	// .in(GamaPreferences.Interface.NAME, GamaPreferences.Interface.APPEARANCE);

	/**
	 * The Class GamaComposite.
	 */
	public static class GamaComposite extends Composite {

		/** The displayer. */
		ITooltipDisplayer displayer;

		/**
		 * Instantiates a new gama composite.
		 *
		 * @param parent
		 *            the parent
		 * @param displayer
		 *            the displayer
		 */
		public GamaComposite(final Composite parent, final ITooltipDisplayer displayer) {
			super(parent, SWT.None);
			this.displayer = displayer;
		}

	}

	/**
	 * Find tooltip displayer.
	 *
	 * @param c
	 *            the c
	 * @return the i tooltip displayer
	 */
	public static ITooltipDisplayer findTooltipDisplayer(final Control c) {
		final GamaComposite gc = findGamaComposite(c);
		return gc == null ? null : gc.displayer;
	}

	/**
	 * Find gama composite.
	 *
	 * @param c
	 *            the c
	 * @return the gama composite
	 */
	public static GamaComposite findGamaComposite(final Control c) {
		if (c instanceof Shell) return null;
		if (c instanceof GamaComposite) return (GamaComposite) c;
		return findGamaComposite(c.getParent());
	}

	/**
	 * The Class ToggleAction.
	 */
	public static abstract class ToggleAction extends Action {

		/**
		 * Instantiates a new toggle action.
		 */
		ToggleAction() {
			super("Toggle toolbar", IAction.AS_PUSH_BUTTON);
			setId("toolbar.toggle");
			setIcon(true);
		}

		/**
		 * Sets the icon.
		 *
		 * @param show
		 *            the new icon
		 */
		protected abstract void setIcon(boolean show);

	}

	/**
	 * The Class ExpandAll.
	 */
	public static class ExpandAll extends Action {

		/**
		 * Instantiates a new expand all.
		 */
		ExpandAll() {
			super("Expand all items", IAction.AS_PUSH_BUTTON);
			setIcon();
		}

		/**
		 * Sets the icon.
		 */
		protected void setIcon() {
			setImageDescriptor(GamaIcons.create("action.toolbar.expand2").descriptor());
		}

	}

	/**
	 * The Class CollapseAll.
	 */
	public static class CollapseAll extends Action {

		/**
		 * Instantiates a new collapse all.
		 */
		CollapseAll() {
			super("Collapse all items", IAction.AS_PUSH_BUTTON);
			setIcon();
		}

		/**
		 * Sets the icon.
		 */
		protected void setIcon() {
			setImageDescriptor(GamaIcons.create("action.toolbar.collapse2").descriptor());
		}

	}

	/**
	 * The Class ToggleSideControls.
	 */
	public static class ToggleSideControls extends Action {

		/**
		 * Instantiates a new toggle side controls.
		 */
		ToggleSideControls() {
			super("Toggle Side Controls", IAction.AS_PUSH_BUTTON);
			setIcon();
		}

		/**
		 * Sets the icon.
		 */
		protected void setIcon() {
			setImageDescriptor(GamaIcons.create("action.toolbar.toggle.side2").descriptor());
		}

	}

	/**
	 * The Class ToggleOverlay.
	 */
	public static class ToggleOverlay extends Action {

		/**
		 * Instantiates a new toggle overlay.
		 */
		ToggleOverlay() {
			super("Toggle Overlay", IAction.AS_PUSH_BUTTON);
			setIcon();
		}

		/**
		 * Sets the icon.
		 */
		protected void setIcon() {
			setImageDescriptor(GamaIcons.create("action.toolbar.toggle.overlay2").descriptor());
		}

	}

	/** The toolbar height. */
	public static int TOOLBAR_HEIGHT = 24; // CORE_ICONS_HEIGHT.getValue();

	/** The toolbar sep. */
	public static int TOOLBAR_SEP = 4;

	/**
	 * Creates a new GamaToolbar object.
	 *
	 * @param view
	 *            the view
	 * @param composite
	 *            the composite
	 * @return the composite
	 */
	private static Composite createIntermediateCompositeFor(final IToolbarDecoratedView view,
			final Composite composite) {
		// First, we create the background composite
		final FillLayout backgroundLayout = new FillLayout(SWT.VERTICAL);
		backgroundLayout.marginHeight = 0;
		backgroundLayout.marginWidth = 0;
		backgroundLayout.spacing = 0;
		composite.setLayout(backgroundLayout);
		Composite parentComposite;
		if (view instanceof ITooltipDisplayer) {
			parentComposite = new GamaComposite(composite, (ITooltipDisplayer) view);
		} else {
			parentComposite = new Composite(composite, SWT.None);
		}
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.horizontalSpacing = 0;
		parentLayout.verticalSpacing = 0;
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentComposite.setLayout(parentLayout);
		return parentComposite;
	}

	/**
	 * Gets the layout data for child.
	 *
	 * @return the layout data for child
	 */
	public static GridData getLayoutDataForChild() {
		final GridData result = new GridData(SWT.FILL, SWT.FILL, true, true);
		result.verticalSpan = 5;
		return result;
	}

	/**
	 * Gets the layout for child.
	 *
	 * @return the layout for child
	 */
	public static FillLayout getLayoutForChild() {
		final FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}

	/**
	 * Creates a new GamaToolbar object.
	 *
	 * @param composite
	 *            the composite
	 * @return the composite
	 */
	public static Composite createToolbarComposite(final Composite composite) {
		final Composite toolbarComposite = new Composite(composite, SWT.None);
		final GridData toolbarCompositeData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		toolbarComposite.setLayoutData(toolbarCompositeData2);
		final GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		final int margin = 0; // REDUCED_VIEW_TOOLBAR_HEIGHT.getValue() ? -1 : 0;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		layout.marginHeight = margin;
		toolbarComposite.setLayout(layout);
		// toolbarComposite.setBackground(IGamaColors.WHITE.color());
		GamaColors.setBackground(isDark() ? composite.getShell().getBackground() : IGamaColors.WHITE.color(),
				toolbarComposite);
		return toolbarComposite;

	}

	/**
	 * Creates a new GamaToolbar object.
	 *
	 * @param view
	 *            the view
	 * @param composite
	 *            the composite
	 * @return the composite
	 */
	public static Composite createToolbars(final IToolbarDecoratedView view, final Composite composite) {
		final Composite intermediateComposite = createIntermediateCompositeFor(view, composite);
		final Composite toolbarComposite = createToolbarComposite(intermediateComposite);
		final Composite childComposite = new Composite(intermediateComposite, SWT.None);
		childComposite.setLayoutData(getLayoutDataForChild());
		childComposite.setLayout(getLayoutForChild());

		final GamaToolbar2 tb =
				new GamaToolbar2(toolbarComposite, SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS, TOOLBAR_HEIGHT);
		final GridData data = layoutDataForToolbar();
		tb.setLayoutData(data);
		composite.addDisposeListener(e -> disposeToolbar(view, tb));
		buildToolbar(view, tb);

		// Creating the toggles
		final ToggleAction toggle = new ToggleAction() {

			@Override
			public void run() {

				final boolean show = !tb.isVisible();
				DEBUG.OUT("Show toolbar " + ((IViewPart) view).getTitle() + " " + show);
				tb.setVisible(show);
				((GridData) tb.getLayoutData()).exclude = !show;
				tb.getParent().setVisible(show);
				tb.getParent().getParent().layout();
				setIcon(show);
			}

			@Override
			protected void setIcon(final boolean show) {
				setImageDescriptor(GamaIcons
						.create(show ? "action.toolbar.toggle.small2" : "action.toolbar.toggle.small3").descriptor());
			}
		};

		tb.setToogleAction(toggle);

		// Install the toogles in the view site
		final IWorkbenchSite site = view.getSite();
		if (site instanceof IViewSite) {
			final IToolBarManager tm = ((IViewSite) site).getActionBars().getToolBarManager();
			tm.add(toggle);
			if (view instanceof IToolbarDecoratedView.Expandable) {
				final Action collapseAll = new CollapseAll() {
					@Override
					public void run() {
						((IToolbarDecoratedView.Expandable) view).collapseAll();
					}
				};
				final Action expandAll = new ExpandAll() {
					@Override
					public void run() {
						((IToolbarDecoratedView.Expandable) view).expandAll();
					}
				};
				tm.add(collapseAll);
				tm.add(expandAll);
			}

			if (view instanceof IGamaView.Display) {
				// final Action toggleSideControls = new ToggleSideControls() {
				// @Override
				// public void run() {
				// ((IGamaView.Display) view).toggleSideControls();
				// }
				// };

				final Action toggleOverlay = new ToggleOverlay() {
					@Override
					public void run() {
						((IGamaView.Display) view).toggleOverlay();
					}
				};
				tm.add(toggleOverlay);
				// tm.add(toggleSideControls);
			}
			tm.update(true);
		}

		return childComposite;
	}

	/**
	 * Layout data for toolbar.
	 *
	 * @return the grid data
	 */
	public static GridData layoutDataForToolbar() {
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.minimumWidth = TOOLBAR_HEIGHT * 2;
		return data;
	}

	/**
	 * Dispose toolbar.
	 *
	 * @param view
	 *            the view
	 * @param tb
	 *            the tb
	 */
	public static void disposeToolbar(final IToolbarDecoratedView view, final GamaToolbar2 tb) {
		if (tb != null && !tb.isDisposed()) { tb.dispose(); }
	}

	/**
	 * Builds the toolbar.
	 *
	 * @param view
	 *            the view
	 * @param tb
	 *            the tb
	 */
	public static void buildToolbar(final IToolbarDecoratedView view, final GamaToolbar2 tb) {
		if (view instanceof IToolbarDecoratedView.Sizable) {
			final FontSizer fs = new FontSizer((IToolbarDecoratedView.Sizable) view);
			fs.install(tb);
		}
		if (view instanceof IToolbarDecoratedView.Pausable) {
			final FrequencyController fc = new FrequencyController((IToolbarDecoratedView.Pausable) view);
			fc.install(tb);
		}
		if (view instanceof IToolbarDecoratedView.Zoomable) {
			final ZoomController zc = new ZoomController((IToolbarDecoratedView.Zoomable) view);
			zc.install(tb);
		}
		if (view instanceof IToolbarDecoratedView.Colorizable) {
			final BackgroundChooser b = new BackgroundChooser((IToolbarDecoratedView.Colorizable) view);
			b.install(tb);
		}
		if (view instanceof IToolbarDecoratedView.CSVExportable) {
			final CSVExportationController csv =
					new CSVExportationController((IToolbarDecoratedView.CSVExportable) view);
			csv.install(tb);
		}
		if (view instanceof IToolbarDecoratedView.LogExportable) {
			final LogExportationController log =
					new LogExportationController((IToolbarDecoratedView.LogExportable) view);
			log.install(tb);
		}

		view.createToolItems(tb);
		tb.refresh(true);
	}

	/**
	 * Visually update.
	 *
	 * @param tb
	 *            the tb
	 */
	// public static void visuallyUpdate(final ToolBar tb) {
	// Not needed anymore in Eclipse 2021-09. See issue #3210
	// WorkbenchHelper.runInUI("", 0, m -> {
	// if (tb.isDisposed()) return;
	// for (ToolItem o : tb.getItems()) {
	//
	// o.setImage(o.getImage());
	//
	// }
	// tb.layout(true, true);
	// tb.redraw();
	// tb.update();
	// });

	// }

}
