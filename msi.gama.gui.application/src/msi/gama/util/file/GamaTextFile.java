package msi.gama.util.file;

import java.io.*;
import msi.gama.interfaces.*;
import msi.gama.internal.types.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.Files;

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
	protected IMatrix _matrixValue(final IScope scope, final GamaPoint preferredSize)
		throws GamaRuntimeException {
		final String string = stringValue();
		if ( string == null ) { return null; }
		return GamaMatrixType.from(string, preferredSize); // Necessary ?

	}

	@Override
	public String _stringValue() throws GamaRuntimeException {
		fillBuffer();
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
