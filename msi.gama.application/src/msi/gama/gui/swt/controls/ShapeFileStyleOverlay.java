/*********************************************************************************************
 * 
 * 
 * 'LayersOverlay.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import java.awt.Color;
import java.io.IOException;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.viewers.shapefile.ShapeFileViewer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.*;
import org.geotools.map.StyleLayer;
import org.geotools.styling.*;
import org.geotools.swt.styling.simple.*;
import org.opengis.feature.simple.SimpleFeatureType;

// import scala.actors.threadpool.Arrays;

/**
 * The class Popup.
 * 
 * @author drogoul
 * @since 19 aug. 2013
 * 
 */
public class ShapeFileStyleOverlay extends PopupDialog {

	ShapeFileViewer view;
	Composite content;
	protected StyleBuilder build = new StyleBuilder();
	protected SimpleFeatureCollection featureCollection;
	final protected Style style;
	protected FeatureSource source;
	/** The current mode we are working with */
	private static final String DEFAULT_GEOMETRY = "(default)";
	public static StyleFactory sf = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());
	StringEditor modeEditor;
	StringEditor geometryNameEditor;
	ColorEditor lineColorEditor;
	IntEditor lineWidthEditor;
	FloatEditor lineOpacityEditor;
	ColorEditor fillColorEditor;
	private final StyleLayer layer;
	final FeatureTypeStyle fts;
	final Mode mode;

	public ShapeFileStyleOverlay(final ShapeFileViewer parent, final FeatureSource source, final StyleLayer layer,
		final int shellStyle) {
		super(parent.getSite().getShell(), shellStyle, true, true, true, true, true, null, null);
		this.source = source;
		this.layer = layer;
		try {
			featureCollection = (SimpleFeatureCollection) source.getFeatures();
		} catch (IOException e) {
			e.printStackTrace();
		}
		view = parent;
		style = layer.getStyle() == null ? getStyle() : layer.getStyle();

		List<FeatureTypeStyle> ftsList = style.featureTypeStyles();
		if ( ftsList.size() > 0 ) {
			fts = ftsList.get(0);
		} else {
			fts = null;
		}
		this.mode = determineMode((SimpleFeatureType) source.getSchema(), true);
	}

	private Style getStyle() {
		if ( style != null ) { return style; }
		Style s = null;
		SimpleFeatureType schema = featureCollection.getSchema();
		if ( SLDs.isLine(schema) ) {
			s = SLD.createLineStyle(Color.blue, 1);
		} else if ( SLDs.isPoint(schema) ) {
			s = SLD.createPointStyle("Circle", Color.blue, Color.lightGray, 1f, 3f);
		} else if ( SLDs.isPolygon(schema) ) {
			s = SLD.createPolygonStyle(Color.blue, Color.lightGray, 1f);
		}
		return s;
	}

	@Override
	protected Point getDefaultLocation(final Point initialSize) {
		return view.getMapComposite().toDisplay(new Point(0, 0));
	}

	@Override
	protected Point getInitialLocation(final Point initialSize) {
		return view.getMapComposite().toDisplay(new Point(0, 0));
	}

	@Override
	protected Point getInitialSize() {
		Point size = view.getMapComposite().getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected Point getDefaultSize() {
		Point size = view.getMapComposite().getSize();
		return new Point(size.x, size.y / 2);
	}

	@Override
	protected org.eclipse.swt.graphics.Color getBackground() {
		return IGamaColors.WHITE.color();
	}

	@Override
	protected org.eclipse.swt.graphics.Color getForeground() {
		return IGamaColors.BLACK.color();
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		getShell().setAlpha(220);
		content = new Composite(parent, SWT.None);
		content.setLayout(new GridLayout());
		ParameterExpandBar viewer = new ParameterExpandBar(content, SWT.V_SCROLL, false, false, new ItemList() {

			@Override
			public boolean addItem(final Object obj) {
				return false;
			}

			@Override
			public void removeItem(final Object obj) {}

			@Override
			public void pauseItem(final Object obj) {}

			@Override
			public void resumeItem(final Object obj) {}

			@Override
			public void focusItem(final Object obj) {}

			@Override
			public List getItems() {
				return Arrays.asList(new String[] { "Colors", "Lines", "Attributes" });
			}

			@Override
			public String getItemDisplayName(final Object obj, final String previousName) {
				return previousName;
			}

			@Override
			public void updateItemValues() {}
		});
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		viewer.setSpacing(5);
		createItem("Colors", createColorContentsFor(viewer), viewer, true);
		content.layout();
		viewer.addListener(SWT.Collapse, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				content.redraw();
			}

		});
		viewer.addListener(SWT.Expand, new Listener() {

			@Override
			public void handleEvent(final Event e) {
				content.redraw();
			}

		});
		return content;
	}

	protected Composite createColorContentsFor(final Composite viewer) {

		final Composite compo = new Composite(viewer, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);

		// List<String> geometryNames = new ArrayList();
		// geometryNames.add(DEFAULT_GEOMETRY);
		// if ( source != null ) {
		// for ( PropertyDescriptor descriptor : source.getSchema().getDescriptors() ) {
		// if ( descriptor instanceof GeometryDescriptor ) {
		// geometryNames.add(((GeometryDescriptor) descriptor).getLocalName());
		// }
		// }
		// }
		// geometryNameEditor =
		// EditorFactory.choose(compo, "Attribute to use for geometry:", DEFAULT_GEOMETRY, false, geometryNames, null);

		// List<String> modeNames = Arrays.asList(new String[] { "All", "Point", "Line", "Polygon" });
		// modeEditor = EditorFactory.choose(compo, "Mode:", "All", false, modeNames, null);

		lineColorEditor =
			EditorFactory.create(compo, "Line color:", SLD.color(getStroke()), new EditorListener<Color>() {

				@Override
				public void valueModified(final Color newValue) throws GamaRuntimeException {
					setStrokeColor(newValue);
					layer.setStyle(style);
				}

			});
		if ( mode != Mode.LINE ) {
			fillColorEditor =
				EditorFactory.create(compo, "Fill color:", SLD.color(getFill()), new EditorListener<Color>() {

					@Override
					public void valueModified(final Color newValue) throws GamaRuntimeException {
						setFillColor(newValue);
						layer.setStyle(style);
					}
				});
		}
		return compo;
	}

	private void setStrokeColor(final Color color) {
		if ( mode == Mode.LINE ) {
			LineSymbolizer sym = SLD.lineSymbolizer(fts);
			SLD.setLineColour(sym, color);
		} else if ( mode == Mode.POLYGON ) {
			PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			Stroke s = new StyleBuilder().createStroke(color);
			sym.setStroke(s);
		} else if ( mode == Mode.POINT || mode == Mode.ALL ) { // default to handling as Point
			PointSymbolizer sym = SLD.pointSymbolizer(fts);
			SLD.setPointColour(sym, color);
		}
	}

	private Stroke getStroke() {
		Stroke stroke = null;
		if ( mode == Mode.LINE ) {
			LineSymbolizer sym = SLD.lineSymbolizer(fts);
			return SLD.stroke(sym);
		} else if ( mode == Mode.POLYGON ) {
			PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			return SLD.stroke(sym);
		} else if ( mode == Mode.POINT || mode == Mode.ALL ) { // default to handling as Point
			PointSymbolizer sym = SLD.pointSymbolizer(fts);
			return SLD.stroke(sym);
		}
		return new StyleBuilder().createStroke();
	}

	private Fill getFill() {
		if ( mode == Mode.POLYGON ) {
			PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			return SLD.fill(sym);
		} else if ( mode == Mode.POINT || mode == Mode.ALL ) { // default to handling as Point
			PointSymbolizer sym = SLD.pointSymbolizer(fts);
			return SLD.fill(sym);
		}
		return new StyleBuilder().createFill();
	}

	private void setFillColor(final Color color) {
		if ( mode == Mode.POLYGON ) {
			PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			Fill s = new StyleBuilder().createFill(color);
			sym.setFill(s);
		} else if ( mode == Mode.POINT || mode == Mode.ALL ) { // default to handling as Point
			PointSymbolizer sym = SLD.pointSymbolizer(fts);
			SLD.setPointColour(sym, color);
		}
	}

	public Mode determineMode(final SimpleFeatureType schema, final boolean askUser) {
		if ( schema == null ) {
			return Mode.NONE;
		} else if ( SLDs.isLine(schema) ) {
			return Mode.LINE;
		} else if ( SLDs.isPolygon(schema) ) {
			return Mode.POLYGON;
		} else if ( SLDs.isPoint(schema) ) {
			return Mode.POINT;
		} else {
			// we must be Geometry?
			if ( askUser ) {
				// could not figure it out from the schema
				// try trusting the user?
				String _mode = modeEditor.getCurrentValue();
				if ( _mode.equals("Polygon") ) {
					return Mode.POLYGON;
				} else if ( _mode.equals("Line") ) {
					return Mode.LINE;
				} else if ( _mode.equals("Point") ) { return Mode.POINT; }
			}
			return Mode.ALL; // we are a generic geometry
		}
	}

	protected void refresh() {
		Style style = getStyle(); // grab an SLD style or bust

		List<FeatureTypeStyle> ftsList = style.featureTypeStyles();
		FeatureTypeStyle fts = null;
		if ( ftsList.size() > 0 ) {
			fts = ftsList.get(0);
		}

		SimpleFeatureType schema = featureCollection.getSchema();
		String name = DEFAULT_GEOMETRY;

		Stroke stroke = null;
		Fill fill = null;
		Graphic graphic = null;
		TextSymbolizer text = null;
		LabelPlacement placement = null;

		List<Rule> rules = Collections.emptyList();
		if ( fts != null ) {
			rules = fts.rules();
		}
		if ( rules.size() > 1 ) {
			// simple mode trimms away all but the first rule
			Rule keepRule = rules.get(0);
			rules.clear();
			rules.add(keepRule);
		}
		// this.mode = determineMode(schema, true);

		// if ( mode == Mode.NONE ) {
		// pointMode.setSelection(false);
		// polyMode.setSelection(false);
		// lineMode.setSelection(false);
		// } else
		if ( mode == Mode.LINE ) {
			// modeEditor.getParam().setValue(null, "Line");
			LineSymbolizer sym = SLD.lineSymbolizer(fts);
			stroke = SLD.stroke(sym);
			placement = SLD.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);
			name = sym == null ? null : sym.getGeometryPropertyName();
		} else if ( mode == Mode.POLYGON ) {
			// modeEditor.getParam().setValue(null, "Polygon");
			PolygonSymbolizer sym = SLD.polySymbolizer(fts);
			stroke = SLD.stroke(sym);
			fill = SLD.fill(sym);
			placement = SLD.getPlacement(SLDs.ALIGN_CENTER, SLDs.ALIGN_MIDDLE, 0);
			name = sym == null ? null : sym.getGeometryPropertyName();
		} else if ( mode == Mode.POINT || mode == Mode.ALL ) { // default to handling as Point
			// modeEditor.getParam().setValue(null, "Point");
			PointSymbolizer sym = SLD.pointSymbolizer(fts);
			stroke = SLD.stroke(sym);
			fill = SLD.fill(sym);
			graphic = SLD.graphic(sym);
			placement = SLD.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);
			name = sym == null ? null : sym.getGeometryPropertyName();
		}
		text = SLD.textSymbolizer(fts);
		if ( text != null && placement != null ) {
			text.setLabelPlacement(placement);
		}

		if ( name == null ) {
			name = DEFAULT_GEOMETRY;
		}
		geometryNameEditor.getParam().setValue(null, name);
		// Mode raw = determineMode(schema, false);
		// pointMode.setEnabled(raw == Mode.ALL);
		// polyMode.setEnabled(raw == Mode.ALL);
		// lineMode.setEnabled(raw == Mode.ALL);

		// double minScaleDen = SLDs.minScale(fts);
		// double maxScaleDen = SLDs.maxScale(fts);
		Color defaultColor = Color.red;
		lineColorEditor.getParam().setValue(null, defaultColor);
		// this.line.setStroke(stroke, this.mode, defaultColor);
		fillColorEditor.getParam().setValue(null, defaultColor);
		// this.fill.setFill(fill, this.mode, defaultColor);
		// this.point.setGraphic(graphic, this.mode, defaultColor);
		//
		// this.label.set(schema, text, this.mode);
		// this.minScale.setScale(minScaleDen, 0l);
		// this.maxScale.setScale(maxScaleDen, 1111111l);
	}

	protected ParameterExpandItem createItem(final String name, final Composite control, final ParameterExpandBar bar,
		final boolean expanded) {
		ParameterExpandItem i = new ParameterExpandItem(bar, null, SWT.None);
		if ( name != null ) {
			i.setText(name);
		}
		control.pack(true);
		control.layout();
		i.setControl(control);
		i.setHeight(control.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(expanded);
		return i;
	}

}
