/*********************************************************************************************
 *
 * 'UtilTriangulation.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.vaoGenerator;

import ummisco.gama.opengl.utils.Utils;

/*
 * This class is using the ear cutting algorithm to compute the triangulation.
 */

public class UtilTriangulation {

	private float[] coords;
	
	public UtilTriangulation(float[] coordinates) {
		coords = coordinates;
	}
	
	public int[] ear_cutting_triangulatation(final int[] face) {
		float[] polygonCoords = new float[face.length*3];
		for (int i = 0 ; i < face.length ; i++) {
			polygonCoords[3*i] = coords[face[i]*3];
			polygonCoords[3*i+1] = coords[face[i]*3+1];
			polygonCoords[3*i+2] = coords[face[i]*3+2];
		}
		boolean isClockwise = Utils.isClockwise(polygonCoords);
		int[] result = new int[(face.length - 2) * 3];
		int[] tempPolygon = face;
		int position_in_result = 0;
		while (tempPolygon.length > 3){
			// the polygon has at least 2 ears.
			for (int i = 0; i < tempPolygon.length ; i++) {
				int vertex_i = tempPolygon[i];
				int vertex_i_minus_1 = (i == 0) ? tempPolygon[tempPolygon.length-1] : tempPolygon[i-1];
				int vertex_i_plus_1 = (i == tempPolygon.length-1) ? tempPolygon[0] : tempPolygon[i+1];
				if (is_principal_vertex(vertex_i,new int[]{vertex_i_minus_1,vertex_i_plus_1},tempPolygon)) {
					if (is_ear(new int[]{vertex_i_minus_1,vertex_i,vertex_i_plus_1}	,tempPolygon,isClockwise)) {
						// add the ear to the triangulate list,
						result[position_in_result*3] = vertex_i_minus_1;
						result[position_in_result*3+1] = vertex_i;
						result[position_in_result*3+2] = vertex_i_plus_1;
						// .. and remove the ear from the polygon.
						int[] newPolygon = new int[tempPolygon.length-1];
						int pointer = 0;
						for (int j = 0 ; j < tempPolygon.length ; j++) {
							if (j != i) {
								newPolygon[pointer] = tempPolygon[j];
								pointer++;
							}
						}
						tempPolygon = newPolygon;
						position_in_result++;
						break;
					}
				}
				if (i == tempPolygon.length-1)
					// the polygon is corrupted : it will not be built.
					return null;
			}
		}
		// add the last polygon (it is automatically a ear)
		result[result.length-3] = tempPolygon[0];
		result[result.length-2] = tempPolygon[1];
		result[result.length-1] = tempPolygon[2];
		return result;
	}
	
	private boolean is_principal_vertex(int vertex_i, int[] line, int[] tempPolygon) {
		// returns true if the vertex "vertex_i" from the list "tempPolygon" is a principal vertex, ..
		// .. which means that the diagonal ["vertex_i-1" "vertex_i+1"] intersects the boundary of the polygon ..
		// .. tempPolygon only at "vertex_i-1" and "vertex_i+1".
		float[] firstLine = new float[]{
				coords[line[0]*3], // x value for first point
				coords[line[0]*3+1], // y value for first point
				coords[line[1]*3], // x value for second point
				coords[line[1]*3+1], // y value for second point
		};
		for (int i = 0 ; i < tempPolygon.length ; i++) {
			// eliminate all the 3 vertices that compose the supposing ear (already handeled by the firstLine)
			if (tempPolygon[i] != line[0] && tempPolygon[i] != vertex_i && tempPolygon[i] != line[1]) {
				// we build the second line as a diagonal between tempPolygon[i-1] and tempPolygon[i+1]
				int v_begin = tempPolygon[i];
				int v_end = (i == tempPolygon.length-1) ? tempPolygon[0] : tempPolygon[i+1];
				if (v_begin != line[1] && v_end != line[0]) {
					float[] secondLine = new float[]{
						coords[v_begin*3], // x value for first point
						coords[v_begin*3+1], // y value for first point
						coords[v_end*3], // x value for second point
						coords[v_end*3+1], // y value for second point
					};
					if (intersects_segment(firstLine,secondLine)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean intersects_segment(float[] coords_first_seg, float[] coords_second_seg) {
		float p0_x = coords_first_seg[0];
		float p0_y = coords_first_seg[1];
		float p1_x = coords_first_seg[2];
		float p1_y = coords_first_seg[3]; 
		float p2_x = coords_second_seg[0];
		float p2_y = coords_second_seg[1];
		float p3_x = coords_second_seg[2];
		float p3_y = coords_second_seg[3];
	    float s1_x, s1_y, s2_x, s2_y;
	    s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
	    s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

	    float s, t;
	    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
	    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

	    if (s >= 0 && s <= 1 && t >= 0 && t <= 1)
	    {
	        // Collision detected
	        return true;
	    }

	    return false; // No collision
	}
	
	private boolean is_inside_triangle(int pointId, int[] triangleIds) {
		double p0x = coords[triangleIds[0]*3];
		double p0y = coords[triangleIds[0]*3+1];
		double p1x = coords[triangleIds[1]*3];
		double p1y = coords[triangleIds[1]*3+1];
		double p2x = coords[triangleIds[2]*3];
		double p2y = coords[triangleIds[2]*3+1];
		double px = coords[pointId*3];
		double py = coords[pointId*3+1];
		double Area = 0.5 *(-p1y*p2x + p0y*(-p1x + p2x) + p0x*(p1y - p2y) + p1x*p2y);
		double s = 1/(2*Area)*(p0y*p2x - p0x*p2y + (p2y - p0y)*px + (p0x - p2x)*py);
		double t = 1/(2*Area)*(p0x*p1y - p0y*p1x + (p0y - p1y)*px + (p1x - p0x)*py);
		return (s > 0 && t > 0 && (1-s-t)>0);
	}
	
	private boolean is_ear(int[] ear, int[] tempPolygon, boolean generalPolygonIsClockwise) {
		// returns true if the triangle (ear[0],ear[1],ear[2]) is an ear of the polygon tempPolygon, ..
		// .. which means that the segment [ear[0],ear[2]] is inside the polygon tempPolygon.
		
		// we check if there are some vertices inside the ear
		for (int i = 0 ; i < tempPolygon.length ; i++) {
			if (tempPolygon[i] != ear[0] && tempPolygon[i] != ear[1] && tempPolygon[i] != ear[2]) {
				if (is_inside_triangle(tempPolygon[i],ear)) {
					return false;
				}
			}
		}
		
		// we check if the ear is inside the polygon (by checking if both are clockwise or anti-clockwise)
		float[] earCoords = new float[]{coords[ear[0]*3],coords[ear[0]*3+1],coords[ear[0]*3+2],
				coords[ear[1]*3],coords[ear[1]*3+1],coords[ear[1]*3+2],
				coords[ear[2]*3],coords[ear[2]*3+1],coords[ear[2]*3+2],};
		
		return (Utils.isClockwise(earCoords) == generalPolygonIsClockwise);
	}
	
}
