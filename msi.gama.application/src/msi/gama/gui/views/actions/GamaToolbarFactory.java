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

import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView;
import org.eclipse.jface.action.*;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

/**
 * The class GamaToolbarFactory.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class GamaToolbarFactory implements IGamaViewActions {

	public static class GamaComposite extends Composite {

		ITooltipDisplayer displayer;

		public GamaComposite(final Composite parent, final ITooltipDisplayer displayer) {
			super(parent, SWT.None);
			this.displayer = displayer;
		}
	}

	public static ITooltipDisplayer findTooltipDisplayer(final Control c) {
		if ( c instanceof Shell ) { return null; }
		if ( c instanceof GamaComposite ) { return ((GamaComposite) c).displayer; }
		String parents = "";
		Control t = c;
		// while (t != null && !(t instanceof Shell)) {
		// parents = t.getClass().getSimpleName() + " > " + parents;
		// t = t.getParent();
		// }
		// System.out.println("Hierarchy: " + parents);
		return findTooltipDisplayer(c.getParent());
	}

	public static class ToggleAction extends Action {

		boolean show = true;

		ToggleAction() {
			super("Toggle toolbar", IAction.AS_PUSH_BUTTON);
			setIcon();
		}

		protected void setIcon() {
			setImageDescriptor(GamaIcons.create(show ? "action.toolbar.toggle2" : "action.toolbar.toggle3")
				.descriptor());
		}

	}

	public static int TOOLBAR_HEIGHT = GamaIcons.CORE_ICONS_HEIGHT.getValue();
	public static int TOOLBAR_SEP = 10;

	private static void
		createContributionItemOld(final IToolbarDecoratedView view, final int code, final GamaToolbar tb) {
		switch (code) {
			case IToolbarDecoratedView.SEP:
				tb.sep(TOOLBAR_SEP);
				break;
			default:
				view.createToolItem(code, tb);
		}
	}

	private static void createContributionItem(final IToolbarDecoratedView view, final int code, final GamaToolbar2 tb) {
		switch (code) {
			case IToolbarDecoratedView.SEP:
				tb.sep(TOOLBAR_SEP, SWT.RIGHT);
				break;
			default:
				view.createToolItem(code, tb);
		}
	}

	private static Composite
		createIntermediateCompositeFor(final IToolbarDecoratedView view, final Composite composite) {
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

	private static Composite createToolbarCompositeOld(final IToolbarDecoratedView view, final Composite composite) {
		final Composite toolbarComposite = new Composite(composite, SWT.None);
		final GridData toolbarCompositeData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		toolbarComposite.setLayoutData(toolbarCompositeData2);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
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
			IToolBarManager2 tm = (IToolBarManager2) ((IViewSite) site).getActionBars().getToolBarManager();
			tm.add(toggle);
			tm.update(true);
		} else if ( site instanceof IEditorSite ) {
			// WARNING Disabled for the moment.
			// IActionBars tm = ((IEditorSite) site).getActionBars();
			// tm.getToolBarManager().add(toggle);
			// tm.updateActionBars();
		}
		return toolbarComposite;

	}

	private static Composite createToolbarComposite(final IToolbarDecoratedView view, final Composite composite) {
		final Composite toolbarComposite = new Composite(composite, SWT.None);
		final GridData toolbarCompositeData2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		toolbarComposite.setLayoutData(toolbarCompositeData2);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
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
			IToolBarManager2 tm = (IToolBarManager2) ((IViewSite) site).getActionBars().getToolBarManager();
			tm.add(toggle);
			tm.update(true);
		} else if ( site instanceof IEditorSite ) {
			// WARNING Disabled for the moment.
			// IActionBars tm = ((IEditorSite) site).getActionBars();
			// tm.getToolBarManager().add(toggle);
			// tm.updateActionBars();
		}
		return toolbarComposite;

	}

	public static Composite createToolbarsOld(final IToolbarDecoratedView view, final Composite composite) {
		final Composite intermediateComposite = createIntermediateCompositeFor(view, composite);
		final Composite toolbarComposite = createToolbarCompositeOld(view, intermediateComposite);
		Composite childComposite = new Composite(intermediateComposite, SWT.None);
		childComposite.setLayoutData(getLayoutDataForChild());
		childComposite.setLayout(getLayoutForChild());

		final GamaToolbar leftToolbar =
			new GamaToolbar(toolbarComposite, SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP | SWT.LEFT | SWT.NO_FOCUS)
				.height(TOOLBAR_HEIGHT);

		// leftToolbar.setBackground(IGamaColors.WHITE.color());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.LEFT;
		data.minimumWidth = TOOLBAR_HEIGHT * 2;
		leftToolbar.setLayoutData(data);

		final GamaToolbar rightToolbar =
			new GamaToolbar(toolbarComposite, SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP | SWT.LEFT | SWT.NO_FOCUS)
				.height(TOOLBAR_HEIGHT);
		// rightToolbar.setBackground(IGamaColors.WHITE.color());
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.RIGHT;
		data.minimumWidth = TOOLBAR_HEIGHT * 2;
		rightToolbar.setLayoutData(data);
		view.setToolbars(leftToolbar, rightToolbar);
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				disposeToolbarsOld(view, leftToolbar, rightToolbar);
			}
		});
		buildToolbarOld(view, rightToolbar, view.getToolbarActionsId());
		return childComposite;
	}

	public static Composite createToolbars(final IToolbarDecoratedView view, final Composite composite) {
		final Composite intermediateComposite = createIntermediateCompositeFor(view, composite);
		final Composite toolbarComposite = createToolbarComposite(view, intermediateComposite);
		Composite childComposite = new Composite(intermediateComposite, SWT.None);
		childComposite.setLayoutData(getLayoutDataForChild());
		childComposite.setLayout(getLayoutForChild());

		final GamaToolbar2 leftToolbar =
			new GamaToolbar2(toolbarComposite, SWT.FLAT | SWT.HORIZONTAL | SWT.NO_FOCUS, TOOLBAR_HEIGHT);

		// leftToolbar.setBackground(IGamaColors.WHITE.color());
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.verticalIndent = 0;
		data.horizontalAlignment = SWT.LEFT;
		data.minimumWidth = TOOLBAR_HEIGHT * 2;
		leftToolbar.setLayoutData(data);

		// final GamaToolbar rightToolbar =
		// new GamaToolbar(toolbarComposite, SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP | SWT.LEFT | SWT.NO_FOCUS)
		// .height(TOOLBAR_HEIGHT);
		// // rightToolbar.setBackground(IGamaColors.WHITE.color());
		// data = new GridData(SWT.FILL, SWT.FILL, true, false);
		// data.verticalIndent = 0;
		// data.horizontalAlignment = SWT.RIGHT;
		// data.minimumWidth = TOOLBAR_HEIGHT * 2;
		// rightToolbar.setLayoutData(data);
		view.setToolbar(leftToolbar);
		composite.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(final DisposeEvent e) {
				disposeToolbar(view, leftToolbar);
			}
		});
		buildToolbar(view, leftToolbar, view.getToolbarActionsId());
		return childComposite;
	}

	public static void disposeToolbarsOld(final IToolbarDecoratedView view, final GamaToolbar leftToolbar,
		final GamaToolbar rightToolbar) {
		if ( leftToolbar != null && !leftToolbar.isDisposed() ) {
			leftToolbar.dispose();
		}
		if ( rightToolbar != null && !rightToolbar.isDisposed() ) {
			rightToolbar.dispose();
		}
		view.setToolbars(null, null);
	}

	public static void disposeToolbar(final IToolbarDecoratedView view, final GamaToolbar2 leftToolbar) {
		if ( leftToolbar != null && !leftToolbar.isDisposed() ) {
			leftToolbar.dispose();
		}
		view.setToolbar(null);
	}

	public static void resetToolbarOld(final IToolbarDecoratedView view, final GamaToolbar tb) {
		if ( tb == null ) { return; }
		tb.wipe();
		buildToolbarOld(view, tb, view.getToolbarActionsId());
	}

	public static void resetToolbar(final IToolbarDecoratedView view, final GamaToolbar2 tb) {
		if ( tb == null ) { return; }
		tb.wipe(SWT.RIGHT);
		buildToolbar(view, tb, view.getToolbarActionsId());
	}

	public static void buildToolbarOld(final IToolbarDecoratedView view, final GamaToolbar tb, final Integer ... codes) {
		if ( codes == null ) { return; }
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
		for ( Integer i : codes ) {
			createContributionItemOld(view, i, tb);
		}
	}

	public static void buildToolbar(final IToolbarDecoratedView view, final GamaToolbar2 tb, final Integer ... codes) {
		if ( codes == null ) { return; }
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
		for ( Integer i : codes ) {
			createContributionItem(view, i, tb);
		}
	}

}
