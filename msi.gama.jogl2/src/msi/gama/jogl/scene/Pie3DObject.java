package msi.gama.jogl.scene;

import java.awt.Color;
import java.util.List;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaColor;
import com.vividsolutions.jts.geom.Geometry;

public class Pie3DObject extends GeometryObject {

	public List<Double> ratio;
	public List<GamaColor> colors;

	public Pie3DObject(final Geometry geometry, final IAgent agent, final double z_layer, final int layerId,
		final Color color, final Double alpha, final Boolean fill, final Color border, final Boolean isTextured,
		final List<String> textureFileNames, final int angle, final double height, final boolean rounded,
		final Type type, final List<Double> ratio2, final List<GamaColor> colors2) {
		super(geometry, agent, z_layer, layerId, color, alpha, fill, border, isTextured, textureFileNames, angle,
			height, rounded, type);
		this.ratio = ratio2;
		this.colors = colors2;
	}

	@Override
	public boolean isPie3D() {
		return true;
	}

}
