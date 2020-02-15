/**
* Name: Pie Examples
* Author: Philippe Caillou
* Description: A demonstration of charts composed of pies
* Tags: gui, chart
*/
model pies


global
{
}

experiment "Different Pies" type: gui
{
	output
	{
		layout #split parameters: false navigator: false editors: false consoles: false ;	
		
		display "data_pie_chart" type: java2D synchronized: true
		{
			chart "Nice Ring Pie Chart" type: pie style: ring background: # darkblue color: # lightgreen axes: # yellow title_font: font( 'Serif', 32.0, #italic)
			tick_font: font('Monospaced' , 14, #bold) label_font: font('Arial', 32 #bold) x_label: 'Nice Xlabel' y_label:
			'Nice Ylabel'
			{
				data "BCC" value: 100 + cos(100 * cycle) * cycle * cycle color: # black;
				data "ABC" value: cycle * cycle color: # blue;
				data "BCD" value: cycle + 1;
			}

		}

		display "data_3Dpie_chart" type: java2D
		{
			chart "data_3Dpie_chart" type: pie style: "3d"
			{
				data "BCC" value: 2 * cycle color: # black;
				data "ABC" value: cycle * cycle color: # blue;
				data "BCD" value: cycle + 1;
			}

		}

		display "datalist_pie_chart" type: java2D
		{
			chart "datalist_pie_chart" type: pie style: exploded
			{
				datalist legend: ["A", "B", "C"] value: [[cycle, cycle + 1, 2], [cycle / 2, cycle * 2, 1], [cycle + 2, cycle - 2, cycle]] x_err_values: [3, 2, 10] y_err_values:
				[3, cycle, 2 * cycle]
				//					categoriesnames:["C1","C2","C3"]
				color: [# black, # blue, # red];
			}

		}

	}

}