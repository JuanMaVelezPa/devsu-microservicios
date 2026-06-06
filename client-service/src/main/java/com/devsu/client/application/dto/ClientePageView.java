package com.devsu.client.application.dto;

import java.util.List;

public record ClientePageView(
        List<ClienteView> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
}
