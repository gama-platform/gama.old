package ummisco.gama.opengl.scene;

import static msi.gama.metamodel.shape.IShape.Type.LINESTRING;
import static msi.gama.metamodel.shape.IShape.Type.POLYGON;

import java.awt.Color;
import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LightPropertiesStructure;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.ModernRenderer;

public class LightsLayerObject extends StaticLayerObject.World {

	final static String[] LABELS = new String[] { "X", "Y", "Z" };
	final static GamaColor[] COLORS = new GamaColor[] { GamaColor.getInt(Color.red.getRGB()),
			GamaColor.getInt(Color.green.getRGB()), GamaColor.getInt(Color.blue.getRGB()) };
	final static GamaPoint DEFAULT_SCALE = new GamaPoint(.15, .15, .15);

	public LightsLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
	}

	@Override
	public GamaPoint getScale() {
		return scale == null ? DEFAULT_SCALE : super.getScale();
	}
	
	public void updateLights() {
		List<LightPropertiesStructure> lights = renderer.data.getDiffuseLights();
		for (LightPropertiesStructure light : lights) {
			if (light.isDrawLight()) {
//				if (light.type.equals(LightPropertiesStructure.TYPE.POINT)) {
//					GeometryObject geomObj = new GeometryObject(GamaGeometryType.buildSphere(5, light.position),light.color,IShape.Type.SPHERE,this);
//					((ModernRenderer)renderer).getDrawer().addDrawingEntities(((ModernRenderer)renderer).getVAOGenerator().GenerateVAO(geomObj));
//				}
			}
		}
	}

	@Override
	void fillWithObjects(final List<AbstractObject> list) {
		
		
		
		final double size = renderer.getMaxEnvDim();
		for (int i = 0; i < 3; i++) {
			final GamaPoint p = new GamaPoint(i == 0 ? size : 0, i == 1 ? size : 0, i == 2 ? size : 0);
			list.add(new GeometryObject(GamaGeometryType.buildLine(p), COLORS[i], LINESTRING, this));
			list.add(new StringObject(LABELS[i], p.times(1.2).yNegated(), this));
			list.add(new GeometryObject(GamaGeometryType.buildArrow(p.times(1.1), size / 6), COLORS[i], POLYGON, this));
		}
	}
}