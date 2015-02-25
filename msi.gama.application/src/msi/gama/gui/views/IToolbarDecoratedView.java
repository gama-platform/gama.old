/**
 * Created by drogoul, 7 déc. 2014
 * 
 */
package msi.gama.gui.views;

import msi.gama.gui.swt.controls.GamaToolbar;
import msi.gama.outputs.IDisplayOutput;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;

/**
 * Class IToolbarDecoratedView.
 * 
 * @author drogoul
 * @since 7 déc. 2014
 * 
 */
public interface IToolbarDecoratedView {

	public final static int SEP = Integer.MAX_VALUE;

	public IWorkbenchSite getSite();

	public void setToolbars(GamaToolbar left, GamaToolbar right);

	public Integer[] getToolbarActionsId();

	public void createToolItem(int code, GamaToolbar tb);

	public static interface Pausable extends IToolbarDecoratedView {

		public void pauseChanged();

		public IDisplayOutput getOutput();
	}

	public static interface Sizable extends IToolbarDecoratedView {

		Control getSizableFontControl();
	}

	public static interface Zoomable extends IToolbarDecoratedView {

		void zoomIn();

		void zoomOut();

		void zoomFit();

		Control getZoomableControl();

	}

}
