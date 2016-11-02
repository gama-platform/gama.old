/*********************************************************************************************
 *
 * 'WrappedWorld.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import msi.gaml.descriptions.ModelDescription;
import ummisco.gama.ui.resources.GamaIcons;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.Image;

/**
 * Class WrappedWorld.
 * 
 * @author drogoul
 * @since 19 nov. 2014
 * 
 */
public class WrappedWorld extends WrappedSpecies {

	public WrappedWorld(final IFile root, final ModelDescription object) {
		super(root, object);
	}

	@Override
	public Image getImage() {
		return GamaIcons.create("gaml/_model").image();
	}

	@Override
	public String getName() {
		return "Contents (model " + getObject().getName().replace(ModelDescription.MODEL_SUFFIX, "") + ")";
	}

	@Override
	public int compareTo(final WrappedGamlObject other) {
		return +1;
	}

}
