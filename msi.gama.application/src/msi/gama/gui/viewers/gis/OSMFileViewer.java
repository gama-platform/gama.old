package msi.gama.gui.viewers.gis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.geotools.swt.MapLayerComposite;
import org.geotools.swt.SwtMapPane;
import org.geotools.swt.utils.Utils;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import msi.gama.gui.swt.GamaColors;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaOsmFile;

public class OSMFileViewer extends GISFileViewer {

	Map<String, String> attributes;

	@Override
	public void createPartControl(final Composite composite) {
		Composite parent = GamaToolbarFactory.createToolbars(this, composite);
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.NULL);
		displayInfoString();
		MapLayerComposite mapLayerTable = new CustomMapLayerComposite(sashForm, SWT.BORDER);
		pane = new SwtMapPane(sashForm, SWT.BORDER | SWT.NO_BACKGROUND, new StreamingRenderer(), content);
		pane.setBackground(GamaColors.system(SWT.COLOR_WHITE));
		pane.setCursorTool(newDragTool());
		mapLayerTable.setMapPane(pane);
		sashForm.setWeights(new int[] { 1, 4 });
		pane.redraw();

	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		FileEditorInput fi = (FileEditorInput) input;
		file = fi.getFile();
		IPath path = fi.getPath();

		File f = path.makeAbsolute().toFile();

		try {
			GamaOsmFile osmfile = new GamaOsmFile(null, f.getAbsolutePath());
			attributes = osmfile.getAttributes();
			SimpleFeatureType TYPE = DataUtilities.createType("geometries", "geom:LineString");

			ArrayList<SimpleFeature> list = new ArrayList<SimpleFeature>();

			for ( IShape shape : osmfile.iterable(null) ) {
				list.add(SimpleFeatureBuilder.build(TYPE, new Object[] { shape.getInnerGeometry() }, null));
			}
			SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, list);
			featureSource = DataUtilities.source(collection);
			content = new MapContent();
			style = Utils.createStyle(f, featureSource);
			layer = new FeatureLayer(featureSource, style);
			/*mode = determineMode(featureSource.getSchema(), "Polygon");
			List<FeatureTypeStyle> ftsList = style.featureTypeStyles();
			if ( ftsList.size() > 0 ) {
				fts = ftsList.get(0);
			} else {
				fts = null;
			}
			if ( fts != null ) {
				this.setFillColor(SwtGui.SHAPEFILE_VIEWER_FILL.getValue(), mode, fts);
				this.setStrokeColor(SwtGui.SHAPEFILE_VIEWER_LINE_COLOR.getValue(), mode, fts);
				((StyleLayer) layer).setStyle(style);
			}*/
			// content.addLayer(layer);
			List<String> layers = new ArrayList<String>(osmfile.getLayers().keySet());
			Collections.sort(layers);
			Collections.reverse(layers);
			for ( String val : layers) {
				boolean isPoint = val.endsWith("(point)");
				boolean isLine = val.endsWith("(line)");
				SimpleFeatureType TYPET = isPoint ? DataUtilities.createType(val, "geom:Point") : (isLine
					? DataUtilities.createType(val, "geom:LineString") : DataUtilities.createType(val, "geom:Polygon"));

				ArrayList<SimpleFeature> listT = new ArrayList<SimpleFeature>();

				for ( IShape shape : osmfile.getLayers().get(val) ) {
					listT.add(SimpleFeatureBuilder.build(TYPET, new Object[] { shape.getInnerGeometry() }, null));
				}
				SimpleFeatureCollection collectionT = new ListFeatureCollection(TYPET, listT);
				SimpleFeatureSource featureSourceT = DataUtilities.source(collectionT);

				Style styleT = Utils.createStyle(f, featureSourceT);
				FeatureLayer layerT = new FeatureLayer(featureSourceT, styleT);
				content.addLayer(layerT);

			}
		} catch (SchemaException e) {
			e.printStackTrace();
		} catch (GamaRuntimeException e) {
			e.printStackTrace();
		}
		this.setPartName(path.lastSegment());
		setInput(input);
	}

	protected void displayInfoString() {
		String s = "";
		try {
			s = featureSource.getFeatures().size() + " objects | " +
				(int) (featureSource.getBounds().getWidth() * (Math.PI / 180) * 6378137) + "m x " +
				(int) (featureSource.getBounds().getHeight() * (Math.PI / 180) * 6378137) + "m";
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		GamaUIColor color = IGamaColors.OK;

		ToolItem item = toolbar.menu(color, s, SWT.LEFT);

		((FlatButton) item.getControl()).addSelectionListener(new SelectionAdapter() {

			Menu menu;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( menu == null ) {
					menu = new Menu(toolbar.getShell(), SWT.POP_UP);
					fillMenu();
				}
				Point point = toolbar.toDisplay(new Point(e.x, e.y + toolbar.getSize().y));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);
			}

			private void fillMenu() {
				AgentsMenu.separate(menu, "Bounds");
				try {
					ReferencedEnvelope env = featureSource.getBounds();
					MenuItem m2 = new MenuItem(menu, SWT.NONE);
					m2.setEnabled(false);
					m2.setText("     - upper corner : " + env.getUpperCorner().getOrdinate(0) + " " +
						env.getUpperCorner().getOrdinate(1));
					m2 = new MenuItem(menu, SWT.NONE);
					m2.setEnabled(false);
					m2.setText("     - lower corner : " + env.getLowerCorner().getOrdinate(0) + " " +
						env.getLowerCorner().getOrdinate(1));

					m2 = new MenuItem(menu, SWT.NONE);
					m2.setEnabled(false);
					// approximation
					m2.setText("     - dimensions : " + (int) (env.getWidth() * (Math.PI / 180) * 6378137) + "m x " +
						(int) (env.getHeight() * (Math.PI / 180) * 6378137) + "m");
				} catch (Exception e) {
					e.printStackTrace();
				}
				AgentsMenu.separate(menu);
				AgentsMenu.separate(menu, "Attributes");
				try {
					List<String> atts = new ArrayList<String>(attributes.keySet());
					Collections.sort(atts);
					String currentType = "";
					
					for ( String att : atts ) {
						String[] attP = att.split(";");
						if (!currentType.equals(attP[0]) ) {
							currentType = attP[0];
							MenuItem m3 = new MenuItem(menu, SWT.NONE);
							m3.setEnabled(false);
							m3.setText("  * " + currentType );
						}
						MenuItem m2 = new MenuItem(menu, SWT.NONE);
						m2.setEnabled(false);
						m2.setText("       - " + attP[1] + " (" + attributes.get(att) + ")");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		});

	}

}
