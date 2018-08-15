package ummisco.gama.opengl.renderer;

import java.awt.Point;

import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.swt.GLCanvas;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.helpers.CameraHelper;
import ummisco.gama.opengl.renderer.helpers.KeystoneHelper;
import ummisco.gama.opengl.renderer.helpers.LightHelper;
import ummisco.gama.opengl.renderer.helpers.PickingHelper;
import ummisco.gama.opengl.renderer.helpers.SceneHelper;
import ummisco.gama.opengl.view.SWTOpenGLDisplaySurface;

public interface IOpenGLRenderer extends GLEventListener, IGraphics.ThreeD {

	void setCanvas(GLCanvas canvas);

	GLCanvas getCanvas();

	void initScene();

	double getWidth();

	double getHeight();

	GamaPoint getRealWorldPointFromWindowPoint(final Point mouse);

	@Override
	public SWTOpenGLDisplaySurface getSurface();

	CameraHelper getCameraHelper();

	KeystoneHelper getKeystoneHelper();

	PickingHelper getPickingHelper();

	OpenGL getOpenGLHelper();

	LightHelper getLightHelper();

	SceneHelper getSceneHelper();

	default LayeredDisplayData getData() {
		return getSurface().getData();
	}

	int getLayerWidth();

	int getLayerHeight();

	default boolean useShader() {
		return false;
	}

}