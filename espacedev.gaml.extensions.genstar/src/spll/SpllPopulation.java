package spll;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.entity.EntityUniqueId;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import core.util.exception.GSIllegalRangedData;
import gospl.GosplEntity;
import spll.entity.SpllFeature;
import spll.io.SPLGeofileBuilder;
import spll.io.SPLVectorFile;
import spll.io.exception.InvalidGeoFormatException;
import spll.util.SpllUtil;

/**
 * A population of spatialized entities {@link SpllEntity} 
 * which contain attributes as all the {@link AGeoEntity} do, and is 
 * also linked to a geometry.
 * 
 * @author Kevin Chapuis
 * @author Samuel Thiriot
 */
public class SpllPopulation implements IPopulation<SpllEntity, Attribute<? extends IValue>> {

	private Set<SpllEntity> population;

	/**
	 * The geo file from which the population was generated from (might be null)
	 */
	private IGSGeofile<? extends AGeoEntity<? extends IValue>, IValue> geoFile; 

	private Set<Attribute<? extends IValue>> attributes;
	
	/**
	 * A map which associates each geoentity with its corresponding SpllEntity.
	 * Enables to find back which Entity was created thanks to each Geographic feature
	 * read from a shapefile.
	 */
	protected Map<AGeoEntity<IValue>,SpllEntity> feature2entity = new HashMap<>();
	
	public SpllPopulation(IPopulation<ADemoEntity, Attribute<? extends IValue>> population,
			IGSGeofile<? extends AGeoEntity<? extends IValue>, IValue> geoFile) {
		this.population = population.stream()
				.map(entity -> {
					SpllEntity e = new SpllEntity(entity);
					e._setEntityId(EntityUniqueId.createNextId(this));
					return e;})
				.collect(Collectors.toSet());
		this.attributes = population.getPopulationAttributes();
		this.geoFile = geoFile;
	}

	/**
	 * Creates a SPLL population from a file which will be decoded based on the dictionnary passed as a parameter.
	 * 
	 * @param sfFile
	 * @param dictionnary
	 * @param charset (or null for default)
	 * @param maxEntities
	 * @throws IOException
	 * @throws InvalidGeoFormatException
	 * @throws GSIllegalRangedData 
	 */
	public SpllPopulation(	File sfFile, 
							Collection<Attribute<? extends IValue>> dictionnary, 
							Charset charset, 
							int maxEntities) throws IOException, InvalidGeoFormatException, GSIllegalRangedData {

		this.population=new HashSet<>();
		this.attributes=new HashSet<>(dictionnary);
		
		System.err.println("dict at spll :"+dictionnary);
		System.err.println("max entities:"+maxEntities);

		List<String> attributesNamesToKeep = dictionnary.stream()
													.map(Attribute::getAttributeName)
													.toList();
		SPLVectorFile sf = SPLGeofileBuilder.getShapeFile(sfFile,  attributesNamesToKeep, charset);
		this.geoFile = sf;

		addDataFromVector(sf, dictionnary, maxEntities);
	}
	
	/**
	 * Creates a SPLL population from a shapefile based on the dictionnary passed as parameter. 
	 * Attributes which are not explicitely added are ignored.
	 * @param sf
	 * @param dictionnary
	 * @param the maximum count of entities to read (-1 to ignore)
	 */
	public SpllPopulation(	SPLVectorFile sf, 
							Collection<Attribute<? extends IValue>> dictionnary, 
							int maxEntities) {
		
		this.population=new HashSet<>();
		this.attributes=new HashSet<>(dictionnary);
		this.geoFile = sf;

		addDataFromVector(sf, dictionnary, maxEntities);	
	}


	/**
	 * "Clone" constructor. 
	 * @param population
	 * @param geoFile
	 */
	public SpllPopulation(SpllPopulation pop) {
		this(pop, null);
	}
	
	/**
	 * "Clone" operator which also returns in the map passed as parameter (if not null)
	 * the mapping between each entity before and its clone.
	 * @param populationOfParentCandidates
	 * @param mappingBetweenEntities
	 */
	public SpllPopulation(SpllPopulation pop,
			Map<SpllEntity, SpllEntity> mappingBetweenEntities) {
		
		this.population = new HashSet<>();
		for (SpllEntity original : pop.population) {
			SpllEntity clone = new SpllEntity(original);
			this.population.add(clone);
			if (mappingBetweenEntities != null)
				mappingBetweenEntities.put(original, clone);
		}

		this.attributes = new HashSet<>(pop.getPopulationAttributes());
		this.geoFile = pop.geoFile;
	}
	
	
	/**
	 * Adds 
	 * @param sf
	 * @param dictionnary
	 * @param maxEntities
	 */
	private void addDataFromVector(	SPLVectorFile sf, 
									Collection<Attribute<? extends IValue>> dictionnary, 
									int maxEntities) {

		// index the dictionnary by name
		Map<String,Attribute<? extends IValue>> dictionnaryName2attribute = new HashMap<>(dictionnary.size());
		for (Attribute<? extends IValue> a: dictionnary)
			dictionnaryName2attribute.put(a.getAttributeName(), a);
		//System.out.println("working on attributes: "+dictionnaryName2attribute);

		// will contain the list of all the attributes which were ignored 
		Set<String> ignoredAttributes = new HashSet<>();
		//Map<String,Set<String>> attributeName2ignoredValues = new HashMap<>();
		
		// iterate entities
		Iterator<SpllFeature> itGeoEntity = sf.getGeoEntityIterator();
		int i=0;
		while (itGeoEntity.hasNext()) {
			
			// retrieve the geospatial entity considered
			SpllFeature feature = itGeoEntity.next();
			//System.out.println("working on feature: "+feature.getGenstarName());

			Map<Attribute<? extends IValue>,IValue> attribute2value = new HashMap<>(dictionnary.size());
			
			for (Map.Entry<Attribute<? extends IValue>, IValue> attributeAndValue : 
								feature.getAttributeMap().entrySet()) {
				
				final Attribute<? extends IValue> attribute = attributeAndValue.getKey();
				final IValue value = attributeAndValue.getValue();
				

				// find in the dictionnary the attribute definition with the same name as this geo attribute
				Attribute<? extends IValue> gosplType = dictionnaryName2attribute.get(
						attribute.getAttributeName());
				
				// skip attributes not defined
				if (gosplType == null) {
					ignoredAttributes.add(attribute.getAttributeName());
					continue;
				}
				
				/*System.out.println(	attribute.getAttributeName()
									+"="+
									value.getStringValue());*/
				
				// find the value according to the dictionnary
				if (value.getStringValue().trim().isEmpty()) {
					// this value is empty in the shapefile
					attribute2value.put(gosplType, gosplType.getEmptyValue());
				}
				try {
					IValue valueEncoded = null;
					try {
						valueEncoded = gosplType.getValueSpace().getValue(value.getStringValue());
						attribute2value.put(gosplType, valueEncoded);
					} catch (NullPointerException e) {
						valueEncoded = gosplType.getValueSpace().addValue(value.getStringValue());
						attribute2value.put(gosplType, valueEncoded);
					}
					attribute2value.put(gosplType, valueEncoded);

				} catch (RuntimeException e) {
					System.err.println("error while decoding values: "+e.getMessage());
					e.printStackTrace();
				}
				
			}
			
			// add the resulting entity to this population
			GosplEntity entity = new GosplEntity(attribute2value);
			SpllEntity spllEntity = new SpllEntity(entity);
			add(spllEntity);
			feature2entity.put(feature, spllEntity);
			this.attributes.addAll(entity.getAttributes());
			
			if (maxEntities > 0 && ++i >= maxEntities)
				break;
		}
		
		if (!ignoredAttributes.isEmpty())
			System.err.println("the following attributes were ignored because "+
								"they are not defined in the dictionnary: "+ignoredAttributes);
		
	}
	
	/**
	 * Gives the specific coordinate system this population
	 * have been localized with
	 * 
	 * @return
	 */
	public CoordinateReferenceSystem getCrs(){
		return SpllUtil.getCRSfromWKT(geoFile.getWKTCoordinateReferentSystem());
	}
	
	/**
	 * Gives the geography this population is localized in
	 * 
	 * @return
	 */
	public IGSGeofile<? extends AGeoEntity<? extends IValue>, IValue> getGeography() {
		return geoFile;
	}
	
	@Override
	public Set<Attribute<? extends IValue>> getPopulationAttributes() {
		return Collections.unmodifiableSet(attributes);
	}
	
	// ------------------------------------------- //
	// ----------- COLLECTION CONTRACT ----------- //
	// ------------------------------------------- //
	
	
	@Override
	public int size() {
		return population.size();
	}

	@Override
	public boolean isEmpty() {
		return population.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return population.contains(o);
	}

	@Override
	public Iterator<SpllEntity> iterator() {
		return this.population.iterator();
	}

	@Override
	public Object[] toArray() {
		return population.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return population.toArray(a);
	}

	@Override
	public boolean add(SpllEntity e) {
		if (population.add(e)) {
			e._setEntityId(EntityUniqueId.createNextId(this, e.getEntityType()));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		return population.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return population.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends SpllEntity> c) {
		boolean anyChange = false;
		for (SpllEntity e: c) {
			anyChange = add(e) || anyChange;
		}
		return anyChange;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return population.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return population.retainAll(c);
	}

	@Override
	public void clear() {
		population.clear();
	}
	
	@Override
	public SpllPopulation clone() {
		return new SpllPopulation(this);
	}

	@Override
	public boolean isAllPopulationOfType(String type) {
		for (ADemoEntity e: population) {
			if (type != e.getEntityType() || !type.equals(e.getEntityType()))
				return false;
		}
		return true;
	}

	public SpllEntity getEntityForFeature(AGeoEntity<? extends IValue> feature) {
		return this.feature2entity.get(feature);
	}
	
	/**
	 * Returns the internal mapping between the spatial entity and the corresponding SPLEntity.
	 * @return
	 */
	public Map<AGeoEntity<IValue>,SpllEntity> getFeatureToEntityMapping() {
		return Collections.unmodifiableMap(this.feature2entity);
	}

	@Override
	public Attribute<? extends IValue> getPopulationAttributeNamed(String name) {
		if (attributes == null)
			return null;
		for (Attribute<? extends IValue> a: attributes) {
			if (a.getAttributeName().equals(name))
				return a;
		}
		return null;
	}
	
}
