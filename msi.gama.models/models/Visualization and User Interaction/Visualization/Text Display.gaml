/***
* Name: TextDisplay
* Author: A. Drogoul
* Description: A simple model to show the usage of the 'anchor' facet to draw strings in display. Also demonstrates some of the attributes with which text can be drawn (depth, border, precision, wireframe...)
* Tags: Display, Draw, String
***/
model TextDisplay

global {}

experiment Strings {
	list<font> fonts <- [font("Helvetica", 36, #plain),font("Times", 24, #plain) ,font("Courier", 30, #plain), font("Arial", 18, #bold),font("Times", 24, #bold+#italic) ,font("Courier", 18, #plain)];
	map<string, point> anchors <- ["center"::#center, "top_left"::#top_left, "left_center"::#left_center, "bottom_left"::#bottom_left, "bottom_center"::#bottom_center, "bottom_right"::#bottom_right, "right_center"::#right_center, "top_right"::#top_right, "top_center"::#top_center];
	font currentFont <- one_of(fonts) update: one_of(fonts);
	rgb currentColor <- rnd_color(255) update: rnd_color(255);
	float currentDepth <- rnd(8) - 4.0;

	image_file g <- image_file("3D Visualization/images/building_texture/texture3.jpg");


	output {
		layout #split;
		display "With antialias" type: opengl antialias: true synchronized: true {
			graphics Strings {
				draw world.shape wireframe: true color: #black;
				int y <- 5;
				float precision <- 0.01;
				loop p over: anchors.pairs {
					draw circle(0.5) at: {50, y} color: #red;
					draw p.key + " precision " + precision at: {50, y} anchor: p.value color: currentColor font: currentFont depth: currentDepth precision: precision ;
					y <- y + 5;
					precision <- precision * 2;
				}

				draw circle(0.5) at: {50, y} color: #green;
				draw "custom {0.6, 0.1} wireframe" at: {50, y} anchor: {0.6, 0.1} border: currentColor font:  currentFont  wireframe: true width: 1;
				draw circle(0.5) at: {50, y + 5} color: #red;
				draw "custom {0.2, 0.2} with border" at: {50, y + 5} anchor: {0.2, 0.2} color: currentColor font: currentFont  depth: 3 border: currentColor.darker.darker width: 3;
				draw circle(0.5) at: {50, y + 10} color: #red;
				draw "custom {0.8, 0.8} with texture" at: {50, y + 10} anchor: {0.8, 0.8}  font:  currentFont  depth: 8 texture: g ;
			}

		}
		
		display "Without antialias" parent: "With antialias" antialias: false {}

//		display "Strings Java2D" type: java2D {
//			graphics Strings {
//				draw world.shape wireframe: true color: #black;
//				int y <- 5;
//				loop p over: anchors.pairs {
//					draw circle(0.5) at: {50, y} color: #red;
//					draw p.key at: {50, y} anchor: p.value color: #black font: my_font;
//					y <- y + 5;
//				}
//
//				draw circle(0.5) at: {50, y} color: #red;
//				draw "custom {0.6, 0.1}" at: {50, y} anchor: {0.6, 0.1} color: #black font: my_font;
//				draw circle(0.5) at: {50, y + 5} color: #red;
//				draw "custom {0.2, 0.2}" at: {50, y + 5} anchor: {0.2, 0.2} color: #black font: my_font;
//				draw circle(0.5) at: {50, y + 10} color: #red;
//				draw "custom {0.8, 0.8}" at: {50, y + 10} anchor: {0.8, 0.8} color: #black font: my_font;
//			}
//
//		}

	}

}



