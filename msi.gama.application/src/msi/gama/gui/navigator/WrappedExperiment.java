/**
 * Created by drogoul, 19 nov. 2014
 *
 */
package msi.gama.gui.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.graphics.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.util.file.GAMLFile;

/**
 * Class WrappedExperiment.
 *
 * @author drogoul
 * @since 19 nov. 2014
 *
 */
public class WrappedExperiment extends WrappedGamlObject {

	private static String prefix = "Experiment ";
	final String internalName;
	final boolean isBatch;

	/**
	 * @param root
	 * @param object
	 */
	public WrappedExperiment(final IFile root, final String name) {
		super(root, prefix + name.replace(GAMLFile.GamlInfo.BATCH_PREFIX, ""));
		isBatch = name.contains(GAMLFile.GamlInfo.BATCH_PREFIX);
		internalName = name.replace(GAMLFile.GamlInfo.BATCH_PREFIX, "");
	}

	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	// @Override
	// public boolean isParentOf(final Object element) {
	// return false;
	// }

	@Override
	public Image getImage() {
		return GamaIcons.create(isBatch ? "gaml/_batch" : "gaml/_gui").image();

	}

	@Override
	public Color getColor() {
		return IGamaColors.BLACK.color();
	}

	@Override
	public boolean handleDoubleClick() {
		GuiUtils.runModel(getParent(), internalName);
		return true;
	}

}
