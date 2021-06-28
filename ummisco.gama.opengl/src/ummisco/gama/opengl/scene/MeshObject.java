/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.FieldObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.MeshDrawingAttributes;

public class MeshObject extends AbstractObject<IField, MeshDrawingAttributes> {

	public MeshObject(final IField dem, final MeshDrawingAttributes attributes) {
		super(dem, attributes, DrawerType.MESH);
	}

}
