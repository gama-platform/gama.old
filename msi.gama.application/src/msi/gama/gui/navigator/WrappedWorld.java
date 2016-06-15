/**
 * Created by drogoul, 19 nov. 2014
 * 
 */
package msi.gama.gui.navigator;

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
