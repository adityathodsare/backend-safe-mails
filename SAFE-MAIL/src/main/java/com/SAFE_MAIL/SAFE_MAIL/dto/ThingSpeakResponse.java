package com.SAFE_MAIL.SAFE_MAIL.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ThingSpeakResponse {
    private Channel channel;
    private List<Feed> feeds;

    @Data
    public static class Channel {
        private Long id;
        private String name;
        private String description;
        private String latitude;
        private String longitude;
        private String field1;
        private String field2;
        private String field3;
        private String field4;
        private String field5;
        private String field6;
        @JsonProperty("created_at")
        private String createdAt;
        @JsonProperty("updated_at")
        private String updatedAt;
        @JsonProperty("last_entry_id")
        private Long lastEntryId;
    }

    @Data
    public static class Feed {
        @JsonProperty("created_at")
        private String createdAt;
        @JsonProperty("entry_id")
        private Long entryId;
        private String field1;
        private String field2;
        private String field3;
        private String field4;
        private String field5;
        private String field6;
    }
}