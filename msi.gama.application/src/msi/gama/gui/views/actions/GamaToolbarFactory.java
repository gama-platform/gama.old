/*********************************************************************************************
 *
 *
 * 'GamaToolbarFactory.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.IToolbarDecoratedView.Colorizable;

/**
 * The class GamaToolbarFactory.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public class GamaToolbarFactory {

	public static class GamaComposite extends Composite {

		ITooltipDisplayer displayer;

		public GamaComposite(final Composite parent, final ITooltipDisplayer displayer) {
			super(parent, SWT.None);
			this.displayer = displayer;
		}

	}

	public static ITooltipDisplayer findTooltipDisplayer(final Control c) {
		GamaComposite gc = findGamaComposite(c);
		return gc == null ? null : gc.displayer;
	}

	public static GamaComposite findGamaComposite(final Control c) {
		if ( c instanceof Shell ) { return null; }
		if ( c instanceof GamaComposite ) { return (GamaComposite) c; }
		// Control t = c;
		return findGamaComposite(c.getParent());
	}

	public static class ToggleAction extends Action {

		boolean show = true;

		ToggleAction() {
			super("Toggle toolbar", IAction.AS_PUSH_BUTTON);
			setIcon();
		}

		protected void setIcon() {
			setImageDescriptor(
				GamaIcons.create(show ? "action.toolbar.toggle.small2" : "action.toolbar.toggle.small3").descriptor());
		}

	}

	public static int TOOLBAR_HEIGHT = GamaIcons.CORE_ICONS_HEIGHT.getValue();
	public static int TOOLBAR_SEP = 4;

	private static Composite createIntermediateCompositeFor(final IToolbarDecoratedView view,
		final Composite composite) {
		// First, we create the background composite
		FillLayout backgroundLayout = new FillLayout(SWT.VERTICAL);
		backgroundLayout.marginHeight = 0;
		backgroundLayout.marginWidth = 0;
		composite.setLayout(backgroundLayout);
		Composite parentComposite;
		if ( view instanceof ITooltipDisplayer ) {
			parentComposite = new GamaComposite(composite, (ITooltipDisplayer) view);
		} else {
			parentComposite = new Composite(composite, SWT.None);
		}
		GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.horizontalSpacing = 0;
		parentLayout.verticalSpacing = 0;
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;
		parentComposite.setLayout(parentLayout);
		return parentComposite;
	}

	public static GridData getLayoutDataForChild() {
		GridData result = new GridData(SWT.FILL, SWT.FILL, true, true);
		result.verticalSpan = 5;
		return result;
	}

	public static FillLayout getLayoutForChild() {
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}

	private static Composite createToolbarComposite(final IToolbarDecoratedView view, final Composite composite) {
		final Composite toolbarComposite = new Composite(composite, SWT.None);
		final GridData toolbarCompositeData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		toolbarComposite.setLayoutData(toolbarCompositeData2);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		toolbarComposite.setLayout(layout);
		toolbarComposite.setBackground(IGamaColors.WHITE.color());
		// Creating the toggle
		Action toggle = new ToggleAction() {

			@Override
			public void run() {
				show = !show;
				toolbarCompositeData2.exclude = !show;
				toolbarComposite.setVisible(show);
				toolbarComposite.getParent().layout();
				setIcon();
			}
		};
		// Install the toogle in the view site
		IWorkbenchSite site = view.getSite();
		if ( site instanceof IViewSite ) {
			IToolBarManager tm = ((IViewSite) site).getActionBars().getToolBarManager();
			tm.add(toggle);
			tm.update(true);
			view.setToogle(toggle);
		} else if ( site instanceof IEditorSite ) {
			// WARNING Disabled for the moment.
			// IActionBars tm = ((IEditorSite) site).getActionBars();
			// tm.getToolBarManager().add(toggle);
			// tm.updateActionBars();
		}
		return toolbarComposite;

	}

	public static Composite createToolbars(final IToolbarDecoratedView view, final Composite composite) {
		final Composite intermediateComposite = createIntermediateCompositeFor(view, composite);
		final Composite toolbarComposite = createToolbarComposite(view, intermediateComposite);
		Composite childComposite = new Composite(intermediateComposite, SWT.None);
		childComposite.setLayoutData(getLayoutDataForChild());
		childComposite.setLayout(getLayoutForChild());

		final GamaToolbar2 tb =
			new GamaToolbar2(toolbarComposite, SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS, TOOLBAR_HEIGHT);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.minimumWidth = TOOLBAR_HEIGHT * 2;
		tb.setLayoutData(data);
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				disposeToolbar(view, tb);
			}
		});
		// intermediateComposite.addControlListener(new ControlAdapter() {
		//
		// /**
		// * Method controlResized()
		// * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
		// */
		// @Override
		// public void controlResized(final ControlEvent e) {
		// tb.refresh(true);
		// // intermediateComposite.removeControlListener(this);
		// }
		//
		// });
		buildToolbar(view, tb);
		return childComposite;
	}

	public static void disposeToolbar(final IToolbarDecoratedView view, final GamaToolbar2 tb) {
		if ( tb != null && !tb.isDisposed() ) {
			tb.dispose();
		}
	}

	public static void buildToolbar(final IToolbarDecoratedView view, final GamaToolbar2 tb) {
		if ( view instanceof IToolbarDecoratedView.Sizable ) {
			FontSizer fs = new FontSizer((IToolbarDecoratedView.Sizable) view);
			fs.install(tb);
		}
		if ( view instanceof IToolbarDecoratedView.Pausable ) {
			FrequencyController fc = new FrequencyController((IToolbarDecoratedView.Pausable) view);
			fc.install(tb);
		}
		if ( view instanceof IToolbarDecoratedView.Zoomable ) {
			ZoomController zc = new ZoomController((IToolbarDecoratedView.Zoomable) view);
			zc.install(tb);
		}
		if ( view instanceof IToolbarDecoratedView.Colorizable ) {
			BackgroundChooser b = new BackgroundChooser((Colorizable) view);
			b.install(tb);
		}
		if ( view instanceof IToolbarDecoratedView.CSVExportable ) {
			CSVExportationController csv = new CSVExportationController((IToolbarDecoratedView.CSVExportable) view);
			csv.install(tb);
		}
		
		view.createToolItems(tb);
		tb.refresh(true);
	}

}
