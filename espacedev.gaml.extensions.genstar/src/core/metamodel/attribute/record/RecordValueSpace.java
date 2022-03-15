package core.metamodel.attribute.record;

import core.metamodel.attribute.IAttribute;
import core.metamodel.value.categoric.NominalSpace;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.template.GSCategoricTemplate;

public class RecordValueSpace extends NominalSpace {

	public RecordValueSpace(IAttribute<NominalValue> attribute, 
			GSCategoricTemplate ct, String record) {
		super(attribute, ct);
		this.values.put(record, this.proposeValue(record));
	}
		
	@Override
	public NominalValue addValue(String value) throws IllegalArgumentException {
		throw new IllegalAccessError("You cannot add value to a record value space");
	}
	
}
