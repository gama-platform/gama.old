/*******************************************************************************************************
 *
 * GosplSurveyFactory.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.IEntity;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.io.IGSSurvey;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.io.exception.InvalidSurveyFormatException;

/**
 * Factory to setup data input into gospl generator
 *
 * @author kevinchapuis
 *
 */
public class GosplSurveyFactory {

	/** The precision. */
	@SuppressWarnings ("unused") private final double precision = Math.pow(10, -2);

	/** The dfs. */
	private final DecimalFormatSymbols dfs;

	/** The decimal format. */
	private final DecimalFormat decimalFormat;

	/** The separator. */
	private char separator = ';'; // default separator for writing files if not specified

	/** The stored in memory. */
	private boolean storedInMemory;

	/** The sheet nb. */
	private int sheetNb;

	/** The first row data idx. */
	private int firstRowDataIdx;

	/** The first column data idx. */
	private int firstColumnDataIdx;

	/** The Constant UNKNOWN_VARIABLE. */
	public static final String UNKNOWN_VARIABLE = "?";

	/** The Constant CSV_EXT. */
	public static final String CSV_EXT = ".csv";

	/** The Constant XLS_EXT. */
	public static final String XLS_EXT = ".xls";

	/** The Constant XLSX_EXT. */
	public static final String XLSX_EXT = ".xlsx";

	/** The Constant DBF_EXT. */
	public static final String DBF_EXT = ".dbf";

	/**
	 * The list of supported file formats (provided as extensions)
	 */
	public static final List<String> supportedFileFormat =
			Collections.unmodifiableList(Arrays.asList(CSV_EXT, XLS_EXT, XLSX_EXT, DBF_EXT));

	/**
	 * Instantiates a new gospl survey factory.
	 */
	public GosplSurveyFactory() {

		this.dfs = new DecimalFormatSymbols(Locale.FRANCE);
		this.decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.FRANCE));
		this.dfs.setDecimalSeparator('.');
	}

	/**
	 * Replace default factory value by explicit ones
	 *
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 */
	public GosplSurveyFactory(final int sheetNn, final char csvSeparator, final int firstRowDataIndex,
			final int firstColumnDataIndex) {

		this(sheetNn, csvSeparator, false, firstRowDataIndex, firstColumnDataIndex,
				new DecimalFormatSymbols(Locale.FRANCE),
				new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.FRANCE)));
	}

	/**
	 * Replace default factory value by explicit ones
	 *
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param locale
	 */
	public GosplSurveyFactory(final int sheetNn, final char csvSeparator, final boolean storedInMemory,
			final int firstRowDataIndex, final int firstColumnDataIndex, final Locale locale) {

		this(sheetNn, csvSeparator, storedInMemory, firstRowDataIndex, firstColumnDataIndex,
				new DecimalFormatSymbols(locale), new DecimalFormat("#.##", new DecimalFormatSymbols(locale)));
	}

	/**
	 * Replace default factory value by explicit ones
	 *
	 *
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param decimalFormatSymbols
	 * @param decimalFormat
	 */
	public GosplSurveyFactory(final int sheetNn, final char csvSeparator, final boolean storedInMemory,
			final int firstRowDataIndex, final int firstColumnDataIndex,
			final DecimalFormatSymbols decimalFormatSymbols, final DecimalFormat decimalFormat) {

		this.dfs = decimalFormatSymbols;
		this.decimalFormat = decimalFormat;
		this.storedInMemory = storedInMemory;
		this.sheetNb = sheetNn;
		this.separator = csvSeparator;
		this.firstRowDataIdx = firstRowDataIndex;
		this.firstColumnDataIdx = firstColumnDataIndex;
	}

	/**
	 * Gives the list of format (extension) the file this factory can setup input data with
	 *
	 * @return
	 */
	public List<String> getSupportedFileFormat() { return supportedFileFormat; }

	// ----------------------------------------------------------------------- //
	// ------------------------- DATA IMPORT SECTION ------------------------- //
	// ----------------------------------------------------------------------- //

	/**
	 * Retrieve a survey from a wrapper (lighter memory version of a survey)
	 *
	 * @see GSSurveyWrapper
	 *
	 * @param wrapper
	 * @param basePath
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey getSurvey(final GSSurveyWrapper wrapper, final Path basePath)
			throws IOException, InvalidSurveyFormatException {

		Path expectedPath = wrapper.getRelativePath();

		// first try to decode this file as such
		File surveyFile = expectedPath.toFile();
		if (!surveyFile.exists()) {

			expectedPath = basePath.resolve(expectedPath);
			surveyFile = expectedPath.toFile();

			// does not seem necessary anymore (and that's weird !)
			if (!surveyFile.exists()) throw new IllegalArgumentException("cannot load file " + surveyFile + " .");
			if (!surveyFile.canRead()) throw new IllegalArgumentException("cannot read file " + surveyFile + " .");

		}
		return this.getSurvey(surveyFile, wrapper.getStoredInMemory(), wrapper.getSheetNumber(),
				wrapper.getCsvSeparator(), wrapper.getFirstRowIndex(), wrapper.getFirstColumnIndex(),
				wrapper.getSurveyType());
	}

	/**
	 *
	 * @param filepath
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @return
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 * @throws InvalidFormatException
	 */
	public IGSSurvey getSurvey(final String filepath, final int sheetNn, final char csvSeparator,
			final int firstRowDataIndex, final int firstColumnDataIndex, final GSSurveyType dataFileType)
			throws IOException, InvalidSurveyFormatException {

		if (filepath.endsWith(XLS_EXT))
			return new XlsInputHandler(filepath, sheetNn, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		if (filepath.endsWith(CSV_EXT))
			return new CsvInputHandler(filepath, csvSeparator, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		// if (filepath.endsWith(DBF_EXT))
		// return new DBaseInputHandler(dataFileType, filepath);

		final String[] pathArray = filepath.split(File.separator);
		throw new InvalidSurveyFormatException(pathArray[pathArray.length - 1], supportedFileFormat);
	}

	/**
	 *
	 * @param filepath
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @param processAsFormat
	 *            one of the formats supportedFileFormat
	 * @return
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 * @throws InvalidFormatException
	 */
	public IGSSurvey getSurvey(final String filepath, final int sheetNn, final char csvSeparator,
			final int firstRowDataIndex, final int firstColumnDataIndex, final GSSurveyType dataFileType,
			final String processAsFormat) throws IOException, InvalidSurveyFormatException {

		if (XLS_EXT.equals(processAsFormat))
			return new XlsInputHandler(filepath, sheetNn, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		if (CSV_EXT.equals(processAsFormat))
			return new CsvInputHandler(filepath, csvSeparator, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		// if (processAsFormat.equals(DBF_EXT))
		// return new DBaseInputHandler(dataFileType, filepath);

		final String[] pathArray = filepath.split(File.separator);
		throw new InvalidSurveyFormatException(pathArray[pathArray.length - 1], supportedFileFormat);
	}

	/**
	 *
	 * @param filepath
	 * @param dataFileType
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey getSurvey(final String filepath, final GSSurveyType dataFileType)
			throws IOException, InvalidSurveyFormatException {
		return this.getSurvey(filepath, sheetNb, separator, firstRowDataIdx, firstColumnDataIdx, dataFileType);
	}

	/**
	 *
	 * @param file
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @return
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey getSurvey(final File file, final boolean storeInMemory, final int sheetNn, final char csvSeparator,
			final int firstRowDataIndex, final int firstColumnDataIndex, final GSSurveyType dataFileType)
			throws IOException, InvalidSurveyFormatException {
		if (file.getName().endsWith(XLS_EXT))
			return new XlsInputHandler(file, sheetNn, firstRowDataIndex, firstColumnDataIndex, dataFileType);
		if (file.getName().endsWith(CSV_EXT)) return new CsvInputHandler(file, storeInMemory, csvSeparator,
				firstRowDataIndex, firstColumnDataIndex, dataFileType);
		// if (file.getName().endsWith(DBF_EXT))
		// return new DBaseInputHandler(dataFileType, file);
		final String[] pathArray = file.getPath().split(File.separator);
		throw new InvalidSurveyFormatException(pathArray[pathArray.length - 1], supportedFileFormat);
	}

	/**
	 * Retriev survey based on default factory parameter
	 *
	 * @param file
	 * @param dataFileType
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey getSurvey(final File file, final GSSurveyType dataFileType)
			throws IOException, InvalidSurveyFormatException {
		return this.getSurvey(file, storedInMemory, sheetNb, separator, firstRowDataIdx, firstColumnDataIdx,
				dataFileType);
	}

	/**
	 *
	 * @param fileName
	 * @param surveyIS
	 * @param sheetNn
	 * @param csvSeparator
	 * @param firstRowDataIndex
	 * @param firstColumnDataIndex
	 * @param dataFileType
	 * @return
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey getSurvey(final String fileName, final InputStream surveyIS, final int sheetNn,
			final char csvSeparator, final int firstRowDataIndex, final int firstColumnDataIndex,
			final GSSurveyType dataFileType) throws IOException, InvalidSurveyFormatException {
		if (fileName.endsWith(XLS_EXT)) return new XlsInputHandler(surveyIS, sheetNn, fileName, firstRowDataIndex,
				firstColumnDataIndex, dataFileType);
		if (fileName.endsWith(CSV_EXT)) return new CsvInputHandler(fileName, surveyIS, csvSeparator, firstRowDataIndex,
				firstColumnDataIndex, dataFileType);
		if (fileName.endsWith(DBF_EXT))
			throw new IllegalArgumentException("Cannot read format " + DBF_EXT + " from a, input stream, sorry");
		throw new InvalidSurveyFormatException(fileName, supportedFileFormat);
	}

	/**
	 *
	 * @param fileName
	 * @param surveyIS
	 * @param dataFileType
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey getSurvey(final String fileName, final InputStream surveyIS, final GSSurveyType dataFileType)
			throws IOException, InvalidSurveyFormatException {
		return this.getSurvey(fileName, surveyIS, sheetNb, separator, firstRowDataIdx, firstColumnDataIdx,
				dataFileType);
	}

	// ----------------------------------------------------------------------- //
	// ---------------------- POPULATION EXPORT SECTION ---------------------- //
	// ----------------------------------------------------------------------- //

	/**
	 * Export a population to a file. Each {@link GSSurveyType} will cause the export to be of the corresponding type;
	 * i.e. either a sample of the complete population or the contingency / frequency of each attribute
	 * <p>
	 * WARNING: make use of parallelism using {@link Stream#parallel()}
	 *
	 * @param surveyFile
	 * @param surveyType
	 * @param population
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 */
	public IGSSurvey createSummary(final File surveyFile, final GSSurveyType surveyType,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population)
			throws InvalidFormatException, IOException, InvalidSurveyFormatException {
		return switch (surveyType) {
			case Sample -> createSample(surveyFile, true, population);
			case ContingencyTable -> createTableSummary(surveyFile, surveyType, population);
			case GlobalFrequencyTable -> createTableSummary(surveyFile, surveyType, population);
			default -> createTableSummary(surveyFile, GSSurveyType.GlobalFrequencyTable, population);
		};
	}

	/**
	 * Export a population to a given file. The output format is a matrix with dimension being attribute set
	 * {@code format} passed as parameter.
	 *
	 * @param surveyFile
	 * @param format
	 * @param population
	 * @return
	 * @throws IOException
	 * @throws InvalidSurveyFormatException
	 * @throws InvalidFormatException
	 */
	public IGSSurvey createContingencyTable(final File surveyFile, final Set<Attribute<? extends IValue>> format,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population)
			throws IOException, InvalidSurveyFormatException {

		GSPerformanceUtil gspu = new GSPerformanceUtil("TEST OUTPUT TABLES", Level.TRACE);

		AFullNDimensionalMatrix<Integer> popMatrix =
				GosplNDimensionalMatrixFactory.getFactory().createContingency(population);

		if (format.stream().anyMatch(att -> !popMatrix.getDimensions().contains(att)))
			throw new IllegalArgumentException("Format is not entirely aligned with population: \n" + "Format: "
					+ Arrays.toString(format.toArray()) + "\n" + "Population: "
					+ Arrays.toString(popMatrix.getDimensions().toArray()));

		List<Attribute<? extends IValue>> columnHeaders = format.stream().skip(format.size() / 2).toList();
		List<Attribute<? extends IValue>> rowHeaders =
				format.stream().filter(att -> !columnHeaders.contains(att)).toList();

		gspu.sysoStempMessage("Columns: "
				+ columnHeaders.stream().map(Attribute::getAttributeName).collect(Collectors.joining(" + ")));
		gspu.sysoStempMessage(
				"Rows: " + rowHeaders.stream().map(Attribute::getAttributeName).collect(Collectors.joining(" + ")));

		String report = "";

		if (rowHeaders.isEmpty()) {
			List<? extends IValue> vals =
					columnHeaders.stream().flatMap(att -> att.getValueSpace().getValues().stream()).toList();
			report += vals.stream().map(IValue::getStringValue).collect(Collectors.joining(String.valueOf(separator)))
					+ String.valueOf(separator) + "TOTAL\n";
			report += vals.stream().map(val -> popMatrix.getVal(val, true).getValue().toString())
					.collect(Collectors.joining(String.valueOf(separator))) + String.valueOf(separator)
					+ vals.stream().mapToInt(val -> popMatrix.getVal(val, true).getValue()).sum();
		} else {
			Map<Integer, List<IValue>> columnHead = getTableHeader(columnHeaders);
			Map<Integer, List<IValue>> rowHead = getTableHeader(rowHeaders);

			String blankHeadLine = rowHeaders.stream().map(rAtt -> " " + separator).collect(Collectors.joining());
			report += IntStream.range(0, columnHeaders.size()).mapToObj(
					index -> blankHeadLine + columnHead.values().stream().map(col -> col.get(index).getStringValue())
							.collect(Collectors.joining(String.valueOf(separator))))
					.collect(Collectors.joining("\n")) + String.valueOf(separator) + "TOTAL";

			gspu.sysoStempMessage("HEAD: " + report);
			gspu.sysoStempMessage("ROW VALUE" + rowHead.keySet().stream().sorted()
					.map(i -> rowHead.get(i).stream().map(IValue::getStringValue).collect(Collectors.joining(" + ")))
					.collect(Collectors.joining(" // ")));

			for (int rowIdx = 0; rowIdx < rowHead.size(); rowIdx++) {
				List<Integer> data = new ArrayList<>();
				for (Integer colIdx : columnHead.keySet()) {
					data.add(colIdx,
							popMatrix
									.getVal(Stream.concat(columnHead.get(colIdx).stream(), rowHead.get(rowIdx).stream())
											.collect(Collectors.toSet()))
									.getValue());
				}
				report += "\n"
						+ rowHead.get(rowIdx).stream().map(IValue::getStringValue)
								.collect(Collectors.joining(String.valueOf(separator)))
						+ separator
						+ data.stream().map(i -> i.toString()).collect(Collectors.joining(String.valueOf(separator)));
				report += separator + data.stream().reduce(0, (i1, i2) -> i1 + i2);
				gspu.sysoStempMessage("New line (" + Arrays.toString(rowHead.get(rowIdx).toArray()) + ") = "
						+ Arrays.toString(data.toArray()));
			}

			List<Integer> colTotals = IntStream.range(0, columnHead.size())
					.mapToObj(colIdx -> popMatrix.getVal(columnHead.get(colIdx)).getValue()).toList();
			report += "\n"
					+ colTotals.stream().map(col -> col.toString())
							.collect(Collectors.joining(String.valueOf(separator)))
					+ separator + colTotals.stream().reduce(1, (i1, i2) -> i1 + i2);
		}

		Files.write(surveyFile.toPath(), report.getBytes());
		return this.getSurvey(surveyFile, GSSurveyType.ContingencyTable);
	}

	// ------------------------------------------------------------------- //
	// ---------------------- MATRIX EXPORT SECTION ---------------------- //
	// ------------------------------------------------------------------- //

	/**
	 * Make a summary of a full matrix - print each total value
	 *
	 * @param surveyFile
	 * @param matrix
	 * @param factorReport
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws InvalidSurveyFormatException
	 */
	public <T extends Number> IGSSurvey createSummary(final Path surveyFile,
			final INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> matrix)
			throws IOException, InvalidFormatException, InvalidSurveyFormatException {

		Collection<Attribute<? extends IValue>> attributes = matrix.getDimensions();

		StringBuilder report = new StringBuilder()
				.append(attributes.stream().map(att -> att.getAttributeName() + separator + "frequence")
						.collect(Collectors.joining(String.valueOf(separator))))
				.append("\n");
		List<String> lines = IntStream.range(0,
				attributes.stream().mapToInt(att -> att.getValueSpace().getValues().size() + 1).max().getAsInt())
				.mapToObj(i -> "").toList();

		for (Attribute<? extends IValue> attribute : attributes) {

			int lineNumber = 0;

			Set<IValue> attValues =
					Stream.concat(attribute.getValueSpace().getValues().stream(), Stream.of(attribute.getEmptyValue()))
							.collect(Collectors.toSet());

			for (IValue value : attValues) {
				String val = "";
				if (!value.getValueSpace().getEmptyValue().equals(value)) { val = matrix.getVal(value).toString(); }
				lines.set(lineNumber,
						lines.get(lineNumber).concat(lines.get(lineNumber++).isEmpty() ? "" : String.valueOf(separator))
								+ value.getStringValue() + separator + val);
			}

			for (int i = lineNumber; i < lines.size(); i++) {
				lines.set(i, lines.get(i).concat(lines.get(i).isEmpty() ? "" : separator + "") + separator + "");
			}
		}

		report.append(String.join("\n", lines));
		Files.write(surveyFile, report.toString().getBytes());
		return this.getSurvey(surveyFile.toFile(), matrix.getMetaDataType());

	}

	// ---------------------- inner methods ---------------------- //

	/**
	 * Creates a new GosplSurvey object.
	 *
	 * @param surveyFile
	 *            the survey file
	 * @param surveyType
	 *            the survey type
	 * @param population
	 *            the population
	 * @return the IGS survey
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InvalidFormatException
	 *             the invalid format exception
	 * @throws InvalidSurveyFormatException
	 *             the invalid survey format exception
	 */
	private IGSSurvey createTableSummary(final File surveyFile, final GSSurveyType surveyType,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population)
			throws IOException, InvalidFormatException, InvalidSurveyFormatException {

		Collection<Attribute<? extends IValue>> attributes = population.getPopulationAttributes();

		StringBuilder report = new StringBuilder()
				.append(attributes.stream().map(att -> att.getAttributeName() + separator + "frequence")
						.collect(Collectors.joining(String.valueOf(separator))))
				.append("\n");
		List<String> lines = IntStream.range(0,
				attributes.stream().mapToInt(att -> att.getValueSpace().getValues().size() + 1).max().getAsInt())
				.mapToObj(i -> "").toList();

		Map<IValue, Integer> mapReport = attributes.stream()
				.flatMap(att -> Stream.concat(att.getValueSpace().getValues().stream(), Stream.of(att.getEmptyValue()))
						.collect(Collectors.toSet()).stream())
				.collect(Collectors.toMap(Function.identity(), value -> 0));

		population.stream().forEach(
				entity -> entity.getValues().forEach(eValue -> mapReport.put(eValue, mapReport.get(eValue) + 1)));

		for (Attribute<? extends IValue> attribute : attributes) {

			int lineNumber = 0;

			Set<IValue> attValues =
					Stream.concat(attribute.getValueSpace().getValues().stream(), Stream.of(attribute.getEmptyValue()))
							.collect(Collectors.toSet());

			for (IValue value : attValues) {
				String val = "";
				if (GSSurveyType.ContingencyTable.equals(surveyType)) {
					val = String.valueOf(mapReport.get(value));
				} else {
					val = decimalFormat.format(mapReport.get(value).doubleValue() / population.size());
				}
				lines.set(lineNumber,
						lines.get(lineNumber).concat(lines.get(lineNumber++).isEmpty() ? "" : String.valueOf(separator))
								+ value.getStringValue() + separator + val);
			}

			for (int i = lineNumber; i < lines.size(); i++) {
				lines.set(i, lines.get(i).concat(lines.get(i).isEmpty() ? "" : separator + "") + separator + "");
			}
		}

		report.append(String.join("\n", lines));
		Files.write(surveyFile.toPath(), report.toString().getBytes());
		return this.getSurvey(surveyFile, GSSurveyType.GlobalFrequencyTable);
	}

	/**
	 * Create a sample survey from a population: writes the population passed as a parameter into a CSV file, and
	 * returns it in the form of a {@link IGSSurvey} pointing to this file.
	 *
	 * @param surveyFile
	 * @param population
	 * @return
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws InvalidFileTypeException
	 */
	@SuppressWarnings ("null")
	private IGSSurvey createSample(final File surveyFile, boolean withParent,
			final IPopulation<ADemoEntity, Attribute<? extends IValue>> population)
			throws IOException, InvalidSurveyFormatException, InvalidFormatException {

		try (final BufferedWriter bw = Files.newBufferedWriter(surveyFile.toPath())) {
			final Collection<Attribute<? extends IValue>> attributes = population.getPopulationAttributes();
			final Collection<IEntity<? extends IAttribute<? extends IValue>>> parents = population.stream()
					.map(ADemoEntity::getParent).filter(Objects::nonNull).collect(Collectors.toSet());
			Set<IAttribute<? extends IValue>> parentAttributes = null;
			if (parents.isEmpty()) {
				withParent = false;
			} else {
				parentAttributes = parents.stream().flatMap(e -> e.getAttributes().stream())
						.map(a -> (IAttribute<? extends IValue>) a).collect(Collectors.toSet());
			}
			bw.write("ID");
			bw.write(separator);
			bw.write("__type");
			bw.write(separator);
			bw.write(attributes.stream().map(Attribute::getAttributeName)
					.collect(Collectors.joining(String.valueOf(separator))));
			bw.write(separator);
			bw.write("parentId");
			bw.write(separator);
			if (withParent) {
				bw.write("parent__type");
				bw.write(separator);
				bw.write(parentAttributes.stream().map(IAttribute::getAttributeName)
						.collect(Collectors.joining(String.valueOf(separator))));
				bw.write(separator);
				bw.write("count_parent_children");
				bw.write(separator);
			}
			bw.write("count_children");
			bw.write("\n");

			bw.write("Individual");
			bw.write(separator);
			bw.write("Type of the entity");
			bw.write(separator);
			bw.write(attributes.stream().map(Attribute::getDescription)
					.collect(Collectors.joining(String.valueOf(separator))));
			bw.write(separator);
			bw.write("ID of the parent entity");
			bw.write(separator);
			if (withParent) {
				bw.write("parent__type");
				bw.write(separator);
				bw.write(parentAttributes.stream().map(IAttribute::getDescription)
						.collect(Collectors.joining(String.valueOf(separator))));
				bw.write(separator);
			}
			bw.write("count of children of this entity");
			bw.write("\n");

			for (final ADemoEntity e : population) {
				if (e._hasEntityId()) {
					bw.write(e.getEntityId()); // String.valueOf(individual++)
				} else {
					bw.write(" ");
				}

				bw.write(separator);
				if (e.getEntityType() != null) {
					bw.write(e.getEntityType());
				} else {
					bw.write(" ");
				}
				for (final Attribute<? extends IValue> attribute : attributes) {
					bw.write(separator);
					try {

						IValue val = e.getValueForAttribute(attribute);
						String v = val.getStringValue();

						if (!attribute.getValueSpace().getType().isNumericValue()) {
							bw.write("\"");
							bw.write(v);
							bw.write("\"");
						} else {
							bw.write(v);
						}

					} catch (NullPointerException e2) {
						bw.write(UNKNOWN_VARIABLE + "\""); // WARNING: i don't understand why i have to add extra "
					}
				}
				bw.write(separator);
				IEntity<?> parent = e.getParent();
				if (parent != null) {
					bw.write(e.getParent().getEntityId());
				} else {
					bw.write(" ");
				}
				if (withParent) {
					ADemoEntity p = (ADemoEntity) parent;
					bw.write(separator);
					if (p.getEntityType() != null) {
						bw.write(p.getEntityType());
					} else {
						bw.write(" ");
					}
					for (final IAttribute<? extends IValue> attribute : parentAttributes) {
						bw.write(separator);

						try {

							IValue val = p.getValueForAttribute((Attribute<? extends IValue>) attribute);
							String v = val.getStringValue();

							if (!attribute.getValueSpace().getType().isNumericValue()) {
								bw.write("\"");
								bw.write(v);
								bw.write("\"");
							} else {
								bw.write(v);
							}

						} catch (NullPointerException e2) {
							bw.write(UNKNOWN_VARIABLE + "\""); // WARNING: i don't understand why i have to add extra "
						}
					}
					bw.write(separator);
					bw.write(Integer.toString(p.getCountChildren().getActualValue()));
				}
				bw.write(separator);
				bw.write(Integer.toString(e.getCountChildren().getActualValue()));
				bw.write("\n");
			}
		}
		return this.getSurvey(surveyFile, GSSurveyType.Sample);
	}

	/*
	 *
	 */

	/**
	 *
	 * @param headerAttributes
	 * @return
	 */
	private Map<Integer, List<IValue>> getTableHeader(final Collection<Attribute<? extends IValue>> headerAttributes) {

		Attribute<? extends IValue> anchor = headerAttributes.iterator().next();
		List<List<IValue>> head = new ArrayList<>();
		for (IValue value : anchor.getValueSpace().getValues()) { head.add(new ArrayList<>(Arrays.asList(value))); }
		headerAttributes.remove(anchor);
		for (Attribute<? extends IValue> headAtt : headerAttributes) {
			List<List<IValue>> tmpHead = new ArrayList<>();
			for (List<IValue> currentHead : head) {
				tmpHead.addAll(headAtt.getValueSpace().getValues().stream()
						.map(val -> Stream.concat(currentHead.stream(), Stream.of(val)).toList()).toList());
			}
			head = tmpHead;
		}
		final List<List<IValue>> headFinal = head;
		return head.stream().collect(Collectors.toMap(headFinal::indexOf, Function.identity()));
	}

}
