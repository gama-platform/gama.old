/*******************************************************************************************************
 *
 * GenStarGamaSurveyUtils.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.csv.CsvReader;
import msi.gama.util.file.csv.CsvReader.Stats;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.STRINGS;

/**
 * The Class GenStarGamaSurveyUtils.
 */
/*
 * Encapsulation (more a copy/past) of CsvReader utilities to explore csv files
 */
public class GenStarGamaSurveyUtils {

	/**
	 * Instantiates a new gen star gama survey utils.
	 *
	 * @param scope
	 *            the scope
	 * @param survey
	 *            the survey
	 * @param atts
	 *            the atts
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	public GenStarGamaSurveyUtils(final IScope scope, final GamaCSVFile survey,
			final List<Attribute<? extends IValue>> atts) {
		this.path = Paths.get(FileUtils.constructAbsoluteFilePath(scope, survey.getPath(scope), false));
		this.stats = CsvReader.getStats(this.path.toString(), null);
		this.atts = atts;
	}

	/** The stats. */
	private final Stats stats;

	/** The path. */
	private final Path path;

	/** The atts. */
	private final List<Attribute<? extends IValue>> atts;

	/** The row header number. */
	// All is here
	private final int[] rowHeaderNumber = { -1, -1 };

	/** The column header number. */
	private final int[] columnHeaderNumber = { -1, -1 };

	/** The infered type. */
	@SuppressWarnings ("rawtypes") private IType inferedType = null;

	/** The total. */
	private Double total = null;

	/**
	 * find the number of row headers
	 *
	 * @return
	 */
	public int inferRowHeaders() {
		if (rowHeaderNumber[0] == -1) {

			int first = rowHeaderNumber[0];
			int headerLength = 0;
			try (CsvReader reader = new CsvReader(path.toString(), stats.delimiter)) {
				boolean isData = false;

				do {
					if (reader.readRecord()) {
						List<String> vals =
								Arrays.asList(reader.getValues()).stream().filter(s -> !s.isBlank()).toList();
						Optional<Attribute<? extends IValue>> oa = atts.stream().filter(a -> a.getValueSpace()
								.getValues().stream().allMatch(v -> vals.contains(v.getStringValue()))).findFirst();
						if (oa.isPresent()) {
							headerLength = oa.get().getValueSpace().getValues().size();
						} else {
							isData = true;
						}
					}
					first += 1;
				} while (!isData);

			} catch (final IOException e) {

			}
			rowHeaderNumber[0] = first;
			columnHeaderNumber[1] = headerLength;
		}

		return rowHeaderNumber[0];
	}

	/**
	 * find the number of column headers
	 *
	 * @return
	 */
	public int inferColumnHeaders() {
		if (columnHeaderNumber[0] == -1) {
			int first = 0;
			int columnLength = 0;
			try (CsvReader reader = new CsvReader(path.toString(), stats.delimiter)) {

				int idx = inferRowHeaders();
				do { reader.skipLine(); } while (--idx > 0);

				boolean isData = false;
				if (reader.readRecord()) {
					List<String> vals = Arrays.asList(reader.getValues()).stream().filter(s -> !s.isBlank()).toList();
					int tmp = 0;
					do {
						String current = vals.get(tmp++);
						Optional<Attribute<? extends IValue>> oa =
								atts.stream().filter(a -> a.getValueSpace().contains(current)).findFirst();
						if (oa.isPresent()) {
							columnLength = oa.get().getValueSpace().getValues().size() > columnLength
									? oa.get().getValueSpace().getValues().size() : columnLength;
						} else {
							isData = true;
							first = vals.indexOf(current);
						}
					} while (!isData);

				}

			} catch (final IOException e) {}
			columnHeaderNumber[0] = first;
			rowHeaderNumber[1] = columnLength;
		}
		return columnHeaderNumber[0];
	}

	/**
	 * Infer the type of data contains in the csv file
	 *
	 * @return
	 */
	@SuppressWarnings ("rawtypes")
	public IType inferDataType() {
		if (inferedType != null) return inferedType;
		inferedType = Types.NO_TYPE;
		try (CsvReader reader = new CsvReader(path.toString(), stats.delimiter)) {
			int idx = inferRowHeaders();
			do { reader.skipLine(); } while (--idx > 0);

			if (reader.readRecord()) {
				String[] firstLineData = reader.getValues();
				inferedType = processRecordType(
						Arrays.copyOfRange(firstLineData, inferColumnHeaders(), firstLineData.length));
			}

		} catch (final IOException e) {}
		return inferedType;
	}

	/**
	 * Compute the sum of data contained in the csv file: if there is non numerical data, then
	 *
	 * @return
	 */
	public Double getTotalData() {
		if (total != null) return total;
		if (!inferedType.isNumber()) { total = -1d; }
		try (CsvReader reader = new CsvReader(path.toString(), stats.delimiter)) {
			int[] min = { inferRowHeaders(), inferColumnHeaders() };
			int[] max = { columnHeaderNumber[0] + columnHeaderNumber[1], rowHeaderNumber[0] + rowHeaderNumber[1] };
			DEBUG.OUT("Data matrix is" + STRINGS.TO_STRING(min) + STRINGS.TO_STRING(max));
			int idx = min[0];
			do { reader.skipLine(); } while (--idx > 0);

			int ldx = max[1];
			while (ldx-- > 0) {
				if (reader.readRecord()) {
					String[] lineData = Arrays.copyOfRange(reader.getValues(), min[1], max[0]);
					for (String element : lineData) { total += Double.valueOf(element); }
				}
			}

			// System.out.println("Total value is " + total);

		} catch (final IOException e) {}
		return total;
	}

	/**
	 * The Class StringAnalysis.
	 */
	private static class StringAnalysis {

		/** The is float. */
		boolean isFloat = true;

		/** The is int. */
		boolean isInt = true;

		/**
		 * Instantiates a new string analysis.
		 *
		 * @param s
		 *            the s
		 */
		StringAnalysis(final String s) {

			for (final char c : s.toCharArray()) {
				final boolean isDigit = Character.isDigit(c);
				if (!isDigit) {
					if (c == '.') {
						isInt = false;
					} else if (Character.isLetter(c)) {
						isInt = false;
						isFloat = false;
						break;
					} else if (c == ',' || c == ';' || c == '|' || c == ':' || c == '/' || Character.isWhitespace(c)) {
						isInt = false;
						isFloat = false;
					}
				}
			}
			if (isInt && isFloat) { isFloat = false; }
		}

	}

	/**
	 * Process record type.
	 *
	 * @param values
	 *            the values
	 * @return the i type
	 */
	@SuppressWarnings ("rawtypes")
	protected IType processRecordType(final String[] values) {
		IType temp = null;
		for (final String s : values) {
			final StringAnalysis sa = new StringAnalysis(s);
			if (sa.isInt) {
				if (temp == null) { temp = Types.INT; }
			} else if (sa.isFloat) {
				if (temp == null || temp == Types.INT) { temp = Types.FLOAT; }
			} else {
				temp = Types.NO_TYPE;
			}

		}
		// in case nothing has been read (i.e. empty file)
		if (temp == null) { temp = Types.NO_TYPE; }
		return temp;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public Path getPath() { return path; }

	/**
	 * Gets the delimiter.
	 *
	 * @return the delimiter
	 */
	public Character getDelimiter() { return stats.delimiter; }

}
