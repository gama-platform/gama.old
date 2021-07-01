package espacedev.gaml.extensions.genstar.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.csv.CsvReader;
import msi.gama.util.file.csv.CsvReader.Stats;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/*
 * Encapsulation (more a copy/past) of CsvReader utilities to explore csv files 
 */
public class GenStarGamaSurveyUtils {

	public GenStarGamaSurveyUtils(IScope scope, GamaCSVFile survey, List<Attribute<? extends IValue>> atts) throws FileNotFoundException {
		this.path =  Paths.get(FileUtils.constructAbsoluteFilePath(scope, survey.getPath(scope), false));
		this.stats =  CsvReader.getStats(this.path.toString(),null);
		this.atts = atts;
	}
	
	private final Stats stats;
	private final Path path;
	private final List<Attribute<? extends IValue>> atts;
	
	// All is here
	private int[] rowHeaderNumber = new int[] {-1,-1};
	private int[] columnHeaderNumber = new int[] {-1,-1};
	@SuppressWarnings("rawtypes")
	private IType inferedType = null;
	private Double total = null;
	
	/**
	 * TODO : find the number of row headers
	 * @return
	 */
	public int inferRowHeaders() {
		if (rowHeaderNumber[0]==-1) {
			
			CsvReader reader = null;
			int first = rowHeaderNumber[0];
			int headerLength = 0;
			try {
				reader = new CsvReader(path.toString(),stats.delimiter);
				boolean isData = false;
				
				do { 
					if (reader.readRecord()) {
						List<String> vals = Arrays.asList(reader.getValues()).stream()
								.filter(s -> !s.isBlank()).collect(Collectors.toList());
						Optional<Attribute<? extends IValue>> oa = atts.stream()
								.filter(a -> a.getValueSpace().getValues().stream()
										.allMatch(v -> vals.contains(v.getStringValue())))
								.findFirst();
						if (oa.isPresent()) { headerLength = oa.get().getValueSpace().getValues().size(); } else { isData = true; } 
					}
					first += 1;
				} while (!isData);
				
				 
			} catch (final IOException e) {}
			reader.close();
			rowHeaderNumber[0] = first;
			columnHeaderNumber[1] = headerLength;
		}
		
		return rowHeaderNumber[0];
	}
	
	/**
	 * TODO : find the number of column headers
	 * @return
	 */
	public int inferColumnHeaders() { 
		if (columnHeaderNumber[0]==-1) {
			CsvReader reader = null;
			int first = 0;
			int columnLength = 0;
			try {
				reader = new CsvReader(path.toString(),stats.delimiter);
				
				int idx = inferRowHeaders();
				do { reader.skipLine(); } while (--idx>0);
				
				boolean isData = false;
				if (reader.readRecord()) {
					List<String> vals = Arrays.asList(reader.getValues()).stream()
							.filter(s -> !s.isBlank()).collect(Collectors.toList());
					int tmp = 0;
					do { 
						String current = vals.get(tmp++);
						Optional<Attribute<? extends IValue>> oa = atts.stream()
								.filter(a -> a.getValueSpace().contains(current))
								.findFirst();
						if (oa.isPresent()) { 
							columnLength = oa.get().getValueSpace().getValues().size() > columnLength ? 
								oa.get().getValueSpace().getValues().size() : columnLength;
						} else {
							isData = true;
							first = vals.indexOf(current);
						}
					}
					while (!isData);
					 
				}
				 
			} catch (final IOException e) {}
			reader.close();
			columnHeaderNumber[0] = first;
			rowHeaderNumber[1] = columnLength;
			
		}
		return columnHeaderNumber[0];
	}
	
	/**
	 * Infer the type of data contains in the csv file
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public IType inferDataType() {
		if (inferedType!=null) { return inferedType; }
		inferedType = Types.NO_TYPE;
		CsvReader reader = null;
		try {
			
			reader = new CsvReader(path.toString(),stats.delimiter);
			int idx = inferRowHeaders();
			do { reader.skipLine(); } while (--idx>0);
		
			if(reader.readRecord()) { 
				String[] firstLineData = reader.getValues();
				inferedType = processRecordType(Arrays.copyOfRange(firstLineData,inferColumnHeaders(),firstLineData.length));
			}
			 
		} catch (final IOException e) {}
		reader.close();
		return inferedType;
	}
	
	/**
	 * Compute the sum of data contained in the csv file: if there is non numerical data, then 
	 * @return
	 */
	public Double getTotalData() {
		if (total!=null) {return total;}
		if (!inferedType.isNumber()) {total=-1d;}
		CsvReader reader = null;
		try {
			reader = new CsvReader(path.toString(),stats.delimiter);
			int[] min = new int[] {inferRowHeaders(),inferColumnHeaders()};
			int[] max = new int[] {columnHeaderNumber[0]+columnHeaderNumber[1],rowHeaderNumber[0]+rowHeaderNumber[1]};
			System.out.println("Data matrix is"+min+max);
			int idx = min[0];
			do { reader.skipLine(); } while (--idx>0);
			
			int ldx = max[1];
			while (ldx-- > 0) {
				if(reader.readRecord()) { 
					String[] lineData = Arrays.copyOfRange(reader.getValues(),min[1],max[0]);
					for (int i = 0; i < lineData.length; i++) {total += Double.valueOf(lineData[i]);}
				}
			}
			
			System.out.println("Total value is "+total);
			
		} catch (final IOException e) {}
		reader.close();
		return total;
	}
	
	private class StringAnalysis {

		boolean isFloat = true;
		boolean isInt = true;

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
					} else if (c == ',' || c == ';' || c == '|' || c == ':' || c == '/'
							|| Character.isWhitespace(c)) {
						isInt = false;
						isFloat = false;
					}
				}
			}
			if (isInt && isFloat) {
				isFloat = false;
			}
		}

	}
	
	@SuppressWarnings("rawtypes")
	protected IType processRecordType(final String[] values) {
		IType temp = null;
		for (final String s : values) {
			final StringAnalysis sa = new StringAnalysis(s);
			if (sa.isInt) {
				if (temp == null) {
					temp = Types.INT;
				}
			} else if (sa.isFloat) {
				if (temp == null || temp == Types.INT) {
					temp = Types.FLOAT;
				}
			} else {
				temp = Types.NO_TYPE;
			}

		}
		// in case nothing has been read (i.e. empty file)
		if (temp == null) {
			temp = Types.NO_TYPE;
		}
		return temp;
	}
	
	public Path getPath() { return path; }
	public Character getDelimiter() { return stats.delimiter; }
	
}
