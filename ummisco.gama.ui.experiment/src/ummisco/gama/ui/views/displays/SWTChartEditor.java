/*******************************************************************************************************
 *
 * SWTChartEditor.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package ummisco.gama.ui.views.displays;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Stroke;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.editor.ChartEditor;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.title.Title;

import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.utils.GraphicsHelper;

/**
 * An editor for chart properties.
 */
public class SWTChartEditor implements ChartEditor {

	/** The shell */
	final Shell shell;

	/** The chart which the properties have to be edited */
	final JFreeChart chart;

	/** A composite for displaying/editing the properties of the title. */
	final SWTTitleEditor titleEditor;

	/** A composite for displaying/editing the properties of the plot. */
	final SWTPlotEditor plotEditor;

	/** A composite for displaying/editing the other properties of the chart. */
	final SWTOtherEditor otherEditor;

	/** The resourceBundle for the localization. */
	// protected static ResourceBundle localizationResources =
	// ResourceBundleWrapper
	// .getBundle("org.jfree.chart.editor.LocalizationBundle");

	/**
	 * Creates a new editor.
	 *
	 * @param display
	 *            the display.
	 * @param chart2edit
	 *            the chart to edit.
	 */
	public SWTChartEditor(final Display display, final JFreeChart chart2edit, final Point position) {
		this.shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.TOOL | SWT.TITLE);
		this.shell.setSize(400, 500);
		this.chart = chart2edit;
		this.shell.setText("Chart properties");
		this.shell.setLocation(position);
		final GridLayout layout = new GridLayout(2, false);
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 5;
		this.shell.setLayout(layout);
		final Composite main = new Composite(this.shell, SWT.NONE);
		main.setLayout(new FillLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		final TabFolder tab = new TabFolder(main, SWT.BORDER);
		// build first tab
		final TabItem item1 = new TabItem(tab, SWT.NONE);
		item1.setText(" " + "Title" + " ");
		this.titleEditor = new SWTTitleEditor(tab, SWT.NONE, this.chart.getTitle());
		item1.setControl(this.titleEditor);
		// build second tab
		final TabItem item2 = new TabItem(tab, SWT.NONE);
		item2.setText(" " + "Plot" + " ");
		this.plotEditor = new SWTPlotEditor(tab, SWT.NONE, this.chart.getPlot());
		item2.setControl(this.plotEditor);
		// build the third tab
		final TabItem item3 = new TabItem(tab, SWT.NONE);
		item3.setText(" " + "Other" + " ");
		this.otherEditor = new SWTOtherEditor(tab, SWT.NONE, this.chart);
		item3.setControl(this.otherEditor);

		// ok and cancel buttons
		final Button cancel = new Button(this.shell, SWT.PUSH);
		cancel.setText(" Cancel ");
		cancel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));
		cancel.pack();
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SWTChartEditor.this.shell.dispose();
			}
		});
		final Button ok = new Button(this.shell, SWT.PUSH | SWT.OK);
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
			if (!this.shell.getDisplay().readAndDispatch()) { this.shell.getDisplay().sleep(); }
		}
	}

	/**
	 * Updates the chart properties.
	 *
	 * @param chart
	 *            the chart.
	 */
	@Override
	public void updateChart(final JFreeChart chart) {
		this.titleEditor.setTitleProperties(chart);
		this.plotEditor.updatePlotProperties(chart.getPlot());
		this.otherEditor.updateChartProperties(chart);
	}

	/**
	 * The Class SWTTitleEditor.
	 */
	class SWTTitleEditor extends Composite {

		/** Whether or not to display the title on the chart. */
		boolean showTitle;

		/** The checkbox to indicate whether or not to display the title. */
		final Button showTitleCheckBox;

		/** A field for displaying/editing the title text. */
		final Text titleField;

		/** The font used to draw the title. */
		FontData titleFont;

		/** A field for displaying a description of the title font. */
		final Text fontField;

		/** The button to use to select a new title font. */
		final Button selectFontButton;

		/** The paint (color) used to draw the title. */
		Color titleColor;

		/**
		 * The button to use to select a new paint (color) to draw the title.
		 */
		final Button selectColorButton;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources =
		// ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/** Font object used to handle a change of font. */
		Font font;

		/**
		 * Standard constructor: builds a panel for displaying/editing the properties of the specified title.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 * @param title
		 *            the title, which should be changed.
		 *
		 */
		SWTTitleEditor(final Composite parent, final int style, final Title title) {
			super(parent, style);
			final FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			final TextTitle t = title != null ? (TextTitle) title : new TextTitle("Title");
			this.showTitle = title != null;
			this.titleFont = GraphicsHelper.toSwtFontData(getDisplay(), t.getFont(), true);
			this.titleColor = GraphicsHelper.toSwtColor(getDisplay(), t.getPaint());

			final Group general = new Group(this, SWT.NONE);
			general.setLayout(new GridLayout(3, false));
			general.setText("General");
			// row 1
			final Label label = new Label(general, SWT.NONE);
			label.setText("Show Title");
			final GridData gridData = new GridData();
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
					final FontDialog dlg = new FontDialog(getShell());
					dlg.setText("Font_Selection");
					dlg.setFontList(new FontData[] { SWTTitleEditor.this.titleFont });
					if (dlg.open() != null) {
						// Dispose of any fonts we have created
						if (SWTTitleEditor.this.font != null) { SWTTitleEditor.this.font.dispose(); }
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
			final GridData canvasGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			canvasGridData.heightHint = 20;
			colorCanvas.setLayoutData(canvasGridData);
			this.selectColorButton = new Button(general, SWT.PUSH);
			this.selectColorButton.setText("Select...");
			this.selectColorButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the color-change dialog
					final ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Title_Color");
					dlg.setRGB(SWTTitleEditor.this.titleColor.getRGB());
					final RGB rgb = dlg.open();
					if (rgb != null) {
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
		public String getTitleText() { return this.titleField.getText(); }

		/**
		 * Returns the font selected in the panel.
		 *
		 * @return The font selected in the panel.
		 */
		public FontData getTitleFont() { return this.titleFont; }

		/**
		 * Returns the font selected in the panel.
		 *
		 * @return The font selected in the panel.
		 */
		public Color getTitleColor() { return this.titleColor; }

		/**
		 * Sets the properties of the specified title to match the properties defined on this panel.
		 *
		 * @param chart
		 *            the chart whose title is to be modified.
		 */
		public void setTitleProperties(final JFreeChart chart) {
			if (this.showTitle) {
				TextTitle title = chart.getTitle();
				if (title == null) {
					title = new TextTitle();
					chart.setTitle(title);
				}
				title.setText(getTitleText());
				title.setFont(GraphicsHelper.toAwtFont(getDisplay(), getTitleFont(), true));
				title.setPaint(GamaColors.toAwtColor(getTitleColor()));
			} else {
				chart.setTitle((TextTitle) null);
			}
		}
	}

	/**
	 * The Class SWTPlotEditor.
	 */
	class SWTPlotEditor extends Composite {

		/**
		 * A panel used to display/edit the properties of the domain axis (if any).
		 */
		private final SWTAxisEditor domainAxisPropertyPanel;

		/**
		 * A panel used to display/edit the properties of the range axis (if any).
		 */
		private final SWTAxisEditor rangeAxisPropertyPanel;

		/** The plot appearance. */
		private final SWTPlotAppearanceEditor plotAppearance;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources =
		// ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/**
		 * Creates a new editor for the specified plot.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 * @param plot
		 *            the plot.
		 */
		public SWTPlotEditor(final Composite parent, final int style, final Plot plot) {
			super(parent, style);
			final FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			final Group plotType = new Group(this, SWT.NONE);
			final FillLayout plotTypeLayout = new FillLayout();
			plotTypeLayout.marginHeight = plotTypeLayout.marginWidth = 4;
			plotType.setLayout(plotTypeLayout);
			plotType.setText(plot.getPlotType() + ":");

			final TabFolder tabs = new TabFolder(plotType, SWT.NONE);

			// deal with domain axis
			final TabItem item1 = new TabItem(tabs, SWT.NONE);
			item1.setText("Domain Axis");
			Axis domainAxis = null;
			if (plot instanceof CategoryPlot) {
				domainAxis = ((CategoryPlot) plot).getDomainAxis();
			} else if (plot instanceof XYPlot) { domainAxis = ((XYPlot) plot).getDomainAxis(); }
			this.domainAxisPropertyPanel = SWTAxisEditor.getInstance(tabs, SWT.NONE, domainAxis);
			item1.setControl(this.domainAxisPropertyPanel);

			// deal with range axis
			final TabItem item2 = new TabItem(tabs, SWT.NONE);
			item2.setText("Range Axis");
			Axis rangeAxis = null;
			if (plot instanceof CategoryPlot) {
				rangeAxis = ((CategoryPlot) plot).getRangeAxis();
			} else if (plot instanceof XYPlot) { rangeAxis = ((XYPlot) plot).getRangeAxis(); }
			this.rangeAxisPropertyPanel = SWTAxisEditor.getInstance(tabs, SWT.NONE, rangeAxis);
			item2.setControl(this.rangeAxisPropertyPanel);

			// deal with plot appearance
			final TabItem item3 = new TabItem(tabs, SWT.NONE);
			item3.setText("Appearance");
			this.plotAppearance = new SWTPlotAppearanceEditor(tabs, SWT.NONE, plot);
			item3.setControl(this.plotAppearance);
		}

		/**
		 * Returns the current outline stroke.
		 *
		 * @return The current outline stroke.
		 */
		public Color getBackgroundPaint() { return this.plotAppearance.getBackGroundPaint(); }

		/**
		 * Returns the current outline stroke.
		 *
		 * @return The current outline stroke.
		 */
		public Color getOutlinePaint() { return this.plotAppearance.getOutlinePaint(); }

		/**
		 * Returns the current outline stroke.
		 *
		 * @return The current outline stroke.
		 */
		public Stroke getOutlineStroke() { return this.plotAppearance.getStroke(); }

		/**
		 * Updates the plot properties to match the properties defined on the panel.
		 *
		 * @param plot
		 *            The plot.
		 */
		public void updatePlotProperties(final Plot plot) {
			// set the plot properties...
			plot.setBackgroundPaint(GamaColors.toAwtColor(getBackgroundPaint()));
			plot.setOutlinePaint(GamaColors.toAwtColor(getOutlinePaint()));
			plot.setOutlineStroke(getOutlineStroke());

			// set the axis properties
			if (this.domainAxisPropertyPanel != null) {
				Axis domainAxis = null;
				if (plot instanceof CategoryPlot) {
					final CategoryPlot p = (CategoryPlot) plot;
					domainAxis = p.getDomainAxis();
				} else if (plot instanceof XYPlot) {
					final XYPlot p = (XYPlot) plot;
					domainAxis = p.getDomainAxis();
				}
				if (domainAxis != null) { this.domainAxisPropertyPanel.setAxisProperties(domainAxis); }
			}
			if (this.rangeAxisPropertyPanel != null) {
				Axis rangeAxis = null;
				if (plot instanceof CategoryPlot) {
					final CategoryPlot p = (CategoryPlot) plot;
					rangeAxis = p.getRangeAxis();
				} else if (plot instanceof XYPlot) {
					final XYPlot p = (XYPlot) plot;
					rangeAxis = p.getRangeAxis();
				}
				if (rangeAxis != null) { this.rangeAxisPropertyPanel.setAxisProperties(rangeAxis); }
			}
			if (this.plotAppearance.getPlotOrientation() != null) {
				if (plot instanceof CategoryPlot) {
					final CategoryPlot p = (CategoryPlot) plot;
					p.setOrientation(this.plotAppearance.getPlotOrientation());
				} else if (plot instanceof XYPlot) {
					final XYPlot p = (XYPlot) plot;
					p.setOrientation(this.plotAppearance.getPlotOrientation());
				}
			}
		}
	}

	/**
	 * The Class SWTOtherEditor.
	 */
	class SWTOtherEditor extends Composite {

		/**
		 * A checkbox indicating whether or not the chart is drawn with anti-aliasing.
		 */
		final Button antialias;

		/** The chart background color. */
		final SWTPaintCanvas backgroundPaintCanvas;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources =
		// ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/**
		 * Creates a new instance.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 * @param chart
		 *            the chart.
		 */
		public SWTOtherEditor(final Composite parent, final int style, final JFreeChart chart) {
			super(parent, style);
			final FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			final Group general = new Group(this, SWT.NONE);
			general.setLayout(new GridLayout(3, false));
			general.setText("General");

			// row 1: antialiasing
			this.antialias = new Button(general, SWT.CHECK);
			this.antialias.setText("Draw anti-aliased");
			this.antialias.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
			this.antialias.setSelection(chart.getAntiAlias());

			// row 2: background paint for the chart
			new Label(general, SWT.NONE).setText("Background paint");
			this.backgroundPaintCanvas = new SWTPaintCanvas(general, SWT.NONE,
					GraphicsHelper.toSwtColor(getDisplay(), chart.getBackgroundPaint()));
			final GridData bgGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			bgGridData.heightHint = 20;
			this.backgroundPaintCanvas.setLayoutData(bgGridData);
			final Button selectBgPaint = new Button(general, SWT.PUSH);
			selectBgPaint.setText("Select...");
			selectBgPaint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			selectBgPaint.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					final ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Background_paint");
					dlg.setRGB(SWTOtherEditor.this.backgroundPaintCanvas.getColor().getRGB());
					final RGB rgb = dlg.open();
					if (rgb != null) {
						SWTOtherEditor.this.backgroundPaintCanvas.setColor(new Color(getDisplay(), rgb));
					}
				}
			});
		}

		/**
		 * Updates the chart.
		 *
		 * @param chart
		 *            the chart.
		 */
		public void updateChartProperties(final JFreeChart chart) {
			chart.setAntiAlias(this.antialias.getSelection());
			chart.setBackgroundPaint(GamaColors.toAwtColor(this.backgroundPaintCanvas.getColor()));
		}

	}

	/**
	 * The Class SWTAxisEditor.
	 */
	public static class SWTAxisEditor extends Composite {

		/** The axis label. */
		final Text label;

		/** The font used to draw the axis labels. */
		FontData labelFont;

		/** The paint (color) used to draw the axis labels. */
		Color labelPaintColor;

		/** The font used to draw the axis tick labels. */
		FontData tickLabelFont;

		/** The paint (color) used to draw the axis tick labels. */
		final Color tickLabelPaintColor;

		/** A field showing a description of the label font. */
		final Text labelFontField;

		/**
		 * A field containing a description of the font for displaying tick labels on the axis.
		 */
		final Text tickLabelFontField;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources =
		// ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		/** Font object used to handle a change of font. */
		Font font;

		/** A flag that indicates whether or not the tick labels are visible. */
		final Button showTickLabelsCheckBox;

		/** A flag that indicates whether or not the tick marks are visible. */
		final Button showTickMarksCheckBox;

		/** A tabbed pane for... */
		final TabFolder otherTabs;

		/**
		 * Standard constructor: builds a composite for displaying/editing the properties of the specified axis.
		 *
		 * @param parent
		 *            The parent composite.
		 * @param style
		 *            The SWT style of the SwtAxisEditor.
		 * @param axis
		 *            the axis whose properties are to be displayed/edited in the composite.
		 */
		public SWTAxisEditor(final Composite parent, final int style, final Axis axis) {
			super(parent, style);
			this.labelFont = GraphicsHelper.toSwtFontData(getDisplay(), axis.getLabelFont(), true);
			this.labelPaintColor = GraphicsHelper.toSwtColor(getDisplay(), axis.getLabelPaint());
			this.tickLabelFont = GraphicsHelper.toSwtFontData(getDisplay(), axis.getTickLabelFont(), true);
			this.tickLabelPaintColor = GraphicsHelper.toSwtColor(getDisplay(), axis.getTickLabelPaint());

			final FillLayout layout = new FillLayout(SWT.VERTICAL);
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);
			final Group general = new Group(this, SWT.NONE);
			general.setLayout(new GridLayout(3, false));
			general.setText("General");
			// row 1
			new Label(general, SWT.NONE).setText("Label");
			this.label = new Text(general, SWT.BORDER);
			if (axis.getLabel() != null) { this.label.setText(axis.getLabel()); }
			this.label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			new Label(general, SWT.NONE).setText(""); // empty cell
			// row 2
			new Label(general, SWT.NONE).setText("Font");
			this.labelFontField = new Text(general, SWT.BORDER);
			this.labelFontField.setText(this.labelFont.toString());
			this.labelFontField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			final Button selectFontButton = new Button(general, SWT.PUSH);
			selectFontButton.setText("Select...");
			selectFontButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the color-change dialog
					final FontDialog dlg = new FontDialog(getShell());
					dlg.setText("Font Selection");
					dlg.setFontList(new FontData[] { SWTAxisEditor.this.labelFont });
					if (dlg.open() != null) {
						// Dispose of any fonts we have created
						if (SWTAxisEditor.this.font != null) { SWTAxisEditor.this.font.dispose(); }
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
			final GridData canvasGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			canvasGridData.heightHint = 20;
			colorCanvas.setLayoutData(canvasGridData);
			final Button selectColorButton = new Button(general, SWT.PUSH);
			selectColorButton.setText("Select...");
			selectColorButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the color-change dialog
					final ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Title_Color");
					dlg.setRGB(SWTAxisEditor.this.labelPaintColor.getRGB());
					final RGB rgb = dlg.open();
					if (rgb != null) {
						// create the new color and set it to the
						// SwtPaintCanvas
						SWTAxisEditor.this.labelPaintColor = new Color(getDisplay(), rgb);
						colorCanvas.setColor(SWTAxisEditor.this.labelPaintColor);
					}
				}
			});
			final Group other = new Group(this, SWT.NONE);
			final FillLayout tabLayout = new FillLayout();
			tabLayout.marginHeight = tabLayout.marginWidth = 4;
			other.setLayout(tabLayout);
			other.setText("Other");

			this.otherTabs = new TabFolder(other, SWT.NONE);
			final TabItem item1 = new TabItem(this.otherTabs, SWT.NONE);
			item1.setText(" " + "Ticks" + " ");
			final Composite ticks = new Composite(this.otherTabs, SWT.NONE);
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
			final Button selectTickLabelFontButton = new Button(ticks, SWT.PUSH);
			selectTickLabelFontButton.setText("Select...");
			selectTickLabelFontButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					// Create the font-change dialog
					final FontDialog dlg = new FontDialog(getShell());
					dlg.setText("Font Selection");
					dlg.setFontList(new FontData[] { SWTAxisEditor.this.tickLabelFont });
					if (dlg.open() != null) {
						// Dispose of any fonts we have created
						if (SWTAxisEditor.this.font != null) { SWTAxisEditor.this.font.dispose(); }
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
		 * A static method that returns a panel that is appropriate for the axis type.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 * @param axis
		 *            the axis whose properties are to be displayed/edited in the composite.
		 * @return A composite or <code>null</code< if axis is <code>null</code> .
		 */
		public static SWTAxisEditor getInstance(final Composite parent, final int style, final Axis axis) {

			if (axis == null) return null;
			// return the appropriate axis editor
			if (axis instanceof NumberAxis) return new SWTNumberAxisEditor(parent, style, (NumberAxis) axis);
			return new SWTAxisEditor(parent, style, axis);
		}

		/**
		 * Returns a reference to the tabbed composite.
		 *
		 * @return A reference to the tabbed composite.
		 */
		public TabFolder getOtherTabs() { return this.otherTabs; }

		/**
		 * Returns the current axis label.
		 *
		 * @return The current axis label.
		 */
		public String getLabel() { return this.label.getText(); }

		/**
		 * Returns the current label font.
		 *
		 * @return The current label font.
		 */
		public java.awt.Font getLabelFont() { return GraphicsHelper.toAwtFont(getDisplay(), this.labelFont, true); }

		/**
		 * Returns the current label paint.
		 *
		 * @return The current label paint.
		 */
		public Paint getTickLabelPaint() { return GamaColors.toAwtColor(this.tickLabelPaintColor); }

		/**
		 * Returns the current label font.
		 *
		 * @return The current label font.
		 */
		public java.awt.Font getTickLabelFont() {
			return GraphicsHelper.toAwtFont(getDisplay(), this.tickLabelFont, true);
		}

		/**
		 * Returns the current label paint.
		 *
		 * @return The current label paint.
		 */
		public Paint getLabelPaint() { return GamaColors.toAwtColor(this.labelPaintColor); }

		/**
		 * Sets the properties of the specified axis to match the properties defined on this panel.
		 *
		 * @param axis
		 *            the axis.
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

	/**
	 * The Class SWTPaintCanvas.
	 */
	public static class SWTPaintCanvas extends Canvas {

		/** The my color. */
		private Color myColor;

		/**
		 * Creates a new instance.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 * @param color
		 *            the color.
		 */
		public SWTPaintCanvas(final Composite parent, final int style, final Color color) {
			this(parent, style);
			this.setColor(color);
		}

		/**
		 * Creates a new instance.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 */
		public SWTPaintCanvas(final Composite parent, final int style) {
			super(parent, style);
			addPaintListener(e -> {
				e.gc.setForeground(e.gc.getDevice().getSystemColor(SWT.COLOR_BLACK));
				e.gc.setBackground(SWTPaintCanvas.this.myColor);
				e.gc.fillRectangle(getClientArea());
				e.gc.drawRectangle(getClientArea().x, getClientArea().y, getClientArea().width - 1,
						getClientArea().height - 1);
			});
		}

		/**
		 * Sets the color.
		 *
		 * @param color
		 *            the color.
		 */
		public void setColor(final Color color) {
			if (this.myColor != null) { this.myColor.dispose(); }
			// this.myColor = new Color(getDisplay(), color.getRGB());
			this.myColor = color;
		}

		/**
		 * Returns the color.
		 *
		 * @return The color.
		 */
		public Color getColor() { return this.myColor; }

		/**
		 * Overridden to do nothing.
		 *
		 * @param c
		 *            the color.
		 */
		@Override
		public void setBackground(final Color c) {}

		/**
		 * Overridden to do nothing.
		 *
		 * @param c
		 *            the color.
		 */
		@Override
		public void setForeground(final Color c) {}

		/**
		 * Frees resources.
		 */
		@Override
		public void dispose() {
			this.myColor.dispose();
		}
	}

	/**
	 * The Class SWTPlotAppearanceEditor.
	 */
	static class SWTPlotAppearanceEditor extends Composite {

		/** The select stroke. */
		final Spinner selectStroke;

		/** The stroke (pen) used to draw the outline of the plot. */
		final SWTStrokeCanvas strokeCanvas;

		/** The paint (color) used to fill the background of the plot. */
		final SWTPaintCanvas backgroundPaintCanvas;

		/** The paint (color) used to draw the outline of the plot. */
		final SWTPaintCanvas outlinePaintCanvas;

		/** The orientation for the plot. */
		PlotOrientation plotOrientation;

		/** The orientation. */
		Combo orientation;

		/** Orientation constants. */
		final static String[] orientationNames = { "Vertical", "Horizontal" };

		/** The Constant ORIENTATION_VERTICAL. */
		final static int ORIENTATION_VERTICAL = 0;

		/** The Constant ORIENTATION_HORIZONTAL. */
		final static int ORIENTATION_HORIZONTAL = 1;

		/** The resourceBundle for the localization. */
		// protected static ResourceBundle localizationResources =
		// ResourceBundleWrapper
		// .getBundle("org.jfree.chart.editor.LocalizationBundle");

		SWTPlotAppearanceEditor(final Composite parent, final int style, final Plot plot) {
			super(parent, style);
			final FillLayout layout = new FillLayout();
			layout.marginHeight = layout.marginWidth = 4;
			setLayout(layout);

			final Group general = new Group(this, SWT.NONE);
			final GridLayout groupLayout = new GridLayout(3, false);
			groupLayout.marginHeight = groupLayout.marginWidth = 4;
			general.setLayout(groupLayout);
			general.setText("General");

			// row 1: stroke
			new Label(general, SWT.NONE).setText("Outline stroke");
			this.strokeCanvas = new SWTStrokeCanvas(general, SWT.NONE);
			this.strokeCanvas.setStroke(plot.getOutlineStroke());
			final GridData strokeGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			strokeGridData.heightHint = 20;
			this.strokeCanvas.setLayoutData(strokeGridData);
			this.selectStroke = new Spinner(general, SWT.BORDER);
			this.selectStroke.setMinimum(1);
			this.selectStroke.setMaximum(3);
			this.selectStroke.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			this.selectStroke.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					final int w = SWTPlotAppearanceEditor.this.selectStroke.getSelection();
					if (w > 0) {
						SWTPlotAppearanceEditor.this.strokeCanvas.setStroke(new BasicStroke(w));
						SWTPlotAppearanceEditor.this.strokeCanvas.redraw();
					}
				}
			});
			// row 2: outline color
			new Label(general, SWT.NONE).setText("Outline Paint");
			this.outlinePaintCanvas = new SWTPaintCanvas(general, SWT.NONE,
					GraphicsHelper.toSwtColor(getDisplay(), plot.getOutlinePaint()));
			final GridData outlineGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			outlineGridData.heightHint = 20;
			this.outlinePaintCanvas.setLayoutData(outlineGridData);
			final Button selectOutlineColor = new Button(general, SWT.PUSH);
			selectOutlineColor.setText("Select...");
			selectOutlineColor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			selectOutlineColor.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					final ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Outline Paint");
					dlg.setRGB(SWTPlotAppearanceEditor.this.outlinePaintCanvas.getColor().getRGB());
					final RGB rgb = dlg.open();
					if (rgb != null) {
						SWTPlotAppearanceEditor.this.outlinePaintCanvas.setColor(new Color(getDisplay(), rgb));
					}
				}
			});
			// row 3: background paint
			new Label(general, SWT.NONE).setText("Background paint");
			this.backgroundPaintCanvas = new SWTPaintCanvas(general, SWT.NONE,
					GraphicsHelper.toSwtColor(getDisplay(), plot.getBackgroundPaint()));
			final GridData bgGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			bgGridData.heightHint = 20;
			this.backgroundPaintCanvas.setLayoutData(bgGridData);
			final Button selectBgPaint = new Button(general, SWT.PUSH);
			selectBgPaint.setText("Select...");
			selectBgPaint.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
			selectBgPaint.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent event) {
					final ColorDialog dlg = new ColorDialog(getShell());
					dlg.setText("Background paint");
					dlg.setRGB(SWTPlotAppearanceEditor.this.backgroundPaintCanvas.getColor().getRGB());
					final RGB rgb = dlg.open();
					if (rgb != null) {
						SWTPlotAppearanceEditor.this.backgroundPaintCanvas.setColor(new Color(getDisplay(), rgb));
					}
				}
			});
			// row 4: orientation
			if (plot instanceof CategoryPlot) {
				this.plotOrientation = ((CategoryPlot) plot).getOrientation();
			} else if (plot instanceof XYPlot) { this.plotOrientation = ((XYPlot) plot).getOrientation(); }
			if (this.plotOrientation != null) {
				final boolean isVertical = this.plotOrientation.equals(PlotOrientation.VERTICAL);
				final int index = isVertical ? ORIENTATION_VERTICAL : ORIENTATION_HORIZONTAL;
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
		public PlotOrientation getPlotOrientation() { return this.plotOrientation; }

		/**
		 * Returns the background paint.
		 *
		 * @return The background paint.
		 */
		public Color getBackGroundPaint() { return this.backgroundPaintCanvas.getColor(); }

		/**
		 * Returns the outline paint.
		 *
		 * @return The outline paint.
		 */
		public Color getOutlinePaint() { return this.outlinePaintCanvas.getColor(); }

		/**
		 * Returns the stroke.
		 *
		 * @return The stroke.
		 */
		public Stroke getStroke() { return this.strokeCanvas.getStroke(); }
	}

	/**
	 * The Class SWTNumberAxisEditor.
	 */
	static class SWTNumberAxisEditor extends SWTAxisEditor implements FocusListener {

		/**
		 * A flag that indicates whether or not the axis range is determined automatically.
		 */
		private boolean autoRange;

		/** The lowest value in the axis range. */
		private double minimumValue;

		/** The highest value in the axis range. */
		private double maximumValue;

		/**
		 * A checkbox that indicates whether or not the axis range is determined automatically.
		 */
		private final Button autoRangeCheckBox;

		/** A text field for entering the minimum value in the axis range. */
		private final Text minimumRangeValue;

		/** A text field for entering the maximum value in the axis range. */
		private final Text maximumRangeValue;

		/**
		 * Creates a new editor.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 * @param axis
		 *            the axis.
		 */
		public SWTNumberAxisEditor(final Composite parent, final int style, final NumberAxis axis) {
			super(parent, style, axis);
			this.autoRange = axis.isAutoRange();
			this.minimumValue = axis.getLowerBound();
			this.maximumValue = axis.getUpperBound();

			final TabItem item2 = new TabItem(getOtherTabs(), SWT.NONE);
			item2.setText(" " + "Range" + " ");
			final Composite range = new Composite(getOtherTabs(), SWT.NONE);
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
			if (this.autoRange) {
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
		 * Revalidate the range minimum: it should be less than the current maximum.
		 *
		 * @param candidate
		 *            the minimum value
		 *
		 * @return A boolean.
		 */
		public boolean validateMinimum(final String candidate) {
			boolean valid = true;
			try {
				if (Double.parseDouble(candidate) >= this.maximumValue) { valid = false; }
			} catch (final NumberFormatException e) {
				valid = false;
			}
			return valid;
		}

		/**
		 * Revalidate the range maximum: it should be greater than the current minimum
		 *
		 * @param candidate
		 *            the maximum value
		 *
		 * @return A boolean.
		 */
		public boolean validateMaximum(final String candidate) {
			boolean valid = true;
			try {
				if (Double.parseDouble(candidate) <= this.minimumValue) { valid = false; }
			} catch (final NumberFormatException e) {
				valid = false;
			}
			return valid;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.swt.events.FocusListener#focusGained( org.eclipse.swt.events.FocusEvent)
		 */
		@Override
		public void focusGained(final FocusEvent e) {
			// don't need to do anything
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.swt.events.FocusListener#focusLost( org.eclipse.swt.events.FocusEvent)
		 */
		@Override
		public void focusLost(final FocusEvent e) {
			if (e.getSource() == this.minimumRangeValue) {
				// verify min value
				if (!validateMinimum(this.minimumRangeValue.getText())) {
					this.minimumRangeValue.setText(String.valueOf(this.minimumValue));
				} else {
					this.minimumValue = Double.parseDouble(this.minimumRangeValue.getText());
				}
			} else if (e.getSource() == this.maximumRangeValue) {
				// verify max value
				if (!validateMaximum(this.maximumRangeValue.getText())) {
					this.maximumRangeValue.setText(String.valueOf(this.maximumValue));
				} else {
					this.maximumValue = Double.parseDouble(this.maximumRangeValue.getText());
				}
			}
		}

		/**
		 * Sets the properties of the specified axis to match the properties defined on this panel.
		 *
		 * @param axis
		 *            the axis.
		 */
		@Override
		public void setAxisProperties(final Axis axis) {
			super.setAxisProperties(axis);
			final NumberAxis numberAxis = (NumberAxis) axis;
			numberAxis.setAutoRange(this.autoRange);
			if (!this.autoRange) { numberAxis.setRange(this.minimumValue, this.maximumValue); }
		}
	}

	/**
	 * The Class SWTStrokeCanvas.
	 */
	static class SWTStrokeCanvas extends Canvas {

		/**
		 * Creates a new instance.
		 *
		 * @param parent
		 *            the parent.
		 * @param style
		 *            the style.
		 */
		public SWTStrokeCanvas(final Composite parent, final int style) {
			super(parent, style);
			addPaintListener(e -> {
				final BasicStroke stroke = getStroke();
				if (stroke != null) {
					int x, y;
					final Rectangle rect = getClientArea();
					x = (rect.width - 100) / 2;
					y = (rect.height - 16) / 2;
					final Transform swtTransform = new Transform(e.gc.getDevice());
					e.gc.getTransform(swtTransform);
					swtTransform.translate(x, y);
					e.gc.setTransform(swtTransform);
					swtTransform.dispose();
					e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
					e.gc.setLineWidth((int) stroke.getLineWidth());
					e.gc.drawLine(10, 8, 90, 8);
				}
			});
		}

		/**
		 * Sets the stroke.
		 *
		 * @param stroke
		 *            the stroke.
		 */
		public void setStroke(final Stroke stroke) {
			if (!(stroke instanceof BasicStroke))
				throw new RuntimeException("Can only handle 'Basic Stroke' at present.");
			setData(stroke);
		}

		/**
		 * Returns the stroke.
		 *
		 * @return The stroke.
		 */
		public BasicStroke getStroke() { return (BasicStroke) this.getData(); }

	}
}
