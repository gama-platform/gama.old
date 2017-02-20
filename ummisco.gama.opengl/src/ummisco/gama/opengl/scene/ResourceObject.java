/*********************************************************************************************
 *
 * 'ResourceObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.DrawingAttributes;

public class ResourceObject extends GeometryObject {

	public final GamaGeometryFile file;

	public ResourceObject(final GamaGeometryFile file, final DrawingAttributes attributes) {
		super(null, attributes);
		this.file = file;
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.GEOMETRY;
	}

	@Override
	public GamaGeometryFile getFile() {
		return file;
	}

	@Override
	public AxisAngle getInitRotation() {
		return file.getInitRotation();
	}

	@Override
	public Envelope3D getEnvelope(final OpenGL gl) {
		return gl.getEnvelopeFor(file);
	}

}
