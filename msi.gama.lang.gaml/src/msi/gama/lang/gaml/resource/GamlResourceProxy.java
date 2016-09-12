package msi.gama.lang.gaml.resource;

import java.io.IOException;

public class GamlResourceProxy {

	GamlResource resource;
	Boolean isSynthetic;

	public GamlResourceProxy(final GamlResource r, final boolean synthetic) {
		setRealResource(r, synthetic);
	}

	public void setRealResource(final GamlResource r, final boolean synthetic) {
		resource = r;
		isSynthetic = synthetic;
	}

	public GamlResource getRealResource() {
		return resource;
	}

	public boolean isSynthetic() {
		return resource != null && isSynthetic;
	}

	public void dispose() throws IOException {
		if (isSynthetic && resource != null) {
			resource.delete(null);
			resource = null;
		}
	}

}
