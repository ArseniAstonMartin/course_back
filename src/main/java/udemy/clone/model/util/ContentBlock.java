package udemy.clone.model.util;

import lombok.Data;

@Data
public class ContentBlock {
    private ContentType contentType;
    private String content;
    private int order;

    public enum ContentType {
        TEXT, VIDEO, IMAGE, HEADER
    }
}
