package ru.weu.dsport.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.math.BigDecimal;

public class UpdateSetEntryRequestDeserializer extends JsonDeserializer<UpdateSetEntryRequest> {

    @Override
    public UpdateSetEntryRequest deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        UpdateSetEntryRequest request = new UpdateSetEntryRequest();
        if (node.has("orderIndex")) {
            request.setOrderIndexProvided(true);
            JsonNode value = node.get("orderIndex");
            if (!value.isNull()) {
                request.setOrderIndex(value.asInt());
            }
        }
        if (node.has("reps")) {
            request.setRepsProvided(true);
            JsonNode value = node.get("reps");
            if (!value.isNull()) {
                request.setReps(value.asInt());
            }
        }
        if (node.has("weight")) {
            request.setWeightProvided(true);
            JsonNode value = node.get("weight");
            if (!value.isNull()) {
                request.setWeight(value.decimalValue());
            }
        }
        if (node.has("durationSeconds")) {
            request.setDurationSecondsProvided(true);
            JsonNode value = node.get("durationSeconds");
            if (!value.isNull()) {
                request.setDurationSeconds(value.asInt());
            }
        }
        return request;
    }
}
