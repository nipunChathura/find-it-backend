package lk.icbt.findit.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetAllMerchantsResponse extends Response {
    /** Single list: main merchants (type=MERCHANT) and sub-merchants (type=SUB_MERCHANT, with parentMerchantName). */
    private List<MerchantListItemResponse> merchants;
}
