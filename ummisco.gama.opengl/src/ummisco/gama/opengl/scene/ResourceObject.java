/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.ResourceObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.FileDrawingAttributes;

public class ResourceObject extends AbstractObject<GamaGeometryFile, FileDrawingAttributes> {

	public ResourceObject(final GamaGeometryFile file, final FileDrawingAttributes attributes) {
		super(file, attributes);
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.RESOURCE;
	}

}
