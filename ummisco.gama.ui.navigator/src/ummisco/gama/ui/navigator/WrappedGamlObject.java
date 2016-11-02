/*********************************************************************************************
 *
 * 'WrappedGamlObject.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;


import msi.gaml.descriptions.IDescription;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.IGamaColors;
import org.eclipse.swt.graphics.*;

/**
 * Class WrappedGamlObject.
 * 
 * @author drogoul
 * @since 18 nov. 2014
 * 
 */
public abstract class WrappedGamlObject extends VirtualContent implements Comparable<WrappedGamlObject> {

	private final IDescription object;

	public WrappedGamlObject(final Object root, final IDescription object) {
		super(root, object.getName());
		this.object = object;
	}

	public WrappedGamlObject(final Object root, final String object) {
		super(root, object);
		this.object = null;
	}

	public IDescription getObject() {
		return object;
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigFileFont(); // by default
	}

	@Override
	public int compareTo(final WrappedGamlObject other) {
		return getName().compareTo(other.getName());
	}

	@Override
	public Color getColor() {
		return IGamaColors.BLUE.inactive();
	}

}
