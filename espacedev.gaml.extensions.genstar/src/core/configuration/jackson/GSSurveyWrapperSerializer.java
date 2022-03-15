package core.configuration.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import core.metamodel.io.GSSurveyWrapper;

public class GSSurveyWrapperSerializer extends StdSerializer<GSSurveyWrapper> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GSSurveyWrapperSerializer() {
		this(null);
	}
	
	protected GSSurveyWrapperSerializer(Class<GSSurveyWrapper> clazz) {
		super(clazz);
	}

	@Override
	public void serialize(GSSurveyWrapper wrapper, JsonGenerator gen, SerializerProvider provider) throws IOException {
		
		gen.writeStartObject();
		gen.writeObjectField(GSSurveyWrapper.RELATIVE_PATH, wrapper.getRelativePath());
		gen.writeBooleanField(GSSurveyWrapper.STORE_IN_MEMORY, wrapper.getStoredInMemory());
		gen.writeStringField(GSSurveyWrapper.SURVEY_TYPE, wrapper.getSurveyType().toString());
		gen.writeStringField(GSSurveyWrapper.CSV_SEPARATOR, String.valueOf(wrapper.getCsvSeparator()));
		gen.writeStringField(GSSurveyWrapper.FIRST_ROW_INDEX, String.valueOf(wrapper.getFirstRowIndex()));
		gen.writeStringField(GSSurveyWrapper.FIRST_COLUMN_INDEX, String.valueOf(wrapper.getFirstColumnIndex()));
		gen.writeStringField(GSSurveyWrapper.SHEET_NB, String.valueOf(wrapper.getSheetNumber()));
		gen.writeEndObject();
		
	}

}
