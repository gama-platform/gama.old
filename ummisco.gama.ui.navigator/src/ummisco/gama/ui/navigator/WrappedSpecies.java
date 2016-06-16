/**
 * Created by drogoul, 19 nov. 2014
 *
 */
package ummisco.gama.ui.navigator;

import java.util.*;
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
		return !getObject().getMicroSpecies().isEmpty();
	}

	@Override
	public String getName() {
		return (getObject().isGrid() ? "Grid " : "Species ") + getObject().getName();
	}

	@Override
	public Object[] getNavigatorChildren() {
		List result = new ArrayList();
		Collection<SpeciesDescription> sd = getObject().getMicroSpecies().values();
		for ( SpeciesDescription s : sd ) {
			result.add(new WrappedSpecies(this, s));
		}
		return result.toArray();
	}

	// @Override
	// public boolean isParentOf(final Object element) {
	// if ( !(element instanceof WrappedSpecies) ) { return false; }
	// return getObject().getMicroSpecies(((WrappedSpecies) element).getObject().getName()) != null;
	// }

	@Override
	public Image getImage() {
		if ( getObject().isGrid() ) { return GamaIcons.create("gaml/_grid").image(); }
		return GamaIcons.create("gaml/_species").image();
	}

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().editModel(getObject().getUnderlyingElement(null));
		return true;
	}

}
