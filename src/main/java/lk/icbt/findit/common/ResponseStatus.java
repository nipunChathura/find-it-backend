package lk.icbt.findit.common;

import lombok.Getter;

@Getter
public enum ResponseStatus {

    SUCCESS("success"),
    FAILURE("failure");

    private final String status;

    private ResponseStatus(String status) {
        this.status = status;
    }

    public static ResponseStatus getByStatus(String status) {
        for (ResponseStatus requestStatus : values()) {
            if (requestStatus.getStatus().equals(status)) {
                return requestStatus;
            }
        }
        throw new AssertionError("Request status not found for given status [status: " + status + "]");
    }

}
