package lk.icbt.findit.request;

import lombok.Data;

@Data
public class RejectRequest {
    /** Optional reason for rejection (e.g. for merchant/sub-merchant/user). */
    private String reason;
}
