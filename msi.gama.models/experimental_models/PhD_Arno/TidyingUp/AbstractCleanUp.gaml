/**
 *  model1
 *  Author: Arno
 *  Description: 
 */
model abstractCleanUp

import "./ReferenceModel/ColorShapeModel.gaml"
global {

	string layoutType <- "lineLayout" among:["fillup","lineLayout","unifromlayout"];

	init {
		
	}
	reflex init when: time =1{
		
		if(layoutType = "fillup"){
		  do fillup;	
		}		
		if(layoutType = "lineLayout"){
		  do lineLayout;	
		}		
		if(layoutType = "unifromlayout"){
		  do unifromlayout;	
		}
		if(layoutType = "unifromMacrolayout"){
		  do unifromMacrolayout;	
		}
	}
	
	
	action fillup{
		int col <- 0;
		//loop times: length(class) {

			int curAbstCell <-0;
			ask abstractCells{
				if(curAbstCell *4 mod 100 =0){
					col <- col + 1;
				}
				
				location <- point({curAbstCell * 4 mod 100 ,col *8});
				curAbstCell <- curAbstCell+1;
			}
			
		//}	
	}
	
	action lineLayout{
		int i <- 0;
		loop times: length(class) {
			list<abstractCells> tmp <- abstractCells where (cells(each.target).myClass = class[i]);
			int curAbstCell <-0;
			int curLineLength <-rnd(50);
			loop pp over:tmp{
				pp.location <- point({i * 20  ,100 - curAbstCell /length(tmp) * length(tmp) });
				curAbstCell <- curAbstCell+1;
			}
			i <- i + 1;
		}	
	}
	
	
	
	
	
	action unifromlayout{
		do unifromlayoutGeneric("");
	}
	
	action unifromMacrolayout{
		do unifromlayoutGeneric("myGeom");
	}
	
	action unifromlayoutGeneric (string macroShape) {
	    int i <- 0;
		loop times: length(class) {
			list<abstractCells> tmp <- abstractCells where (cells(each.target).myClass = class[i]);
			float radius <- float(length(tmp));
			geometry  bounds_geom <-rectangle(radius/4,radius);
			if(macroShape = "myGeom"){
			   //bounds_geom <- class[i] scaled_by 10;	
			}

			
			float size <- sqrt((radius^2 * 3.14159265359) /(length(tmp)));
            list<geometry> rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
            
            loop while: (length(rectangles) < length(tmp)) {
              size <- size * 0.99;
              rectangles <- list<geometry>(to_rectangles(bounds_geom, {size,size}, false));
             }
               
			int i1 <- 0;
            ask tmp{
	            location <- (rectangles[i1]).location;
	            location <- {-world.shape.width /2 +10 + i*30 + location.x, location.y};
	            i1 <- i1+ 1;
            }
			i <- i + 1;
		}	
	}
	
	action layoutConstainInShape{
		int curGeom <- 0;
		loop times: length(class) {
			list<abstractCells> tmp <- abstractCells where (cells(each.target).myClass = class[curGeom]);
		    int curAbstCell <-0;
			//geometry the_shape <- rectangle(curGeom+1,length(tmp)/(curGeom+1));
			geometry the_shape <- circle(length(tmp)/2);	
			write "to rectangles" + length(to_rectangles(the_shape, {1,1}, false));
			write "abstractCells" + length(tmp);
			/*loop geom over: to_rectangles(the_shape, {1,1}, false) { //to_rectangles(geometry, dimension, true = overlaps, false = contains inside
               (tmp[curAbstCell]).location <- {geom.location.x + curGeom*10 - world.shape.width/2,geom.location.y};
               curAbstCell <- curAbstCell+1;      
            }*/
			curGeom <- curGeom + 1;
		}	
	}
	


}

species abstractCells mirrors: list(cells) {
	
	list<abstractCells> neigbhours update: abstractCells at_distance (size*1.1);
	abstractCells upper_cell update: neigbhours first_with (shape.location.y > each.shape.location.y and (shape.location.x = each.shape.location.x));
	abstractCells side_cell update: neigbhours first_with (shape.location.x > each.shape.location.x and (shape.location.y = each.shape.location.y));

 
    reflex upperSwap when: upper_cell != nil and cells(target).attributeToSort < cells(upper_cell.target).attributeToSort and time >1{
		point tmp1Loc <- location;
		location <- upper_cell.location;
		upper_cell.location <- tmp1Loc;
	}
	
	/*reflex sideSwap when: side_cell != nil and cells(target).attributeToSort < cells(side_cell.target).attributeToSort and time >1{
		point tmp1Loc <- location;
		location <- side_cell.location;
		side_cell.location <- tmp1Loc;
	}*/
	
	aspect abstract {
		if(cells(target).mySize = 0 ){
			draw cells(target).myGeom  color: cells(target).color border: °black  at: location;
		}
		else{
			draw cells(target).myGeom scaled_by (cells(target).mySize) color: cells(target).color border: °black  at: location;
		}
		  	
	}

}


species macroCells{

    geometry myGeom; 
	aspect default{
		draw myGeom;
	}
	
}

experiment Display type: gui {
	parameter "Layout Type" var:layoutType category:"Init";
	output {
		display AbstractView  type:opengl ambient_light:100  background:rgb(code_couleur[0]) draw_env:false{ 
		species cells;
		species abstractCells aspect: abstract position: { world.shape.width * 1.2, 0.0, 0 };
		}
	}

}

