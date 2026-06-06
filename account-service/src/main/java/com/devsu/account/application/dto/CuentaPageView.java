package com.devsu.account.application.dto;

import java.util.List;

public record CuentaPageView(
        List<CuentaView> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
