/**
 *  newtest
 *  Author: HPhi
 *  Description: 
 */

model newtest

global
{
}

experiment my_experiment type: gui {
	output {
		display "data_pie_chart" type:java2D {
			chart "Nice Ring Pie Chart" type:pie style:ring
			 	background: #darkblue
			 	color: #lightgreen 
			 	axes: #yellow
			 	title_font: 'Serif'
			 	title_font_size: 32.0
			 	title_font_style: 'italic'
			 	tick_font: 'Monospaced'
			 	tick_font_size: 14
			 	tick_font_style: 'bold' 
			 	label_font: 'Arial'
			 	label_font_size: 32
			 	label_font_style: 'bold' 
			 	x_label:'Nice Xlabel'
			 	y_label:'Nice Ylabel'
			 	
			{
				data "BCC" value:100+cos(100*cycle)*cycle*cycle
				color:#black;
				data "ABC" value:cycle*cycle 
					color:#blue;
				data "BCD" value:cycle+1;
			}
		} 
 		
		display "data_3Dpie_chart" type:java2D {
			chart "data_3Dpie_chart" type:pie style:"3d" {
				data "BCC" value:2*cycle
				color:#black;
				data "ABC" value:cycle*cycle 
					color:#blue;
				data "BCD" value:cycle+1;
			}
		} 
 		
		display "datalist_pie_chart" type:java2D {
			chart "datalist_pie_chart" type:pie style:exploded{
				datalist legend:["A","B","C"] 
					value:[[cycle,cycle+1,2],[cycle/2,cycle*2,1],[cycle+2,cycle-2,cycle]] 
					x_err_values:[3,2,10]
					y_err_values:[3,cycle,2*cycle]
//					categoriesnames:["C1","C2","C3"]
					color:[#black,#blue,#red];
			}
		}
/*
		display "datalist_xy_non_cumulative_chart" type:java2D {
			chart "datalist_xy_non_cumulative_chart" type:xy {
				datalist legend:["A","B","C"] 
					value:[[10,10],[12,10],[20+cycle,10]]
					accumulate_values: false 
					x_err_values:[3,1,2]
					y_err_values:[[9,20],[5,11],[8,10+cycle/2]] // different low/high values for yerr
					marker_size: [1,cycle,2] 					// size keyword instead of size in values
//					categoriesnames:["C1","C2","C3"]
//					style:stack 
					marker_shape:marker_circle 					// same for all
					color:[#green,#blue,#red];
			}
		}
		display "data_cumulative_serie_chart" type:java2D {
			chart "data_cumulative_serie_chart" type:series {
				data "A" value:[1,2];
				data "ABC" value:[cycle,cycle] 
					marker_shape:marker_circle
					x_err_values:cycle
					y_err_values:cycle
					color:#black;
				
				data "BCD" value:[cycle+1,1] ;
				data "BCC" value:[2,cycle];
			}
		}
		display "my_data_cumulative_xy" type:java2D {
			chart "my_data_cumulative_xy" type:xy {
				data "123" value:[1,2,3] marker_shape:marker_down_triangle;
				data "ABC" value:[cycle+1,cycle*2,cycle] 
					marker_shape:marker_circle
					fill:false
					line_visible:false
					color:#black
					x_err_values:cycle
					y_err_values:cycle;
				
			}
		}
*/
	}
}