/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.views;

import msi.gama.kernel.experiment.IExperiment;

public class ExperimentParametersView extends AttributesEditorsView<String> {

	public static final String ID = "msi.gama.gui.application.view.ParameterView";

	public void addItem(final IExperiment exp) {
		if ( exp != null ) {
			reset();
			editors = exp.getParametersEditors();
			if ( editors == null ) { return; }
			displayItems();
		}
	}

	@Override
	public boolean addItem(final String object) {
		createItem(object, true);
		return true;
	}

}
