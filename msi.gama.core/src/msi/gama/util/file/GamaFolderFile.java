/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.operators.Files;

public class GamaFolderFile extends GamaFile<Integer, String> {

	public GamaFolderFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		setWritable(true);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		if ( !getFile().isDirectory() ) { throw new GamaRuntimeException(getFile()
			.getAbsolutePath() + "is not a folder"); }
		if ( !getFile().exists() ) { throw new GamaRuntimeException("The folder " +
			getFile().getAbsolutePath() + " does not exist. Please use 'new_folder' instead"); }
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
		return Files.FOLDER + "(" + getFile().getPath() + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer() throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList(getFile().list());
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
