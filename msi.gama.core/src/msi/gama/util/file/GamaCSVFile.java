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

import static org.apache.commons.lang.StringUtils.splitByWholeSeparatorPreserveAllTokens;
import java.io.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.shape.*;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.*;
import msi.gaml.operators.*;
import msi.gaml.types.*;
import org.apache.commons.lang.StringUtils;
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

	public static class CSVInfo extends GamaFileMetaData {

		public final int cols, rows;
		public final boolean header;
		public final Character delimiter;
		public final IType type;
		public final String[] headers;

		public CSVInfo(final String fileName, final long modificationStamp) {
			super(modificationStamp);
			CsvReader.Stats s = CsvReader.getStats(fileName);
			cols = s.cols;
			rows = s.rows;
			header = s.header;
			delimiter = s.delimiter;
			type = s.type;
			headers = s.headers;
		}

		//
		// public CSVInfo(final long modificationStamp, final int cols, final int rows, final boolean header,
		// final Character delimiter, final IType type) {
		// super(modificationStamp);
		// this.cols = cols;
		// this.rows = rows;
		// this.header = header;
		// this.delimiter = delimiter;
		// this.type = type;
		// }

		public CSVInfo(final String propertyString) {
			super(propertyString);
			String[] segments = split(propertyString);
			cols = Integer.valueOf(segments[1]);
			rows = Integer.valueOf(segments[2]);
			header = Boolean.valueOf(segments[3]);
			delimiter = segments[4].charAt(0);
			type = Types.get(segments[5]);
			if ( header ) {
				headers = splitByWholeSeparatorPreserveAllTokens(segments[6], SUB_DELIMITER);
			} else {
				headers = null;
			}
		}

		@Override
		public String getDocumentation() {
			StringBuilder sb = new StringBuilder();
			sb.append("CSV File ").append(header ? "with header" : "no header").append(Strings.LN);
			sb.append("Dimensions: ").append(cols + " columns x " + rows + " rows").append(Strings.LN);
			sb.append("Delimiter: ").append(delimiter).append(Strings.LN);
			sb.append("Contents type: ").append(type).append(Strings.LN);
			if ( headers != null ) {
				sb.append("Headers: ");
				for ( int i = 0; i < headers.length; i++ ) {
					sb.append(headers[i]).append(" | ");
				}
				sb.setLength(sb.length() - 3);
			}
			return sb.toString();
		}

		@Override
		public String getSuffix() {
			return "" + cols + "x" + rows + " | " + (header ? "with header" : "no header") + " | " + "delimiter: '" +
				delimiter + "' | " + type;
		}

		/**
		 * @return
		 */
		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + cols + DELIMITER + rows + DELIMITER + header + DELIMITER +
				delimiter + DELIMITER + type + (header ? DELIMITER + StringUtils.join(headers, SUB_DELIMITER) : "");
		}

	}

	String csvSeparator = null;
	IType contentsType;
	GamaPoint userSize;
	Boolean hasHeader;
	IList<String> headers;

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	public GamaCSVFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, (String) null);
	}

	public GamaCSVFile(final IScope scope, final String pathName, final Boolean withHeader) {
		this(scope, pathName);
		hasHeader = withHeader;
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator) {
		this(scope, pathName, separator, (IType) null);
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final Boolean withHeader) {
		this(scope, pathName, separator, (IType) null);
		hasHeader = withHeader;
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type) {
		this(scope, pathName, separator, type, (Boolean) null);
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type,
		final Boolean withHeader) {
		this(scope, pathName, separator, type, (GamaPoint) null);
		hasHeader = withHeader;
	}

	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type,
		final GamaPoint size) {
		super(scope, pathName);
		setCsvSeparators(separator);
		contentsType = type;
		userSize = size;
	}

	public GamaCSVFile(final IScope scope, final String pathName, final IMatrix<Object> matrix) {
		super(scope, pathName, matrix);
		if ( matrix != null ) {
			userSize = (GamaPoint) matrix.getDimensions();
			contentsType = matrix.getType().getContentType();
		}
	}

	public void setCsvSeparators(final String string) {
		if ( string == null ) { return; }
		if ( string.length() >= 1 ) {
			csvSeparator = string;
		}
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		if ( getBuffer() == null ) {
			IFileMetaDataProvider p = GuiUtils.getMetaDataProvider();
			// CSVInfo info = null;
			if ( p != null ) {
				CSVInfo info = (CSVInfo) p.getMetaData(getFile());
				if ( info != null ) { return info.header ? GamaListFactory.createWithoutCasting(Types.STRING,
					info.headers) : GamaListFactory.EMPTY_LIST; }
			}
		}
		fillBuffer(scope);
		return headers == null ? GamaListFactory.EMPTY_LIST : GamaListFactory.createWithoutCasting(Types.STRING,
			headers);
	}

	@Override
	public void fillBuffer(final IScope scope) {
		if ( getBuffer() != null ) { return; }
		if ( csvSeparator == null || contentsType == null || userSize == null ) {
			GuiUtils.beginSubStatus("Opening file " + getName());
			final CsvReader.Stats stats = CsvReader.getStats(getPath());
			csvSeparator = csvSeparator == null ? "" + stats.delimiter : csvSeparator;
			contentsType = contentsType == null ? stats.type : contentsType;
			userSize = userSize == null ? new GamaPoint(stats.cols, stats.rows) : userSize;
			// AD We cant take the decision for the modeler is he/she hasn't specified if the header must be read or not
			hasHeader = hasHeader == null ? /* stats.header */false : hasHeader;
			GuiUtils.endSubStatus("");
		}
		CsvReader reader = null;
		try {
			reader = new CsvReader(getPath(), csvSeparator.charAt(0));
			if ( hasHeader ) {
				reader.readHeaders();
				headers = GamaListFactory.createWithoutCasting(Types.STRING, reader.getHeaders());
				// we remove one row
				userSize.y = userSize.y - 1;
				// we remove one line so as to not read the headers as well
				// Not necessary: userSize.y--;
			}
			// long t = System.currentTimeMillis();
			setBuffer(createMatrixFrom(scope, reader));
			// System.out.println("CSV stats: " + userSize.x * userSize.y + " cells read in " +
			// (System.currentTimeMillis() - t) + " ms");
		} catch (FileNotFoundException e) {
			throw GamaRuntimeException.create(e, scope);
		} catch (IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if ( reader != null ) {
				reader.close();
			}
		}

		// if ( csvSeparator != null ) {} else {
		// GamaTextFile textFile = new GamaTextFile(scope, path);
		// final String string = textFile.stringValue(scope);
		// if ( string == null ) { return; }
		// setBuffer(GamaMatrixType.from(scope, string, null)); // Use the default CVS reader
		// }
	}

	@Override
	public IContainerType getType() {
		IType ct = getBuffer() == null ? Types.NO_TYPE : getBuffer().getType().getContentType();
		return Types.FILE.of(ct);
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
				matrix = new GamaObjectMatrix(userSize, Types.STRING);
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

	/**
	 * @param asBool
	 */
	public void forceHeader(final Boolean asBool) {
		hasHeader = asBool;
	}

	public Boolean hasHeader() {
		return hasHeader == null ? false : hasHeader;
	}

}
