/**
* Name: Bar Examples
* Author: Philippe Caillou
* Description: A demonstration of charts composed of bars
* Tags: gui, chart
*/

model bars

global
{
}

experiment "Multiple displays" type: gui {
	output synchronized: true {
		layout #split parameters: false navigator: false editors: false consoles: false toolbars: false tray: false tabs: true;	
		display "nice_bar_chart"  type: 2d {
			chart "Nice Cumulative Bar Chart" type:histogram 
			 	background: #darkblue
			 	color: #lightgreen 
			 	axes: #lightgreen
			 	title_font: font('Serif', 32.0, #italic)
			 	tick_font: font('Monospaced', 14, #bold) 
			 	label_font: font('Arial', 18, #bold) 
			 	legend_font: font('SanSerif', 14, #bold) 
			 	y_range:[-20,40]
			 	y_tick_unit:10
			 	x_label:'Nice Xlabel'
			 	y_label:'Nice Ylabel'
			 {
				data "BCC" value:10*cos(100*cycle)
					accumulate_values: true						
					style:stack
					color:#yellow;
				data "ABC" value:10*sin(100*cycle)
					accumulate_values: true						
					style: stack
					color:#blue;
				data "BCD" value:(cycle mod 10)
					accumulate_values: true						
					style: stack  
					marker_shape:marker_circle ;
			}
		} 
		display "data_cumulative_bar_chart" type:2d {
			chart "data_cumulative_bar_chart" type:histogram 
			style:stack
			x_serie_labels:("cycle"+cycle)
			x_range:5
			{
				data "BCC" value:cos(100*cycle)*cycle*cycle
				accumulate_values: true						
				color:#yellow;
				data "ABC" value:cycle*cycle 
				accumulate_values: true						
					color:#blue;
				data "BCD" value:cycle+1
				accumulate_values: true						
				marker_shape:marker_circle ;
			}
		} 
 		
		display "data_non_cumulative_bar_chart" type: 2d {
			chart "data_non_cumulative_bar_chart" type:histogram 
			x_serie_labels: ["categ1","categ2"]
			style:"3d"
			series_label_position: xaxis
			{
				data "BCC" value:cos(100*cycle)*cycle*cycle
//				style:stack
				color:#yellow;
				data "ABC" value:cycle*cycle 
//				style: stack
					color:#blue;
				data "BCD" value:[cycle+1,cycle]
//				style: stack  
				marker_shape:marker_circle ;
			}
		} 
 		
		display "datalist_bar_cchart" type:2d {
			chart "datalist_bar" type:histogram 
			series_label_position: onchart
			{
				datalist legend:["cycle","cosinus normalized","offsetted cosinus normalized"] 
					style: bar
					value:[cycle,(sin(100*cycle) +  1) * cycle/2,(sin(100*(cycle+30)) + 1) * cycle/2] 
					color:[#green,#black,#purple];
			}
		}

		display "onvalue_cumulative_bar_chart" type:2d {
			chart "onvalue_cumulative_bar_chart" type:histogram 
			series_label_position: yaxis
			x_label: "my_time_label"
			{
				data "unique data value" 
					value:cos(cycle*10) 
					accumulate_values: true						
					color: #red;
			}
		}
		display "data_cumulative_style_chart" type:2d {
			chart "Style Cumulative chart" type:histogram style:stack
			 	{ 
				data "Step" value:cos(100*cycle+40)
					accumulate_values: true						
					color:#blue;
				data "Bar" value:cos(100*cycle+60)
					accumulate_values: true						
					color:#green;
				data "Line" value:cos(100*cycle)
					accumulate_values: true						
					color:#orange;
				data "Dot" value:cos(100*cycle)*0.3
					accumulate_values: true						
					color:#red;
			}
		} 


	}
}