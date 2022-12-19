/*******************************************************************************************************
 *
 * AbstractInputHandler.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.io;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import core.configuration.dictionary.IGenstarDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.IGSSurvey;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import core.util.GSPerformanceUtil.Level;

/**
 * Abstraction for any input handler: able to store the path to the file its based on, and the nature of data stored
 * there. Also provides basic algos for detecting row and column headers, suitable for tabular files. </br>
 * TODO : build an explicit header object
 *
 * @author Samuel Thiriot
 * @author Kevin Chapuis
 *
 */
public abstract class AbstractInputHandler implements IGSSurvey {

	/** The survey complete file. */
	protected final String surveyCompleteFile;

	/** The survey file name. */
	protected final String surveyFileName;

	/** The survey file path. */
	protected final String surveyFilePath;

	/** The data file type. */
	protected final GSSurveyType dataFileType;

	/** The gspu. */
	private final GSPerformanceUtil gspu;

	/** The log level. */
	public static Level LOG_LEVEL = Level.TRACE;

	/**
	 * Instantiates a new abstract input handler.
	 *
	 * @param dataFileType
	 *            the data file type
	 * @param fileName
	 *            the file name
	 */
	public AbstractInputHandler(final GSSurveyType dataFileType, final String fileName) {

		this.dataFileType = dataFileType;
		this.gspu = new GSPerformanceUtil("Read header from survey file");

		this.surveyCompleteFile = fileName;
		this.surveyFileName = Paths.get(fileName).getFileName().toString();
		this.surveyFilePath = Paths.get(fileName).toAbsolutePath().toString();

	}

	/**
	 * Instantiates a new abstract input handler.
	 *
	 * @param dataFileType
	 *            the data file type
	 * @param file
	 *            the file
	 */
	public AbstractInputHandler(final GSSurveyType dataFileType, final File file) {

		this.dataFileType = dataFileType;
		this.gspu = new GSPerformanceUtil("Read header from survey file");

		this.surveyCompleteFile = file.getAbsolutePath();
		this.surveyFileName = file.getName();
		this.surveyFilePath = file.getAbsolutePath();

	}

	/**
	 * The default implementation tries to read the first line, and infer the corresponding values. It is valid as long
	 * as the content includes the title line. Else inherited classes should override it.
	 *
	 * @return returns for each column id the list of attributes values
	 */
	@Override
	public Map<Integer, Set<IValue>>
			getColumnHeaders(final IGenstarDictionary<Attribute<? extends IValue>> dictionary) {

		final Map<Integer, Set<IValue>> columnHeaders = new HashMap<>();

		for (int i = getFirstColumnIndex(); i <= getLastColumnIndex(); i++) {
			final List<String> column = readLines(0, getFirstRowIndex(), i);

			gspu.sysoStempMessage("trying to detect an attribute based on row values: " + column, LOG_LEVEL);

			for (String columnVal : column) {
				Set<IValue> vals = dictionary.getAttributeAndRecord().stream()
						.flatMap(att -> att.getValueSpace().getValues().stream())
						.filter(asp -> asp.getStringValue().equals(columnVal)).collect(Collectors.toSet());
				if (vals.isEmpty()) { continue; }
				if (vals.size() > 1) {
					final Set<IValue> vals2 = new HashSet<>(vals);
					vals = column.stream()
							.flatMap(s -> dictionary.getAttributeAndRecord().stream()
									.filter(att -> att.getAttributeName().equals(s)))
							.flatMap(att -> vals2.stream().filter(v -> v.getValueSpace().getAttribute().equals(att)))
							.collect(Collectors.toSet());
				}
				if (columnHeaders.containsKey(i)) {
					columnHeaders.get(i).addAll(vals);
				} else {
					columnHeaders.put(i, new HashSet<>(vals));
				}
			}
		}
		return columnHeaders;
	}

	/**
	 * Default implementation for tabular data. Override if not suitable for another file format.
	 */
	@Override
	public Map<Integer, Set<IValue>> getRowHeaders(final IGenstarDictionary<Attribute<? extends IValue>> dictionary) {

		final Set<Integer> attributeIdx = new TreeSet<>();
		for (int line = 0; line < getFirstRowIndex(); line++) {
			final List<String> sLine = readLine(line);
			for (int idx = 0; idx < getFirstColumnIndex(); idx++) {
				final String headAtt = sLine.get(idx);

				if (dictionary.containsAttribute(headAtt) || dictionary.containsRecord(headAtt)) {
					// if this attribute (or record) is explicitely defined,
					// we found it.
					attributeIdx.add(idx);
				}

				if (headAtt.isEmpty()) {
					// detect the attribute by finding an attribute which has
					// all of these values as modalities
					final List<String> valList = readColumn(idx);
					gspu.sysoStempMessage("trying to detect an attribute based on header values: " + valList,
							LOG_LEVEL);

					if (dictionary.getAttributes().stream()
							.anyMatch(att -> att.getValueSpace().containsAllLabels(valList))) {
						attributeIdx.add(idx);

					} else {
						gspu.sysoStempMessage("the values " + valList + " match none of our attributes: " + dictionary
								.getAttributes().stream()
								.map(a -> a.getValueSpace().getValues().stream().map(IValue::getStringValue).toList())
								.toList(), Level.WARN);
					}
				}
			}
		}

		final Map<Integer, Set<IValue>> rowHeaders = new HashMap<>();
		for (int i = getFirstRowIndex(); i <= getLastRowIndex(); i++) {
			final List<String> rawLine = readColumns(0, getFirstColumnIndex(), i);
			final List<String> line = attributeIdx.stream().map(idx -> rawLine.get(idx)).toList();
			for (int j = 0; j < line.size(); j++) {
				final String lineVal = line.get(j);
				final Set<IValue> vals = dictionary.getAttributeAndRecord().stream()
						.flatMap(att -> att.getValueSpace().getValues().stream())
						.filter(asp -> asp.getStringValue().equals(lineVal)).collect(Collectors.toSet());
				if (vals.isEmpty()) { continue; }
				if (vals.size() > 1) {
					final Set<IAttribute<? extends IValue>> inferedHeads = new HashSet<>();
					final List<String> headList = readLines(0, getFirstRowIndex(), j);
					if (headList.stream().allMatch(String::isEmpty)) {
						for (final List<String> column : readColumns(0, getFirstColumnIndex())) {
							inferedHeads.addAll(dictionary.getAttributeAndRecord().stream()
									.filter(a -> a.getValueSpace().getValues().stream()
											.allMatch(av -> column.contains(av.getStringValue())))
									.collect(Collectors.toSet()));
						}
					} else {
						inferedHeads.addAll(dictionary.getAttributeAndRecord().stream()
								.flatMap(s -> dictionary.getAttributeAndRecord().stream()
										.filter(a -> a.getAttributeName().equals(s.getAttributeName())))
								.collect(Collectors.toSet()));
					}
					final Set<IValue> vals2 = new HashSet<>(vals);
					for (final IValue val : vals2)
						if (!inferedHeads.contains(val.getValueSpace().getAttribute())) { vals.remove(val); }
				}
				if (rowHeaders.containsKey(i)) {
					rowHeaders.get(i).addAll(vals);
				} else {
					rowHeaders.put(i, new HashSet<>(vals));
				}
			}
		}
		return rowHeaders;
	}

	@Override
	public Map<Integer, Attribute<? extends IValue>>
			getColumnSample(final IGenstarDictionary<Attribute<? extends IValue>> dictionnary) {

		Map<Integer, Attribute<? extends IValue>> columnHeaders = new HashMap<>();

		for (int i = getFirstColumnIndex(); i <= getLastColumnIndex(); i++) {
			List<String> columnAtt = readLines(0, getFirstRowIndex(), i);
			Set<Attribute<? extends IValue>> attSet = dictionnary.getAttributes().stream()
					.filter(att -> columnAtt.stream().anyMatch(s -> att.getAttributeName().equals(s)))
					.collect(Collectors.toSet());
			if (attSet.isEmpty()) { continue; }
			if (attSet.size() > 1) {
				int row = getFirstRowIndex();
				Optional<Attribute<? extends IValue>> opAtt = null;
				do {
					String value = read(row++, i);
					opAtt = attSet.stream().filter(att -> att.getValueSpace().getValues().stream()
							.anyMatch(val -> val.getStringValue().equals(value))).findAny();
				} while (opAtt.isPresent());
				columnHeaders.put(i, opAtt.get());
			} else {
				columnHeaders.put(i, attSet.iterator().next());
			}
		}
		return columnHeaders;
	}

	@Override
	public Map<String, Integer>
			getColumnIdAndWeight(final IGenstarDictionary<Attribute<? extends IValue>> dictionnary) {
		Map<String, Integer> columnHeaders = new HashMap<>();

		List<String> idwgt = new ArrayList<>();
		String id = dictionnary.getIdentifierAttributeName();
		String wgt = dictionnary.getWeightAttributeName();
		if (!id.isEmpty()) { idwgt.add(id); }
		if (!wgt.isEmpty()) { idwgt.add(wgt); }

		if (idwgt.isEmpty()) return columnHeaders;

		for (int i = getFirstColumnIndex(); i <= getLastColumnIndex(); i++) {
			List<String> columnAtt = readLines(0, getFirstRowIndex(), i);
			Optional<String> opIdWgt = idwgt.stream().filter(iw -> columnAtt.stream().anyMatch(iw::equals)).findFirst();
			if (opIdWgt.isPresent()) { columnHeaders.put(opIdWgt.get(), i); }
		}
		return columnHeaders;
	}

	@Override
	public final GSSurveyType getDataFileType() { return this.dataFileType; }

	@Override
	public String getSurveyFilePath() { return surveyFilePath; }

}
