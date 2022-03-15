package gospl.distribution.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;

import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.IAttribute;
import core.metamodel.io.GSSurveyType;
import core.metamodel.value.IValue;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.distribution.matrix.coordinate.GosplCoordinate;

/**
 * 
 * Implement a full n-dimensional matrix (meaning that all dimension are actually connected)
 * 
 * <p>
 * WARNING: the inner data collection is concurrent friendly. This implied a low efficiency when no parallelism
 * <p>
 * 
 * @see INDimensionalMatrix
 * 
 * @author kevinchapuis
 *
 * @param <T>
 */
public abstract class AFullNDimensionalMatrix<T extends Number> implements INDimensionalMatrix<Attribute<? extends IValue>, IValue, T> {

	private GSSurveyType dataType; 

	private final Set<Attribute<? extends IValue>> dimensions;
	protected final Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> matrix;

	private ACoordinate<Attribute<? extends IValue>, IValue> emptyCoordinate = null;

	protected String label = null;
	
	protected List<String> genesis = new LinkedList<>();
	
	// ----------------------- CONSTRUCTORS ----------------------- //

	/**
	 * TODO: javadoc
	 * 
	 * @param dimensionAspectMap
	 * @param metaDataType
	 */
	public AFullNDimensionalMatrix(Set<Attribute<? extends IValue>> dimensions, GSSurveyType metaDataType) {
		this.dimensions = new HashSet<>(dimensions);
		this.matrix = new ConcurrentHashMap<>(dimensions.stream()
				.mapToInt(d -> d.getValueSpace().getValues().size())
				.reduce(1, (ir, dimSize) -> ir * dimSize) / 4);
		this.dataType = metaDataType;
		this.emptyCoordinate = new GosplCoordinate(Collections.emptyMap());
		this.label = dimensions.stream().map(dim -> dim.getAttributeName().length()>3 ? 
				dim.getAttributeName().substring(0, 3) : dim.getAttributeName())
				.collect(Collectors.joining(" x "));
	}
	
	/**
	 * Protected constructor in order for {@link GosplNDimensionalMatrixFactory} to initialize
	 * a n dimensional matrix from the map inner collection structure itself
	 * <p>
	 * WARNING: may not fit to the required structure of {@link AFullNDimensionalMatrix}
	 * 
	 * @param matrix
	 */
	protected AFullNDimensionalMatrix(Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> matrix){
		this.dimensions = matrix.keySet().stream()
				.flatMap(coord -> coord.getDimensions().stream())
				.collect(Collectors.toSet());
		this.matrix = matrix;
	}
	
	// --------------------------------------------------------------- //

	/**
	 * Returns the genesis of the matrix, that is the successive steps that brought it to its 
	 * current state. Useful to expose meaningful error messages to the user.
	 * @return
	 */
	public List<String> getGenesisAsList() {
		return Collections.unmodifiableList(genesis);
	}
		
	/**
	 * Returns the genesis of the matrix, that is the successive steps that brought it to its 
	 * current state. Useful to expose meaningful error messages to the user.
	 * @return
	 */
	public String getGenesisAsString() {
		return String.join("->", genesis);
	}
	
	/**
	 * imports into this matrix the genesis of another one. 
	 * Should be called after creating a matrix to keep a memory of where it comes from.
	 * @param o
	 */
	public void inheritGenesis(AFullNDimensionalMatrix<?> o) {
		genesis.addAll(o.getGenesisAsList());
	}
	
	/**
	 * add one line to the genesis (history) of this matrix. 
	 * This line should better be kept quiet short for readibility.
	 * @param step
	 */
	public void addGenesis(String step) {
		genesis.add(step);
	}
	
	// ------------------------- META DATA ------------------------ //

	@Override
	public boolean isSegmented(){
		return false;
	}

	@Override
	public GSSurveyType getMetaDataType() {
		return dataType;
	}

	public boolean setMetaDataType(GSSurveyType metaDataType) {
		if(dataType == null || !dataType.equals(metaDataType))
			dataType = metaDataType;
		else 
			return false;
		return true;
	}

	/**
	 * Returns a human readable label, or null if undefined.
	 * @return
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * Sets the label which describes the table.
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public int size(){
		return matrix.size();
	}
	
	
	@Override
	public int getDegree() {
		return this.getDimensions().stream()
				.mapToInt(d -> d.getValueSpace().getValues().size() - 1)
			.reduce(1, (i1, i2) -> i1 * i2);
	}
	
	// ---------------------- GLOBAL ACCESSORS ---------------------- //


	@Override
	public final boolean addValue(T value, String... coordinates) {
		return this.addValue(GosplCoordinate.createCoordinate(dimensions, coordinates), value);
	}

	@Override
	public final boolean setValue(T value, String... coordinates) {
		return this.setValue(GosplCoordinate.createCoordinate(dimensions, coordinates), value);
	}

	@Override
	public Set<Attribute<? extends IValue>> getDimensions(){
		return Collections.unmodifiableSet(dimensions);
	}
	
	@Override
	public Map<Attribute<? extends IValue>, Set<? extends IValue>> getDimensionsAsAttributesAndValues() {
		return dimensions.stream().collect(Collectors.toMap(Function.identity(), dim -> dim.getValueSpace().getValues()));
	}

	@Override
	public Attribute<? extends IValue> getDimension(IValue aspect) {
		if(!getDimensions().contains(aspect.getValueSpace().getAttribute()))
			throw new NullPointerException("aspect "+aspect+ " does not fit any known dimension");
		return dimensions.stream().filter(d -> d.getValueSpace().contains(aspect)
					|| d.getEmptyValue().equals(aspect))
				.findFirst().get();
	}

	@Override
	public Set<IValue> getAspects(){
		return dimensions.stream().flatMap(dim -> dim.getValueSpace().getValues().stream()).collect(Collectors.toSet());
	}

	@Override
	public Set<IValue> getAspects(Attribute<? extends IValue> dimension) {
		if(!dimensions.contains(dimension))
			throw new NullPointerException("dimension "+dimension+" is not present in the joint distribution");
		return Collections.unmodifiableSet(dimension.getValueSpace().getValues());
	}
	

	@Override
	public Set<IValue> getValues(String... keyAndVal) throws IllegalArgumentException {

		Set<IValue> coordinateValues = new HashSet<>();
		
		// collect all the attributes and index their names
		Map<String,Attribute<? extends IValue>> name2attribute = getDimensionsAsAttributesAndValues().keySet().stream()
															.collect(Collectors.toMap(Attribute::getAttributeName,Function.identity()));

		if (keyAndVal.length/2 != name2attribute.size()) {
			throw new IllegalArgumentException("you should pass pairs of attribute name and corresponding value, such as attribute 1 name, value for attribute 1, attribute 2 name, value for attribute 2...");
		}
		
		// lookup values
		for (int i=0; i<keyAndVal.length; i=i+2) {
			final String attributeName = keyAndVal[i];
			final String attributeValueStr = keyAndVal[i+1];
			
			Attribute<? extends IValue> attribute = name2attribute.get(attributeName);
			if (attribute == null)
				throw new IllegalArgumentException("unknown attribute "+attributeName);
			coordinateValues.add(attribute.getValueSpace().addValue(attributeValueStr)); // will raise exception if the value is not ok

		}
		
		return coordinateValues;
	}

	@Override
	public Map<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> getMatrix(){
		return Collections.unmodifiableMap(matrix);
	}
	
	@Override
	public LinkedHashMap<ACoordinate<Attribute<? extends IValue>, IValue>, AControl<T>> getOrderedMatrix() {
		return matrix.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> getEmptyCoordinate(){
		return emptyCoordinate;
	}

	///////////////////////////////////////////////////////////////////
	// -------------------------- GETTERS -------------------------- //
	///////////////////////////////////////////////////////////////////

	@Override
	public AControl<T> getVal() {
		AControl<T> result = getNulVal();
		for(AControl<T> control : this.matrix.values())
			getSummedControl(result, control);
		return result;
	}
	
	@Override
	public AControl<T> getVal(ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		
		AControl<T> res = this.matrix.get(coordinate);
		
		if (res == null) {
			if (isCoordinateCompliant(coordinate)) {
				// return the default null value
				return this.getNulVal();
			} else {
				throw new NullPointerException("Coordinate "+coordinate+" is absent from this control table ("+this.hashCode()+")");
			}
		} 
			
		return res;
		 
	}

	@Override
	public AControl<T> getVal(IValue aspect) {
		return this.getVal(aspect, false);
	}
	
	@Override
	public AControl<T> getVal(IValue aspect, boolean defaultToNul){
		if(!matrix.keySet().stream().anyMatch(coord -> coord.contains(aspect)))
			if(defaultToNul)
				return getNulVal();
			else
				throw new NullPointerException("Aspect "+aspect+" is absent from this control table ("+this.hashCode()+")");
		return this.matrix.entrySet().stream()
				.filter(e -> e.getKey().values().contains(aspect))
				.map(Entry::getValue)
				.reduce(getNulVal(), (c1, c2) -> getSummedControl(c1, c2));
	}

	@Override
	public AControl<T> getVal(Collection<IValue> aspects) {
		return getVal(aspects, false);
	}
	
	@Override
	public AControl<T> getVal(Collection<IValue> aspects, boolean defaultToNul) {
		Collection<IValue> correctedAspects = new HashSet<>(aspects);
		if(!aspects.stream().allMatch(a -> matrix.keySet()
				.stream().anyMatch(coord -> coord.contains(a))))
			if (defaultToNul)
				return getNulVal();
			else if(aspects.stream().filter(a -> a.getValueSpace().getEmptyValue().equals(a)
						&& this.getDimensions().contains(a.getValueSpace().getAttribute()))
					.allMatch(a -> matrix.keySet().stream().anyMatch(coord -> coord.contains(a)))) {
				correctedAspects.removeAll(aspects.stream()
						.filter(a -> a.getValueSpace().getEmptyValue().equals(a))
						.collect(Collectors.toSet()));
			} else
				throw new NullPointerException("Aspect collection "+Arrays.toString(aspects.toArray())+" of size "
					+ aspects.size()+" is absent from this matrix"
					+ " (size = "+this.size()+" - attribute = "+Arrays.toString(this.getDimensions().toArray())+")");
		
		Map<IAttribute<? extends IValue>, Set<IValue>> attAsp = correctedAspects.stream()
				.collect(Collectors.groupingBy(aspect -> aspect.getValueSpace().getAttribute(),
				Collectors.mapping(Function.identity(), Collectors.toSet())));
		
		return this.matrix.entrySet().stream().filter(e -> attAsp.values()
						.stream().allMatch(set -> e.getKey().values().stream()
								.anyMatch(a -> set.contains(a))))
				.map(Entry::getValue).reduce(getNulVal(), (c1, c2) -> getSummedControl(c1, c2));
	}
	
	@Override
	public final AControl<T> getVal(String ... coordinates) {
		
		Set<IValue> l = new HashSet<>();
		
		// collect all the attributes and index their names
		Map<String,Attribute<? extends IValue>> name2attribute = dimensions.stream()
															.collect(Collectors.toMap(Attribute::getAttributeName,Function.identity()));

		if (coordinates.length % 2 != 0) {
			throw new IllegalArgumentException("values should be passed in even count, "
					+ "such as attribute 1 name, value for attribute 1, attribute 2 name, value for attribute 2...");
		}
		
		// lookup values
		for (int i=0; i<coordinates.length; i=i+2) {
			
			final String attributeName = coordinates[i];
			final String attributeValueStr = coordinates[i+1];
			
			Attribute<? extends IValue> attribute = name2attribute.get(attributeName);
			if (attribute == null)
				throw new IllegalArgumentException("unknown attribute "+attributeName);
			l.add(attribute.getValueSpace().getValue(attributeValueStr)); // will raise exception if the value is not ok
			
		}
		
		return getVal(l);
	}
	
	@Override
	public final AControl<T> getVal(IValue ... aspects) {
		return getVal(new HashSet<>(Arrays.asList(aspects)));
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	// ----------------------- COORDINATE MANAGEMENT ----------------------- //
	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isCoordinateCompliant(ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		return dimensions.containsAll(coordinate.getDimensions()) && dimensions.size() == coordinate.getDimensions().size();
	}
	
	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>> getCoordinates(Set<IValue> values){
		if(matrix.isEmpty()) {return Collections.emptyList();}
		Map<IAttribute<? extends IValue>, Set<IValue>> attValues = values.stream()
				.filter(val -> this.getDimensions().contains(val.getValueSpace().getAttribute()))
				.collect(Collectors.groupingBy(value -> value.getValueSpace().getAttribute(),
				Collectors.mapping(Function.identity(), Collectors.toSet())));
		return this.matrix.keySet().stream().filter(coord -> attValues.values()
				.stream().allMatch(attVals -> attVals.stream().anyMatch(val -> coord.contains(val))))
				.collect(Collectors.toList());
	}
	
	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>> getOrCreateCoordinates(Set<IValue> values) {
		Collection<ACoordinate<Attribute<? extends IValue>, IValue>> res = this.getCoordinates(values);
		if(res.isEmpty()) {
			res = new HashSet<>();
			Map<IAttribute<? extends IValue>, Set<IValue>> attValues = values.stream()
					.filter(val -> this.getDimensions().contains(val.getValueSpace().getAttribute()))
					.collect(Collectors.groupingBy(value -> value.getValueSpace().getAttribute(),
					Collectors.mapping(Function.identity(), Collectors.toSet())));
			for (List<IValue> coord : Sets.cartesianProduct(attValues.values().stream().collect(Collectors.toList()))) {
				Map<Attribute<? extends IValue>, IValue> mapCoord = coord.stream().collect(Collectors.toMap(
								v -> (Attribute<? extends IValue>) v.getValueSpace().getAttribute(), 
								Function.identity())); 
				res.add(new GosplCoordinate(mapCoord));
			}
		}
		return res;
	}

	@Override
	public Attribute<? extends IValue> getDimension(String name) throws IllegalArgumentException {
		
		for (Attribute<? extends IValue> a: dimensions)
			if (a.getAttributeName().equals(name))
				return a;

		throw new IllegalArgumentException(
				"unknown dimension "+name+"; available dimensions are "+
				dimensions.stream().map(d -> d.getAttributeName()).reduce("", (u,t)->u+","+t)
				);
	}


	@Override
	public Collection<ACoordinate<Attribute<? extends IValue>, IValue>> getCoordinates(String... keyAndVal)
			throws IllegalArgumentException {

		return getCoordinates(getValues(keyAndVal));
	}
	
	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> getCoordinate(Set<IValue> values)
			throws NullPointerException {
		List<ACoordinate<Attribute<? extends IValue>, IValue>> coords = this.matrix.keySet().stream()
				.filter(c -> c.containsAll(values) && c.size() == values.size()).collect(Collectors.toList());
		if(coords.size() == 1)
			return coords.get(0);
		throw new NullPointerException("Trying to access coordinate with values "+Arrays.toString(values.toArray())
			+" but find "+coords.size()+" associated coordinate(s) in the matrix "+this.getLabel());
	}
	

	@Override
	public ACoordinate<Attribute<? extends IValue>, IValue> getCoordinate(String... keyAndVal)
			throws IllegalArgumentException {
		
		Collection<ACoordinate<Attribute<? extends IValue>, IValue>> s = getCoordinates(keyAndVal);
				
		if (s.size() > 1) 
			throw new IllegalArgumentException("these coordinates do not map to a single cell of the matrix");
		
		if (s.isEmpty()) 
			throw new IllegalArgumentException("these coordinates do not map to any cell in the matrix");

		
		return s.iterator().next();
	}

	/*
	 * (non-Javadoc)
	 * @see gospl.distribution.matrix.INDimensionalMatrix#getEmptyReferentCorrelate(java.util.Set)
	 * 
	 * TODO: turn values to coordinate to unsure one value per dimension
	 */
	@Override
	public Set<IValue> getEmptyReferentCorrelate(ACoordinate<Attribute<? extends IValue>, IValue> coordinate){
		// Only focus on values of mapped attribute
		Map<Attribute<? extends IValue>, IValue> dimRef = this.getDimensions()
				.stream().filter(dim -> !dim.getReferentAttribute().equals(dim) 
						&& coordinate.getDimensions().contains(dim.getReferentAttribute()))
				.collect(Collectors.toMap(Function.identity(), 
						dim -> coordinate.getMap().get(dim.getReferentAttribute())));
		
		if(dimRef.isEmpty())
			return Collections.emptySet();
		Set<IValue> emptyReferentValue = dimRef.entrySet().stream()
				.flatMap(e -> e.getKey().findMappedAttributeValues(e.getValue()).stream())
				.filter(val -> val.getValueSpace().getEmptyValue().equals(val))
				.collect(Collectors.toSet());
		if(emptyReferentValue.isEmpty())
			return Collections.emptySet();
		return this.getDimensions().stream().filter(dim -> emptyReferentValue
				.stream().noneMatch(val -> val.getValueSpace().getAttribute().equals(dim)))
				.map(dim -> dim.getEmptyValue()).collect(Collectors.toSet());
	}

	// -------------------------- UTILITY -------------------------- //
	
	@Override
	public String toString(){
		int theoreticalSpaceSize = this.getDimensions().stream().mapToInt(d -> d.getValueSpace().getValues().size()).reduce(1, (i1, i2) -> i1 * i2);
		StringBuffer sb = new StringBuffer();
		sb.append("-- Matrix: ").append(dimensions.size()).append(" dimensions and ").append(dimensions.stream()
				.mapToInt(dim -> dim.getValueSpace().getValues().size()).sum())
					.append(" aspects (theoretical size:").append(theoreticalSpaceSize).append(")--\n");
		AControl<T> empty = getNulVal();
		for(Attribute<? extends IValue> dimension : dimensions){
			sb.append(" -- dimension: ").append(dimension.getAttributeName());
			sb.append(" with ").append(dimension.getValueSpace().getValues().size()).append(" aspects -- \n");
			for(IValue aspect : dimension.getValueSpace().getValues()) {
				AControl<T> value = null;
				try {
					value = getVal(aspect);
				} catch (NullPointerException e) {
					//e.printStackTrace();
					value = empty;
				}
				sb.append("| ").append(aspect).append(": ").append(value).append("\n");
			}
		}
		sb.append(" ----------------------------------- \n");
		return sb.toString();
	}

	@Override
	public String toCsv(char csvSeparator) {
		List<Attribute<? extends IValue>> atts = new ArrayList<>(getDimensions());
		AControl<T> emptyVal = getNulVal();
		String csv = "";
		for(Attribute<? extends IValue> att :atts){
			if(!csv.isEmpty())
				csv += csvSeparator;
			csv+=att.getAttributeName();
		}
		csv += csvSeparator+"value\n";
		for(ACoordinate<Attribute<? extends IValue>, IValue> coordVal : matrix.keySet()){
			String csvLine = "";
			for(Attribute<? extends IValue> att :atts){
				if(!csvLine.isEmpty())
					csvLine += csvSeparator;
				if(!coordVal.values()
						.stream().anyMatch(asp -> asp.getValueSpace().getAttribute().equals(att)))
					csvLine += " ";
				else {
					String val = coordVal.values()
							.stream().filter(asp -> asp.getValueSpace().getAttribute().equals(att))
							.findFirst().get().getStringValue();
					if(val.isEmpty())
						val = "empty value";
					csvLine += val;
				}
			}
			try {
				csv += csvLine+csvSeparator+getVal(coordVal).getValue()+"\n";
			} catch (NullPointerException e) {
				e.printStackTrace();
				csv += csvLine+csvSeparator+emptyVal+"\n";
			}
		}
		return csv;
	}
	
	public String toMatrixCsv(char csvSeparator) {
		List<Attribute<? extends IValue>> atts = new ArrayList<>(getDimensions());
		
		List<Attribute<? extends IValue>> xAtts = atts.subList(0, (atts.size()%2==0?atts.size()/2:atts.size()/2+1)+1);
		List<List<IValue>> xVals = new ArrayList<>();
		for(Attribute<? extends IValue> a : xAtts) { xVals.add(new ArrayList<>(a.getValueSpace().getValues())); }
		List<List<IValue>> xValCoord = Lists.cartesianProduct(xVals);
		
		List<Attribute<? extends IValue>> yAtts = atts.subList((atts.size()%2==0?atts.size()/2:atts.size()/2+1)+1,atts.size()+1);
		List<List<IValue>> yVals = new ArrayList<>();
		for(Attribute<? extends IValue> a : yAtts) { yVals.add(new ArrayList<>(a.getValueSpace().getValues())); }
		List<List<IValue>> yValCoord = Lists.cartesianProduct(yVals);
		
		String csv = "";
		
		// TODO : build header
		for(int i = 0; i < xValCoord.size(); i++){ 
			csv += yValCoord.stream().map(a->"").collect(Collectors.joining(String.valueOf(csvSeparator)));
			final int index = i;
			csv += String.valueOf(csvSeparator)
					+xValCoord.stream().map(column -> column.get(index).getStringValue())
						.collect(Collectors.joining(String.valueOf(csvSeparator)));
			csv += "\n";
		}
		
		// TODO : for each line, build row header
		for(List<IValue> yCoord : yValCoord) {
			csv += yCoord.stream().map(IValue::getStringValue).collect(Collectors.joining(String.valueOf(csvSeparator)));
			for(List<IValue> xCoord : xValCoord) {
				csv += String.valueOf(csvSeparator)
						+this.getVal(Streams.concat(xCoord.stream(),yCoord.stream()).collect(Collectors.toSet()),true).getValue();
			}
			csv += "\n";
		}
		
		return csv;
	}

	@Override
	public boolean checkAllCoordinatesHaveValues() {
		
		return matrix.size() == dimensions.stream().mapToInt(dim -> dim.getValueSpace().getValues().size()).reduce(1, (a, b) -> a * b);

	}
	
	@Override
	public boolean checkGlobalSum() {
		
		switch (dataType) {
			
			case GlobalFrequencyTable:
				return Math.abs(getVal().getValue().doubleValue() - 1d) < Math.pow(10, -4);
			case LocalFrequencyTable:
				return true;
			case Sample:
				throw new IllegalStateException("This matrix cannot be of type "+dataType);
			case ContingencyTable:
				return true;
			default:
				throw new IllegalStateException("unknown state "+dataType);
			
		}
		
	}
	
	private AControl<T> getSummedControl(AControl<T> controlOne, AControl<T> controlTwo){
		return controlOne.add(controlTwo);
	}

}
