package gospl.distribution.matrix.coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;

/**
 * Mulit-level coordinate to represent coordinate of attribute / value for several (probabilist) universe
 * 
 * @author kevinchapuis
 *
 */
public class GosplMultiLayerCoordinate extends ACoordinate<Attribute<? extends IValue>, IValue>{

	private Set<GosplMultiLayerCoordinate> childs;
	
	public GosplMultiLayerCoordinate(ACoordinate<Attribute<? extends IValue>, IValue> coordinate) {
		super(coordinate.getMap());
		childs = new HashSet<>();
	}
	
	public GosplMultiLayerCoordinate(Map<Attribute<? extends IValue>, IValue> self) {
		super(self);
		childs = new HashSet<>();
	}
	
	/**
	 * Whether this multi layer coordinate have child or not
	 * @return
	 */
	public boolean hasChild() {
		return childs == null || childs.isEmpty() ? true : false;
	}
	
	/**
	 * Return the set of all child coordinate
	 * @return
	 */
	public Set<GosplMultiLayerCoordinate> getChilds(){
		return Collections.unmodifiableSet(childs);
	}
	
	/**
	 * Add new child coordinate
	 * @param coordinate
	 */
	public void addChild(GosplMultiLayerCoordinate coordinate) {
		this.childs.add(coordinate);
	}
	
	@Override
	protected boolean isCoordinateSetComplient(Map<Attribute<? extends IValue>, IValue> coordinateSet) {
		return coordinateSet.entrySet().stream()
				.allMatch(e -> e.getValue().getValueSpace().getAttribute().equals(e.getKey()));
	}
	
}
