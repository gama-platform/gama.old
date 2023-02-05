/**
* Name: Long Series Examples
* Author: Philippe Caillou
* Description: A demonstration of charts composed of very long series
* Tags: gui, chart
*/
model long_series

global {
	int serie_length <- 1000;
	list<float> xlist <- [];
	list<float> coslist <- [];
	list<float> sinlist <- [];
	float base;

	reflex update_sinchart {
		loop i from: 0 to: serie_length {
			base <- float(serie_length * cycle + i);
			add base to: xlist;
			add cos(base / 1000) to: coslist;
			add sin(base / 1000) to: sinlist;
		}

	}

}

experiment "Long series" type: gui {
	output synchronized: true {
		display "long_series" type: 2d {
			chart "Long series values" type: series x_label: "#points to draw at each step" memorize: false {
				data "Cosinus" value: coslist color: #blue marker: false style: line;
				data "Sinus" value: sinlist color: #red marker: false style: line;
			}

		}

	}

}
