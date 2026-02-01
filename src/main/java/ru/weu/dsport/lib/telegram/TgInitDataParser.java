package ru.weu.dsport.lib.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import ru.weu.dsport.exception.InvalidInitDataException;

@Component
@RequiredArgsConstructor
public class TgInitDataParser {

    private final ObjectMapper objectMapper;

    public long parseUserId(String initData) {
        if (!StringUtils.hasText(initData)) {
            throw new InvalidInitDataException("initData is empty");
        }

        String userJson = UriComponentsBuilder.fromUriString("/?" + initData)
                .build()
                .getQueryParams()
                .getFirst("user");

        if (!StringUtils.hasText(userJson)) {
            throw new InvalidInitDataException("initData does not contain user parameter");
        }

        try {
            // TODO: add Telegram initData signature validation when security layer is introduced.
            JsonNode root = objectMapper.readTree(userJson);
            JsonNode idNode = root.get("id");
            if (idNode == null || !idNode.canConvertToLong()) {
                throw new InvalidInitDataException("initData user.id is invalid");
            }
            return idNode.asLong();
        } catch (JsonProcessingException ex) {
            throw new InvalidInitDataException("initData user payload is invalid", ex);
        }
    }
}
