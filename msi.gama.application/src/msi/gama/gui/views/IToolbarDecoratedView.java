/**
 * Created by drogoul, 7 déc. 2014
 *
 */
package msi.gama.gui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.outputs.IDisplayOutput;

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

	// public void setToolbar(GamaToolbar2 toolbar);

	// public Integer[] getToolbarActionsId();

	public void createToolItems(GamaToolbar2 tb);

	public static interface Pausable extends IToolbarDecoratedView {

		public void pauseChanged();

		public IDisplayOutput getOutput();

		public void synchronizeChanged();
	}

	public static interface Sizable extends IToolbarDecoratedView {

		Control getSizableFontControl();
	}

	public static interface Colorizable extends IToolbarDecoratedView {

		public String[] getColorLabels();

		public GamaUIColor getColor(int index);

		public void setColor(int index, GamaUIColor c);
	}

	public static interface Zoomable extends IToolbarDecoratedView {

		void zoomIn();

		void zoomOut();

		void zoomFit();

		/**
		 * @return the controls that will react to gestures / mouse doucle-cliks
		 */
		Control[] getZoomableControls();

		/**
		 * @return true if the scroll triggers the zooming
		 */
		boolean zoomWhenScrolling();

	}

	/**
	 * @param toggle
	 */
	public void setToogle(Action toggle);

}
