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
