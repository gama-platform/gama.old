/*******************************************************************************************************
 *
 * SimpleConfigurator.java, in ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.viewers.gis.geotools.styling;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.Layer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.LabelPlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Defines a "simple" StyleConfigurator for working with SLD documents.
 * <p>
 * This style configurator is defined as follows:
 *
 * <pre>
 * &lt;code&gt;
 *         Mode: (*) Point ( ) Line ( ) Polygon
 *               +-+ +-------+ +------+ +------+
 *         Line: |x| | color | |size\/| |100%\/|
 *           	 +-+ +-------+ +------+ +------+
 *           	 +-+ +-------+ +------+
 *         Fill: |x| | color | | 90%\/|
 *           	 +-+ +-------+ +------+
 *           	 +-+ +----------------+ +------+
 *        Label: |x| |         title\/| | Font |
 *           	 +-+ +----------------+ +------+
 *           	 +-+ +-------+ +------+
 *        Point: |x| | star\/| |size\/|
 *           	 +-+ +-------+ +------+
 *               +-+ +-------------+
 * Min scale d.: |x| |      scale\/|
 *               +-+ +-------------+
 *               +-+ +-------------+
 * Max scale d.: |x| |      scale\/|
 *               +-+ +-------------+
 * &lt;/code&gt;
 * </pre>
 *
 * </p>
 * Where:
 * <ul>
 * <li>Mode is used to switch between Point / Line / Polygon
 * <li>Line is used for: <br>
 * LineString: line color, width, opacity <br>
 * Polygon: border color, width, opacity <br>
 * Point: border color, width, opacity
 * <li>Fill is used for Polygon or Point fill color, opacity
 * <li>Label is used to choose attribute and set font (the only dialog)
 * <li>Point is used to set the marker type and size
 * <li>Min/max scale denominator define at which scale the layer is visible
 * </ul>
 * </p>
 * <p>
 * Notes:
 * <ul>
 * <li>RasterSymbolizer is handled by its own thing, as is WMS etc...
 * <li>Layout as per the SLDEditorPart examples - so we can take advantage of more or less room.
 * <li>Presets is a good idea, just not here
 * <li>Apply/Revert buttons to be green/red
 * <li>Advanced (edit the SLD) can be in the view menu
 * <li>If possible replace color button with a drop down list (may not be possible)
 * </ul>
 * </P>
 * <p>
 * We will do our best to make this thing reusable on an Array of Symbolizers.
 * </p>
 *
 * @author Jody Garnett
 * @since 1.0.0
 *
 *
 *
 * @source $URL$
 */
public class SimpleConfigurator extends Dialog {

	/** The build. */
	protected StyleBuilder build = new StyleBuilder();
	
	/** The feature collection. */
	protected SimpleFeatureCollection featureCollection;
	
	/** The style. */
	protected Style style;

	/** Viewer used to allow interaction with Stroke definition */
	private final StrokeViewer line = new StrokeViewer();

	/** Viewer used to allow interaction with Fill definition */
	private final FillViewer fill = new FillViewer();

	/** Viewer used to allow interaction with Graphic definition */
	private final GraphicViewer point = new GraphicViewer();

	/** The current mode we are working with */
	private Mode mode;

	/**
	 * Used to respond to any widget selection event; will call synchronize() method to extract any changes of state
	 * from the user interface
	 */
	private final SelectionListener synchronize = new SelectionListener() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			synchronize();
		}

		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
			synchronize();
		}
	};

	/** The replace. */
	private Button replace;

	/**
	 * Construct <code>SimpleStyleConfigurator</code>.
	 */
	public SimpleConfigurator(final Shell parent, final SimpleFeatureCollection featureCollection, final Style style) {
		super(parent);
		this.featureCollection = featureCollection;
		this.style = style;
		this.line.addListener(this.synchronize);
		this.fill.addListener(this.synchronize);
		this.point.addListener(this.synchronize);
	}

	/**
	 * Sets the layout.
	 *
	 * @param parent the new layout
	 */
	protected void setLayout(final Composite parent) {
		final RowLayout layout = new RowLayout();
		layout.pack = false;
		layout.wrap = true;
		layout.type = SWT.HORIZONTAL;
		layout.fill = true;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.spacing = 0;
		parent.setLayout(layout);
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	protected Style getStyle() {
		assert featureCollection != null;
		Style style = this.style;
		if (style == null) {
			final SimpleFeatureType schema = featureCollection.getSchema();
			if (SLDs.isLine(schema)) {
				style = SLD.createLineStyle(Color.red, 1);
			} else if (SLDs.isPoint(schema)) {
				style = SLD.createPointStyle("Circle", Color.red, Color.green, 1f, 3f);
			} else if (SLDs.isPolygon(schema)) {
				style = SLD.createPolygonStyle(Color.red, Color.green, 1f);
			}
		}
		this.style = style;
		return style;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 370);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite parentPanel = (Composite) super.createDialogArea(parent);
		setLayout(parentPanel);
		// ensure vertical layout
		((RowLayout) parentPanel.getLayout()).type = SWT.VERTICAL;
		((RowLayout) parentPanel.getLayout()).spacing = 3;

		final KeyAdapter adp = new KeyAdapter() {

			@Override
			public void keyReleased(final KeyEvent e) {
				/*
				 * I don't like having different ways of checking for keypad enter and the normal one. Using the keyCode
				 * would be better, but I couldn't readily find the value for CR.
				 */
				if (e.keyCode == SWT.KEYPAD_CR || e.character == SWT.CR) {
					refresh(); // makeActionDoStuff();
				}
			}
		};

		this.line.createControl(parentPanel, adp);
		this.fill.createControl(parentPanel, adp);
		this.point.createControl(parentPanel, adp, this.build);
		// this.label.createControl(parentPanel, adp);
		// this.minScale.createControl(parentPanel, adp);
		// this.maxScale.createControl(parentPanel, adp);

		final Composite replaceComp = subpart(parentPanel, "Replace");
		this.replace = new Button(replaceComp, SWT.CHECK);
		replace.addSelectionListener(synchronize);
		replace.setSelection(true);

		refresh();

		return parentPanel;
	}

	/**
	 * Determine mode.
	 *
	 * @param schema the schema
	 * @param askUser the ask user
	 * @return the mode
	 */
	public Mode determineMode(final SimpleFeatureType schema, final boolean askUser) {
		if (schema == null) {
			return Mode.NONE;
		} else if (SLDs.isLine(schema)) {
			return Mode.LINE;
		} else if (SLDs.isPolygon(schema)) {
			return Mode.POLYGON;
		} else if (SLDs.isPoint(schema)) {
			return Mode.POINT;
		} else {
			return Mode.ALL; // we are a generic geometry
		}
	}

	/**
	 * Refresh.
	 */
	protected void refresh() {
		final Style style = getStyle(); // grab an SLD style or bust

		final List<FeatureTypeStyle> ftsList = style.featureTypeStyles();
		FeatureTypeStyle fts = null;
		if (ftsList.size() > 0) {
			fts = ftsList.get(0);
		}

		final SimpleFeatureType schema = featureCollection.getSchema();

		Stroke stroke = null;
		Fill fill = null;
		Graphic graphic = null;
		TextSymbolizer text = null;
		LabelPlacement placement = null;

		List<Rule> rules = Collections.emptyList();
		if (fts != null) {
			rules = fts.rules();
		}
		if (rules.size() > 1) {
			// simple mode trimms away all but the first rule
			final Rule keepRule = rules.get(0);
			rules.clear();
			rules.add(keepRule);
		}
		this.mode = determineMode(schema, true);

		if (mode == Mode.LINE) {
			final LineSymbolizer sym = SLDs.lineSymbolizer(fts);
			stroke = SLDs.stroke(sym);
			placement = SLDs.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);
		} else if (mode == Mode.POLYGON) {
			final PolygonSymbolizer sym = SLDs.polySymbolizer(fts);
			stroke = SLDs.stroke(sym);
			fill = SLDs.fill(sym);
			placement = SLDs.getPlacement(SLDs.ALIGN_CENTER, SLDs.ALIGN_MIDDLE, 0);
		} else if (mode == Mode.POINT || mode == Mode.ALL) {
			final PointSymbolizer sym = SLDs.pointSymbolizer(fts);
			stroke = SLDs.stroke(sym);
			fill = SLDs.fill(sym);
			graphic = SLDs.graphic(sym);
			placement = SLDs.getPlacement(SLDs.ALIGN_LEFT, SLDs.ALIGN_MIDDLE, 0);
		}

		text = SLDs.textSymbolizer(fts);
		if (text != null && placement != null) {
			text.setLabelPlacement(placement);
		}

		final Color defaultColor = Color.red;

		this.line.setStroke(stroke, this.mode, defaultColor);

		this.fill.setFill(fill, this.mode, defaultColor);
		this.point.setGraphic(graphic, this.mode, defaultColor);

	}

	/** Synchronize the SLD with the array of symbolizers */
	public void synchronize() {
		final List<Symbolizer> acquire = new ArrayList<>();

		final SimpleFeatureType schema = featureCollection.getSchema();
		this.mode = determineMode(schema, true);

		final String geometryPropertyName = null;

		switch (this.mode) {
			case LINE: {
				final LineSymbolizer lineSymbolizer = this.build.createLineSymbolizer(this.line.getStroke(this.build));
				acquire.add(lineSymbolizer);
				lineSymbolizer.setGeometryPropertyName(geometryPropertyName);
			}
				break;

			case POLYGON: {
				final PolygonSymbolizer polygonSymbolizer = this.build
						.createPolygonSymbolizer(this.line.getStroke(this.build), this.fill.getFill(this.build));
				polygonSymbolizer.setGeometryPropertyName(geometryPropertyName);
				acquire.add(polygonSymbolizer);
			}
				break;

			case POINT: {
				final PointSymbolizer pointSymbolizer = this.build.createPointSymbolizer(this.point
						.getGraphic(this.fill.getFill(this.build), this.line.getStroke(this.build), this.build));
				pointSymbolizer.setGeometryPropertyName(geometryPropertyName);
				acquire.add(pointSymbolizer);
			}
				break;
			case ALL: {
				final LineSymbolizer lineSymbolizer = this.build.createLineSymbolizer(this.line.getStroke(this.build));
				acquire.add(lineSymbolizer);
				acquire.add(lineSymbolizer);
				final PolygonSymbolizer polygonSymbolizer = this.build
						.createPolygonSymbolizer(this.line.getStroke(this.build), this.fill.getFill(this.build));
				polygonSymbolizer.setGeometryPropertyName(geometryPropertyName);
				acquire.add(polygonSymbolizer);
				final PointSymbolizer pointSymbolizer = this.build.createPointSymbolizer(this.point
						.getGraphic(this.fill.getFill(this.build), this.line.getStroke(this.build), this.build));
				pointSymbolizer.setGeometryPropertyName(geometryPropertyName);
				acquire.add(pointSymbolizer);
			}
				break;
			case NONE:
		}

		final Symbolizer[] array = acquire.toArray(new Symbolizer[acquire.size()]);
		final Rule rule = this.build.createRule(array);
		final FeatureTypeStyle featureTypeStyle =
				this.build.createFeatureTypeStyle(SLDs.GENERIC_FEATURE_TYPENAME, rule);
		featureTypeStyle.setName("simple"); //$NON-NLS-1$

		final Style style = getStyle();
		style.setDefault(true);
		if (replace.getSelection()) {
			// if repalce was hit we are going to completly redfine the style
			// based on what the user has here
			//
			style.featureTypeStyles().clear();
			style.featureTypeStyles().add(featureTypeStyle);
		} else {
			// if we are just responding to what is going on we will try and
			// update the existing
			// style in place (leaving any other content alone)
			//
			final List<FeatureTypeStyle> fts = style.featureTypeStyles();
			boolean match = false;
			for (int i = fts.size() - 1; i > -1; i--) {
				if (SLDs.isSemanticTypeMatch(fts.get(i), "simple")) { //$NON-NLS-1$
					fts.set(i, featureTypeStyle);
					match = true;
					break;
				}
			}
			if (match) {
				style.featureTypeStyles().clear();
				style.featureTypeStyles().addAll(fts);
			} else {
				// add the new entry to the array
				final List<FeatureTypeStyle> fts2 = new ArrayList<>(fts);
				Collections.copy(fts2, fts);
				fts2.add(featureTypeStyle);
				style.featureTypeStyles().clear();
				style.featureTypeStyles().addAll(fts2);
			}
		}

		this.style = style;
	}

	/**
	 * Show dialog.
	 *
	 * @param parent the parent
	 * @param layer the layer
	 * @return the style
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Style showDialog(final Shell parent, final Layer layer) throws IOException {
		final SimpleFeatureSource featureSource = (SimpleFeatureSource) layer.getFeatureSource();
		final Style style = layer.getStyle();
		showDialog(parent, featureSource, style);
		return null;
	}

	/**
	 * Show dialog.
	 *
	 * @param parent the parent
	 * @param featureSource the feature source
	 * @param style the style
	 * @return the style
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Style showDialog(final Shell parent, final SimpleFeatureSource featureSource, final Style style)
			throws IOException {
		final SimpleFeatureCollection features = featureSource.getFeatures();
		final SimpleConfigurator tmp = new SimpleConfigurator(parent, features, style);
		tmp.setBlockOnOpen(true);
		tmp.open();
		return tmp.getStyle();
	}

	/**
	 * Subpart.
	 *
	 * @param parent the parent
	 * @param label the label
	 * @return the composite
	 */
	public static Composite subpart(final Composite parent, final String label) {
		final Composite subpart = new Composite(parent, SWT.NONE);
		final RowLayout across = new RowLayout();
		across.type = SWT.HORIZONTAL;
		across.wrap = true;
		across.pack = true;
		across.fill = true;
		across.marginBottom = 1;
		across.marginRight = 2;

		subpart.setLayout(across);

		final Label labell = new Label(subpart, SWT.LEFT);
		labell.setText(label);

		final RowData data = new RowData();
		data.width = 40;
		// check to see if width is not enough space
		final GC gc = new GC(parent.getParent());
		gc.setFont(parent.getParent().getFont());
		final FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();
		final int labelWidth = Dialog.convertWidthInCharsToPixels(fontMetrics, labell.getText().length() + 1);
		if (labelWidth > data.width) {
			data.width = labelWidth;
		}
		// TODO: adjust the methods that call this one to keep a consistent
		// width (otherwise they're misaligned)
		data.height = 10;
		labell.setLayoutData(data);

		return subpart;
	}

	/**
	 * Selection event.
	 *
	 * @param e the e
	 * @return the selection event
	 */
	public static SelectionEvent selectionEvent(final ModifyEvent e) {
		final Event event = new Event();
		event.widget = e.widget;
		event.data = e.data;
		event.display = e.display;
		event.time = e.time;
		return new SelectionEvent(event) {
			/** <code>serialVersionUID</code> field */
			private static final long serialVersionUID = 6544345585295778029L;

			@Override
			public Object getSource() {
				return e.getSource();
			}
		};
	}
}
