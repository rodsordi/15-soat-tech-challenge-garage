package br.com.fiap.commons.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

@Getter
@Setter
public abstract class PagedFilter {

    @Schema(example = "0",
            description = "Page index.")
    private Integer page = 0;

    @Schema(example = "20",
            description = "Page size.")
    private Integer size = 20;

    public PageRequest buildPageRequest() {
        return PageRequest.of(page, size);
    }
}
