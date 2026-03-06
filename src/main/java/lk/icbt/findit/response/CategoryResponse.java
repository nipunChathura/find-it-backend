package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.icbt.findit.entity.CategoryType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse extends Response {
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String categoryImage;
    private CategoryType categoryType;
    private String categoryStatus;
    private Date createdDatetime;
}
