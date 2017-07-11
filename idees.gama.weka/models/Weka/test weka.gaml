/**
 *  testweka
 *  Author: Patrick Taillandier
 *  Description: shows how to use the Weka operators
 */

model testweka

global {
	string clusterer_type <- "k_means";
	list<list<bug>> groups <- [];
	init {
		create bug number: 100;
		switch clusterer_type { 
			match "x_means" {
				groups <- list<list<bug>>(clustering_xmeans(bug, ["age" , "weight", "size"], map(["distance_f"::"manhattan", "max_num_clusters"::10, "min_num_clusters"::2])));
			}
			match "k_means" {
				groups <- list<list<bug>>(clustering_simple_kmeans(bug, ["age" , "weight", "size"], map(["distance_f"::"manhattan", "num_clusters"::4])));
			}
			match "em" {
				groups <- list<list<bug>>(clustering_em(bug, ["age" , "weight", "size"], map(["max_iterations"::10])));
			}
			match "db_scan" {
				groups <- list<list<bug>>(clustering_DBScan(bug, ["age" , "weight", "size"], map([])));
			}
			match "cob_web" {
				groups <- list<list<bug>>(clustering_cobweb(bug, ["age" , "weight", "size"], map([])));
			}
			match "farthest_first" {
				groups <- list<list<bug>>(clustering_farthestFirst(bug, ["age" , "weight", "size"], map(["num_clusters"::4])));
			}
		} 
		
		loop gp over: groups {
			rgb col <- rnd_color(255);
			ask gp {
				color <- col;
			}
		}
	}
}

species bug {
	rgb color;
	int age <- int(gauss(30, 20)) min: 1 max: 100;
	float weight <- gauss(65, 50) min: 20.0 max: 200.0;
	float size <- gauss(175, 10) min: 150.0 max: 230.0;
	
	aspect age {
		draw circle(3*(1 + age - 1) / 99) color: color;
	}
	aspect weight {
		draw circle(2 * (1 + weight - 20.0) / 180.0) color: color;
	}
	aspect size {
		draw circle(2 * (1 + size - 150) / 80.0) color: color;
	}
	
}
experiment testweka type: gui {
	parameter "clusterer type" var: clusterer_type among: ["x_means", "k_means", "em", "db_scan","cob_web", "farthest_first"];
	output {
		display map_age {
			species bug aspect: age;
		}
		display map_weight {
			species bug aspect: weight;
		}
		display map_size {
			species bug aspect: size;
		}
		display charts {
			chart 'Cluster distribution' type: pie background: rgb('lightGray') size: {0.9, 0.4} position: {0.05, 0.05} {
				int cnt <- 0;
				loop gp over: groups {
					data string(cnt) value: length(gp) color: one_of(gp).color;
					cnt<-cnt+1;
				}
			}
		}
	}
}
