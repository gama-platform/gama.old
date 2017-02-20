/*********************************************************************************************
 *
 * 'IToolbarDecoratedView.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchSite;

import msi.gama.outputs.IDisplayOutput;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;

/**
 * Class IToolbarDecoratedView.
 *
 * @author drogoul
 * @since 7 déc. 2014
 *
 */
public interface IToolbarDecoratedView {

	public IWorkbenchSite getSite();

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

	public static interface CSVExportable extends IToolbarDecoratedView {

		public void saveAsCSV();

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

}
