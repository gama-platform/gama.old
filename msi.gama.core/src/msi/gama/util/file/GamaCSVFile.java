/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaCSVFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file;

import static org.apache.commons.lang.StringUtils.splitByWholeSeparatorPreserveAllTokens;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.csv.CsvReader;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Strings;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class GamaCSVFile.
 *
 * @author drogoul
 * @since 9 janv. 2014
 *
 */
@file (
		name = "csv",
		extensions = { "csv", "tsv" },
		buffer_type = IType.MATRIX,
		buffer_index = IType.POINT,
		concept = { IConcept.CSV, IConcept.FILE },
		doc = @doc ("A type of text file that contains comma-separated values"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaCSVFile extends GamaFile<IMatrix<Object>, Object> implements IFieldMatrixProvider {

	public static class CSVInfo extends GamaFileMetaData {

		public int cols;
		public int rows;
		public boolean header;
		public Character delimiter;
		public Character qualifier;
		public final IType type;
		public String[] headers;

		public CSVInfo(final String fileName, final long modificationStamp, final String CSVsep) {
			super(modificationStamp);
			final CsvReader.Stats s = CsvReader.getStats(fileName, CSVsep);
			cols = s.cols;
			rows = s.rows;
			header = s.header;
			delimiter = s.delimiter;
			type = s.type;
			headers = s.headers;
		}

		public CSVInfo(final String propertyString) {
			super(propertyString);
			final String[] segments = split(propertyString);
			cols = Integer.parseInt(segments[1]);
			rows = Integer.parseInt(segments[2]);
			header = Boolean.valueOf(segments[3]);
			delimiter = segments[4].charAt(0);
			type = Types.get(segments[5]);
			if (header) {
				headers = splitByWholeSeparatorPreserveAllTokens(segments[6], SUB_DELIMITER);
			} else {

				headers = new String[cols];
				Arrays.fill(headers, "");
			}
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("CSV File ").append(header ? "with header" : "no header").append(Strings.LN);
			sb.append("Dimensions: ").append(cols + " columns x " + rows + " rows").append(Strings.LN);
			sb.append("Delimiter: ").append(delimiter).append(Strings.LN);
			sb.append("Contents type: ").append(type).append(Strings.LN);
			if (header && headers != null) {
				sb.append("Headers: ");
				for (final String header2 : headers) {
					sb.append(header2).append(" | ");
				}
				sb.setLength(sb.length() - 3);
			}
			return sb.toString();
		}

		@Override
		public String getSuffix() {
			return "" + cols + "x" + rows + " | " + (header ? "with header" : "no header") + " | " + "delimiter: '"
					+ delimiter + "' | " + type;
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			sb.append(cols).append("x").append(rows).append(SUFFIX_DEL);
			sb.append(header ? "with header" : "no header").append(SUFFIX_DEL);
			sb.append("delimiter: '").append(delimiter).append("'").append(SUFFIX_DEL).append(type);
		}

		/**
		 * @return
		 */
		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + cols + DELIMITER + rows + DELIMITER + header + DELIMITER
					+ delimiter + DELIMITER + type
					+ (header ? DELIMITER + StringUtils.join(headers, SUB_DELIMITER) : "");
		}

		/**
		 * @param header2
		 */
		public void setHeaders(final String[] newHeaders) {
			header = newHeaders != null;
			headers = newHeaders;
		}

	}

	String csvSeparator = null;
	Character textQualifier = null;
	IType contentsType;
	GamaPoint userSize;
	Boolean hasHeader;
	IList<String> headers;
	CSVInfo info;

	/**
	 * @param scope
	 * @param pathName
	 * @throws GamaRuntimeException
	 */
	@doc (
			value = "This file constructor allows to read a CSV file with the default separator (coma), no header, and no assumption on the type of data. No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\");",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, (String) null);
	}

	@doc (
			value = "This file constructor allows to read a CSV file with the default separator (coma), with specifying if the model has a header or not (boolean), and no assumption on the type of data. No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\",true);",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final Boolean withHeader) {
		this(scope, pathName);
		hasHeader = withHeader;
	}

	@doc (
			value = "This file constructor allows to read a CSV file and specify the separator used, without making any assumption on the type of data. Headers should be detected automatically if they exist. No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", \";\");",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator) {
		this(scope, pathName, separator, (IType) null);
	}

	@doc (
			value = "This file constructor allows to read a CSV file and specify (1) the separator used; (2) if the model has a header or not, without making any assumption on the type of data. No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", \";\",true);",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final Boolean withHeader) {
		this(scope, pathName, separator, (IType) null);
		hasHeader = withHeader;
	}

	@doc (
			value = "This file constructor allows to read a CSV file and specify (1) the separator used; (2) the text qualifier used; (3) if the model has a header or not, without making any assumption on the type of data",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", ';', '\"', true);",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final String qualifier,
			final Boolean withHeader) {
		this(scope, pathName, separator, (IType) null);
		textQualifier = qualifier == null || qualifier.isEmpty() ? null : qualifier.charAt(0);
		hasHeader = withHeader;
	}

	@doc (
			value = "This file constructor allows to read a CSV file with a given separator, no header, and the type of data. No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", \";\",int);",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type) {
		this(scope, pathName, separator, type, (Boolean) null);
	}

	@doc (
			value = "This file constructor allows to read a CSV file and specify the separator, text qualifier to use, and the type of data to read.  Headers should be detected automatically if they exist.  ",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", ';', '\"', int);",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final String qualifier,
			final IType type) {
		this(scope, pathName, separator, type, (Boolean) null);
		textQualifier = qualifier == null || qualifier.isEmpty() ? null : qualifier.charAt(0);
	}

	@doc (
			value = "This file constructor allows to read a CSV file with a given separator, the type of data, with specifying if the model has a header or not (boolean). No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", \";\",int,true);",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type,
			final Boolean withHeader) {
		this(scope, pathName, separator, type, (GamaPoint) null);
		hasHeader = withHeader;
	}

	@doc (
			value = "This file constructor allows to read a CSV file with a given separator, the type of data, with specifying the number of cols and rows taken into account. No text qualifier will be used",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", \";\",int,true, {5, 100});",
					isExecutable = false) })
	public GamaCSVFile(final IScope scope, final String pathName, final String separator, final IType type,
			final GamaPoint size) {
		super(scope, pathName);
		setCsvSeparators(separator);
		contentsType = type;
		userSize = size;
	}

	@doc (
			value = "This file constructor allows to store a matrix in a CSV file (it does not save it - just store it in memory),",
			examples = { @example (
					value = "csv_file f <- csv_file(\"file.csv\", matrix([10,10],[10,10]));",
					isExecutable = false) })

	public GamaCSVFile(final IScope scope, final String pathName, final IMatrix<Object> matrix) {
		super(scope, pathName, matrix);
		if (matrix != null) {
			userSize = matrix.getDimensions();
			contentsType = matrix.getGamlType().getContentType();
		}
	}

	public void setCsvSeparators(final String string) {
		if (string == null) return;
		if (string.length() >= 1) { csvSeparator = string; }
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		if (getBuffer() == null) {
			final CSVInfo info = getInfo(scope, null);
			if (info != null)
				return info.header ? GamaListFactory.wrap(Types.STRING, info.headers) : GamaListFactory.EMPTY_LIST;
		}
		fillBuffer(scope);
		return headers == null ? GamaListFactory.EMPTY_LIST : headers;
	}

	private CSVInfo getInfo(final IScope scope, final String CSVSep) {
		if (info != null) return info;
		final IFileMetaDataProvider p = scope.getGui().getMetaDataProvider();
		if (p != null) {
			final IGamaFileMetaData metaData = p.getMetaData(getFile(scope), false, true);
			if (metaData instanceof CSVInfo) {
				info = (CSVInfo) metaData;
				if (CSVSep != null && info != null && !info.delimiter.equals(CSVSep.charAt(0))) { info = null; }
			}
		}
		if (info == null) {
			info = new CSVInfo(getFile(scope).getAbsolutePath(), 0, CSVSep);
			// if (p != null) {
			// p.storeMetadata(getFile(), info);
			// }

		}
		if (hasHeader != null && hasHeader) {
			if (!info.header) {
				try {
					final CsvReader reader = new CsvReader(getPath(scope), info.delimiter);
					if (reader.readHeaders()) { info.headers = reader.getHeaders(); }
					reader.close();
				} catch (final FileNotFoundException e) {} catch (final IOException e) {}
			}
			info.header = hasHeader;
		}
		return info;
	}

	@Override
	public void fillBuffer(final IScope scope) {
		if (getBuffer() != null) return;
		if (csvSeparator == null || contentsType == null || userSize == null) {
			scope.getGui().getStatus(scope).beginSubStatus("Opening file " + getName(scope));
			final CSVInfo stats = getInfo(scope, csvSeparator);
			csvSeparator = csvSeparator == null ? "" + stats.delimiter : csvSeparator;
			contentsType = contentsType == null ? stats.type : contentsType;
			if (userSize == null) { userSize = new GamaPoint(stats.cols, stats.rows); }

			// AD We take the decision for the modeler is he/she hasn't
			// specified if the header must be read or not.
			hasHeader = hasHeader == null ? stats.header : hasHeader;
			scope.getGui().getStatus(scope).endSubStatus("");
		}
		CsvReader reader = null;
		try {

			reader = new CsvReader(getPath(scope), csvSeparator.charAt(0));
			reader.setTextQualifier(textQualifier);
			if (hasHeader) {
				reader.readHeaders();
				headers = GamaListFactory.createWithoutCasting(Types.STRING, reader.getHeaders());
				// we remove one row so as to not read the headers as well
				// Cause for issue #3036
				userSize.y = userSize.y - 1;
			}
			// long t = System.currentTimeMillis();
			setBuffer(createMatrixFrom(scope, reader));
			// DEBUG.LOG("CSV stats: " + userSize.x * userSize.y + "
			// cells read in " +
			// (System.currentTimeMillis() - t) + " ms");
		} catch (final FileNotFoundException e) {
			throw GamaRuntimeException.create(e, scope);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		} finally {
			if (reader != null) { reader.close(); }
			// See Issue #3036 -- value must be modified when the file is reloaded
			if (hasHeader != null && hasHeader) { userSize.y++; }
		}

	}

	@Override
	public IContainerType getGamlType() {
		final IType ct = getBuffer() == null ? Types.NO_TYPE : getBuffer().getGamlType().getContentType();
		return Types.FILE.of(ct);
	}

	private IMatrix createMatrixFrom(final IScope scope, final CsvReader reader) throws IOException {
		final int t = contentsType.id();
		double percentage = 0;
		IMatrix matrix;
		try {
			scope.getGui().getStatus(scope).beginSubStatus("Reading file " + getName(scope));
			if (t == IType.INT) {
				matrix = new GamaIntMatrix(userSize);
				final int[] m = ((GamaIntMatrix) matrix).getMatrix();
				int i = 0;
				while (reader.readRecord()) {
					percentage = reader.getCurrentRecord() / userSize.y;
					scope.getGui().getStatus(scope).setSubStatusCompletion(percentage);
					int nbC = 0;
					for (final String s : reader.getValues()) {
						m[i++] = Cast.asInt(scope, s);
						nbC++;
					}
					while (nbC < matrix.getCols(null)) {
						m[i++] = 0;
						nbC++;
					}
				}
			} else if (t == IType.FLOAT) {
				matrix = new GamaFloatMatrix(userSize);
				final double[] m = ((GamaFloatMatrix) matrix).getMatrix();
				int i = 0;
				while (reader.readRecord()) {
					percentage = reader.getCurrentRecord() / userSize.y;
					scope.getGui().getStatus(scope).setSubStatusCompletion(percentage);
					int nbC = 0;
					for (final String s : reader.getValues()) {
						m[i++] = Cast.asFloat(scope, s);
						nbC++;
					}
					while (nbC < matrix.getCols(null)) {
						m[i++] = 0.0;
						nbC++;
					}
				}
			} else {
				matrix = new GamaObjectMatrix(userSize, Types.STRING);
				final Object[] m = ((GamaObjectMatrix) matrix).getMatrix();
				int i = 0;
				while (reader.readRecord()) {
					percentage = reader.getCurrentRecord() / userSize.y;
					scope.getGui().getStatus(scope).setSubStatusCompletion(percentage);
					int nbC = 0;

					for (final String s : reader.getValues()) {
						if (i == m.length) {
							GAMA.reportError(scope, GamaRuntimeException.warning("The file " + getFile(scope).getName()
									+ " seems to contain data that have not been processed", scope), false);
							break;
						}
						nbC++;
						m[i++] = s;
					}
					while (nbC < matrix.getCols(null)) {
						m[i++] = null;
						nbC++;
					}
				}
			}

			return matrix;
		} finally {
			scope.getGui().getStatus(scope).endSubStatus("Reading CSV File");
		}
	}

	/**
	 * Method computeEnvelope()
	 *
	 * @see msi.gama.util.file.IGamaFile#computeEnvelope(msi.gama.runtime.IScope)
	 */
	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		// See how to read information from there
		return null;
	}

	/**
	 * @param asBool
	 */
	public void forceHeader(final Boolean asBool) {
		hasHeader = asBool;
	}

	public Boolean hasHeader() {
		return hasHeader == null ? false : hasHeader;
	}

	@Override
	public int getRows(final IScope scope) {
		return getInfo(scope, null).rows;
	}

	@Override
	public int getCols(final IScope scope) {
		return getInfo(scope, null).cols;
	}

	@Override
	public double[] getBand(final IScope scope, final int index) {
		if (index > 0) return null;
		GamaFloatMatrix m = (GamaFloatMatrix) GamaMatrixType.from(scope, getContents(scope), Types.FLOAT, null, false);
		return m.getMatrix();

	}

}
