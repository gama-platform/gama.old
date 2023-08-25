/**
* Name: Heatmap Examples
* Author: Philippe Caillou
* Description: A demonstration of charts composed of heatmaps
* Tags: gui, chart
*/
model heatmaps


global
{
	list<float> myldata <- [];
	list<list<float>> mylldata <- [[]];
	list<list<float>> mylldata2 <- [[]];
	int xsize <- 100;
	int ysize <- 100;
	init
	{
		do update_chart();
	}

	reflex do_action
	{
		do update_chart();
	}

	action update_chart
	{
		myldata <- [];
		mylldata <- [];
		loop xi from: 0 to: xsize - 1
		{
			add cos((xi + cycle * 20) * 2) to: myldata;
			add [] to: mylldata;
			loop yi from: 0 to: ysize - 1
			{
				add cos((xi + cycle * 10)) + cos((yi + cycle * 10)) to: mylldata[xi];
			}

		}

		mylldata2 <- [];
		loop xi from: 0 to: xsize - 1
		{
			add [] to: mylldata2;
			loop yi from: 0 to: ysize - 1
			{
				add sin((xi + cycle * 10)) + sin((yi)) to: mylldata2[xi];
			}

		}

	}

}

experiment "Different heatmaps" type: gui
{
	output synchronized: true
	{
		layout #split
		parameters: false 
		navigator: false 
		editors: false 
		consoles: false 
		toolbars: false 
		tray: false 
		tabs: true
		;	
		
		display "Nice Heatmap" type: 2d
		{
			chart "Nice Heatmap" type: heatmap background: # darkblue color: # lightgreen axes: # lightgreen title_font: font('Serif', 32, #italic) tick_font:
			font('Monospaced', 14, #bold) label_font: font('Serif', 18, #plain) legend_font: font('SanSerif', 18, #bold) x_label: 'Nice Xlabel' y_label: 'Nice Ylabel'
			{
				data "test" value: mylldata color: [# darkblue, # orange] accumulate_values: false;
			}
		}

		display "listOflist_heatmap" type: 2d
		{
			chart "listOflist_heatmap" type: heatmap
			{
				data "cosX" value: mylldata color: [# blue] accumulate_values: false;
				data "sinY" value: mylldata2 color: [# darkred] accumulate_values: false;
			}
		}

		display "list_heatmap" type: 2d antialias:false
		{
			chart "list_heatmap" type: heatmap 
			{
				data "test" value: myldata color: [# cyan, # red] accumulate_values: false;
			}

		}

		display "simple_heatmap2" type: 2d
		{
			chart "simple heatmap2" type: heatmap
			{
				data "test" value: [[1, 2, 3, 4, 5], [6, 7, 8, 9, 10]] color: [# cyan, # red] accumulate_values: false;
			}

		}

	}

}

experiment Heatmap type: gui
{
	output synchronized: true
	{
		display "list_heatmap" type: 2d antialias:false
		{
			chart "list_heatmap" type: heatmap
			{
				data "test" value: myldata color: [# cyan, # red] accumulate_values: false;
			}

		}

	}

}
