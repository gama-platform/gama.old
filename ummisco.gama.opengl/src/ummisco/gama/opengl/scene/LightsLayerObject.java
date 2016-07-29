package ummisco.gama.opengl.scene;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.LightPropertiesStructure;
import msi.gama.util.GamaPair;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.Types;
import ummisco.gama.opengl.Abstract3DRenderer;

public class LightsLayerObject extends LayerObject {

	public LightsLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
		constantRedrawnLayer = true;
	}

	@Override
	public void clear(final GL2 gl) {
	}
	
	@Override
	public void draw(final GL2 gl) {
		updateObjectList();
		super.draw(gl);
	}
	
	public void updateObjectList() {
		objects.clear();
		for (LightPropertiesStructure light : renderer.data.getDiffuseLights()) {
			if (light.drawLight && light.id != 0) {
				
				double size = renderer.getMaxEnvDim() / 20;
				
				ArrayList<AbstractObject> newElem = new ArrayList<AbstractObject>();
				GamaPoint pos = light.position;

				if (light.type == LightPropertiesStructure.TYPE.POINT) {
					IShape sphereShape = GamaGeometryType.buildSphere(size, pos);
					GeometryObject pointLight = new GeometryObject(sphereShape,light.color,IShape.Type.SPHERE,this);
					pointLight.disableLightInteraction();
					newElem.add(pointLight);
				}
				else if (light.type == LightPropertiesStructure.TYPE.SPOT) {
					// TODO
					final double baseSize = Math.sin(Math.toRadians(light.spotAngle)) * size;
					IShape coneShape = GamaGeometryType.buildCone3D(baseSize, size, pos);
					GeometryObject spotLight = new GeometryObject(coneShape,light.color,IShape.Type.CONE,this);
//					spotLight.getAttributes().rotation = new GamaPair(Cast.asFloat(null, 0), Cast.asPoint(null, light.direction),
//							Types.FLOAT, Types.POINT);
					spotLight.disableLightInteraction();
					newElem.add(spotLight);
				}
				else {
					// TODO
				}
				objects.add(newElem);
			}
		}
	}

}