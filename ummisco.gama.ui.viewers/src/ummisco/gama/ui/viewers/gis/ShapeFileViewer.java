package ummisco.gama.ui.viewers.gis;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.map.StyleLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import msi.gama.gui.navigator.FileMetaDataProvider;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.util.file.GamaShapeFile;
import msi.gama.util.file.GamaShapeFile.ShapeInfo;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.viewers.gis.geotools.styling.simple.Mode;
import ummisco.gama.ui.viewers.gis.geotools.styling.simple.SLDs;
import ummisco.gama.ui.viewers.gis.geotools.utils.Utils;

public class ShapeFileViewer extends GISFileViewer implements IToolbarDecoratedView.Colorizable {

	Mode mode;
	FeatureTypeStyle fts;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		final FileEditorInput fi = (FileEditorInput) input;
		file = fi.getFile();
		final IPath path = fi.getPath();
		final File f = path.makeAbsolute().toFile();
		try {
			pathStr = f.getAbsolutePath();
			final ShapefileDataStore store = new ShapefileDataStore(f.toURI().toURL());
			content = new MapContent();
			featureSource = store.getFeatureSource();
			style = Utils.createStyle(f, featureSource);
			layer = new FeatureLayer(featureSource, style);
			mode = determineMode(featureSource.getSchema(), "Polygon");
			final List<FeatureTypeStyle> ftsList = style.featureTypeStyles();
			if (ftsList.size() > 0) {
				fts = ftsList.get(0);
			} else {
				fts = null;
			}
			if (fts != null) {
				this.setFillColor(SwtGui.SHAPEFILE_VIEWER_FILL.getValue(), mode, fts);
				this.setStrokeColor(SwtGui.SHAPEFILE_VIEWER_LINE_COLOR.getValue(), mode, fts);
				((StyleLayer) layer).setStyle(style);
			}
			content.addLayer(layer);
		} catch (final IOException e) {
			System.out.println("Unable to view file " + path);
		}
		this.setPartName(path.lastSegment());
		setInput(input);
	}

	@Override
	protected void displayInfoString() {
		String s;
		GamaUIColor color;

		final GamaShapeFile.ShapeInfo info = (ShapeInfo) FileMetaDataProvider.getInstance().getMetaData(file, false,
				true);
		if (info == null) {
			s = "Error in reading file information";
			color = IGamaColors.ERROR;
		} else {
			s = info.getSuffix();
			if (info.getCRS() == null) {
				color = IGamaColors.WARNING;
				noCRS = true;
			} else {
				color = IGamaColors.OK;
			}
		}

		final ToolItem item = toolbar.menu(color, s, SWT.LEFT);
		if (info != null) {
			((FlatButton) item.getControl()).addSelectionListener(new SelectionAdapter() {

				Menu menu;

				@Override
				public void widgetSelected(final SelectionEvent e) {
					if (menu == null) {
						menu = new Menu(toolbar.getShell(), SWT.POP_UP);
						fillMenu();
					}
					final Point point = toolbar.toDisplay(new Point(e.x, e.y + toolbar.getSize().y));
					menu.setLocation(point.x, point.y);
					menu.setVisible(true);

				}

				private void fillMenu() {
					AgentsMenu.separate(menu, "Bounds");
					try {
						ReferencedEnvelope env = featureSource.getBounds();
						MenuItem m2 = new MenuItem(menu, SWT.NONE);
						m2.setEnabled(false);
						m2.setText("     - upper corner : " + env.getUpperCorner().getOrdinate(0) + " "
								+ env.getUpperCorner().getOrdinate(1));
						m2 = new MenuItem(menu, SWT.NONE);
						m2.setEnabled(false);
						m2.setText("     - lower corner : " + env.getLowerCorner().getOrdinate(0) + " "
								+ env.getLowerCorner().getOrdinate(1));
						if (!noCRS) {
							env = env.transform(new ProjectionFactory().getTargetCRS(), true);
						}
						m2 = new MenuItem(menu, SWT.NONE);
						m2.setEnabled(false);
						m2.setText(
								"     - dimensions : " + (int) env.getWidth() + "m x " + (int) env.getHeight() + "m");
					} catch (final Exception e) {
						e.printStackTrace();
					}
					AgentsMenu.separate(menu);
					AgentsMenu.separate(menu, "Attributes");
					try {
						for (final Map.Entry<String, String> entry : info.getAttributes().entrySet()) {
							final MenuItem m2 = new MenuItem(menu, SWT.NONE);
							m2.setEnabled(false);
							m2.setText("     - " + entry.getKey() + " (" + entry.getValue() + ")");
						}
						// java.util.List<AttributeDescriptor> att_list =
						// store.getSchema().getAttributeDescriptors();
					} catch (final Exception e) {
						e.printStackTrace();
					}

				}

			});
		}

	}

	public void setStrokeColor(final Color color, final Mode mode, final FeatureTypeStyle fts) {
		if (mode == Mode.LINE) {
			final LineSymbolizer sym = SLD.lineSymbolizer(fts);
			SLD.setLineColour(sym, color);
		} else if (mode == Mode.POLYGON) {
			final PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			final Stroke s = new StyleBuilder().createStroke(color);
			sym.setStroke(s);
		} else if (mode == Mode.POINT || mode == Mode.ALL) { // default to
																// handling as
																// Point
			final PointSymbolizer sym = SLD.pointSymbolizer(fts);
			SLD.setPointColour(sym, color);
		}
	}

	public Stroke getStroke(final Mode mode, final FeatureTypeStyle fts) {
		// Stroke stroke = null;
		if (mode == Mode.LINE) {
			final LineSymbolizer sym = SLD.lineSymbolizer(fts);
			return SLD.stroke(sym);
		} else if (mode == Mode.POLYGON) {
			final PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			return SLD.stroke(sym);
		} else if (mode == Mode.POINT || mode == Mode.ALL) { // default to
																// handling as
																// Point
			final PointSymbolizer sym = SLD.pointSymbolizer(fts);
			return SLD.stroke(sym);
		}
		return new StyleBuilder().createStroke();
	}

	public Fill getFill(final Mode mode, final FeatureTypeStyle fts) {
		if (mode == Mode.POLYGON) {
			final PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			return SLD.fill(sym);
		} else if (mode == Mode.POINT || mode == Mode.ALL) { // default to
																// handling as
																// Point
			final PointSymbolizer sym = SLD.pointSymbolizer(fts);
			return SLD.fill(sym);
		}
		return new StyleBuilder().createFill();
	}

	public void setFillColor(final Color color, final Mode mode, final FeatureTypeStyle fts) {
		if (mode == Mode.POLYGON) {
			final PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			final Fill s = new StyleBuilder().createFill(color);
			sym.setFill(s);
		} else if (mode == Mode.POINT || mode == Mode.ALL) { // default to
																// handling as
																// Point
			final PointSymbolizer sym = SLD.pointSymbolizer(fts);
			SLD.setPointColour(sym, color);
		}
	}

	public Mode determineMode(final SimpleFeatureType schema, final String def) {
		if (schema == null) {
			return Mode.NONE;
		} else if (SLDs.isLine(schema)) {
			return Mode.LINE;
		} else if (SLDs.isPolygon(schema)) {
			return Mode.POLYGON;
		} else if (SLDs.isPoint(schema)) {
			return Mode.POINT;
		} else { // default
			if (def.equals("Polygon")) {
				return Mode.POLYGON;
			} else if (def.equals("Line")) {
				return Mode.LINE;
			} else if (def.equals("Point")) {
				return Mode.POINT;
			}
		}
		return Mode.ALL; // we are a generic geometry
	}

	/**
	 * Method getColorLabels()
	 * 
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Colorizable#getColorLabels()
	 */
	@Override
	public String[] getColorLabels() {
		if (mode == Mode.POLYGON || mode == Mode.ALL) {
			return new String[] { "Set line color...", "Set fill color..." };
		} else {
			return new String[] { "Set line color..." };
		}
	}

	/**
	 * Method getColor()
	 * 
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Colorizable#getColor(int)
	 */
	@Override
	public GamaUIColor getColor(final int index) {
		Color c;
		if (index == 0) {
			c = SLD.color(getStroke(mode, fts));
		} else {
			c = SLD.color(getFill(mode, fts));
		}
		return GamaColors.get(c);

	}

	/**
	 * Method setColor()
	 * 
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Colorizable#setColor(int,
	 *      ummisco.gama.ui.resources.GamaColors.GamaUIColor)
	 */
	@Override
	public void setColor(final int index, final GamaUIColor gc) {
		final RGB rgb = gc.getRGB();
		final Color c = new Color(rgb.red, rgb.green, rgb.blue);
		if (index == 0) {
			setStrokeColor(c, mode, fts);
		} else {
			setFillColor(c, mode, fts);
		}
		((StyleLayer) layer).setStyle(style);
	}

	@Override
	public void saveAsCSV() {
		final List<String> attributes = new ArrayList<String>();
		for (final PropertyDescriptor v : layer.getFeatureSource().getSchema().getDescriptors()) {
			attributes.add(v.getName().toString());
		}
		saveAsCSV(attributes, null, null);
	}

}
