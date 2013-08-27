/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.GamaViewPart;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IViewSite;

/**
 * The class GamaContributionItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public abstract class GamaViewItem implements IContributionItem {

	GamaViewPart view;
	private final IContributionItem item;
	boolean disposed = false;

	GamaViewItem(final GamaViewPart view) {
		this.view = view;
		item = createItem();
	}

	protected void updateToolbar() {
		((IViewSite) view.getSite()).getActionBars().updateActionBars();
	}

	GamaViewPart getView() {
		return view;
	}

	protected ImageDescriptor getImageDescriptor(final String id) {
		return SwtGui.getImageDescriptor(id);
	}

	protected abstract IContributionItem createItem();

	/**
	 * @see org.eclipse.jface.action.IContributionItem#dispose()
	 */
	@Override
	public void dispose() {
		if ( disposed ) { return; }
		try {
			item.dispose();
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			disposed = true;
		}

	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void fill(final Composite parent) {
		item.fill(parent);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Menu, int)
	 */
	@Override
	public void fill(final Menu parent, final int index) {
		item.fill(parent, index);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.ToolBar, int)
	 */
	@Override
	public void fill(final ToolBar parent, final int index) {
		item.fill(parent, index);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.CoolBar, int)
	 */
	@Override
	public void fill(final CoolBar parent, final int index) {
		item.fill(parent, index);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#getId()
	 */
	@Override
	public String getId() {
		return item.getId();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return item.isEnabled();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return item.isDirty();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return item.isDynamic();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isGroupMarker()
	 */
	@Override
	public boolean isGroupMarker() {
		return item.isGroupMarker();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isSeparator()
	 */
	@Override
	public boolean isSeparator() {
		return item.isSeparator();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return item.isVisible();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#saveWidgetState()
	 */
	@Override
	public void saveWidgetState() {
		item.saveWidgetState();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#setParent(org.eclipse.jface.action.IContributionManager)
	 */
	@Override
	public void setParent(final IContributionManager parent) {
		item.setParent(parent);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean visible) {
		item.setVisible(visible);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#update()
	 */
	@Override
	public void update() {
		item.update();
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#update(java.lang.String)
	 */
	@Override
	public void update(final String id) {
		item.update(id);
	}

}
