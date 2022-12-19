/*******************************************************************************************************
 *
 * MeshObject.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.mesh;

import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.DrawingAttributes.DrawerType;
import msi.gaml.statements.draw.MeshDrawingAttributes;
import ummisco.gama.opengl.scene.AbstractObject;

/**
 * The Class MeshObject.
 */
public class MeshObject extends AbstractObject<IField, MeshDrawingAttributes> {

	/**
	 * Instantiates a new mesh object.
	 *
	 * @param dem the dem
	 * @param attributes the attributes
	 */
	public MeshObject(final IField dem, final MeshDrawingAttributes attributes) {
		super(dem, attributes, DrawerType.MESH);
	}

}
