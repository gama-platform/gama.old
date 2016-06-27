package ummisco.gama.opengl.vaoGenerator;

public class UsualShapeFactory {

	public static float[] getRectangleOrder() {
		//	   1-----2
		//      \     \
		//       0-----3
		return new float[]{0,2,1,0,3,2};
	}
	
	public static float[] getCubeOrder() {
		//     5-----6
		//     |\    |\
		//     | 4-----7
		//     1-|---2 |
		//      \|    \|
		//       0-----3
		return new float[]{
				0,1,2,0,2,3, 	// buttom face
				0,3,7,0,7,4,
				1,0,4,1,4,5,
				2,1,5,2,5,6,
				3,2,6,3,6,7,
				4,7,6,4,6,5};	// top face
	}
	
	public static float[] getPyramidOrder() {
		//        4
		//      / | \
		//     1--|--2 
		//      \|    \
		//       0-----3
		return new float[]{
				0,1,2,0,2,3, 	// buttom face
				3,4,0,
				0,4,1,
				1,4,2,
				2,4,3};
	}
	
}
