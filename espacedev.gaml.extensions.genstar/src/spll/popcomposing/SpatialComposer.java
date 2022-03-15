package spll.popcomposing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.locationtech.jts.geom.Geometry;

import core.metamodel.IMultitypePopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.io.IGSGeofile.GeoGSFileType;
import core.metamodel.value.IValue;
import gospl.GosplMultitypePopulation;
import spll.SpllEntity;
import spll.SpllPopulation;
import spll.localizer.constraint.ISpatialConstraint;

public class SpatialComposer implements ISpatialComposer<SpllEntity> {

	private Logger logger = LogManager.getLogger(SpatialComposer.class);

	protected List<ISpatialConstraint> constraints = new LinkedList<ISpatialConstraint>();
	
	protected SpllPopulation populationOfParentCandidates;
	protected String parentType;
	
	protected SpllPopulation populationOfChildrenCandidates;
	protected String childrenType;
	
	protected Integer countEmptyParents = null;
	protected Integer countOrphanChildren = null;
	
	protected GosplMultitypePopulation<SpllEntity> resultPopulation = null;
	
	protected int maxBuffer = 300;
	
	/**
	 * Keeps in memory the link between an entity in the original population used as input 
	 * and the corresponding agent in the output. 
	 * 
	 */
	protected Map<AGeoEntity<IValue>,SpllEntity> feature2ParentEntity = new HashMap<>();
	protected Map<AGeoEntity<IValue>,SpllEntity> feature2ChildEntity = new HashMap<>();
	
	
	public SpatialComposer() {
		
		// start with an empty population
		resultPopulation = new GosplMultitypePopulation<>();
	}

	@Override
	public void setPopulationOfParentCandidates(SpllPopulation populationOfParentCandidates, String parentType) {
		
		assert parentType != null;
		
		this.parentType = parentType;
		this.populationOfParentCandidates = populationOfParentCandidates;
				
		// gives the mapping between the SPLentities before and after clone.
		Map<SpllEntity,SpllEntity> mappingBetweenEntities = new HashMap<>();
		
		this.resultPopulation.addAll(
				parentType, 
				// clones the population
				new SpllPopulation(populationOfParentCandidates, mappingBetweenEntities)
				);
		
		// based on the cloning mapping, we keep in memory the link between the original 
		// spatial file and the novel entities
		Map<AGeoEntity<IValue>,SpllEntity> geo2orig = this.populationOfParentCandidates.getFeatureToEntityMapping();
		for (AGeoEntity<IValue> geo: geo2orig.keySet()) {		
			feature2ParentEntity.put(geo, mappingBetweenEntities.get(geo2orig.get(geo)));
		}
		
			
		updateCountOfEmptyParentCandidates();
	}

	@Override
	public SpllPopulation getPopulationOfParentCandidates() {
		return populationOfParentCandidates;
	}

	@Override
	public void setPopulationOfChildrenCandidates(SpllPopulation populationOfChildrenCandidates, String childrenType) {
		this.childrenType = childrenType;
		this.populationOfChildrenCandidates = populationOfChildrenCandidates;
		
		

		// gives the mapping between the SPLentities before and after clone.
		Map<SpllEntity,SpllEntity> mappingBetweenEntities = new HashMap<>();
		
		this.resultPopulation.addAll(
				childrenType, 
				// clones the population
				new SpllPopulation(populationOfChildrenCandidates, mappingBetweenEntities)
				);
		
		// based on the cloning mapping, we keep in memory the link between the original 
		// spatial file and the novel entities
		Map<AGeoEntity<IValue>,SpllEntity> geo2orig = this.populationOfChildrenCandidates.getFeatureToEntityMapping();
		for (AGeoEntity<IValue> geo: geo2orig.keySet()) {		
			feature2ChildEntity.put(geo, mappingBetweenEntities.get(geo2orig.get(geo)));
		}
		
		updateCountOfOrphanChildrenCandidates();
	}

	@Override
	public SpllPopulation getPopulationOfChildrenCandidates() {
		return this.populationOfChildrenCandidates;
	}

	@Override
	public void matchParentsAndChildren() {
		
		final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> map = populationOfChildrenCandidates.getGeography();
		
		// check inputs
		if (!map.getGeoGSFileType().equals(GeoGSFileType.VECTOR))
			throw new RuntimeException("This algorithm is only qualified to run on Vector shapefiles");
		
		if (populationOfChildrenCandidates == null || populationOfParentCandidates == null)
			throw new NullPointerException("please first define populations of candidates "+
											"for parents and children");
		
		int countParentNotKnown = 0;
		int countChildrenNotKnown = 0;
		
		// first pass: if a children is really located above a parent, just create a link between them
		logger.info("================================================================\n"+
					"first step: iterate all the parents and associate the contained children\n"+
					"================================================================");
		
		Iterator<? extends AGeoEntity<? extends IValue>> itParent = 
				populationOfParentCandidates.getGeography().getGeoEntityIterator();
		while (itParent.hasNext()) {
			
			AGeoEntity<? extends IValue> parentGeom = itParent.next();
			
			SpllEntity parentEntity = feature2ParentEntity.get(parentGeom);
			if (parentEntity == null) {
				countParentNotKnown++;
				logger.trace("parent not found for geom "+parentGeom.toString());
				continue;
			}
				
			logger.trace("searching for direct children for parent: {} -- {}"+parentEntity.getEntityId()+""+ parentEntity.toString());
			
			Iterator<? extends AGeoEntity<? extends IValue>> itChildren = 	
				populationOfChildrenCandidates.getGeography().getGeoEntityIteratorWithin(
						parentGeom.getGeometry());
			
			while (itChildren.hasNext()) {
				
				AGeoEntity<? extends IValue> childGeom = itChildren.next();
				
				SpllEntity childEntity = feature2ChildEntity.get(childGeom);
				if (childEntity == null) {
					countChildrenNotKnown++;
					logger.trace("* child of this geom was not found and will be ignored: {}"+ childGeom.toString());
					continue;
				} 
				logger.trace("* found child entity: {} -- {}"+childEntity.getEntityId()+""+childEntity.toString());
				
				childEntity.setParent(parentEntity);
				parentEntity.addChild(childEntity);
				
			}
			
		}
			
		logger.info("parent geometries without entity counterpart: {}"+ countParentNotKnown);
		logger.info("children geometries without entitiy counterpart: {}"+ countChildrenNotKnown);
		
		updateCountsOfOrphanEntities();
			

		logger.info("================================================================\n"+
					"second step: iterate all the orphan children and associate the closest parents\n"+
					"================================================================");
		Iterator<? extends AGeoEntity<? extends IValue>> itChildOrphan = 
				populationOfChildrenCandidates.getGeography().getGeoEntityIterator();	
		
		// create a cache which will associate the geometry of a child 
		// with the closest parent.
		// the idea is: often we have points which are on the very same location, 
		// so the very same (slow) query is going to be computed again and again
		LRUMap<Geometry,SpllEntity> childGeom2closestGeom = new LRUMap<>(1000);
		
		while (itChildOrphan.hasNext()) {
			
			AGeoEntity<? extends IValue> childGeom = itChildOrphan.next();
			
			SpllEntity parentEntity = childGeom2closestGeom.get(childGeom.getGeometry());

			SpllEntity orphanChildEntity = feature2ChildEntity.get(childGeom);
			if (orphanChildEntity == null) {
				logger.trace("* child of this geom was not found and will be ignored: {}"+childGeom.toString());
				continue;
			}
			
			if (parentEntity != null) {
				// this geometry is found in cache 
				// let's reuse the parent 
				logger.trace("* found parent (cache): {}"+parentEntity.getEntityId());
				orphanChildEntity.setParent(parentEntity);
				parentEntity.addChild(orphanChildEntity);
				continue;
			}
			
			// we focus only on children which have no parent (orphans)
			if (orphanChildEntity.hasParent())
				continue;
			
			logger.trace("studying orphan child entity: {} -- {}"+ orphanChildEntity.getEntityId()+""+ orphanChildEntity.toString());
			
			
			Map<AGeoEntity<? extends IValue>,Double> entity2distance = new HashMap<>();
			int stepBuffer = maxBuffer/5;
			for (int bufferDistance = stepBuffer; bufferDistance <= stepBuffer*5+1; bufferDistance += stepBuffer) {
				// create a buffer around it 
				Geometry buffer = childGeom.getGeometry().buffer(bufferDistance);
				Collection<? extends AGeoEntity<? extends IValue>> nearbyParents = 
						populationOfParentCandidates.getGeography().getGeoEntityWithin(buffer);
				
				if (nearbyParents.size() == entity2distance.size()) {
					// we did not retrieve more entities with this buffer size than before; 
					// let's try again with a bigger buffer
					logger.trace("\t[ not found anything with buffer {}]"+ bufferDistance);
					continue;
				}
				
				// compute the distance between the parent and children 
				for (AGeoEntity<? extends IValue> nearbyParent: nearbyParents) {
					// do not recompute distance if we know it already
					if (entity2distance.containsKey(nearbyParent))
						continue;
					// store this distance 
					entity2distance.put(
							nearbyParent, 
							new Double(childGeom.getGeometry().distance(nearbyParent.getGeometry()))
							);
				}
				// sort by distance 
				List<AGeoEntity<? extends IValue>> sortedEntities = new ArrayList<>(entity2distance.keySet());
				Collections.sort(sortedEntities, new Comparator<AGeoEntity<? extends IValue>>() {

					@Override
					public int compare(AGeoEntity<? extends IValue> o1, AGeoEntity<? extends IValue> o2) {
						return entity2distance.get(o1).compareTo(entity2distance.get(o2));
					}
				});
				
				
				for (AGeoEntity<? extends IValue> e: sortedEntities) {
					String parentId = null;
					try {
						parentId = populationOfParentCandidates.getEntityForFeature(e).getEntityId();
					} catch (NullPointerException e2) {
						parentId = "?";
					}
					logger.trace("\t{}:{}"+parentId+""+entity2distance.get(e));
				}
				// select the closest one
				AGeoEntity<? extends IValue> parent = sortedEntities.get(0);
				parentEntity = feature2ParentEntity.get(parent);
				if (parentEntity == null) {
					countParentNotKnown++;
					continue;
				}
				logger.trace("* found parent: {}"+parentEntity.getEntityId());
				
				// keep it in cache 
				childGeom2closestGeom.put(childGeom.getGeometry(), parentEntity);
				
				// associate it 
				orphanChildEntity.setParent(parentEntity);
				parentEntity.addChild(orphanChildEntity);
				
				parentEntity = null;
				break;
				
			}
			
			if (!orphanChildEntity.hasParent())
				logger.trace("not found anything with buffer {}"+ maxBuffer);
		
		}

		logger.info("parent geometries without entity counterpart: {}"+ countParentNotKnown);
		logger.info("children geometries without entitiy counterpart: {}"+ countChildrenNotKnown);
		
		updateCountsOfOrphanEntities();
		
	}
		
		

	@Override
	public int getCountEmptyParents() {
		if (countEmptyParents == null)
			throw new NullPointerException("the count of empty parents is null, maybe because there is no population of parents");
		return countEmptyParents.intValue();
	}

	@Override
	public int getCountOrphanChildren() {
		if (countOrphanChildren == null)
			throw new NullPointerException("the count of orphan children is null, maybe because there is no population of children");
		return countOrphanChildren.intValue();
	}

	@Override
	public IMultitypePopulation<SpllEntity, Attribute<? extends IValue>> getMatchedPopulation() {
		return resultPopulation;
	}
	
	@Override
	public void clearMatchedPopulation() {
		resultPopulation.clear();
		updateCountsOfOrphanEntities();
	}
	

	protected void updateCountsOfOrphanEntities() {
		updateCountOfEmptyParentCandidates();
		updateCountOfOrphanChildrenCandidates();
	}
	
	protected void updateCountOfEmptyParentCandidates() {
		
		if (this.populationOfParentCandidates == null) {
			this.countEmptyParents = null;
		} else {
			int c = 0;
			Iterator<SpllEntity> it = resultPopulation.iterateSubPopulation(this.parentType);
			while (it.hasNext()) {
				SpllEntity e = it.next();
				if (!e.hasChildren())
					c++;
			}
			this.countEmptyParents = c;
		}
		logger.info("count of empty parents is now: {}/{} ~ {}%"+
						countEmptyParents+""+
						populationOfParentCandidates.size()+""+ 
						(double)100*countEmptyParents/populationOfParentCandidates.size());		
		
	}
	

	protected void updateCountOfOrphanChildrenCandidates() {
		
		if (this.populationOfChildrenCandidates == null) {
			this.countOrphanChildren = null;
		} else {
			int c = 0;
			Iterator<SpllEntity> it = resultPopulation.iterateSubPopulation(this.childrenType);
			while (it.hasNext()) {
				SpllEntity e = it.next();
				if (!e.hasParent())
					c++;
			}
			this.countOrphanChildren = c;
		}
		logger.info("count of empty parents is now: {}/{} ~ {}%"+ 
				countOrphanChildren+""+
				populationOfChildrenCandidates.size()+""+ 
				(double)countOrphanChildren/populationOfChildrenCandidates.size());		

	}
	
	public int getMaxBuffer() {
		return this.maxBuffer;
	}
	
	public void setMaxBuffer(int maxBuffer) {
		this.maxBuffer = maxBuffer;
	}

}
