package core.metamodel.attribute.record;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.attribute.RecordAttributeSerializer;
import core.metamodel.attribute.IAttribute;
import core.metamodel.attribute.MappedAttribute;
import core.metamodel.attribute.mapper.value.EncodedValueMapper;
import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.template.GSCategoricTemplate;

/**
 * A type of attribute which is not aimed to describe entity but only focus on
 * data parsing. Main purpose is to ...
 * 
 * @author kevinchapuis
 *
 * @param <R>
 * @param <P>
 */
@JsonTypeName(RecordAttribute.SELF)
@JsonSerialize(using = RecordAttributeSerializer.class)
public class RecordAttribute<R extends IAttribute<? extends IValue>, 
	P extends IAttribute<? extends IValue>> implements IAttribute<NominalValue> {

	public static final String SELF = "RECORD ATTRIBUTE";
	public static final String PROXY_TYPE = "PROXY ATTRIBUTE "+IValueSpace.TYPE_LABEL;
	
	private final P proxy;
	
	@JsonProperty(MappedAttribute.REF)
	private final R referent;
	
	@JsonIgnore
	private RecordValueSpace valuesSpace = null;
	
	private final String name;

	public RecordAttribute(String name, P proxy, R referent) {
		this.name = name;
		this.proxy = proxy;
		this.referent = referent;
	}
	
	@Override
	public String getAttributeName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return this.getAttributeName()+" >> "+referent.getAttributeName();
	}
	
	@Override
	public RecordValueSpace getValueSpace() {
		if(valuesSpace == null)
			valuesSpace = new RecordValueSpace(this, new GSCategoricTemplate(), this.getAttributeName());
		return valuesSpace;
	}

	
	@Override
	public void setValueSpace(IValueSpace<NominalValue> valueSpace) {
		// DO NOTHING
	}
	
	@Override
	public EncodedValueMapper<NominalValue> getEncodedValueMapper() {
		return null;
	}

	@Override
	public void setEncodedValueMapper(EncodedValueMapper<NominalValue> encodedMapper) {
		// DO NOTHING
	}
	
	/**
	 * Get the referent attribute to which record will be linked
	 * @return
	 */
	public R getReferentAttribute() {
		return referent;
	}
	
	/**
	 * Get the proxy attribute that store record relationship. 
	 * 
	 * @return
	 */
	public P getProxyAttribute(){
		return proxy;
	}

}
