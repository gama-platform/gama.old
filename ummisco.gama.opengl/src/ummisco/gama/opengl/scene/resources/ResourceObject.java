/*******************************************************************************************************
 *
 * ResourceObject.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.resources;

import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.DrawingAttributes.DrawerType;
import ummisco.gama.opengl.scene.AbstractObject;

/**
 * The Class ResourceObject.
 */
public class ResourceObject extends AbstractObject<GamaGeometryFile, DrawingAttributes> {

	/**
	 * Instantiates a new resource object.
	 *
	 * @param file the file
	 * @param attributes the attributes
	 */
	public ResourceObject(final GamaGeometryFile file, final DrawingAttributes attributes) {
		super(file, attributes, DrawerType.RESOURCE);
	}

}
