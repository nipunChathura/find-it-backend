package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageUploadResponse extends Response {

    /** Unique filename under the type folder (e.g. a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg). */
    private String fileName;
    /** Folder type used (e.g. profile, items, receipt). */
    private String type;
    /** Relative path for reference: type/fileName (e.g. profile/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg). */
    private String relativePath;
}
