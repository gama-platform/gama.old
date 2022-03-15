package core.metamodel.entity.tag;

/**
 * Tag associated to entity in order to capture relationship with sub-entities of a super entity
 * 
 * @author kevinchapuis
 *
 */
public enum EntityTag { 
	
	HHHead(EntityTag.UPWARD),
	Parent(EntityTag.HORIZONTAL),
	Child(EntityTag.HORIZONTAL);
	
	public static final int UPWARD = 1;
	public static final int HORIZONTAL = 0;
	public static final int DOWNWARD = -1;
	
	private int layer;

	private EntityTag(int layer) {
		this.layer = layer;
	}
	
	/**
	 * Inform about the attribute relatives in terms of layer: to which layer this attribute talk about, according to the 
	 * layer's entity. <\p>
	 * ==> e.g. #Parent and #Child refer to the same layer (0), while HHHead (a.k.a. Household head) refers to upper layer (1) 
	 * 
	 * @return
	 */
	public int getLayer() {
		return layer;
	}
	
}
