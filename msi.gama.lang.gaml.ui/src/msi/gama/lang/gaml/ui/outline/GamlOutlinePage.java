/**
 * Created by drogoul, 24 nov. 2014
 * 
 */
package msi.gama.lang.gaml.ui.outline;

import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.ui.editor.outline.impl.OutlinePage;

/**
 * The class GamlOutlinePage.
 * 
 * @author drogoul
 * @since 24 nov. 2014
 * 
 */
public class GamlOutlinePage extends OutlinePage implements IToolbarDecoratedView {

	GamaToolbar2 toolbar;
	protected Composite intermediate;

	public GamlOutlinePage() {}

	@Override
	protected void configureActions() {
		super.configureActions();

		IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		toolbar.wipe(SWT.RIGHT);
		for ( IContributionItem item : tbm.getItems() ) {
			toolbar.item(item, SWT.RIGHT);
			// item.fill(toolbar, toolbar.getItemCount());
		}
		tbm.removeAll();
		tbm.update(true);
	}

	@Override
	public Control getControl() {
		return intermediate;
	}

	@Override
	public void createControl(final Composite compo) {
		intermediate = new Composite(compo, SWT.NONE);
		Composite parent = GamaToolbarFactory.createToolbars(this, intermediate);
		super.createControl(parent);
	}

	@Override
	protected int getDefaultExpansionLevel() {
		return 2;
	}

	/**
	 * @see msi.gama.gui.views.IToolbarDecoratedView#setToolbars(msi.gama.gui.swt.controls.GamaToolbar, msi.gama.gui.swt.controls.GamaToolbar)
	 */
	@Override
	public void setToolbars(final GamaToolbarSimple left, final GamaToolbarSimple right) {
		// leftToolbar = left;
		// rightToolbar = right;
	}

	/**
	 * @see msi.gama.gui.views.IToolbarDecoratedView#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { -100, -101 };
	}

	/**
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar)
	 */
	@Override
	public void createToolItem(final int code, final GamaToolbarSimple tb) {}

	/**
	 * @see msi.gama.gui.views.IToolbarDecoratedView#setToolbar(msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void setToolbar(final GamaToolbar2 toolbar) {
		this.toolbar = toolbar;
	}

	/**
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void createToolItem(final int code, final GamaToolbar2 tb) {}

}
