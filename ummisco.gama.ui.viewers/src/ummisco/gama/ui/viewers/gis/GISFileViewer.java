/*******************************************************************************************************
 *
 * GISFileViewer.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.gis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.EditorPart;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;

import msi.gama.metamodel.shape.IShape;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * The Class GISFileViewer.
 */
public abstract class GISFileViewer extends EditorPart
		implements IToolbarDecoratedView.Zoomable, IToolbarDecoratedView.CSVExportable {

	/** The pane. */
	SwtMapPane pane;
	
	/** The content. */
	MapContent content;
	
	/** The toolbar. */
	GamaToolbar2 toolbar;
	
	/** The file. */
	IFile file;
	
	/** The feature source. */
	SimpleFeatureSource featureSource;
	
	/** The no CRS. */
	boolean noCRS = false;
	
	/** The style. */
	Style style;
	
	/** The layer. */
	Layer layer;
	
	/** The path str. */
	String pathStr;

	@Override
	public void doSave(final IProgressMonitor monitor) {}

	@Override
	public void doSaveAs() {}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite composite) {
		final Composite parent = GamaToolbarFactory.createToolbars(this, composite);
		displayInfoString();
		pane = new SwtMapPane(parent, SWT.NO_BACKGROUND, new StreamingRenderer(), content);
		pane.setBackground(GamaColors.system(SWT.COLOR_WHITE));
		pane.redraw();
	}

	/**
	 * Display info string.
	 */
	protected abstract void displayInfoString();

	@Override
	public void setFocus() {
		pane.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		if (content != null) {
			content.dispose();
			content = null;
		}
	}

	@Override
	public void zoomIn() {
		final ReferencedEnvelope env = pane.getDisplayArea();
		env.expandBy(-env.getWidth() / 10, -env.getHeight() / 10);
		pane.setDisplayArea(env);
	}

	@Override
	public void zoomOut() {
		final ReferencedEnvelope env = pane.getDisplayArea();
		env.expandBy(env.getWidth() / 10, env.getHeight() / 10);
		pane.setDisplayArea(env);
	}

	@Override
	public void zoomFit() {
		pane.reset();
	}

	/**
	 * Gets the map composite.
	 *
	 * @return the map composite
	 */
	public Control getMapComposite() {
		return pane;
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { pane };
	}

	/**
	 * Method createToolItem()
	 *
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
	}

	/**
	 * Method zoomWhenScrolling()
	 *
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

	/**
	 * Save as CSV.
	 *
	 * @param attributes the attributes
	 * @param geoms the geoms
	 * @param name the name
	 */
	public void saveAsCSV(final List<String> attributes, final List<IShape> geoms, final String name) {
		String path = "";
		final String[] decomp = pathStr.split("\\.");
		for (int i = 0; i < decomp.length - 1; i++) {
			path += decomp[i] + (i < decomp.length - 2 ? "." : "");
		}
		if (name != null) {
			path += name + ".";
		} else {
			path += ".";
		}
		path += "csv";
		final File fcsv = new File(path);
		try (FileWriter fw = new FileWriter(fcsv, false)) {
			fw.write("id");
			for (final String att : attributes) {
				fw.write(";" + att);
			}
			fw.write(Strings.LN);
			if (geoms != null) {
				int cpt = 0;
				for (final IShape obj : geoms) {
					fw.write(cpt + "");
					cpt++;
					for (final String v : attributes) {
						final String val = obj.hasAttribute(v) ? obj.getAttribute(v).toString().replace(';', ',') : "-";
						fw.write(";" + val);
					}
					fw.write(Strings.LN);
				}
			} else {
				for (final Object obj : layer.getFeatureSource().getFeatures().toArray()) {
					final SimpleFeature feature = (SimpleFeature) obj;
					fw.write(feature.getID());
					for (final String v : attributes) {
						fw.write(";" + feature.getAttribute(v).toString().replace(';', ','));
					}
					fw.write(Strings.LN);
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
