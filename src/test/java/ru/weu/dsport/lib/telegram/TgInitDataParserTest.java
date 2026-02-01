package ru.weu.dsport.lib.telegram;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.weu.dsport.exception.InvalidInitDataException;

class TgInitDataParserTest {

    private final TgInitDataParser parser = new TgInitDataParser(new ObjectMapper());

    @Test
    void parseUserIdParsesUserFromQueryString() {
        String initData = "user=%7B%22id%22%3A12345%7D&hash=abc";

        long userId = parser.parseUserId(initData);

        assertThat(userId).isEqualTo(12345L);
    }

    @Test
    void parseUserIdThrowsWhenUserParamMissing() {
        String initData = "hash=abc";

        assertThatThrownBy(() -> parser.parseUserId(initData))
                .isInstanceOf(InvalidInitDataException.class)
                .hasMessageContaining("user");
    }

    @Test
    void parseUserIdThrowsWhenUserPayloadInvalid() {
        String initData = "user=not-json";

        assertThatThrownBy(() -> parser.parseUserId(initData))
                .isInstanceOf(InvalidInitDataException.class)
                .hasMessageContaining("payload");
    }
}
