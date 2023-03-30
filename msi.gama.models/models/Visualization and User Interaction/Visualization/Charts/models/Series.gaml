/**
* Name: Series Examples
* Author: Philippe Caillou
* Description: A demonstration of charts composed of series
* Tags: gui, chart
*/
model series


global
{
}

experiment "Different series" type: gui
{
	
	

	
	output synchronized: true
	{
		layout #split toolbars: false;
		display "data_cumulative_serie_spline_chart" type: 2d
		{
			chart "Nice cumulative series chart" type: series background: # darkblue color: # lightgreen axes: # lightgreen title_font: font( 'Serif' , 32.0, #italic)
			 tick_font: font('Monospaced' , 14 #bold) label_font: font('Serif', 18 #plain) legend_font: font('SanSerif', 18 #bold) x_range: 50 x_tick_unit: 5 x_serie_labels: ("T+" + cycle) x_label: 'Nice Xlabel' y_label: 'Nice Ylabel'
			{
				data "Spline" value: cos(100 * cycle) * cycle * cycle color: # orange marker_shape: marker_empty style: spline;
				data "Step" value: cycle * cycle style: step color: # lightgrey;
				data "Classic" value: [cycle + 1, cycle] marker_shape: marker_circle color: # yellow;
			}

		}

		display "style_cumulative_style_chart" type: 2d
		{
			chart "Style Cumulative chart" type: series
			{
				data "Spline" value: cos(100 * cycle) color: # orange style: spline;
				data "area" value: cos(100 * cycle) * 0.3 color: # red style: "area";
				data "dot" value: cos(100 * cycle + 60) color: # green style: dot;
			}

		}

		display "style_cumulative_style_chart_without_axes" type: 2d
		{
			chart "Style Cumulative chart Without axes" type: series 
				y_tick_values_visible: false y_tick_line_visible: false x_tick_values_visible: false x_tick_line_visible: false
			{
				data "Spline" value: cos(100 * cycle) color: # orange style: spline;
				data "area" value: cos(100 * cycle) * 0.3 color: # red style: "area";
				data "dot" value: cos(100 * cycle + 60) color: # green style: dot;
			}

		}

		display "datalist_xy_chart" type: 2d
		{
			chart "datalist_xy_cumulative_chart" type: xy
			{
				datalist legend: ["A", "B", "C"] value:
				[[cycle * cos(cycle * 100), cycle * sin(cycle * 100), 2], [cycle / 2 * sin(cycle * 100), cycle * 2 * cos(cycle * 100), 1], [cycle + 2, cycle - 2, cos(cycle * 100)]]
				x_err_values: [3, 2, 10] y_err_values: [3, cos(cycle * 100), 2 * sin(cycle * 100)] marker_shape: marker_circle // same for all
				color: [# green, # blue, # red];
			}

		}

		display "datalist_xy_line_chart" type: 2d
		{
			chart "datalist_xy_cumulative_chart" type: xy
			{
				datalist legend: ["A", "B"] value: [[cycle * cos(cycle * 100), cycle * sin(cycle * 100), 2], [cycle / 2 * sin(cycle * 100), cycle * 2 * cos(cycle * 100), 1]] marker_shape:
				marker_circle // same for all
				color: [# green, # blue] style: line;
			}

		}

		display "datalist_xy_non_cumulative_chart" type: 2d
		{
			chart "datalist_xy_non_cumulative_chart" type: xy
			{
				datalist legend: ["A", "B", "C"] value: [[10, 10], [12, 10], [20 + cycle, 10]] accumulate_values: false x_err_values: [3, 1, 2] y_err_values:
				[[9, 20], [5, 11], [8, 10 + cycle / 2]] // different low/high values for yerr
				marker_size: [1, cycle, 2] // size keyword instead of size in values
				marker_shape: marker_circle // same for all
				color: [# green, # blue, # red];
			}

		}

		display "data_cumulative_serie_chart" type: 2d
		{
			chart "data_cumulative_serie_chart" type: series x_serie_labels: (cycle * cycle)
			{
				data "A" value: [1, 2];
				data "ABC" value: [cycle, cycle] marker_shape: marker_circle x_err_values: 2 * cos(cycle * 100) y_err_values: 2 * sin(cycle * 100) color: # black;
				data "BCD" value: [cycle / 2 + cos(cycle * 100), 1] style: spline;
				data "BCC" value: [2, cycle];
			}

		}

		display "my_data_cumulative_xy" type: 2d
		{
			chart "my_data_cumulative_xy" type: xy
			{
				data "123" value: [1 + cycle, 2, 3] marker_shape: marker_down_triangle;
				data "ABC" value: [cycle + 1, cycle * 2, cos(cycle)] marker_shape: marker_circle fill: false line_visible: false color: # black x_err_values: ln(cycle) y_err_values:
				cos(cycle * 100) * 3;
			}

		}
		
		display "double axes"  type: 2d {
			chart "double Y axes" y_label: "axis 1" y2_label: "axis 2" y_range: {-1,1} y2_range: {0,1000} y2_log_scale: true {
				data "cos" value: cos(100 * cycle) color: #red;
				data "cycle" value: cycle color: #green use_second_y_axis: true;
			}
		}

	}

}