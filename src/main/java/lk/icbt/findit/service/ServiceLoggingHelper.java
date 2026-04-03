package lk.icbt.findit.service;

import org.slf4j.Logger;


public final class ServiceLoggingHelper {

    private ServiceLoggingHelper() {
    }

    
    public static void logStart(Logger log, String serviceName, String methodName, Object... keyValuePairs) {
        if (log.isInfoEnabled()) {
            String params = formatKeyValues(keyValuePairs);
            log.info("[{}] {} start{}", serviceName, methodName, params.isEmpty() ? "" : " - " + params);
        }
    }

    
    public static void logEnd(Logger log, String serviceName, String methodName, Object... keyValuePairs) {
        if (log.isInfoEnabled()) {
            String params = formatKeyValues(keyValuePairs);
            log.info("[{}] {} end - success{}", serviceName, methodName, params.isEmpty() ? "" : " " + params);
        }
    }

    
    public static void logGettingData(Logger log, String dataDescription, Object... keyValuePairs) {
        if (log.isDebugEnabled()) {
            String params = formatKeyValues(keyValuePairs);
            log.debug("[Getting data] {} {}", dataDescription, params.isEmpty() ? "" : params);
        }
    }

    
    public static void logValidationError(Logger log, String code, String message) {
        log.warn("[Validation error] code={} message={}", code, message);
    }

    private static String formatKeyValues(Object... keyValuePairs) {
        if (keyValuePairs == null || keyValuePairs.length == 0) return "";
        if (keyValuePairs.length % 2 != 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            if (sb.length() > 0) sb.append(", ");
            String key = keyValuePairs[i] != null ? keyValuePairs[i].toString() : "null";
            Object val = i + 1 < keyValuePairs.length ? keyValuePairs[i + 1] : null;
            sb.append(key).append("=").append(val);
        }
        return sb.toString();
    }
}
