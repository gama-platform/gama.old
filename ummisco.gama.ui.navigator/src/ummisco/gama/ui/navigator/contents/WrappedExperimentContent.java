/*******************************************************************************************************
 *
 * WrappedExperimentContent.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import org.eclipse.jface.resource.ImageDescriptor;

import msi.gama.common.interfaces.IGamlLabelProvider;
import msi.gama.runtime.GAMA;
import msi.gama.util.file.GamlFileInfo;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class WrappedExperimentContent.
 */
public class WrappedExperimentContent extends WrappedSyntacticContent {

	/** Not used if an EObject is passed to the Wrapper */
	String icon, expName;

	/**
	 * Instantiates a new wrapped experiment content.
	 *
	 * @param file
	 *            the file
	 * @param e
	 *            the e
	 */
	public WrappedExperimentContent(final WrappedGamaFile file, final ISyntacticElement e) {
		super(file, e, WorkbenchHelper.getService(IGamlLabelProvider.class).getText(e));
		expName = e.getName();
	}

	/**
	 * Instantiates a new wrapped experiment content.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param file
	 *            the file
	 * @param e
	 *            the e
	 * @date 2 janv. 2024
	 */
	public WrappedExperimentContent(final WrappedGamaFile file, final String e) {
		super(file, null, "Experiment " + e.replace(GamlFileInfo.BATCH_PREFIX, ""));
		boolean isBatch = e.startsWith(GamlFileInfo.BATCH_PREFIX);
		icon = isBatch ? "gaml/_batch" : "gaml/_gui";
		expName = e.replace(GamlFileInfo.BATCH_PREFIX, "");
	}

	@Override
	public WrappedGamaFile getFile() { return (WrappedGamaFile) getParent(); }

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().runModel(getParent(), expName);
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return element == null ? GamaIcon.named(icon).descriptor()
				: (ImageDescriptor) WorkbenchHelper.getService(IGamlLabelProvider.class).getImageDescriptor(element);
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		sb.append("Double-click to run");
	}

}