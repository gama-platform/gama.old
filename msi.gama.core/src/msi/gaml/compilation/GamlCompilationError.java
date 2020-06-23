/*******************************************************************************************************
 *
 * msi.gaml.compilation.GamlCompilationError.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * The Class GamlCompilationError. Represents the errors produced by the
 * validation/compilation of IDescription's.
 */
public class GamlCompilationError {

	protected boolean isWarning = false;
	protected boolean isInfo = false;
	protected final String message;
	protected String code;
	protected String[] data;
	protected EObject source;
	protected final URI uri;

	public GamlCompilationError(final String string, final String code, final EObject object, final boolean warning,
			final boolean info, final String... data) {

		message = string;
		isWarning = warning;
		isInfo = info;
		this.code = code;
		this.data = data;
		source = object;
		uri = object.eResource().getURI();
	}

	public GamlCompilationError(final String string, final String code, final URI uri, final boolean warning,
			final boolean info, final String... data) {

		message = string;
		isWarning = warning;
		isInfo = info;
		this.code = code;
		this.data = data;
		source = null;
		this.uri = uri;
	}

	public String[] getData() {
		return data;
	}

	public URI getURI() {
		return uri;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return message;
	}

	public boolean isWarning() {
		return isWarning && !isInfo;
	}

	public boolean isInfo() {
		return isInfo;
	}

	public EObject getStatement() {
		return source;
	}

	public boolean isError() {
		return !isInfo && !isWarning;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GamlCompilationError)) {
			return false;
		}
		final GamlCompilationError error = (GamlCompilationError) other;
		return message.equals(error.message) && source == error.source;
	}

	@Override
	public int hashCode() {
		return message.hashCode() + (source == null ? 0 : source.hashCode());
	}
}
