package ummisco.gama.ui.viewers.gis;

import java.io.*;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.EditorPart;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.*;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;

import msi.gama.metamodel.shape.IShape;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.viewers.gis.geotools.SwtMapPane;
import ummisco.gama.ui.viewers.gis.geotools.event.MapMouseEvent;
import ummisco.gama.ui.viewers.gis.geotools.tool.CursorTool;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

public abstract class GISFileViewer extends EditorPart implements IToolbarDecoratedView.Zoomable, IToolbarDecoratedView.CSVExportable {

	public class DragTool extends CursorTool {

		private Point panePos;
		boolean panning;

		public DragTool() {
			super(CursorTool.ANY_BUTTON);
		}

		@Override
		public void onMousePressed(final MapMouseEvent ev) {
			panePos = ev.getPoint();
			panning = true;
		}

		@Override
		public void onMouseDragged(final MapMouseEvent ev) {
			if ( panning ) {
				Point pos = ev.getPoint();
				if ( !pos.equals(panePos) ) {
					pane.moveImage(pos.x - panePos.x, pos.y - panePos.y);
					panePos = pos;
				}
			}
		}

		@Override
		public void onMouseReleased(final MapMouseEvent ev) {
			if ( panning ) {
				panning = false;
				getMapPane().redraw();
			}
		}

		@Override
		public boolean canDraw() {
			return false;
		}

		@Override
		public boolean canMove() {
			return true;
		}
	}

	SwtMapPane pane;
	MapContent content;
	GamaToolbar2 toolbar;
	IFile file;
	SimpleFeatureSource featureSource;
	boolean noCRS = false;
	Style style;
	Layer layer;
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
		Composite parent = GamaToolbarFactory.createToolbars(this, composite);
		displayInfoString();
		pane = new SwtMapPane(parent, SWT.NO_BACKGROUND, new StreamingRenderer(), content);
		pane.setBackground(GamaColors.system(SWT.COLOR_WHITE));
		pane.setCursorTool(new DragTool());
		pane.redraw();
	}

	protected abstract void displayInfoString();

	@Override
	public void setFocus() {
		pane.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		if ( content != null ) {
			content.dispose();
			content = null;
		}
	}

	@Override
	public void zoomIn() {
		ReferencedEnvelope env = pane.getDisplayArea();
		env.expandBy(-env.getWidth() / 10, -env.getHeight() / 10);
		pane.setDisplayArea(env);
	}

	@Override
	public void zoomOut() {
		ReferencedEnvelope env = pane.getDisplayArea();
		env.expandBy(env.getWidth() / 10, env.getHeight() / 10);
		pane.setDisplayArea(env);
	}

	@Override
	public void zoomFit() {
		pane.reset();
	}

	public Control getMapComposite() {
		return pane;
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { pane };
	}

	/**
	 * Method createToolItem()
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int, ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
	}

	/**
	 * Method zoomWhenScrolling()
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Zoomable#zoomWhenScrolling()
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return false;
	}

	public DragTool newDragTool() {
		return new DragTool();
	}
	//
	// @Override
	// public void setToogle(final Action toggle) {}

	public void saveAsCSV(final List<String> attributes, final List<IShape> geoms, final String name) {
		String path = "";
		String[] decomp = pathStr.split("\\.");
		for ( int i = 0; i < decomp.length - 1; i++ ) {
			path += decomp[i] + (i < decomp.length - 1 ? "." : "");
		}
		if ( name != null ) {
			path += name + ".";
		} else {
			path += ".";
		}
		path += "csv";
		File fcsv = new File(path);
		FileWriter fw;
		try {
			fw = new FileWriter(fcsv, false);
			fw.write("id");
			for ( String att : attributes ) {
				fw.write(";" + att);
			}
			fw.write(Strings.LN);
			if ( geoms != null ) {
				int cpt = 0;
				for ( IShape obj : geoms ) {
					fw.write(cpt + "");
					cpt++;
					for ( String v : attributes ) {
						String val = obj.hasAttribute(v) ? obj.getAttribute(v).toString().replace(';', ',') : "-";
						fw.write(";" + val);
					}
					fw.write(Strings.LN);
				}
			} else {
				for ( Object obj : layer.getFeatureSource().getFeatures().toArray() ) {
					SimpleFeature feature = (SimpleFeature) obj;
					fw.write(feature.getID());
					for ( String v : attributes ) {
						fw.write(";" + feature.getAttribute(v).toString().replace(';', ','));
					}
					fw.write(Strings.LN);
				}
			}

			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
