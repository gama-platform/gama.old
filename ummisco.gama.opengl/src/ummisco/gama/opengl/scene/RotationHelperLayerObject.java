/*********************************************************************************************
 *
 * 'RotationHelperLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
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
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class RotationHelperLayerObject extends LayerObject {

	public RotationHelperLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
	}

	@Override
	public void clear(final OpenGL gl) {}

	@Override
	public void draw(final OpenGL gl) {
		updateObjectList();
		super.draw(gl);
	}

	private void updateObjectList() {
		objects.clear();
		if (renderer.getRotationHelperPosition() != null) {
			final double distance =
					Math.sqrt(Math.pow(renderer.camera.getPosition().x - renderer.getRotationHelperPosition().x, 2)
							+ Math.pow(renderer.camera.getPosition().y - renderer.getRotationHelperPosition().y, 2)
							+ Math.pow(renderer.camera.getPosition().z - renderer.getRotationHelperPosition().z, 2));
			// create the rotation helper as "GeometryObject" in the list "objects".
			// the rotation helper is a sphere centered in renderer.getRotationHelperPosition() and a size of "50.0 *
			// (distance / 500)".
			final ArrayList<AbstractObject> newElem = new ArrayList<AbstractObject>();
			final GamaPoint pos = new GamaPoint(renderer.getRotationHelperPosition().x,
					-renderer.getRotationHelperPosition().y, renderer.getRotationHelperPosition().z);
			// interior sphere
			final IShape interiorSphereShape = GamaGeometryType.buildSphere(5.0 * (distance / 500), pos);
			final GeometryObject interiorSphere = new GeometryObject(interiorSphereShape,
					new GamaColor(0.5, 0.5, 0.5, 1.0), IShape.Type.SPHERE, false);
			newElem.add(interiorSphere);
			// exterior sphere
			final IShape exteriorSphereShape = GamaGeometryType.buildSphere(49.0 * (distance / 500), pos);
			final GeometryObject exteriorSphere = new GeometryObject(exteriorSphereShape,
					new GamaColor(0.5, 0.5, 0.5, 0.1), IShape.Type.SPHERE, false);
			newElem.add(exteriorSphere);
			// wireframe sphere
			final IShape wireframeSphereShape = GamaGeometryType.buildSphere(50.0 * (distance / 500), pos);
			final GeometryObject wireframeSphere = new GeometryObject(wireframeSphereShape,
					new GamaColor(0.5, 0.5, 0.5, 1.0), IShape.Type.SPHERE, true);
			newElem.add(wireframeSphere);

			objects.add(newElem);
		}
	}

}