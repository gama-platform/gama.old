/*********************************************************************************************
 *
 *
 * 'SWTChartEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/

package msi.gama.gui.views.displays;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.geom.*;
import javax.swing.JPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.plot.*;
import org.jfree.chart.title.*;
import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.ui.resources.GamaColors;

/**
 * An editor for chart properties.
 */
public class SWTChartEditor implements ChartEditor {

	/** The shell */
	private final Shell shell;

	/** The chart which the properties have to be edited */
	private final JFreeChart chart;

	/** A composite for displaying/editing the properties of the title. */
	private final SWTTitleEditor titleEditor;

	/** A composite for displaying/editing the properties of the plot. */
	private final SWTPlotEditor plotEditor;

	/** A composite for displaying/editing the other properties of the chart. */
	private final SWTOtherEditor otherEditor;

	/** The resourceBundle for the localization. */
	// protected static ResourceBundle localizationResources = ResourceBundleWrapper
	// .getBundle("org.jfree.chart.editor.LocalizationBundle");

	/**
	 * Creates a new editor.
	 *
	 * @param display the display.
	 * @param chart2edit the chart to edit.
	 */
	public SWTChartEditor(final Display display, final JFreeChart chart2edit, final Point position) {
		this.shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.NO_TRIM);
		this.shell.setSize(400, 500);
		this.shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		// this.shell.setAlpha(140);
		this.chart = chart2edit;
		this.shell.setText("Chart properties");
		this.shell.setLocation(position);
		GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 5;
		this.shell.setLayout(layout);
		Composite main = new Composite(this.shell, SWT.NONE);
		main.setLayout(new FillLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		TabFolder tab = new TabFolder(main, SWT.BORDER);
		// build first tab
		TabItem item1 = new TabItem(tab, SWT.NONE);
		item1.setText(" " + "Title" + " ");
		this.titleEditor = new SWTTitleEditor(tab, SWT.NONE, this.chart.getTitle());
		item1.setControl(this.titleEditor);
		// build second tab
		TabItem item2 = new TabItem(tab, SWT.NONE);
		item2.setText(" " + "Plot" + " ");
		this.plotEditor = new SWTPlotEditor(tab, SWT.NONE, this.chart.getPlot());
		item2.setControl(this.plotEditor);
		// build the third tab
		TabItem item3 = new TabItem(tab, SWT.NONE);
		item3.setText(" " + "Other" + " ");
		this.otherEditor = new SWTOtherEditor(tab, SWT.NONE, this.chart);
		item3.setControl(this.otherEditor);

		// ok and cancel buttons
		Button cancel = new Button(this.shell, SWT.PUSH);
		cancel.setText(" Cancel ");
		cancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SWTChartEditor.this.shell.dispose();
			}
		});
		Button ok = new Button(this.shell, SWT.PUSH | SWT.OK);
		ok.setText(" Ok ");
		ok.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		ok.pack();
		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateChart(SWTChartEditor.this.chart);
				SWTChartEditor.this.shell.dispose();
			}
		});

	}

	/**
	 * Opens the editor.
	 */
	public void open() {
		this.shell.open();
		this.shell.layout();
		while (!this.shell.isDisposed()) {
			if ( !this.shell.getDisplay().readAndDispatch() ) {
				this.shell.getDisplay().sleep();
			}
		}
	}

	/**
	 * Updates the chart properties.
	 *
	 * @param chart the chart.
	 */
	@Override
	public void updateChart(final JFreeChart chart) {
		this.titleEditor.setTitleProperties(chart);
		this.plotEditor.updatePlotProperties(chart.getPlot());
		this.otherEditor.updateChartProperties(chart);
	}

	class SWTTitleEditor extends Composite {

		/** Whether or not to display the title on the chart. */
		private boolean showTitle;

		/** The checkbox to indicate whether or not to display the title. */
		private final Button showTitleCheckBox;

		/** A field for displaying/editing the title text. */
		private final Text titleField;

		/** The font used to draw the title. */
		private FontData titleFont;

		/** A field for displaying a description of the title font. */
		private final Text fontField;

		/** The button to use to select a new title font. */
		private final Button selectFontButton;

		/** The paint (color) used to draw the title. */
		private Color titleColor;

		/** The button to use to select a new paint (color) to draw the title. */
		private final Button selectColorButton;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources = ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/** Font object used to handle a change of font. */
		private Font font;

		/**
		 * Standard constructor: builds a panel for displaying/editing the
		 * properties of the specified title.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param title the title, which should be changed.
		 *
		 */
		SWTTitleEditor(final Composite parent, final int style, final Title title) {
			super(parent, style);
			FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			TextTitle t = title != null ? (TextTitle) title : new TextTitle("Title");
			this.showTitle = title != null;
			this.titleFont = SWTUtils.toSwtFontData(getDisplay(), t.getFont(), true);
			this.titleColor = SWTUtils.toSwtColor(getDisplay(), t.getPaint());

			Group general = new Group(this, SWT.NONE);
			general.setLayout(new GridLayout(3, false));
			general.setText("General");
			// row 1
			Label label = new Label(general, SWT.NONE);
			label.setText("Show Title");
			GridData gridData = new GridData();
			gridData.horizontalSpan = 2;
			label.setLayoutData(gridData);
			this.showTitleCheckBox = new Button(general, SWT.CHECK);
			this.showTitleCheckBox.setSelection(this.showTitle);
			this.showTitleCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			this.showTitleCheckBox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					SWTTitleEditor.this.showTitle = SWTTitleEditor.this.showTitleCheckBox.getSelection();
				}
			});
			// row 2
			new Label(general, SWT.NONE).setText("Text");
			this.titleField = new Text(general, SWT.BORDER);
			this.titleField.setText(t.getText());
			this.titleField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			new Label(general, SWT.NONE).setText("");
			// row 3
			new Label(general, SWT.NONE).setText("Font");
			this.fontField = new Text(general, SWT.BORDER);
			this.fontField.setText(this.titleFont.toString());
			this.fontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			this.selectFontButton = new Button(general, SWT.PUSH);
			this.selectFontButton.setText("Select...");
			this.selectFontButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the font-change dialog
					FontDialog dlg = new FontDialog(getShell());
					dlg.setText("Font_Selection");
					dlg.setFontList(new FontData[] { SWTTitleEditor.this.titleFont });
					if ( dlg.open() != null ) {
						// Dispose of any fonts we have created
						if ( SWTTitleEditor.this.font != null ) {
							SWTTitleEditor.this.font.dispose();
						}
						// Create the new font and set it into the title
						// label
						SWTTitleEditor.this.font = new Font(getShell().getDisplay(), dlg.getFontList());
						// titleField.setFont(font);
						SWTTitleEditor.this.fontField.setText(SWTTitleEditor.this.font.getFontData()[0].toString());
						SWTTitleEditor.this.titleFont = SWTTitleEditor.this.font.getFontData()[0];
					}
				}
			});
			// row 4
			new Label(general, SWT.NONE).setText("Color");
			// Use a SwtPaintCanvas to show the color, note that we must set the
			// heightHint.
			final SWTPaintCanvas colorCanvas = new SWTPaintCanvas(general, SWT.NONE, this.titleColor);
			GridData canvasGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			canvasGridData.heightHint = 20;
			colorCanvas.setLayoutData(canvasGridData);
			this.selectColorButton = new Button(general, SWT.PUSH);
			this.selectColorButton.setText("Select...");
			this.selectColorButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the color-change dialog
					ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Title_Color");
					dlg.setRGB(SWTTitleEditor.this.titleColor.getRGB());
					RGB rgb = dlg.open();
					if ( rgb != null ) {
						// create the new color and set it to the
						// SwtPaintCanvas
						SWTTitleEditor.this.titleColor = new Color(getDisplay(), rgb);
						colorCanvas.setColor(SWTTitleEditor.this.titleColor);
					}
				}
			});
		}

		/**
		 * Returns the title text entered in the panel.
		 *
		 * @return The title text entered in the panel.
		 */
		public String getTitleText() {
			return this.titleField.getText();
		}

		/**
		 * Returns the font selected in the panel.
		 *
		 * @return The font selected in the panel.
		 */
		public FontData getTitleFont() {
			return this.titleFont;
		}

		/**
		 * Returns the font selected in the panel.
		 *
		 * @return The font selected in the panel.
		 */
		public Color getTitleColor() {
			return this.titleColor;
		}

		/**
		 * Sets the properties of the specified title to match the properties
		 * defined on this panel.
		 *
		 * @param chart the chart whose title is to be modified.
		 */
		public void setTitleProperties(final JFreeChart chart) {
			if ( this.showTitle ) {
				TextTitle title = chart.getTitle();
				if ( title == null ) {
					title = new TextTitle();
					chart.setTitle(title);
				}
				title.setText(getTitleText());
				title.setFont(SWTUtils.toAwtFont(getDisplay(), getTitleFont(), true));
				title.setPaint(GamaColors.toAwtColor(getTitleColor()));
			} else {
				chart.setTitle((TextTitle) null);
			}
		}
	}

	class SWTPlotEditor extends Composite {

		/**
		 * A panel used to display/edit the properties of the domain axis (if any).
		 */
		private final SWTAxisEditor domainAxisPropertyPanel;

		/**
		 * A panel used to display/edit the properties of the range axis (if any).
		 */
		private final SWTAxisEditor rangeAxisPropertyPanel;

		private final SWTPlotAppearanceEditor plotAppearance;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources = ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/**
		 * Creates a new editor for the specified plot.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param plot the plot.
		 */
		public SWTPlotEditor(final Composite parent, final int style, final Plot plot) {
			super(parent, style);
			FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			Group plotType = new Group(this, SWT.NONE);
			FillLayout plotTypeLayout = new FillLayout();
			plotTypeLayout.marginHeight = plotTypeLayout.marginWidth = 4;
			plotType.setLayout(plotTypeLayout);
			plotType.setText(plot.getPlotType() + ":");

			TabFolder tabs = new TabFolder(plotType, SWT.NONE);

			// deal with domain axis
			TabItem item1 = new TabItem(tabs, SWT.NONE);
			item1.setText("Domain Axis");
			Axis domainAxis = null;
			if ( plot instanceof CategoryPlot ) {
				domainAxis = ((CategoryPlot) plot).getDomainAxis();
			} else if ( plot instanceof XYPlot ) {
				domainAxis = ((XYPlot) plot).getDomainAxis();
			}
			this.domainAxisPropertyPanel = SWTAxisEditor.getInstance(tabs, SWT.NONE, domainAxis);
			item1.setControl(this.domainAxisPropertyPanel);

			// deal with range axis
			TabItem item2 = new TabItem(tabs, SWT.NONE);
			item2.setText("Range Axis");
			Axis rangeAxis = null;
			if ( plot instanceof CategoryPlot ) {
				rangeAxis = ((CategoryPlot) plot).getRangeAxis();
			} else if ( plot instanceof XYPlot ) {
				rangeAxis = ((XYPlot) plot).getRangeAxis();
			}
			this.rangeAxisPropertyPanel = SWTAxisEditor.getInstance(tabs, SWT.NONE, rangeAxis);
			item2.setControl(this.rangeAxisPropertyPanel);

			// deal with plot appearance
			TabItem item3 = new TabItem(tabs, SWT.NONE);
			item3.setText("Appearance");
			this.plotAppearance = new SWTPlotAppearanceEditor(tabs, SWT.NONE, plot);
			item3.setControl(this.plotAppearance);
		}

		/**
		 * Returns the current outline stroke.
		 *
		 * @return The current outline stroke.
		 */
		public Color getBackgroundPaint() {
			return this.plotAppearance.getBackGroundPaint();
		}

		/**
		 * Returns the current outline stroke.
		 *
		 * @return The current outline stroke.
		 */
		public Color getOutlinePaint() {
			return this.plotAppearance.getOutlinePaint();
		}

		/**
		 * Returns the current outline stroke.
		 *
		 * @return The current outline stroke.
		 */
		public Stroke getOutlineStroke() {
			return this.plotAppearance.getStroke();
		}

		/**
		 * Updates the plot properties to match the properties
		 * defined on the panel.
		 *
		 * @param plot The plot.
		 */
		public void updatePlotProperties(final Plot plot) {
			// set the plot properties...
			plot.setBackgroundPaint(GamaColors.toAwtColor(getBackgroundPaint()));
			plot.setOutlinePaint(GamaColors.toAwtColor(getOutlinePaint()));
			plot.setOutlineStroke(getOutlineStroke());

			// set the axis properties
			if ( this.domainAxisPropertyPanel != null ) {
				Axis domainAxis = null;
				if ( plot instanceof CategoryPlot ) {
					CategoryPlot p = (CategoryPlot) plot;
					domainAxis = p.getDomainAxis();
				} else if ( plot instanceof XYPlot ) {
					XYPlot p = (XYPlot) plot;
					domainAxis = p.getDomainAxis();
				}
				if ( domainAxis != null ) {
					this.domainAxisPropertyPanel.setAxisProperties(domainAxis);
				}
			}
			if ( this.rangeAxisPropertyPanel != null ) {
				Axis rangeAxis = null;
				if ( plot instanceof CategoryPlot ) {
					CategoryPlot p = (CategoryPlot) plot;
					rangeAxis = p.getRangeAxis();
				} else if ( plot instanceof XYPlot ) {
					XYPlot p = (XYPlot) plot;
					rangeAxis = p.getRangeAxis();
				}
				if ( rangeAxis != null ) {
					this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis);
				}
			}
			if ( this.plotAppearance.getPlotOrientation() != null ) {
				if ( plot instanceof CategoryPlot ) {
					CategoryPlot p = (CategoryPlot) plot;
					p.setOrientation(this.plotAppearance.getPlotOrientation());
				} else if ( plot instanceof XYPlot ) {
					XYPlot p = (XYPlot) plot;
					p.setOrientation(this.plotAppearance.getPlotOrientation());
				}
			}
		}
	}

	class SWTOtherEditor extends Composite {

		/**
		 * A checkbox indicating whether or not
		 * the chart is drawn with anti-aliasing.
		 */
		private final Button antialias;

		/** The chart background color. */
		private final SWTPaintCanvas backgroundPaintCanvas;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources = ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/**
		 * Creates a new instance.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param chart the chart.
		 */
		public SWTOtherEditor(final Composite parent, final int style, final JFreeChart chart) {
			super(parent, style);
			FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			Group general = new Group(this, SWT.NONE);
			general.setLayout(new GridLayout(3, false));
			general.setText("General");

			// row 1: antialiasing
			this.antialias = new Button(general, SWT.CHECK);
			this.antialias.setText("Draw anti-aliased");
			this.antialias.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
			this.antialias.setSelection(chart.getAntiAlias());

			// row 2: background paint for the chart
			new Label(general, SWT.NONE).setText("Background paint");
			this.backgroundPaintCanvas =
				new SWTPaintCanvas(general, SWT.NONE, SWTUtils.toSwtColor(getDisplay(), chart.getBackgroundPaint()));
			GridData bgGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			bgGridData.heightHint = 20;
			this.backgroundPaintCanvas.setLayoutData(bgGridData);
			Button selectBgPaint = new Button(general, SWT.PUSH);
			selectBgPaint.setText("Select...");
			selectBgPaint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			selectBgPaint.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Background_paint");
					dlg.setRGB(SWTOtherEditor.this.backgroundPaintCanvas.getColor().getRGB());
					RGB rgb = dlg.open();
					if ( rgb != null ) {
						SWTOtherEditor.this.backgroundPaintCanvas.setColor(new Color(getDisplay(), rgb));
					}
				}
			});
		}

		/**
		 * Updates the chart.
		 *
		 * @param chart the chart.
		 */
		public void updateChartProperties(final JFreeChart chart) {
			chart.setAntiAlias(this.antialias.getSelection());
			chart.setBackgroundPaint(GamaColors.toAwtColor(this.backgroundPaintCanvas.getColor()));
		}

	}

	/**
	 * Utility class gathering some useful and general method.
	 * Mainly convert forth and back graphical stuff between
	 * awt and swt.
	 */
	public static class SWTUtils {

		private final static String Az = "ABCpqr";

		/** A dummy JPanel used to provide font metrics. */
		protected static final JPanel DUMMY_PANEL = new JPanel();

		/**
		 * Create a <code>FontData</code> object which encapsulate
		 * the essential data to create a swt font. The data is taken
		 * from the provided awt Font.
		 * <p>
		 * Generally speaking, given a font size, the returned swt font will display differently on the screen than the awt one. Because the SWT toolkit use native graphical resources whenever it is
		 * possible, this fact is platform dependent. To address this issue, it is possible to enforce the method to return a font with the same size (or at least as close as possible) as the awt one.
		 * <p>
		 * When the object is no more used, the user must explicitly call the dispose method on the returned font to free the operating system resources (the garbage collector won't do it).
		 *
		 * @param device The swt device to draw on (display or gc device).
		 * @param font The awt font from which to get the data.
		 * @param ensureSameSize A boolean used to enforce the same size
		 *            (in pixels) between the awt font and the newly created swt font.
		 * @return a <code>FontData</code> object.
		 */
		public static FontData toSwtFontData(final Device device, final java.awt.Font font,
			final boolean ensureSameSize) {
			FontData fontData = new FontData();
			fontData.setName(font.getFamily());
			int style = SWT.NORMAL;
			switch (font.getStyle()) {
				case java.awt.Font.PLAIN:
					style |= SWT.NORMAL;
					break;
				case java.awt.Font.BOLD:
					style |= SWT.BOLD;
					break;
				case java.awt.Font.ITALIC:
					style |= SWT.ITALIC;
					break;
				case java.awt.Font.ITALIC + java.awt.Font.BOLD:
					style |= SWT.ITALIC | SWT.BOLD;
					break;
			}
			fontData.setStyle(style);
			// convert the font size (in pt for awt) to height in pixels for swt
			int height = (int) FastMath.round(font.getSize() * 72.0 / device.getDPI().y);
			fontData.setHeight(height);
			// hack to ensure the newly created swt fonts will be rendered with the
			// same height as the awt one
			if ( ensureSameSize ) {
				GC tmpGC = new GC(device);
				Font tmpFont = new Font(device, fontData);
				tmpGC.setFont(tmpFont);
				if ( tmpGC.textExtent(Az).x > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az) ) {
					while (tmpGC.textExtent(Az).x > DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
						tmpFont.dispose();
						height--;
						fontData.setHeight(height);
						tmpFont = new Font(device, fontData);
						tmpGC.setFont(tmpFont);
					}
				} else if ( tmpGC.textExtent(Az).x < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az) ) {
					while (tmpGC.textExtent(Az).x < DUMMY_PANEL.getFontMetrics(font).stringWidth(Az)) {
						tmpFont.dispose();
						height++;
						fontData.setHeight(height);
						tmpFont = new Font(device, fontData);
						tmpGC.setFont(tmpFont);
					}
				}
				tmpFont.dispose();
				tmpGC.dispose();
			}
			return fontData;
		}

		/**
		 * Create an awt font by converting as much information
		 * as possible from the provided swt <code>FontData</code>.
		 * <p>
		 * Generally speaking, given a font size, an swt font will display differently on the screen than the corresponding awt one. Because the SWT toolkit use native graphical ressources whenever it
		 * is possible, this fact is platform dependent. To address this issue, it is possible to enforce the method to return an awt font with the same height as the swt one.
		 *
		 * @param device The swt device being drawn on (display or gc device).
		 * @param fontData The swt font to convert.
		 * @param ensureSameSize A boolean used to enforce the same size
		 *            (in pixels) between the swt font and the newly created awt font.
		 * @return An awt font converted from the provided swt font.
		 */
		public static java.awt.Font toAwtFont(final Device device, final FontData fontData,
			final boolean ensureSameSize) {
			int style;
			switch (fontData.getStyle()) {
				case SWT.NORMAL:
					style = java.awt.Font.PLAIN;
					break;
				case SWT.ITALIC:
					style = java.awt.Font.ITALIC;
					break;
				case SWT.BOLD:
					style = java.awt.Font.BOLD;
					break;
				default:
					style = java.awt.Font.PLAIN;
					break;
			}
			int height = (int) FastMath.round(fontData.getHeight() * device.getDPI().y / 72.0);
			// hack to ensure the newly created awt fonts will be rendered with the
			// same height as the swt one
			if ( ensureSameSize ) {
				GC tmpGC = new GC(device);
				Font tmpFont = new Font(device, fontData);
				tmpGC.setFont(tmpFont);
				JPanel DUMMY_PANEL = new JPanel();
				java.awt.Font tmpAwtFont = new java.awt.Font(fontData.getName(), style, height);
				if ( DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) > tmpGC.textExtent(Az).x ) {
					while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) > tmpGC.textExtent(Az).x) {
						height--;
						tmpAwtFont = new java.awt.Font(fontData.getName(), style, height);
					}
				} else if ( DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) < tmpGC.textExtent(Az).x ) {
					while (DUMMY_PANEL.getFontMetrics(tmpAwtFont).stringWidth(Az) < tmpGC.textExtent(Az).x) {
						height++;
						tmpAwtFont = new java.awt.Font(fontData.getName(), style, height);
					}
				}
				tmpFont.dispose();
				tmpGC.dispose();
			}
			return new java.awt.Font(fontData.getName(), style, height);
		}

		/**
		 * Create an awt font by converting as much information
		 * as possible from the provided swt <code>Font</code>.
		 *
		 * @param device The swt device to draw on (display or gc device).
		 * @param font The swt font to convert.
		 * @return An awt font converted from the provided swt font.
		 */
		public static java.awt.Font toAwtFont(final Device device, final Font font) {
			FontData fontData = font.getFontData()[0];
			return toAwtFont(device, fontData, true);
		}

		/**
		 * Creates a swt color instance to match the rgb values
		 * of the specified awt paint. For now, this method test
		 * if the paint is a color and then return the adequate
		 * swt color. Otherwise plain black is assumed.
		 *
		 * @param device The swt device to draw on (display or gc device).
		 * @param paint The awt color to match.
		 * @return a swt color object.
		 */
		public static Color toSwtColor(final Device device, final java.awt.Paint paint) {
			java.awt.Color color;
			if ( paint instanceof java.awt.Color ) {
				color = (java.awt.Color) paint;
			} else {
				try {
					throw new Exception(
						"only color is supported at present... " + "setting paint to uniform black color");
				} catch (Exception e) {
					e.printStackTrace();
					color = new java.awt.Color(0, 0, 0);
				}
			}
			return new org.eclipse.swt.graphics.Color(device, color.getRed(), color.getGreen(), color.getBlue());
		}

		/**
		 * Creates a swt color instance to match the rgb values
		 * of the specified awt color. alpha channel is not supported.
		 * Note that the dispose method will need to be called on the
		 * returned object.
		 *
		 * @param device The swt device to draw on (display or gc device).
		 * @param color The awt color to match.
		 * @return a swt color object.
		 */
		public static Color toSwtColor(final Device device, final java.awt.Color color) {
			return new org.eclipse.swt.graphics.Color(device, color.getRed(), color.getGreen(), color.getBlue());
		}

		/**
		 * Transform an awt Rectangle2d instance into a swt one.
		 * The coordinates are rounded to integer for the swt object.
		 * @param rect2d The awt rectangle to map.
		 * @return an swt <code>Rectangle</code> object.
		 */
		public static Rectangle toSwtRectangle(final Rectangle2D rect2d) {
			return new Rectangle((int) FastMath.round(rect2d.getMinX()), (int) FastMath.round(rect2d.getMinY()),
				(int) FastMath.round(rect2d.getWidth()), (int) FastMath.round(rect2d.getHeight()));
		}

		/**
		 * Transform a swt Rectangle instance into an awt one.
		 * @param rect the swt <code>Rectangle</code>
		 * @return a Rectangle2D.Double instance with
		 *         the eappropriate location and size.
		 */
		public static Rectangle2D toAwtRectangle(final Rectangle rect) {
			Rectangle2D rect2d = new Rectangle2D.Double();
			rect2d.setRect(rect.x, rect.y, rect.width, rect.height);
			return rect2d;
		}

		/**
		 * Returns an AWT point with the same coordinates as the specified
		 * SWT point.
		 *
		 * @param p the SWT point (<code>null</code> not permitted).
		 *
		 * @return An AWT point with the same coordinates as <code>p</code>.
		 *
		 * @see #toSwtPoint(java.awt.Point)
		 */
		public static Point2D toAwtPoint(final Point p) {
			return new java.awt.Point(p.x, p.y);
		}

		/**
		 * Returns an SWT point with the same coordinates as the specified
		 * AWT point.
		 *
		 * @param p the AWT point (<code>null</code> not permitted).
		 *
		 * @return An SWT point with the same coordinates as <code>p</code>.
		 *
		 * @see #toAwtPoint(Point)
		 */
		public static Point toSwtPoint(final java.awt.Point p) {
			return new Point(p.x, p.y);
		}

		/**
		 * Returns an SWT point with the same coordinates as the specified AWT
		 * point (rounded to integer values).
		 *
		 * @param p the AWT point (<code>null</code> not permitted).
		 *
		 * @return An SWT point with the same coordinates as <code>p</code>.
		 *
		 * @see #toAwtPoint(Point)
		 */
		public static Point toSwtPoint(final java.awt.geom.Point2D p) {
			return new Point((int) FastMath.round(p.getX()), (int) FastMath.round(p.getY()));
		}

		/**
		 * Creates an AWT <code>MouseEvent</code> from a swt event.
		 * This method helps passing SWT mouse event to awt components.
		 * @param event The swt event.
		 * @return A AWT mouse event based on the given SWT event.
		 */
		public static java.awt.event.MouseEvent toAwtMouseEvent(final org.eclipse.swt.events.MouseEvent event) {
			int button = java.awt.event.MouseEvent.NOBUTTON;
			switch (event.button) {
				case 1:
					button = java.awt.event.MouseEvent.BUTTON1;
					break;
				case 2:
					button = java.awt.event.MouseEvent.BUTTON2;
					break;
				case 3:
					button = java.awt.event.MouseEvent.BUTTON3;
					break;
			}
			int modifiers = 0;
			if ( (event.stateMask & SWT.CTRL) != 0 ) {
				modifiers |= InputEvent.CTRL_DOWN_MASK;
			}
			if ( (event.stateMask & SWT.SHIFT) != 0 ) {
				modifiers |= InputEvent.SHIFT_DOWN_MASK;
			}
			if ( (event.stateMask & SWT.ALT) != 0 ) {
				modifiers |= InputEvent.ALT_DOWN_MASK;
			}
			java.awt.event.MouseEvent awtMouseEvent = new java.awt.event.MouseEvent(DUMMY_PANEL, event.hashCode(),
				event.time, modifiers, event.x, event.y, event.count, false, button);
			return awtMouseEvent;
		}

	}

	public static class SWTAxisEditor extends Composite {

		/** The axis label. */
		private final Text label;

		/** The font used to draw the axis labels. */
		private FontData labelFont;

		/** The paint (color) used to draw the axis labels. */
		private Color labelPaintColor;

		/** The font used to draw the axis tick labels. */
		private FontData tickLabelFont;

		/** The paint (color) used to draw the axis tick labels. */
		private final Color tickLabelPaintColor;

		/** A field showing a description of the label font. */
		private final Text labelFontField;

		/**
		 * A field containing a description of the font
		 * for displaying tick labels on the axis.
		 */
		private final Text tickLabelFontField;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources = ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/** Font object used to handle a change of font. */
		private Font font;

		/** A flag that indicates whether or not the tick labels are visible. */
		private final Button showTickLabelsCheckBox;

		/** A flag that indicates whether or not the tick marks are visible. */
		private final Button showTickMarksCheckBox;

		/** A tabbed pane for... */
		private final TabFolder otherTabs;

		/**
		 * Standard constructor: builds a composite for displaying/editing
		 * the properties of the specified axis.
		 *
		 * @param parent The parent composite.
		 * @param style The SWT style of the SwtAxisEditor.
		 * @param axis the axis whose properties are to be displayed/edited
		 *            in the composite.
		 */
		public SWTAxisEditor(final Composite parent, final int style, final Axis axis) {
			super(parent, style);
			this.labelFont = SWTUtils.toSwtFontData(getDisplay(), axis.getLabelFont(), true);
			this.labelPaintColor = SWTUtils.toSwtColor(getDisplay(), axis.getLabelPaint());
			this.tickLabelFont = SWTUtils.toSwtFontData(getDisplay(), axis.getTickLabelFont(), true);
			this.tickLabelPaintColor = SWTUtils.toSwtColor(getDisplay(), axis.getTickLabelPaint());

			FillLayout layout = new FillLayout(SWT.VERTICAL);
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);
			Group general = new Group(this, SWT.NONE);
			general.setLayout(new GridLayout(3, false));
			general.setText("General");
			// row 1
			new Label(general, SWT.NONE).setText("Label");
			this.label = new Text(general, SWT.BORDER);
			if ( axis.getLabel() != null ) {
				this.label.setText(axis.getLabel());
			}
			this.label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			new Label(general, SWT.NONE).setText(""); // empty cell
			// row 2
			new Label(general, SWT.NONE).setText("Font");
			this.labelFontField = new Text(general, SWT.BORDER);
			this.labelFontField.setText(this.labelFont.toString());
			this.labelFontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			Button selectFontButton = new Button(general, SWT.PUSH);
			selectFontButton.setText("Select...");
			selectFontButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the color-change dialog
					FontDialog dlg = new FontDialog(getShell());
					dlg.setText("Font Selection");
					dlg.setFontList(new FontData[] { SWTAxisEditor.this.labelFont });
					if ( dlg.open() != null ) {
						// Dispose of any fonts we have created
						if ( SWTAxisEditor.this.font != null ) {
							SWTAxisEditor.this.font.dispose();
						}
						// Create the new font and set it into the title
						// label
						SWTAxisEditor.this.font = new Font(getShell().getDisplay(), dlg.getFontList());
						// label.setFont(font);
						SWTAxisEditor.this.labelFontField.setText(SWTAxisEditor.this.font.getFontData()[0].toString());
						SWTAxisEditor.this.labelFont = SWTAxisEditor.this.font.getFontData()[0];
					}
				}
			});
			// row 3
			new Label(general, SWT.NONE).setText("Paint");
			// Use a colored text field to show the color
			final SWTPaintCanvas colorCanvas = new SWTPaintCanvas(general, SWT.NONE, this.labelPaintColor);
			GridData canvasGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			canvasGridData.heightHint = 20;
			colorCanvas.setLayoutData(canvasGridData);
			Button selectColorButton = new Button(general, SWT.PUSH);
			selectColorButton.setText("Select...");
			selectColorButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the color-change dialog
					ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Title_Color");
					dlg.setRGB(SWTAxisEditor.this.labelPaintColor.getRGB());
					RGB rgb = dlg.open();
					if ( rgb != null ) {
						// create the new color and set it to the
						// SwtPaintCanvas
						SWTAxisEditor.this.labelPaintColor = new Color(getDisplay(), rgb);
						colorCanvas.setColor(SWTAxisEditor.this.labelPaintColor);
					}
				}
			});
			Group other = new Group(this, SWT.NONE);
			FillLayout tabLayout = new FillLayout();
			tabLayout.marginHeight = tabLayout.marginWidth = 4;
			other.setLayout(tabLayout);
			other.setText("Other");

			this.otherTabs = new TabFolder(other, SWT.NONE);
			TabItem item1 = new TabItem(this.otherTabs, SWT.NONE);
			item1.setText(" " + "Ticks" + " ");
			Composite ticks = new Composite(this.otherTabs, SWT.NONE);
			ticks.setLayout(new GridLayout(3, false));
			this.showTickLabelsCheckBox = new Button(ticks, SWT.CHECK);
			this.showTickLabelsCheckBox.setText("Show tick labels");
			this.showTickLabelsCheckBox.setSelection(axis.isTickLabelsVisible());
			this.showTickLabelsCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			new Label(ticks, SWT.NONE).setText("Tick label font");
			this.tickLabelFontField = new Text(ticks, SWT.BORDER);
			this.tickLabelFontField.setText(this.tickLabelFont.toString());
			// tickLabelFontField.setFont(SwtUtils.toSwtFontData(getDisplay(),
			// axis.getTickLabelFont()));
			this.tickLabelFontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			Button selectTickLabelFontButton = new Button(ticks, SWT.PUSH);
			selectTickLabelFontButton.setText("Select...");
			selectTickLabelFontButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the font-change dialog
					FontDialog dlg = new FontDialog(getShell());
					dlg.setText("Font Selection");
					dlg.setFontList(new FontData[] { SWTAxisEditor.this.tickLabelFont });
					if ( dlg.open() != null ) {
						// Dispose of any fonts we have created
						if ( SWTAxisEditor.this.font != null ) {
							SWTAxisEditor.this.font.dispose();
						}
						// Create the new font and set it into the title
						// label
						SWTAxisEditor.this.font = new Font(getShell().getDisplay(), dlg.getFontList());
						// tickLabelFontField.setFont(font);
						SWTAxisEditor.this.tickLabelFontField
							.setText(SWTAxisEditor.this.font.getFontData()[0].toString());
						SWTAxisEditor.this.tickLabelFont = SWTAxisEditor.this.font.getFontData()[0];
					}
				}
			});
			this.showTickMarksCheckBox = new Button(ticks, SWT.CHECK);
			this.showTickMarksCheckBox.setText("Show tick marks");
			this.showTickMarksCheckBox.setSelection(axis.isTickMarksVisible());
			this.showTickMarksCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			item1.setControl(ticks);
		}

		/**
		 * A static method that returns a panel that is appropriate
		 * for the axis type.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param axis the axis whose properties are to be displayed/edited
		 *            in the composite.
		 * @return A composite or <code>null</code< if axis is <code>null</code>.
		 */
		public static SWTAxisEditor getInstance(final Composite parent, final int style, final Axis axis) {

			if ( axis != null ) {
				// return the appropriate axis editor
				if ( axis instanceof NumberAxis ) {
					return new SWTNumberAxisEditor(parent, style, (NumberAxis) axis);
				} else {
					return new SWTAxisEditor(parent, style, axis);
				}
			} else {
				return null;
			}
		}

		/**
		 * Returns a reference to the tabbed composite.
		 *
		 * @return A reference to the tabbed composite.
		 */
		public TabFolder getOtherTabs() {
			return this.otherTabs;
		}

		/**
		 * Returns the current axis label.
		 *
		 * @return The current axis label.
		 */
		public String getLabel() {
			return this.label.getText();
		}

		/**
		 * Returns the current label font.
		 *
		 * @return The current label font.
		 */
		public java.awt.Font getLabelFont() {
			return SWTUtils.toAwtFont(getDisplay(), this.labelFont, true);
		}

		/**
		 * Returns the current label paint.
		 *
		 * @return The current label paint.
		 */
		public Paint getTickLabelPaint() {
			return GamaColors.toAwtColor(this.tickLabelPaintColor);
		}

		/**
		 * Returns the current label font.
		 *
		 * @return The current label font.
		 */
		public java.awt.Font getTickLabelFont() {
			return SWTUtils.toAwtFont(getDisplay(), this.tickLabelFont, true);
		}

		/**
		 * Returns the current label paint.
		 *
		 * @return The current label paint.
		 */
		public Paint getLabelPaint() {
			return GamaColors.toAwtColor(this.labelPaintColor);
		}

		/**
		 * Sets the properties of the specified axis to match
		 * the properties defined on this panel.
		 *
		 * @param axis the axis.
		 */
		public void setAxisProperties(final Axis axis) {
			axis.setLabel(getLabel());
			axis.setLabelFont(getLabelFont());
			axis.setLabelPaint(getLabelPaint());
			axis.setTickMarksVisible(this.showTickMarksCheckBox.getSelection());
			axis.setTickLabelsVisible(this.showTickLabelsCheckBox.getSelection());
			axis.setTickLabelFont(getTickLabelFont());
			axis.setTickLabelPaint(getTickLabelPaint());
		}
	}

	public static class SWTPaintCanvas extends Canvas {

		private Color myColor;

		/**
		 * Creates a new instance.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param color the color.
		 */
		public SWTPaintCanvas(final Composite parent, final int style, final Color color) {
			this(parent, style);
			this.setColor(color);
		}

		/**
		 * Creates a new instance.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 */
		public SWTPaintCanvas(final Composite parent, final int style) {
			super(parent, style);
			addPaintListener(new PaintListener() {

				@Override
				public void paintControl(final PaintEvent e) {
					e.gc.setForeground(e.gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
					e.gc.setBackground(SWTPaintCanvas.this.myColor);
					e.gc.fillRectangle(getClientArea());
					e.gc.drawRectangle(getClientArea().x, getClientArea().y, getClientArea().width - 1,
						getClientArea().height - 1);
				}
			});
		}

		/**
		 * Sets the color.
		 *
		 * @param color the color.
		 */
		public void setColor(final Color color) {
			if ( this.myColor != null ) {
				this.myColor.dispose();
			}
			// this.myColor = new Color(getDisplay(), color.getRGB());
			this.myColor = color;
		}

		/**
		 * Returns the color.
		 *
		 * @return The color.
		 */
		public Color getColor() {
			return this.myColor;
		}

		/**
		 * Overridden to do nothing.
		 *
		 * @param c the color.
		 */
		@Override
		public void setBackground(final Color c) {
			return;
		}

		/**
		 * Overridden to do nothing.
		 *
		 * @param c the color.
		 */
		@Override
		public void setForeground(final Color c) {
			return;
		}

		/**
		 * Frees resources.
		 */
		@Override
		public void dispose() {
			this.myColor.dispose();
		}
	}

	static class SWTPlotAppearanceEditor extends Composite {

		private final Spinner selectStroke;

		/** The stroke (pen) used to draw the outline of the plot. */
		private final SWTStrokeCanvas strokeCanvas;

		/** The paint (color) used to fill the background of the plot. */
		private final SWTPaintCanvas backgroundPaintCanvas;

		/** The paint (color) used to draw the outline of the plot. */
		private final SWTPaintCanvas outlinePaintCanvas;

		/** The orientation for the plot. */
		private PlotOrientation plotOrientation;

		private Combo orientation;

		/** Orientation constants. */
		private final static String[] orientationNames = { "Vertical", "Horizontal" };
		private final static int ORIENTATION_VERTICAL = 0;
		private final static int ORIENTATION_HORIZONTAL = 1;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources = ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		SWTPlotAppearanceEditor(final Composite parent, final int style, final Plot plot) {
			super(parent, style);
			FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			Group general = new Group(this, SWT.NONE);
			GridLayout groupLayout = new GridLayout(3, false);
			groupLayout.marginHeight = groupLayout.marginWidth = 4;
			general.setLayout(groupLayout);
			general.setText("General");

			// row 1: stroke
			new Label(general, SWT.NONE).setText("Outline stroke");
			this.strokeCanvas = new SWTStrokeCanvas(general, SWT.NONE);
			this.strokeCanvas.setStroke(plot.getOutlineStroke());
			GridData strokeGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			strokeGridData.heightHint = 20;
			this.strokeCanvas.setLayoutData(strokeGridData);
			this.selectStroke = new Spinner(general, SWT.BORDER);
			this.selectStroke.setMinimum(1);
			this.selectStroke.setMaximum(3);
			this.selectStroke.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			this.selectStroke.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					int w = SWTPlotAppearanceEditor.this.selectStroke.getSelection();
					if ( w > 0 ) {
						SWTPlotAppearanceEditor.this.strokeCanvas.setStroke(new BasicStroke(w));
						SWTPlotAppearanceEditor.this.strokeCanvas.redraw();
					}
				}
			});
			// row 2: outline color
			new Label(general, SWT.NONE).setText("Outline Paint");
			this.outlinePaintCanvas =
				new SWTPaintCanvas(general, SWT.NONE, SWTUtils.toSwtColor(getDisplay(), plot.getOutlinePaint()));
			GridData outlineGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			outlineGridData.heightHint = 20;
			this.outlinePaintCanvas.setLayoutData(outlineGridData);
			Button selectOutlineColor = new Button(general, SWT.PUSH);
			selectOutlineColor.setText("Select...");
			selectOutlineColor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			selectOutlineColor.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Outline Paint");
					dlg.setRGB(SWTPlotAppearanceEditor.this.outlinePaintCanvas.getColor().getRGB());
					RGB rgb = dlg.open();
					if ( rgb != null ) {
						SWTPlotAppearanceEditor.this.outlinePaintCanvas.setColor(new Color(getDisplay(), rgb));
					}
				}
			});
			// row 3: background paint
			new Label(general, SWT.NONE).setText("Background paint");
			this.backgroundPaintCanvas =
				new SWTPaintCanvas(general, SWT.NONE, SWTUtils.toSwtColor(getDisplay(), plot.getBackgroundPaint()));
			GridData bgGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			bgGridData.heightHint = 20;
			this.backgroundPaintCanvas.setLayoutData(bgGridData);
			Button selectBgPaint = new Button(general, SWT.PUSH);
			selectBgPaint.setText("Select...");
			selectBgPaint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			selectBgPaint.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Background paint");
					dlg.setRGB(SWTPlotAppearanceEditor.this.backgroundPaintCanvas.getColor().getRGB());
					RGB rgb = dlg.open();
					if ( rgb != null ) {
						SWTPlotAppearanceEditor.this.backgroundPaintCanvas.setColor(new Color(getDisplay(), rgb));
					}
				}
			});
			// row 4: orientation
			if ( plot instanceof CategoryPlot ) {
				this.plotOrientation = ((CategoryPlot) plot).getOrientation();
			} else if ( plot instanceof XYPlot ) {
				this.plotOrientation = ((XYPlot) plot).getOrientation();
			}
			if ( this.plotOrientation != null ) {
				boolean isVertical = this.plotOrientation.equals(PlotOrientation.VERTICAL);
				int index = isVertical ? ORIENTATION_VERTICAL : ORIENTATION_HORIZONTAL;
				new Label(general, SWT.NONE).setText("Orientation");
				this.orientation = new Combo(general, SWT.DROP_DOWN);
				this.orientation.setItems(orientationNames);
				this.orientation.select(index);
				this.orientation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
				this.orientation.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent event) {
						switch (SWTPlotAppearanceEditor.this.orientation.getSelectionIndex()) {
							case ORIENTATION_HORIZONTAL:
								SWTPlotAppearanceEditor.this.plotOrientation = PlotOrientation.HORIZONTAL;
								break;
							default:
								SWTPlotAppearanceEditor.this.plotOrientation = PlotOrientation.VERTICAL;
						}
					}
				});
			}
		}

		/**
		 * Returns the plot orientation.
		 *
		 * @return The plot orientation.
		 */
		public PlotOrientation getPlotOrientation() {
			return this.plotOrientation;
		}

		/**
		 * Returns the background paint.
		 *
		 * @return The background paint.
		 */
		public Color getBackGroundPaint() {
			return this.backgroundPaintCanvas.getColor();
		}

		/**
		 * Returns the outline paint.
		 *
		 * @return The outline paint.
		 */
		public Color getOutlinePaint() {
			return this.outlinePaintCanvas.getColor();
		}

		/**
		 * Returns the stroke.
		 *
		 * @return The stroke.
		 */
		public Stroke getStroke() {
			return this.strokeCanvas.getStroke();
		}
	}

	static class SWTNumberAxisEditor extends SWTAxisEditor implements FocusListener {

		/**
		 * A flag that indicates whether or not the axis range is determined
		 * automatically.
		 */
		private boolean autoRange;

		/** The lowest value in the axis range. */
		private double minimumValue;

		/** The highest value in the axis range. */
		private double maximumValue;

		/**
		 * A checkbox that indicates whether or not the axis range is determined
		 * automatically.
		 */
		private final Button autoRangeCheckBox;

		/** A text field for entering the minimum value in the axis range. */
		private final Text minimumRangeValue;

		/** A text field for entering the maximum value in the axis range. */
		private final Text maximumRangeValue;

		/**
		 * Creates a new editor.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param axis the axis.
		 */
		public SWTNumberAxisEditor(final Composite parent, final int style, final NumberAxis axis) {
			super(parent, style, axis);
			this.autoRange = axis.isAutoRange();
			this.minimumValue = axis.getLowerBound();
			this.maximumValue = axis.getUpperBound();

			TabItem item2 = new TabItem(getOtherTabs(), SWT.NONE);
			item2.setText(" " + "Range" + " ");
			Composite range = new Composite(getOtherTabs(), SWT.NONE);
			range.setLayout(new GridLayout(2, true));
			item2.setControl(range);

			this.autoRangeCheckBox = new Button(range, SWT.CHECK);
			this.autoRangeCheckBox.setText("Auto-adjust range");
			this.autoRangeCheckBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			this.autoRangeCheckBox.setSelection(this.autoRange);
			this.autoRangeCheckBox.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					toggleAutoRange();
				}
			});
			new Label(range, SWT.NONE).setText("Minimum range value");
			this.minimumRangeValue = new Text(range, SWT.BORDER);
			this.minimumRangeValue.setText(String.valueOf(this.minimumValue));
			this.minimumRangeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			this.minimumRangeValue.setEnabled(!this.autoRange);
			// this.minimumRangeValue.addModifyListener(this);
			// this.minimumRangeValue.addVerifyListener(this);
			this.minimumRangeValue.addFocusListener(this);
			new Label(range, SWT.NONE).setText("Maximum range value");
			this.maximumRangeValue = new Text(range, SWT.BORDER);
			this.maximumRangeValue.setText(String.valueOf(this.maximumValue));
			this.maximumRangeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			this.maximumRangeValue.setEnabled(!this.autoRange);
			// this.maximumRangeValue.addModifyListener(this);
			// this.maximumRangeValue.addVerifyListener(this);
			this.maximumRangeValue.addFocusListener(this);
		}

		/**
		 * Toggle the auto range setting.
		 */
		public void toggleAutoRange() {
			this.autoRange = this.autoRangeCheckBox.getSelection();
			if ( this.autoRange ) {
				this.minimumRangeValue.setText(Double.toString(this.minimumValue));
				this.minimumRangeValue.setEnabled(false);
				this.maximumRangeValue.setText(Double.toString(this.maximumValue));
				this.maximumRangeValue.setEnabled(false);
			} else {
				this.minimumRangeValue.setEnabled(true);
				this.maximumRangeValue.setEnabled(true);
			}
		}

		/**
		 * Revalidate the range minimum:
		 * it should be less than the current maximum.
		 *
		 * @param candidate the minimum value
		 *
		 * @return A boolean.
		 */
		public boolean validateMinimum(final String candidate) {
			boolean valid = true;
			try {
				if ( Double.parseDouble(candidate) >= this.maximumValue ) {
					valid = false;
				}
			} catch (NumberFormatException e) {
				valid = false;
			}
			return valid;
		}

		/**
		 * Revalidate the range maximum:
		 * it should be greater than the current minimum
		 *
		 * @param candidate the maximum value
		 *
		 * @return A boolean.
		 */
		public boolean validateMaximum(final String candidate) {
			boolean valid = true;
			try {
				if ( Double.parseDouble(candidate) <= this.minimumValue ) {
					valid = false;
				}
			} catch (NumberFormatException e) {
				valid = false;
			}
			return valid;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.swt.events.FocusListener#focusGained(
		 * org.eclipse.swt.events.FocusEvent)
		 */
		@Override
		public void focusGained(final FocusEvent e) {
			// don't need to do anything
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.swt.events.FocusListener#focusLost(
		 * org.eclipse.swt.events.FocusEvent)
		 */
		@Override
		public void focusLost(final FocusEvent e) {
			if ( e.getSource() == this.minimumRangeValue ) {
				// verify min value
				if ( !validateMinimum(this.minimumRangeValue.getText()) ) {
					this.minimumRangeValue.setText(String.valueOf(this.minimumValue));
				} else {
					this.minimumValue = Double.parseDouble(this.minimumRangeValue.getText());
				}
			} else if ( e.getSource() == this.maximumRangeValue ) {
				// verify max value
				if ( !validateMaximum(this.maximumRangeValue.getText()) ) {
					this.maximumRangeValue.setText(String.valueOf(this.maximumValue));
				} else {
					this.maximumValue = Double.parseDouble(this.maximumRangeValue.getText());
				}
			}
		}

		/**
		 * Sets the properties of the specified axis to match
		 * the properties defined on this panel.
		 *
		 * @param axis the axis.
		 */
		@Override
		public void setAxisProperties(final Axis axis) {
			super.setAxisProperties(axis);
			NumberAxis numberAxis = (NumberAxis) axis;
			numberAxis.setAutoRange(this.autoRange);
			if ( !this.autoRange ) {
				numberAxis.setRange(this.minimumValue, this.maximumValue);
			}
		}
	}

	static class SWTStrokeCanvas extends Canvas {

		/**
		 * Creates a new instance.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 * @param image the image.
		 */
		public SWTStrokeCanvas(final Composite parent, final int style, final Image image) {
			this(parent, style);
		}

		/**
		 * Creates a new instance.
		 *
		 * @param parent the parent.
		 * @param style the style.
		 */
		public SWTStrokeCanvas(final Composite parent, final int style) {
			super(parent, style);
			addPaintListener(new PaintListener() {

				@Override
				public void paintControl(final PaintEvent e) {
					BasicStroke stroke = getStroke();
					if ( stroke != null ) {
						int x, y;
						Rectangle rect = getClientArea();
						x = (rect.width - 100) / 2;
						y = (rect.height - 16) / 2;
						Transform swtTransform = new Transform(e.gc.getDevice());
						e.gc.getTransform(swtTransform);
						swtTransform.translate(x, y);
						e.gc.setTransform(swtTransform);
						swtTransform.dispose();
						e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
						e.gc.setLineWidth((int) stroke.getLineWidth());
						e.gc.drawLine(10, 8, 90, 8);
					}
				}
			});
		}

		/**
		 * Sets the stroke.
		 *
		 * @param stroke the stroke.
		 */
		public void setStroke(final Stroke stroke) {
			if ( stroke instanceof BasicStroke ) {
				setData(stroke);
			} else {
				throw new RuntimeException("Can only handle 'Basic Stroke' at present.");
			}
		}

		/**
		 * Returns the stroke.
		 *
		 * @return The stroke.
		 */
		public BasicStroke getStroke() {
			return (BasicStroke) this.getData();
		}

	}
}
