/*********************************************************************************************
 *
 * 'GamlProblems.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import java.util.*;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.views.markers.FiltersContributionParameters;

public class GamlProblems extends FiltersContributionParameters {

	private static Map<String, Integer> parametersMap;

	static {
		parametersMap = new HashMap<>();
		parametersMap.put(IMarker.SEVERITY,
			new Integer(IMarker.SEVERITY_WARNING | IMarker.SEVERITY_ERROR | IMarker.SEVERITY_INFO));
	}

	/**
	 * Create a new instance of the receiver.
	 */

	public GamlProblems() {}

	@Override
	public Map<String, Integer> getParameterValues() {
		return parametersMap;
	}

}
