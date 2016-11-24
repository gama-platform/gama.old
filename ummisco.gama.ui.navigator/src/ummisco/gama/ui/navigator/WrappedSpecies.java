/*********************************************************************************************
 *
 * 'WrappedSpecies.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import msi.gama.runtime.GAMA;
import msi.gaml.descriptions.SpeciesDescription;
import ummisco.gama.ui.resources.GamaIcons;

/**
 * Class WrappedSpecies.
 *
 * @author drogoul
 * @since 19 nov. 2014
 *
 */
public class WrappedSpecies extends WrappedGamlObject {

	/**
	 * @param root
	 * @param object
	 */
	public WrappedSpecies(final Object root, final SpeciesDescription object) {
		super(root, object);
	}

	@Override
	public SpeciesDescription getObject() {
		return (SpeciesDescription) super.getObject();
	}

	@Override
	public boolean hasChildren() {
		return !getObject().hasMicroSpecies();
	}

	@Override
	public String getName() {
		return (getObject().isGrid() ? "Grid " : "Species ") + getObject().getName();
	}

	@Override
	public Object[] getNavigatorChildren() {
		final List<WrappedSpecies> result = new ArrayList<>();
		final Collection<SpeciesDescription> sd = getObject().getMicroSpecies().values();
		for (final SpeciesDescription s : sd) {
			result.add(new WrappedSpecies(this, s));
		}
		return result.toArray();
	}

	// @Override
	// public boolean isParentOf(final Object element) {
	// if ( !(element instanceof WrappedSpecies) ) { return false; }
	// return getObject().getMicroSpecies(((WrappedSpecies)
	// element).getObject().getName()) != null;
	// }

	@Override
	public Image getImage() {
		if (getObject().isGrid()) {
			return GamaIcons.create("gaml/_grid").image();
		}
		return GamaIcons.create("gaml/_species").image();
	}

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().editModel(getObject().getUnderlyingElement(null));
		return true;
	}

}
