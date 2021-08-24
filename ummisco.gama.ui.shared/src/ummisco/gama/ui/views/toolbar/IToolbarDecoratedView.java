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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchSite;

import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;

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

	public interface LogExportable extends IToolbarDecoratedView {
		default void saveAsLog() {
			String text = getContents();
			FileDialog fd = new FileDialog(WorkbenchHelper.getShell(), SWT.SAVE);
			fd.setText("Choose a destination file");
			fd.setFilterExtensions(new String[] { "*.log" });
			if (GAMA.getExperiment() != null && GAMA.getExperiment().getAgent() != null) {
				fd.setFilterPath(GAMA.getExperiment().getAgent().getProjectPath());
			} else {
				fd.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
			}
			String f = fd.open();
			if (f == null) return;
			try {
				Files.writeString(Path.of(f), text, StandardCharsets.UTF_8);
			} catch (IOException e) {}

		}

		String getContents();
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
