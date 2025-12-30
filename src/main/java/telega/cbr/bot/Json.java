package telega.cbr.bot;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Json {
    String message, code, status;

    public Json(@JsonProperty("message") String message,
                @JsonProperty("code") String code,
                @JsonProperty("status") String status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}
