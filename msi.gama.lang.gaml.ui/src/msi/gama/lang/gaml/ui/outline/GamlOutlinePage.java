/**
 * Created by drogoul, 24 nov. 2014
 * 
 */
package msi.gama.lang.gaml.ui.outline;

import msi.gama.gui.swt.GamaColors.GamaUIColor;
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
public class GamlOutlinePage extends OutlinePage implements IToolbarDecoratedView, ITooltipDisplayer {

	// protected GamaToolbar leftToolbar, rightToolbar;
	GamaToolbar2 toolbar;
	protected Composite intermediate;

	public GamlOutlinePage() {}

	@Override
	protected void configureActions() {
		super.configureActions();

		IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
		toolbar.wipe(SWT.RIGHT);
		for ( IContributionItem item : tbm.getItems() ) {
			item.fill(toolbar, toolbar.getItemCount());
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
	public void setToolbars(final GamaToolbar left, final GamaToolbar right) {
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
	public void createToolItem(final int code, final GamaToolbar tb) {}

	/**
	 * @see msi.gama.gui.swt.controls.ITooltipDisplayer#stopDisplayingTooltips()
	 */
	@Override
	public void stopDisplayingTooltips() {}

	/**
	 * @see msi.gama.gui.swt.controls.ITooltipDisplayer#displayTooltip(java.lang.String, msi.gama.gui.swt.GamaColors.GamaUIColor)
	 */
	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {}

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
