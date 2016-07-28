package ummisco.gama.opengl.vaoGenerator;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import msi.gaml.operators.fastmaths.FastMath;
import ummisco.gama.opengl.camera.ICamera;

/*
 * This class creates the 3 matrices for the shader :
 *   - The perspective matrix
 *   - The view matrix
 *   - The transformation matrix
 */

public class TransformationMatrix {
	
	public static Matrix4f createProjectionMatrix(final boolean ortho,final int height,final int width,final double maxDim,final double fov ) {
		
		Matrix4f projectionMatrix = new Matrix4f();
		
		final double aspect = (double) width / (double) (height == 0 ? 1 : height);
	
		if (!ortho) {
			final double zNear = maxDim / 1000;
			final double zFar = maxDim*10;
			final double frustum_length = zFar - zNear;
			double fW, fH;
			if (aspect > 1.0) {
				fH = FastMath.tan(fov / 360 * Math.PI) * zNear;
				fW = fH * aspect;
			} else {
				fW = FastMath.tan(fov / 360 * Math.PI) * zNear;
				fH = fW / aspect;
			}
			
			projectionMatrix = new Matrix4f();
			
			projectionMatrix.m00 = (float) (zNear / fW);
			projectionMatrix.m11 = (float) (zNear / fH);
			projectionMatrix.m22 = (float) -((zFar + zNear) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = (float) -((2 * zNear * zFar) / frustum_length);
			projectionMatrix.m33 = 0;
		} else {
			// TODO
		}

		return projectionMatrix;
	}
	
	public static Matrix4f createViewMatrix(ICamera camera) {
		// see http://in2gpu.com/2015/05/17/view-matrix/
		Matrix4f viewMatrix = new Matrix4f();
		
		double[] fVect = new double[3]; // forward vector : direction vector of the camera.
		double[] sVect = new double[3]; // orthogonal vector : "right" or "sideways" vector.
		double[] vVect = new double[3]; // cross product between f and s.
		double[] pVect = new double[3]; // camera position.
		
		double sum = Math.pow(camera.getTarget().x - camera.getPosition().x,2)
				+ Math.pow(camera.getTarget().y - camera.getPosition().y,2)
				+ Math.pow(camera.getTarget().z - camera.getPosition().z,2);
		fVect[0] = -(camera.getTarget().x - camera.getPosition().x) / Math.sqrt(sum);
		fVect[1] = (camera.getTarget().y - camera.getPosition().y) / Math.sqrt(sum);
		fVect[2] = -(camera.getTarget().z - camera.getPosition().z) / Math.sqrt(sum);
		
		double[] crossProduct = GeomMathUtils.CrossProduct(fVect,new double[]{camera.getOrientation().x,
				-camera.getOrientation().y,camera.getOrientation().z});
		sVect = GeomMathUtils.Normalize(crossProduct);
		
		vVect = GeomMathUtils.CrossProduct(sVect,fVect);
		
		pVect = new double[]{camera.getPosition().x,
				-camera.getPosition().y,camera.getPosition().z};
		
		viewMatrix.m00 = (float) sVect[0];
		viewMatrix.m01 = (float) sVect[1];
		viewMatrix.m02 = (float) sVect[2];
		viewMatrix.m03 = (float) -GeomMathUtils.ScalarProduct(sVect,pVect);
		viewMatrix.m10 = (float) vVect[0];
		viewMatrix.m11 = (float) vVect[1];
		viewMatrix.m12 = (float) vVect[2];
		viewMatrix.m13 = (float) -GeomMathUtils.ScalarProduct(vVect,pVect);
		viewMatrix.m20 = (float) fVect[0];
		viewMatrix.m21 = (float) fVect[1];
		viewMatrix.m22 = (float) fVect[2];
		viewMatrix.m23 = (float) -GeomMathUtils.ScalarProduct(fVect,pVect);
		viewMatrix.m30 = (float) 0;
		viewMatrix.m31 = (float) 0;
		viewMatrix.m32 = (float) 0;
		viewMatrix.m33 = (float) 1;
		
		viewMatrix.transpose();
		
		return viewMatrix;
	}
	
	static public Matrix4f createTransformationMatrix(Vector3f positions, float[] quat, float scale, float env_width, float env_height) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		
		// rotation
//		matrix = GeomMathUtils.rotateMatrixAroundLocal(quat, -env_width / 2, env_height / 2, 0, matrix); // FIXME : does not work...
		
		// translation
		matrix = GeomMathUtils.translateMatrix(positions.x, positions.y, positions.z, matrix);
		
		// scale
		matrix.setScale(scale);

		return matrix;
	}
}
