package msi.gama.gui.views;

import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Vector;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
//import msi.gama.headless.executor.BatchExecutor;
import msi.gama.hpc.gui.*;

public class HeadlessChart extends ViewPart {

	public static final String ID = GuiUtils.HEADLESS_CHART_ID;
	public ArrayList<Button> lstchkbox;
//	public XYDataset dataset;
	public ArrayList<XYSeries> series;
	public ArrayList<String> lstvarname_flag;
	public ArrayList<Integer> lsttimestep;
	public XYSeriesCollection dataset;
	public HeadlessChart() {
		super();
	}

	public static String xmlfilename="";
	Simulation sim;

	public void readDataset() {
		if ( xmlfilename != "" ) {
		ResultReader in = new ResultReader(
		// "C://Users//Administrator//Desktop//GAMA//eclipse//samples//predatorPrey//simulation-outputs.xml");
			xmlfilename);
		sim = in.parseXmlFile();
		}

	}

	private void createDataset() {
		lstvarname_flag = new ArrayList<String>();
		lsttimestep = new ArrayList<Integer>();
		lstchkbox=new  ArrayList<Button>();
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
				b1.setText("Show "+varname);
				b1.setSelection(true);
				b1.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						showChart();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
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

		dataset  = new XYSeriesCollection();
		for ( int i = 0; i < series.size(); i++ ) {
			if(lstchkbox.get(i).getSelection())
			{
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