package msi.gama.util.file;

import msi.gama.interfaces.IScope;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Files;

public class GamaFolderFile extends GamaFile<Integer, String> {

	public GamaFolderFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		setWritable(true);
	}

	public GamaFolderFile(final String absoluteFilePath) throws GamaRuntimeException {
		super(absoluteFilePath);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		if ( !file.isDirectory() ) { throw new GamaRuntimeException(file.getAbsolutePath() +
			"is not a folder"); }
		if ( !file.exists() ) { throw new GamaRuntimeException("The folder " +
			file.getAbsolutePath() + " does not exist. Please use 'new_folder' instead"); }
	}

	@Override
	protected IGamaFile _copy() {
		// TODO What to do ?
		return null;
	}

	@Override
	protected boolean _isFixedLength() {
		return true;
	}

	@Override
	public String toGaml() {
		return Files.FOLDER + "(" + file.getPath() + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer() throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList(file.list());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// Nothing to do

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#getKeyword()
	 */
	@Override
	public String getKeyword() {
		return Files.FOLDER;
	}

}
