/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.layers.AxesLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import static msi.gama.common.geometry.Rotation3D.MINUS_I;
import static msi.gama.common.geometry.Rotation3D.PLUS_J;
import static msi.gama.common.geometry.Scaling3D.of;
import static msi.gama.util.GamaColor.getNamed;
import static msi.gaml.operators.IUnits.bottom_center;
import static msi.gaml.operators.IUnits.left_center;
import static msi.gaml.operators.IUnits.top_center;
import static msi.gaml.types.GamaGeometryType.buildCone3D;
import static msi.gaml.types.GamaGeometryType.buildLineCylinder;

import java.util.List;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.StringObject;

public class AxesLayerObject extends StaticLayerObject.World {

	public final static String[] LABELS = new String[] { "X", "Y", "Z" };
	public final static GamaPoint[] ANCHORS = new GamaPoint[] { left_center, top_center, bottom_center };
	public final static AxisAngle[] ROTATIONS =
			new AxisAngle[] { new AxisAngle(PLUS_J, 90), new AxisAngle(MINUS_I, 90), null };
	public final static GamaColor[] COLORS =
			new GamaColor[] { getNamed("gamared"), getNamed("gamaorange"), getNamed("gamablue") };
	protected final static GamaPoint DEFAULT_SCALE = new GamaPoint(.15, .15, .15);
	protected final static GamaPoint ORIGIN = new GamaPoint(0, 0, 0);
	protected final static GamaFont AXES_FONT = new GamaFont("Helvetica", 0, 18);
	final GamaShape arrow;
	final GamaPoint[] dirs;
	final GamaShape[] axes = new GamaShape[3];

	public AxesLayerObject(final IOpenGLRenderer renderer) {
		super(renderer);
		// Addition to fix #2227
		scale.setLocation(DEFAULT_SCALE);
		final double max = renderer.getMaxEnvDim();
		arrow = (GamaShape) buildCone3D(max / 15, max / 6, ORIGIN);
		dirs = new GamaPoint[] { new GamaPoint(max, 0, 0), new GamaPoint(0, max, 0), new GamaPoint(0, 0, max) };
		for (int i = 0; i < 3; i++) {
			axes[i] = (GamaShape) buildLineCylinder(ORIGIN, dirs[i], max / 40);
		}
	}

	@Override
	public void setScale(final GamaPoint s) {
		if (s == null) {
			scale = DEFAULT_SCALE;
		} else {
			super.setScale(s);
		}
	}

	@Override
	public void draw(final OpenGL gl) {
		if (renderer.getOpenGLHelper().isInRotationMode()) {
			final GamaPoint pivotPoint = (GamaPoint) renderer.getCameraTarget();
			setOffset(pivotPoint.yNegated());
			final double size = renderer.getOpenGLHelper().sizeOfRotationElements();
			final double ratio = size / renderer.getMaxEnvDim();
			setScale(new GamaPoint(ratio, ratio, ratio));
		} else {
			setOffset(null);
			setScale(null);
		}
		super.draw(gl);
	}

	@Override
	public void fillWithObjects(final List<AbstractObject<?, ?>> list) {
		for (int i = 0; i < 3; i++) {
			final GamaPoint p = dirs[i];
			// build axis
			addSyntheticObject(list, axes[i], COLORS[i], IShape.Type.LINECYLINDER, false);
			// build labels
			final TextDrawingAttributes text = new TextDrawingAttributes(of(1), null, p.times(1.3).yNegated(),
					ANCHORS[i], COLORS[i], AXES_FONT, false);
			list.add(new StringObject(LABELS[i], text));
			// build arrows
			final GamaShape s = new GamaShape(arrow, null, ROTATIONS[i], p.times(0.98));
			addSyntheticObject(list, s, COLORS[i], IShape.Type.CONE, false);
		}
	}

}