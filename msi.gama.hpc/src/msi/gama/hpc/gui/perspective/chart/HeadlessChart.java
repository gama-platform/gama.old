/*********************************************************************************************
 *
 *
 * 'HeadlessChart.java', in plugin 'msi.gama.hpc', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.hpc.gui.perspective.chart;

import java.awt.*;
import java.util.ArrayList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;
import msi.gama.common.interfaces.IGui;
import msi.gama.hpc.simulation.*;

public class HeadlessChart extends ViewPart {

	public static final String ID = IGui.HEADLESS_CHART_ID;
	public ArrayList<Button> lstchkbox;
	public ArrayList<XYSeries> series;
	public ArrayList<String> lstvarname_flag;
	public ArrayList<Integer> lsttimestep;
	public XYSeriesCollection dataset;

	public HeadlessChart() {
		super();
	}

	public static String xmlfilename = "";
	Simulation sim;

	public void readDataset() {
		if ( xmlfilename != "" ) {
			ResultReader in = new ResultReader(
				"C://Users//Administrator//Desktop//GAMA//eclipse//samples//predatorPrey//simulation-outputs.xml");
			// xmlfilename);
			sim = in.parseXmlFile();
		}

	}

	private void createDataset() {
		lstvarname_flag = new ArrayList<String>();
		lsttimestep = new ArrayList<Integer>();
		lstchkbox = new ArrayList<Button>();
		readDataset();
		ArrayList<Result> listres = sim.result;
		int n = 0;
		series = new ArrayList<XYSeries>();
		for ( int i = 0; i < listres.size(); i++ ) {
			String varname = listres.get(i).getName();
			if ( lstvarname_flag.contains(varname) ) {
				int idx = lstvarname_flag.indexOf(varname);
				lsttimestep.set(idx, lsttimestep.get(idx) + 1);
				series.get(idx).add(lsttimestep.get(idx), listres.get(i).getValue());
			} else {
				lstvarname_flag.add(varname);
				Button b1 = new Button(comp.getParent(), SWT.CHECK);
				b1.setText("Show " + varname);
				b1.setSelection(true);
				b1.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						// TODO Auto-generated method stub
						showChart();
					}

					@Override
					public void widgetDefaultSelected(final SelectionEvent e) {
						// TODO Auto-generated method stub

					}

				});
				lstchkbox.add(b1);
				lsttimestep.add(new Integer(0));
				XYSeries ss = new XYSeries(varname);
				series.add(ss);
			}
			// System.out.println(" " + listres.get(i).getName()+" " + listres.get(i).getValue());
		}

	}

	private JFreeChart createChart(final XYDataset dataset) {

		// create the chart...
		final JFreeChart chart = ChartFactory.createXYLineChart("Line Chart from XML output file", // chart title
			"X", // x axis label
			"Y", // y axis label
			dataset, // data
			PlotOrientation.VERTICAL, true, // include legend
			true, // tooltips
			false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		chart.setBackgroundPaint(Color.white);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		// get a reference to the plot for further customisation...
		final XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);
		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		plot.setRenderer(renderer);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		// OPTIONAL CUSTOMISATION COMPLETED.

		return chart;

	}

	public void showChart() {

		dataset = new XYSeriesCollection();
		for ( int i = 0; i < series.size(); i++ ) {
			if ( lstchkbox.get(i).getSelection() ) {
				dataset.addSeries(series.get(i));
			}

		}

		final JFreeChart chart = createChart(dataset);
		panel = new ChartPanel(chart);

		frame.add(panel);
		frame.validate();

	}

	Composite comp;

	Frame frame;
	ChartPanel panel;

	@Override
	public void createPartControl(final Composite parent) {

		comp = new Composite(parent, SWT.NONE | SWT.EMBEDDED);
		frame = SWT_AWT.new_Frame(comp);
		if ( xmlfilename != "" ) {
			createDataset();
			showChart();
		}

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}