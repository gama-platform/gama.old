/*******************************************************************************************************
 *
 * IToolbarDecoratedView.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.toolbar;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchSite;

import msi.gama.common.preferences.GamaPreferences;
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

	ICameraHelper NULL_CAMERA = new ICameraHelper() {};

	public interface ICameraHelper {
		/**
		 * Gets the camera names.
		 *
		 * @return the camera names
		 */
		default Collection<String> getCameraNames() { return Collections.EMPTY_LIST; }

		/**
		 * Sets the camera name.
		 *
		 * @param p
		 *            the new camera name
		 */
		default void setCameraName(final String p) {}

		/**
		 * Gets the camera name.
		 *
		 * @return the camera name
		 */
		default String getCameraName() { return GamaPreferences.Displays.OPENGL_DEFAULT_CAM.getValue(); }

		/**
		 * Checks if is camera locked.
		 *
		 * @return true, if is camera locked
		 */
		default boolean isCameraLocked() { return false; }

		/**
		 * Toggle camera.
		 */
		default void toggleCamera() {}

		default String getCameraDefinition() { return ""; }
	}

	/**
	 * Gets the site.
	 *
	 * @return the site
	 */
	IWorkbenchSite getSite();

	/**
	 * Creates the tool items.
	 *
	 * @param tb
	 *            the tb
	 */
	void createToolItems(GamaToolbar2 tb);

	/**
	 * Adds the state listener. ✓ Unicode: U+2713, UTF-8: E2 9C 93
	 *
	 * @param listener
	 *            the listener
	 */
	default void addStateListener(final StateListener listener) {}

	/**
	 * The listener interface for receiving state events. The class that is interested in processing a state event
	 * implements this interface, and the object created with that class is registered with a component using the
	 * component's <code>addStateListener<code> method. When the state event occurs, that object's appropriate method is
	 * invoked.
	 *
	 * @see StateEvent
	 */
	public interface StateListener {

		/**
		 * Update to reflect state.
		 */
		void updateToReflectState();
	}

	/**
	 * The Interface Expandable.
	 */
	public interface Expandable extends IToolbarDecoratedView {

		/**
		 * Expand all.
		 */
		void expandAll();

		/**
		 * Collapse all.
		 */
		void collapseAll();
	}

	/**
	 * The Interface Pausable.
	 */
	public interface Pausable extends IToolbarDecoratedView {

		/**
		 * Pause changed.
		 */
		void pauseChanged();

		/**
		 * Gets the output.
		 *
		 * @return the output
		 */
		IDisplayOutput getOutput();

	}

	/**
	 * The Interface Sizable.
	 */
	public interface Sizable extends IToolbarDecoratedView {

		/**
		 * Gets the sizable font control.
		 *
		 * @return the sizable font control
		 */
		Control getSizableFontControl();
	}

	/**
	 * The Interface Colorizable.
	 */
	public interface Colorizable extends IToolbarDecoratedView {

		/**
		 * Gets the color labels.
		 *
		 * @return the color labels
		 */
		String[] getColorLabels();

		/**
		 * Gets the color.
		 *
		 * @param index
		 *            the index
		 * @return the color
		 */
		GamaUIColor getColor(int index);

		/**
		 * Sets the color.
		 *
		 * @param index
		 *            the index
		 * @param c
		 *            the c
		 */
		void setColor(int index, GamaUIColor c);
	}

	/**
	 * The Interface CSVExportable.
	 */
	public interface CSVExportable extends IToolbarDecoratedView {

		/**
		 * Save as CSV.
		 */
		void saveAsCSV();
	}

	/**
	 * The Interface LogExportable.
	 */
	public interface LogExportable extends IToolbarDecoratedView {

		/**
		 * Save as log.
		 */
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

		/**
		 * Gets the contents.
		 *
		 * @return the contents
		 */
		String getContents();
	}

	/**
	 * The Interface Zoomable.
	 */
	public interface Zoomable extends IToolbarDecoratedView {

		/**
		 * Zoom in.
		 */
		void zoomIn();

		/**
		 * Zoom out.
		 */
		void zoomOut();

		/**
		 * Zoom fit.
		 */
		void zoomFit();

		/**
		 * @return the controls that will react to gestures / mouse doucle-cliks
		 */
		Control[] getZoomableControls();

		/**
		 * @return true if the scroll triggers the zooming
		 */
		boolean zoomWhenScrolling();

		default ICameraHelper getCameraHelper() { return NULL_CAMERA; }

		/**
		 * Checks for cameras.
		 *
		 * @return true, if successful
		 */
		default boolean hasCameras() {
			return false;
		}

	}

}
