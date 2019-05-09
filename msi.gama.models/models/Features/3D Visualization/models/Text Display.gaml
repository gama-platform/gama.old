/***
* Name: TextDisplay
* Author: A. Drogoul
* Description: A simple model to show the usage of the 'anchor' facet to draw strings in display.
* Tags: Display, Draw, String
***/
model TextDisplay

global {
}

experiment Strings {
	font my_font <- font("Helvetica", 24, #bold);
	map<string, point>
	anchors <- ["center"::#center, "top_left"::#top_left, "left_center"::#left_center, "bottom_left"::#bottom_left, "bottom_center"::#bottom_center, "bottom_right"::#bottom_right, "right_center"::#right_center, "top_right"::#top_right, "top_center"::#top_center];

	output {
		layout #split;
		display "Strings OpenGL" type: opengl {
			graphics Strings {
				draw world.shape empty: true color: #black;
				int y <- 5;
				loop p over: anchors.pairs {
					draw circle(0.5) at: {50, y} color: #red;
					draw p.key at: {50, y} anchor: p.value color: #black font: my_font;
					y <- y + 5;
				}

				draw circle(0.5) at: {50, y} color: #red;
				draw "custom {0.6, 0.1}" at: {50, y} anchor: {0.6, 0.1} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 5} color: #red;
				draw "custom {0.2, 0.2}" at: {50, y + 5} anchor: {0.2, 0.2} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 10} color: #red;
				draw "custom {0.8, 0.8}" at: {50, y + 10} anchor: {0.8, 0.8} color: #black font: my_font;
			}

		}

		display "Strings Java2D" type: java2D {
			graphics Strings {
				draw world.shape empty: true color: #black;
				int y <- 5;
				loop p over: anchors.pairs {
					draw circle(0.5) at: {50, y} color: #red;
					draw p.key at: {50, y} anchor: p.value color: #black font: my_font;
					y <- y + 5;
				}

				draw circle(0.5) at: {50, y} color: #red;
				draw "custom {0.6, 0.1}" at: {50, y} anchor: {0.6, 0.1} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 5} color: #red;
				draw "custom {0.2, 0.2}" at: {50, y + 5} anchor: {0.2, 0.2} color: #black font: my_font;
				draw circle(0.5) at: {50, y + 10} color: #red;
				draw "custom {0.8, 0.8}" at: {50, y + 10} anchor: {0.8, 0.8} color: #black font: my_font;
			}

		}

	}

}



