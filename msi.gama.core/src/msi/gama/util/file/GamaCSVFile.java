/**
 * Created by drogoul, 9 janv. 2014
 * 
 */
package msi.gama.util.file;

import java.io.*;
import java.util.List;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.GamaMatrixType;
import au.com.bytecode.opencsv.CSVReader;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class GamaCSVFile.
 * 
 * @author drogoul
 * @since 9 janv. 2014
 * 
 */
@file(name = "csv", extensions = { "csv", "tsv" })
public class GamaCSVFile extends GamaFile<IMatrix<Object>, Object, ILocation, Object> {

	String csvSeparator = ",";

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	public GamaCSVFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, ",");
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator) {
		super(scope, pathName);
		setCsvSeparators(separator);
	}

	public GamaCSVFile(final IScope scope, final String pathName, final IMatrix<Object> matrix) {
		super(scope, pathName, matrix);
	}

	public void setCsvSeparators(final String string) {
		if ( string.length() >= 1 ) {
			csvSeparator = string;
		}
	}

	@Override
	public void fillBuffer(final IScope scope) {
		if ( getBuffer() != null ) { return; }
		if ( csvSeparator != null ) {
			try {
				CSVReader reader = new CSVReader(new FileReader(getPath()), csvSeparator.charAt(0));
				List<String[]> strings = reader.readAll();
				setBuffer(GamaMatrixType.fromCSV(scope, strings));
			} catch (FileNotFoundException e) {} catch (IOException e) {
				throw new GamaRuntimeException(e);
			}
		} else {
			GamaTextFile textFile = new GamaTextFile(scope, path);
			final String string = textFile.stringValue(scope);
			if ( string == null ) { return; }
			setBuffer(GamaMatrixType.from(scope, string, null)); // Use the default CVS reader
		}
	}

	/**
	 * Method computeEnvelope()
	 * @see msi.gama.util.file.IGamaFile#computeEnvelope(msi.gama.runtime.IScope)
	 */
	@Override
	public Envelope computeEnvelope(final IScope scope) {
		// See how to read information from there
		return null;
	}

	/**
	 * Method flushBuffer()
	 * @see msi.gama.util.file.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {}
}
