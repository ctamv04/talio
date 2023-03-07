package serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Board;

import java.io.IOException;

public class BoardSerializer extends JsonSerializer<Board> {

    @Override
    public void serialize(Board value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id",value.getId());
        gen.writeEndObject();
    }
}
