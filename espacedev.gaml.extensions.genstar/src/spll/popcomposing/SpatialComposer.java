/*******************************************************************************************************
 *
 * SpatialComposer.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package spll.popcomposing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LRUMap;
import org.locationtech.jts.geom.Geometry;

import core.metamodel.IMultitypePopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.io.IGSGeofile.GeoGSFileType;
import core.metamodel.value.IValue;
import core.util.exception.GenstarException;
import gospl.GosplMultitypePopulation;
import spll.SpllEntity;
import spll.SpllPopulation;
import spll.localizer.constraint.ISpatialConstraint;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class SpatialComposer.
 */
public class SpatialComposer implements ISpatialComposer<SpllEntity> {

	/** The constraints. */
	protected List<ISpatialConstraint> constraints = new LinkedList<>();

	/** The population of parent candidates. */
	protected SpllPopulation populationOfParentCandidates;
	
	/** The parent type. */
	protected String parentType;

	/** The population of children candidates. */
	protected SpllPopulation populationOfChildrenCandidates;
	
	/** The children type. */
	protected String childrenType;

	/** The count empty parents. */
	protected Integer countEmptyParents = null;
	
	/** The count orphan children. */
	protected Integer countOrphanChildren = null;

	/** The result population. */
	protected GosplMultitypePopulation<SpllEntity> resultPopulation = null;

	/** The max buffer. */
	protected int maxBuffer = 300;

	/**
	 * Keeps in memory the link between an entity in the original population used as input and the corresponding agent
	 * in the output.
	 *
	 */
	protected Map<AGeoEntity<IValue>, SpllEntity> feature2ParentEntity = new HashMap<>();
	
	/** The feature 2 child entity. */
	protected Map<AGeoEntity<IValue>, SpllEntity> feature2ChildEntity = new HashMap<>();

	/**
	 * Instantiates a new spatial composer.
	 */
	public SpatialComposer() {

		// start with an empty population
		resultPopulation = new GosplMultitypePopulation<>();
	}

	@Override
	public void setPopulationOfParentCandidates(final SpllPopulation populationOfParentCandidates,
			final String parentType) {

		assert parentType != null;

		this.parentType = parentType;
		this.populationOfParentCandidates = populationOfParentCandidates;

		// gives the mapping between the SPLentities before and after clone.
		Map<SpllEntity, SpllEntity> mappingBetweenEntities = new HashMap<>();

		this.resultPopulation.addAll(parentType,
				// clones the population
				new SpllPopulation(populationOfParentCandidates, mappingBetweenEntities));

		// based on the cloning mapping, we keep in memory the link between the original
		// spatial file and the novel entities
		Map<AGeoEntity<IValue>, SpllEntity> geo2orig = this.populationOfParentCandidates.getFeatureToEntityMapping();
		for (AGeoEntity<IValue> geo : geo2orig.keySet()) {
			feature2ParentEntity.put(geo, mappingBetweenEntities.get(geo2orig.get(geo)));
		}

		updateCountOfEmptyParentCandidates();
	}

	@Override
	public SpllPopulation getPopulationOfParentCandidates() { return populationOfParentCandidates; }

	@Override
	public void setPopulationOfChildrenCandidates(final SpllPopulation populationOfChildrenCandidates,
			final String childrenType) {
		this.childrenType = childrenType;
		this.populationOfChildrenCandidates = populationOfChildrenCandidates;

		// gives the mapping between the SPLentities before and after clone.
		Map<SpllEntity, SpllEntity> mappingBetweenEntities = new HashMap<>();

		this.resultPopulation.addAll(childrenType,
				// clones the population
				new SpllPopulation(populationOfChildrenCandidates, mappingBetweenEntities));

		// based on the cloning mapping, we keep in memory the link between the original
		// spatial file and the novel entities
		Map<AGeoEntity<IValue>, SpllEntity> geo2orig = this.populationOfChildrenCandidates.getFeatureToEntityMapping();
		for (AGeoEntity<IValue> geo : geo2orig.keySet()) {
			feature2ChildEntity.put(geo, mappingBetweenEntities.get(geo2orig.get(geo)));
		}

		updateCountOfOrphanChildrenCandidates();
	}

	@Override
	public SpllPopulation getPopulationOfChildrenCandidates() { return this.populationOfChildrenCandidates; }

	@Override
	public void matchParentsAndChildren() {

		final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> map =
				populationOfChildrenCandidates.getGeography();

		// check inputs
		if (!GeoGSFileType.VECTOR.equals(map.getGeoGSFileType()))
			throw new GenstarException("This algorithm is only qualified to run on Vector shapefiles");

		if (populationOfChildrenCandidates == null || populationOfParentCandidates == null)
			throw new NullPointerException(
					"please first define populations of candidates " + "for parents and children");

		int countParentNotKnown = 0;
		int countChildrenNotKnown = 0;

		// first pass: if a children is really located above a parent, just create a link between them
		DEBUG.LOG("================================================================\n"
				+ "first step: iterate all the parents and associate the contained children\n"
				+ "================================================================");

		Iterator<? extends AGeoEntity<? extends IValue>> itParent =
				populationOfParentCandidates.getGeography().getGeoEntityIterator();
		while (itParent.hasNext()) {

			AGeoEntity<? extends IValue> parentGeom = itParent.next();

			SpllEntity parentEntity = feature2ParentEntity.get(parentGeom);
			if (parentEntity == null) {
				countParentNotKnown++;
				DEBUG.LOG("parent not found for geom " + parentGeom.toString());
				continue;
			}

			DEBUG.LOG("searching for direct children for parent: {} -- {}" + parentEntity.getEntityId() + ""
					+ parentEntity.toString());

			Iterator<? extends AGeoEntity<? extends IValue>> itChildren =
					populationOfChildrenCandidates.getGeography().getGeoEntityIteratorWithin(parentGeom.getGeometry());

			while (itChildren.hasNext()) {

				AGeoEntity<? extends IValue> childGeom = itChildren.next();

				SpllEntity childEntity = feature2ChildEntity.get(childGeom);
				if (childEntity == null) {
					countChildrenNotKnown++;
					DEBUG.LOG("* child of this geom was not found and will be ignored: {}" + childGeom.toString());
					continue;
				}
				DEBUG.LOG("* found child entity: {} -- {}" + childEntity.getEntityId() + "" + childEntity.toString());

				childEntity.setParent(parentEntity);
				parentEntity.addChild(childEntity);

			}

		}

		DEBUG.LOG("parent geometries without entity counterpart: {}" + countParentNotKnown);
		DEBUG.LOG("children geometries without entitiy counterpart: {}" + countChildrenNotKnown);

		updateCountsOfOrphanEntities();

		DEBUG.LOG("================================================================\n"
				+ "second step: iterate all the orphan children and associate the closest parents\n"
				+ "================================================================");
		Iterator<? extends AGeoEntity<? extends IValue>> itChildOrphan =
				populationOfChildrenCandidates.getGeography().getGeoEntityIterator();

		// create a cache which will associate the geometry of a child
		// with the closest parent.
		// the idea is: often we have points which are on the very same location,
		// so the very same (slow) query is going to be computed again and again
		LRUMap<Geometry, SpllEntity> childGeom2closestGeom = new LRUMap<>(1000);

		while (itChildOrphan.hasNext()) {

			AGeoEntity<? extends IValue> childGeom = itChildOrphan.next();

			SpllEntity parentEntity = childGeom2closestGeom.get(childGeom.getGeometry());

			SpllEntity orphanChildEntity = feature2ChildEntity.get(childGeom);
			if (orphanChildEntity == null) {
				DEBUG.LOG("* child of this geom was not found and will be ignored: {}" + childGeom.toString());
				continue;
			}

			if (parentEntity != null) {
				// this geometry is found in cache
				// let's reuse the parent
				DEBUG.LOG("* found parent (cache): {}" + parentEntity.getEntityId());
				orphanChildEntity.setParent(parentEntity);
				parentEntity.addChild(orphanChildEntity);
				continue;
			}

			// we focus only on children which have no parent (orphans)
			if (orphanChildEntity.hasParent()) { continue; }

			DEBUG.LOG("studying orphan child entity: {} -- {}" + orphanChildEntity.getEntityId() + ""
					+ orphanChildEntity.toString());

			Map<AGeoEntity<? extends IValue>, Double> entity2distance = new HashMap<>();
			int stepBuffer = maxBuffer / 5;
			for (int bufferDistance = stepBuffer; bufferDistance <= stepBuffer * 5 + 1; bufferDistance += stepBuffer) {
				// create a buffer around it
				Geometry buffer = childGeom.getGeometry().buffer(bufferDistance);
				Collection<? extends AGeoEntity<? extends IValue>> nearbyParents =
						populationOfParentCandidates.getGeography().getGeoEntityWithin(buffer);

				if (nearbyParents.size() == entity2distance.size()) {
					// we did not retrieve more entities with this buffer size than before;
					// let's try again with a bigger buffer
					DEBUG.LOG("\t[ not found anything with buffer {}]" + bufferDistance);
					continue;
				}

				// compute the distance between the parent and children
				for (AGeoEntity<? extends IValue> nearbyParent : nearbyParents) {
					// do not recompute distance if we know it already
					if (entity2distance.containsKey(nearbyParent)) { continue; }
					// store this distance
					entity2distance.put(nearbyParent, childGeom.getGeometry().distance(nearbyParent.getGeometry()));
				}
				// sort by distance
				List<AGeoEntity<? extends IValue>> sortedEntities = new ArrayList<>(entity2distance.keySet());
				Collections.sort(sortedEntities,
						(o1, o2) -> entity2distance.get(o1).compareTo(entity2distance.get(o2)));

				for (AGeoEntity<? extends IValue> e : sortedEntities) {
					String parentId = null;
					try {
						parentId = populationOfParentCandidates.getEntityForFeature(e).getEntityId();
					} catch (NullPointerException e2) {
						parentId = "?";
					}
					DEBUG.LOG("\t{}:{}" + parentId + "" + entity2distance.get(e));
				}
				// select the closest one
				AGeoEntity<? extends IValue> parent = sortedEntities.get(0);
				parentEntity = feature2ParentEntity.get(parent);
				if (parentEntity == null) {
					countParentNotKnown++;
					continue;
				}
				DEBUG.LOG("* found parent: {}" + parentEntity.getEntityId());

				// keep it in cache
				childGeom2closestGeom.put(childGeom.getGeometry(), parentEntity);

				// associate it
				orphanChildEntity.setParent(parentEntity);
				parentEntity.addChild(orphanChildEntity);

				parentEntity = null;
				break;

			}

			if (!orphanChildEntity.hasParent()) { DEBUG.LOG("not found anything with buffer {}" + maxBuffer); }

		}

		DEBUG.LOG("parent geometries without entity counterpart: {}" + countParentNotKnown);
		DEBUG.LOG("children geometries without entitiy counterpart: {}" + countChildrenNotKnown);

		updateCountsOfOrphanEntities();

	}

	@Override
	public int getCountEmptyParents() {
		if (countEmptyParents == null) throw new NullPointerException(
				"the count of empty parents is null, maybe because there is no population of parents");
		return countEmptyParents.intValue();
	}

	@Override
	public int getCountOrphanChildren() {
		if (countOrphanChildren == null) throw new NullPointerException(
				"the count of orphan children is null, maybe because there is no population of children");
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

	/**
	 * Update counts of orphan entities.
	 */
	protected void updateCountsOfOrphanEntities() {
		updateCountOfEmptyParentCandidates();
		updateCountOfOrphanChildrenCandidates();
	}

	/**
	 * Update count of empty parent candidates.
	 */
	protected void updateCountOfEmptyParentCandidates() {

		if (this.populationOfParentCandidates == null) {
			this.countEmptyParents = null;
		} else {
			int c = 0;
			Iterator<SpllEntity> it = resultPopulation.iterateSubPopulation(this.parentType);
			while (it.hasNext()) {
				SpllEntity e = it.next();
				if (!e.hasChildren()) { c++; }
			}
			this.countEmptyParents = c;
		}
		DEBUG.LOG("count of empty parents is now: {}/{} ~ {}%" + countEmptyParents + ""
				+ populationOfParentCandidates.size() + ""
				+ (double) 100 * countEmptyParents / populationOfParentCandidates.size());

	}

	/**
	 * Update count of orphan children candidates.
	 */
	protected void updateCountOfOrphanChildrenCandidates() {

		if (this.populationOfChildrenCandidates == null) {
			this.countOrphanChildren = null;
		} else {
			int c = 0;
			Iterator<SpllEntity> it = resultPopulation.iterateSubPopulation(this.childrenType);
			while (it.hasNext()) {
				SpllEntity e = it.next();
				if (!e.hasParent()) { c++; }
			}
			this.countOrphanChildren = c;
		}
		DEBUG.LOG("count of empty parents is now: {}/{} ~ {}%" + countOrphanChildren + ""
				+ populationOfChildrenCandidates.size() + ""
				+ (double) countOrphanChildren / populationOfChildrenCandidates.size());

	}

	/**
	 * Gets the max buffer.
	 *
	 * @return the max buffer
	 */
	public int getMaxBuffer() { return this.maxBuffer; }

	/**
	 * Sets the max buffer.
	 *
	 * @param maxBuffer the new max buffer
	 */
	public void setMaxBuffer(final int maxBuffer) { this.maxBuffer = maxBuffer; }

}
