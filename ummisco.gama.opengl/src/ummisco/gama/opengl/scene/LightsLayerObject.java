/*********************************************************************************************
 *
 * 'LightsLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.util.ArrayList;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LightPropertiesStructure;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class LightsLayerObject extends LayerObject {

	public LightsLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
	}

	@Override
	public void clear(final OpenGL gl) {}

	@Override
	public boolean isLightInteraction() {
		return false;
	}

	@Override
	public void draw(final OpenGL gl) {
		updateObjectList();
		super.draw(gl);
	}

	public void updateObjectList() {
		objects.clear();
		for (final LightPropertiesStructure light : renderer.data.getDiffuseLights()) {
			if (light.drawLight && light.id != 0) {

				final double size = renderer.getMaxEnvDim() / 20;

				final ArrayList<AbstractObject> newElem = new ArrayList<>();
				final GamaPoint pos = light.position;

				if (light.type == LightPropertiesStructure.TYPE.POINT) {
					final IShape sphereShape = GamaGeometryType.buildSphere(size, pos);
					final GeometryObject pointLight =
							new GeometryObject(sphereShape, light.color, IShape.Type.SPHERE, false);
					newElem.add(pointLight);
				} else if (light.type == LightPropertiesStructure.TYPE.SPOT) {
					// TODO
					final double baseSize = Math.sin(Math.toRadians(light.spotAngle)) * size;
					final IShape coneShape = GamaGeometryType.buildCone3D(baseSize, size, pos);
					final GeometryObject spotLight =
							new GeometryObject(coneShape, light.color, IShape.Type.CONE, false);
					newElem.add(spotLight);
				} else {
					// TODO
				}
				objects.add(newElem);
			}
		}
	}

}