package com.devsu.account.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado paginado")
public record PageResponse<T>(
        @Schema(description = "Elementos de la pagina actual")
        List<T> content,
        @Schema(description = "Indice de pagina (base 0)", example = "0")
        int page,
        @Schema(description = "Tamano de pagina", example = "20")
        int size,
        @Schema(description = "Total de registros", example = "5")
        long totalElements,
        @Schema(description = "Total de paginas", example = "1")
        int totalPages,
        @Schema(example = "true")
        boolean first,
        @Schema(example = "true")
        boolean last
) {
}
