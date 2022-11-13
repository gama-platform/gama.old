package spll.localizer.linker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.localizer.constraint.ISpatialConstraint;
import spll.localizer.distribution.ISpatialDistribution;

/**
 * General implementation for spatial linker - meant to link entity with a spatial entity
 * using a pre-defined distribution and optional spatial constraints
 * 
 * @author kevinchapuis
 *
 */
public class SPLinker<E extends ADemoEntity> implements ISPLinker<E> {
	
	private ISpatialDistribution<E> distribution;
	private List<ISpatialConstraint> constraints;
	private ConstraintsReleaseRule rule;
	
	public SPLinker(ISpatialDistribution<E> distribution) {
		this.distribution = distribution;
		this.constraints = new ArrayList<>();
		this.rule = ConstraintsReleaseRule.PRIORITY;
	}
	
	public SPLinker(ISpatialDistribution<E> distribution, ConstraintsReleaseRule rule) {
		this(distribution);
		this.rule = rule;
	}

	@Override
	public Optional<AGeoEntity<? extends IValue>> getCandidate(E entity,
			Collection<? extends AGeoEntity<? extends IValue>> candidates) {

		Collection<AGeoEntity<? extends IValue>> filteredCandidates = this.filter(candidates);

		return filteredCandidates.isEmpty() ? Optional.empty() : 
			Optional.ofNullable(distribution.getCandidate(entity, new ArrayList<>(filteredCandidates)));
	}
	
	@Override
	public Map<E, Optional<AGeoEntity<? extends IValue>>> getCandidates(Collection<E> entities,
			Collection<? extends AGeoEntity<? extends IValue>> candidates) {
		
		
		Map<E, Optional<AGeoEntity<? extends IValue>>> res = entities.stream()
				.collect(Collectors.toMap(Function.identity(), e -> this.getCandidate(e,candidates)));
		
		Collection<E> unbindedEntities = new HashSet<>();
		for(E e : res.keySet()) { 
			if(!res.get(e).isPresent()) unbindedEntities.add(e);
			else constraints.forEach(c -> c.updateConstraint(res.get(e).get()));
		}
		
		if(!unbindedEntities.isEmpty()) {
			Collection<? extends AGeoEntity<? extends IValue>> filteredCandidates = null;
			do {
				filteredCandidates = this.filterWithRelease(candidates);
				res.clear();
				for(E e : unbindedEntities) {
					Optional<AGeoEntity<? extends IValue>> oNest = Optional.ofNullable(
							distribution.getCandidate(e, new ArrayList<>(filteredCandidates))); 
					if(oNest.isPresent()) {
						res.put(e, oNest); 
						constraints.stream().forEach(c -> c.updateConstraint(oNest.get()));
					}
				}
				unbindedEntities = res.keySet().stream().filter(e -> !res.get(e).isPresent()).toList();
			} while (!unbindedEntities.isEmpty() || this.constraints.stream().allMatch(ISpatialConstraint::isConstraintLimitReach));
		}
		return res;
	}
	
	@Override
	public void setDistribution(ISpatialDistribution<E> distribution) {
		this.distribution = distribution;
	}

	@Override
	public ISpatialDistribution<E> getDistribution() {
		return distribution;
	}
	
	@Override
	public Collection<AGeoEntity<? extends IValue>> filterWithRelease (
			Collection<? extends AGeoEntity<? extends IValue>> candidates) {
		List<AGeoEntity<? extends IValue>> filteredCandidates = new ArrayList<>(candidates);
		List<ISpatialConstraint> scs = constraints.stream().sorted(
				(c1,c2) -> Integer.compare(c1.getPriority(), c2.getPriority()))
				.toList();
		switch(rule) {
		case LINEAR:
			do {
				List<AGeoEntity<? extends IValue>> newFilteredCandidates = new ArrayList<>(filteredCandidates);
				for(ISpatialConstraint sc : scs.stream()
						.filter(c -> !c.isConstraintLimitReach())
						.toList()) {
					newFilteredCandidates = sc.getCandidates(filteredCandidates);
					if(newFilteredCandidates.isEmpty()) {
						sc.relaxConstraint(newFilteredCandidates);
						newFilteredCandidates = sc.getCandidates(newFilteredCandidates);
					}
				}
				if(!newFilteredCandidates.isEmpty()) {
					return newFilteredCandidates;
				}
			} while(scs.stream().noneMatch(c -> !c.isConstraintLimitReach()));
			return Collections.emptyList();
		default:
			for(ISpatialConstraint sc : scs) {
				List<AGeoEntity<? extends IValue>> newFilteredCandidates = sc.getCandidates(filteredCandidates);
				if(newFilteredCandidates.isEmpty()) {
					do {
						sc.relaxConstraint(filteredCandidates);
						newFilteredCandidates = sc.getCandidates(filteredCandidates);
					} while(!newFilteredCandidates.isEmpty() &&
							!sc.isConstraintLimitReach());
				}
				if(newFilteredCandidates.isEmpty())
					return Collections.emptyList();
				filteredCandidates = newFilteredCandidates;
			}
			return filteredCandidates;
		}
		
	}
	
	@Override
	public Collection<AGeoEntity<? extends IValue>> filter(
			Collection<? extends AGeoEntity<? extends IValue>> candidates) {
		List<AGeoEntity<? extends IValue>> filteredCandidates = new ArrayList<>(candidates);
		List<ISpatialConstraint> scs = constraints.stream().sorted(
				(c1,c2) -> Integer.compare(c1.getPriority(), c2.getPriority()))
				.toList();
		for(ISpatialConstraint sc : scs) {
			filteredCandidates = sc.getCandidates(filteredCandidates);
			if(filteredCandidates.isEmpty())
				return Collections.emptyList();
		}
		return filteredCandidates;	
	}

	@Override
	public void setConstraints(List<ISpatialConstraint> constraints) {
		this.constraints = constraints;
	}
	
	@Override
	public void addConstraints(ISpatialConstraint... constraints) {
		this.constraints.addAll(Arrays.asList(constraints));
		
	}

	@Override
	public List<ISpatialConstraint> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}

	@Override
	public ConstraintsReleaseRule getConstraintsReleaseRule() {
		return this.rule;
	}

	@Override
	public void setConstraintsReleaseRule(ConstraintsReleaseRule rule) {
		this.rule = rule;
	}

}
