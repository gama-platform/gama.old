/*********************************************************************************************
 *
 * 'IToolbarDecoratedView.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
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

	IWorkbenchSite getSite();

	void createToolItems(GamaToolbar2 tb);

	default void addStateListener(final StateListener listener) {}

	public interface StateListener {
		void updateToReflectState();
	}

	public interface Expandable extends IToolbarDecoratedView {

		void expandAll();

		void collapseAll();
	}

	public interface Pausable extends IToolbarDecoratedView {

		void pauseChanged();

		IDisplayOutput getOutput();

		void synchronizeChanged();
	}

	public interface Sizable extends IToolbarDecoratedView {

		Control getSizableFontControl();
	}

	public interface Colorizable extends IToolbarDecoratedView {

		String[] getColorLabels();

		GamaUIColor getColor(int index);

		void setColor(int index, GamaUIColor c);
	}

	public interface CSVExportable extends IToolbarDecoratedView {

		void saveAsCSV();

	}

	public interface Zoomable extends IToolbarDecoratedView {

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
