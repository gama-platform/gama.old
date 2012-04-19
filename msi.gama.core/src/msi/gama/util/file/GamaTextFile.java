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

import java.io.*;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Files;
import msi.gaml.types.*;

public class GamaTextFile extends GamaFile<Integer, String> {

	public GamaTextFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaTextFile(final String absoluteFilePath) throws GamaRuntimeException {
		super(absoluteFilePath);
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isTextFile(file.getName()) ) { throw new GamaRuntimeException(
			"The extension " + this.getExtension() + " is not recognized for text files"); }
	}

	@Override
	protected IGamaFile _copy() {
		return null;
	}

	@Override
	protected boolean _isFixedLength() {
		return false;
	}

	@Override
	protected IMatrix _matrixValue(final IScope scope, final ILocation preferredSize)
		throws GamaRuntimeException {
		final String string = stringValue();
		if ( string == null ) { return null; }
		return GamaMatrixType.from(string, preferredSize); // Necessary ?

	}

	@Override
	public String _stringValue() throws GamaRuntimeException {
		getContents();
		StringBuilder sb = new StringBuilder();
		for ( String s : buffer ) {
			sb.append(s).append("\n"); // TODO Factorize the different calls to "new line" ...
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	@Override
	public String getKeyword() {
		return Files.TEXT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer() throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		try {
			final BufferedReader in = new BufferedReader(new FileReader(file));
			final GamaList<String> allLines = new GamaList();
			String str;
			str = in.readLine();
			while (str != null) {
				allLines.add(str);
				str = in.readLine();
			}
			in.close();
			buffer = allLines;
		} catch (final IOException e) {
			throw new GamaRuntimeException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO A faire.

	}

}
