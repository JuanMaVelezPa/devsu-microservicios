package com.devsu.account.api.context;

import com.devsu.account.api.filter.CorrelationIdFilter;
import org.slf4j.MDC;

public final class CorrelationContext {

    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private CorrelationContext() {
    }

    public static void set(String correlationId) {
        HOLDER.set(correlationId);
    }

    public static String get() {
        String correlationId = HOLDER.get();
        if (correlationId == null) {
            correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        }
        return correlationId;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
