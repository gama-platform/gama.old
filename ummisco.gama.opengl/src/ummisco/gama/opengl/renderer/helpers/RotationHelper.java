package ummisco.gama.opengl.renderer.helpers;

import msi.gama.common.preferences.GamaPreferences;
import ummisco.gama.opengl.renderer.JOGLRenderer;

public class RotationHelper extends AbstractRendererHelper {

	private static boolean SHOULD_DRAW = GamaPreferences.Displays.DRAW_ROTATE_HELPER.getValue();

	static {
		GamaPreferences.Displays.DRAW_ROTATE_HELPER.onChange((v) -> SHOULD_DRAW = v);
	}

	protected boolean drawRotationHelper = false;

	public RotationHelper(final JOGLRenderer renderer) {
		super(renderer);
	}

	@Override
	public void initialize() {}

	public void startDrawRotationHelper() {
		if (!SHOULD_DRAW) { return; }
		drawRotationHelper = true;
		if (getRenderer().getSceneHelper().getSceneToRender() != null) {
			getRenderer().getSceneHelper().getSceneToRender().startDrawRotationHelper();
		}
	}

	public void stopDrawRotationHelper() {
		if (!SHOULD_DRAW) { return; }
		drawRotationHelper = false;
		if (getRenderer().getSceneHelper().getSceneToRender() != null) {
			getRenderer().getSceneHelper().getSceneToRender().stopDrawRotationHelper();
		}
	}

	public boolean isActive() {
		return drawRotationHelper;
	}

	public double sizeOfRotationElements() {
		return Math.min(getMaxEnvDim() / 4d, getRenderer().getCameraHelper().getDistance() / 6d);
	}

	public void draw() {
		getOpenGL().getGeometryDrawer().drawRotationHelper(getRenderer().getCameraHelper().getTarget(),
				getRenderer().getCameraHelper().getDistance(), sizeOfRotationElements());
	}
}
