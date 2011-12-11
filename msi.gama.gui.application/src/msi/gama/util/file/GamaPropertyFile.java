package msi.gama.util.file;

import java.io.*;
import java.util.Properties;
import msi.gama.interfaces.IScope;
import msi.gama.internal.types.GamaFileType;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.operators.Files;

public class GamaPropertyFile extends GamaFile<String, String> {

	public GamaPropertyFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaPropertyFile(final String absoluteFilePath) throws GamaRuntimeException {
		super(absoluteFilePath);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isProperties(file.getName()) ) { throw new GamaRuntimeException(
			"The extension " + this.getExtension() + " is not recognized for properties files"); }
	}

	@Override
	protected IGamaFile _copy() {
		// TODO A faire
		return null;
	}

	@Override
	protected boolean _isFixedLength() {
		return false;
	}

	@Override
	public String getKeyword() {
		return Files.PROPERTIES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer() throws GamaRuntimeException {
		Properties p = new Properties();
		GamaMap m = new GamaMap();
		try {
			p.load(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new GamaRuntimeException(e);
		} catch (IOException e) {
			throw new GamaRuntimeException(e);
		}
		m.putAll(p);
		buffer = m;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO A faire

	}

}
