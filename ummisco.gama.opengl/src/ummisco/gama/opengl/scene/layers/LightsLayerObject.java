/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.layers.LightsLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.ArrayList;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LightPropertiesStructure;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;

public class LightsLayerObject extends LayerObject {

	public LightsLayerObject(final IOpenGLRenderer renderer) {
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
		traces.clear();
		for (final LightPropertiesStructure light : renderer.getData().getDiffuseLights()) {
			if (light.drawLight && light.id != 0) {

				final double size = renderer.getMaxEnvDim() / 20;

				final ArrayList<AbstractObject<?, ?>> newElem = new ArrayList<>();
				final GamaPoint pos = light.position;

				if (light.type == LightPropertiesStructure.TYPE.POINT) {
					final IShape sphereShape = GamaGeometryType.buildSphere(size, pos);
					addSyntheticObject(newElem, sphereShape, light.color, IShape.Type.SPHERE, false);
				} else if (light.type == LightPropertiesStructure.TYPE.SPOT) {
					// TODO
					final double baseSize = Math.sin(Math.toRadians(light.spotAngle)) * size;
					final IShape coneShape = GamaGeometryType.buildCone3D(baseSize, size, pos);
					addSyntheticObject(newElem, coneShape, light.color, IShape.Type.CONE, false);
				} else {
					// TODO
				}
				traces.add(newElem);
			}
		}
	}

}