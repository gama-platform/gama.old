/*********************************************************************************************
 * 
 * 
 * 'GamaCSVFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.util.file;

import java.io.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Class GamaCSVFile.
 * 
 * @author drogoul
 * @since 9 janv. 2014
 * 
 */
@file(name = "csv", extensions = { "csv", "tsv" }, buffer_type = IType.MATRIX, buffer_index = IType.POINT)
public class GamaCSVFile extends GamaFile<IMatrix<Object>, Object, ILocation, Object> {

	String csvSeparator = ",";
	IType contentsType;
	GamaPoint userSize;

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	public GamaCSVFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, ",");
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator) {
		this(scope, pathName, separator, null);
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type) {
		this(scope, pathName, separator, type, null);
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type,
		final GamaPoint size) {
		super(scope, pathName);
		setCsvSeparators(separator);
		contentsType = type;
		userSize = size;
	}

	// public GamaCSVFile(final IScope scope, final String pathName, final String separator, final GamaMatrix matrix) {
	// super(scope, pathName);
	// setCsvSeparators(separator);
	// setBuffer(matrix);
	// fillMatrix = true;
	// }
	//
	// public GamaCSVFile(final IScope scope, final String pathName, final GamaMatrix matrix) {
	// super(scope, pathName);
	// setBuffer(matrix);
	// fillMatrix = true;
	// }

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
			CsvReader reader = null;
			try {
				reader = new CsvReader(getPath(), csvSeparator.charAt(0));
				if ( contentsType == null || userSize == null ) {
					GuiUtils.beginSubStatus("Opening file " + getName());
					final CsvReader.Stats stats = CsvReader.getStats(reader);
					contentsType = contentsType == null ? stats.type : contentsType;
					userSize = userSize == null ? new GamaPoint(stats.cols, stats.rows) : userSize;
					GuiUtils.endSubStatus("");
				}
				long t = System.currentTimeMillis();
				setBuffer(createMatrixFrom(scope, reader));
				System.out.println("CSV stats: " + userSize.x * userSize.y + " cells read in " +
					(System.currentTimeMillis() - t) + " ms");
			} catch (FileNotFoundException e) {
				throw GamaRuntimeException.create(e, scope);
			} catch (IOException e) {
				throw GamaRuntimeException.create(e, scope);
			} finally {
				if ( reader != null ) {
					reader.close();
				}
			}
		} else {
			GamaTextFile textFile = new GamaTextFile(scope, path);
			final String string = textFile.stringValue(scope);
			if ( string == null ) { return; }
			setBuffer(GamaMatrixType.from(scope, string, null)); // Use the default CVS reader
		}
	}

	private IMatrix createMatrixFrom(final IScope scope, final CsvReader reader) throws IOException {
		int t = contentsType.id();
		double percentage = 0;
		IMatrix matrix;
		try {
			GuiUtils.beginSubStatus("Reading file " + getName());
			if ( t == IType.INT ) {
				matrix = new GamaIntMatrix(userSize);
				int[] m = ((GamaIntMatrix) matrix).getMatrix();
				int i = 0;
				while (reader.readRecord()) {
					percentage = reader.getCurrentRecord() / userSize.y;
					GuiUtils.updateSubStatusCompletion(percentage);
					for ( String s : reader.getValues() ) {
						m[i++] = Cast.asInt(scope, s);
					}
				}
			} else if ( t == IType.FLOAT ) {
				matrix = new GamaFloatMatrix(userSize);
				double[] m = ((GamaFloatMatrix) matrix).getMatrix();
				int i = 0;
				while (reader.readRecord()) {
					percentage = reader.getCurrentRecord() / userSize.y;
					GuiUtils.updateSubStatusCompletion(percentage);
					for ( String s : reader.getValues() ) {
						m[i++] = Cast.asFloat(scope, s);
					}
				}
			} else {
				matrix = new GamaObjectMatrix(userSize);
				Object[] m = ((GamaObjectMatrix) matrix).getMatrix();
				int i = 0;
				while (reader.readRecord()) {
					percentage = reader.getCurrentRecord() / userSize.y;
					GuiUtils.updateSubStatusCompletion(percentage);

					for ( String s : reader.getValues() ) {
						m[i++] = s;
					}
				}
			}

			return matrix;
		} finally {
			GuiUtils.endSubStatus("Reading CSV File");
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
