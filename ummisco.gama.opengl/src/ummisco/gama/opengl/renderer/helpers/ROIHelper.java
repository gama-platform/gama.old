package ummisco.gama.opengl.renderer.helpers;

import java.awt.Point;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

public class ROIHelper extends AbstractRendererHelper {

	protected Envelope3D ROIEnvelope = null;

	public ROIHelper(final IOpenGLRenderer renderer) {
		super(renderer);
	}

	@Override
	public void initialize() {}

	public void cancelROI() {
		if (getRenderer().getCameraHelper().isROISticky()) { return; }
		ROIEnvelope = null;
	}

	public Envelope3D getROIEnvelope() {
		return ROIEnvelope;
	}

	public void defineROI(final Point start, final Point end) {
		final GamaPoint startInWorld = getRenderer().getRealWorldPointFromWindowPoint(start);
		final GamaPoint endInWorld = getRenderer().getRealWorldPointFromWindowPoint(end);
		ROIEnvelope =
				new Envelope3D(startInWorld.x, endInWorld.x, startInWorld.y, endInWorld.y, 0, getMaxEnvDim() / 20d);
	}

	public boolean mouseInROI(final Point mousePosition) {
		final Envelope3D env = getROIEnvelope();
		if (env == null) { return false; }
		final GamaPoint p = getRenderer().getRealWorldPointFromWindowPoint(mousePosition);
		return env.contains(p);
	}

	public boolean isActive() {
		return ROIEnvelope != null;
	}

}
