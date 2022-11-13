package gospl.sampler.co;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandomUtils;
import core.util.random.roulette.ARouletteWheelSelection;
import core.util.random.roulette.RouletteWheelSelectionFactory;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.sampler.IEntitySampler;

/**
 * Draw an entity from a collection of entity
 * 
 * @author kevinchapuis
 *
 */
public class MicroDataSampler implements IEntitySampler<IPopulation<ADemoEntity, Attribute<? extends IValue>>> {

	private final boolean clone;
	private Collection<ADemoEntity> sample;
	private ARouletteWheelSelection<Double, ADemoEntity> roulette;
	
	public MicroDataSampler() {this(true);}
	
	public MicroDataSampler(boolean cloneEntity) {this.clone = cloneEntity;}
	
	@Override
	public ADemoEntity draw() {
		ADemoEntity original = (roulette!=null ? (ADemoEntity) roulette.drawObject() : GenstarRandomUtils.oneOf(sample)); 
		return clone ? original.clone() : original;
	}

	@Override
	public Collection<ADemoEntity> draw(int numberOfDraw) {
		Set<ADemoEntity> pop = new HashSet<>();
		IntStream.range(0, numberOfDraw).mapToObj(i -> draw()).forEach(e -> pop.add(e));
		return pop;
	}
	
	/**
	 * Draw as many entities to obtain a certain number of children {@link ADemoEntity#getChildren()}
	 * 
	 * @param numberOfDraw
	 * @return
	 */
	public Collection<ADemoEntity> drawWithChildrenNumber(int numberOfDraw) {
		Set<ADemoEntity> pop = new HashSet<>();
		int childDrawn = 0;
		do { ADemoEntity e = draw(); 
			pop.add(e); 
			childDrawn += e.getChildren().size();
		} while (childDrawn < numberOfDraw);
		return pop;
	}
	
	/**
	 * Add a sample with weights on individual
	 * @param sample
	 */
	@Override
	public void setSample(IPopulation<ADemoEntity, Attribute<? extends IValue>> sample, boolean weights) {
		if(weights) {
			List<Double> w = new ArrayList<>();
			List<ADemoEntity> e = new ArrayList<>();
			for (ADemoEntity entity : sample) { w.add(entity.getWeight()); e.add(entity); }
			this.roulette = RouletteWheelSelectionFactory.getRouletteWheel(w, e);
		} else {
			this.sample = sample;
		}
	}
	
	@Override
	public void addObjectives(INDimensionalMatrix<Attribute<? extends IValue>, IValue, Integer> objectives) {
		throw new IllegalAccessError("There is not any objectives to setup in RandomSampler");
	}
	
	@Override
	public String toCsv(String csvSeparator) {
		
		return null;
	}

	/**
	 * Intended to check if current object has the ability to draw {@link ADemoEntity} or not, i.e.
	 * if it is empty then it will not be able to draw entities
	 * 
	 * @return If the sample have not been initialized correctly, then will return true
	 * otherwise false
	 */
	public boolean isEmpty() {
		return (this.sample==null && this.roulette==null) ||
				(this.sample!=null && this.sample.isEmpty()) || 
				(this.roulette!=null && this.roulette.getKeys().isEmpty());
	}
}
