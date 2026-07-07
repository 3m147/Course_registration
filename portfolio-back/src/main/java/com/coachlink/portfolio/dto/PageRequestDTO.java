package com.coachlink.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
	@Builder.Default
	private int page = 1;

	@Builder.Default
	private int size = 10;

	public Pageable getPageable(String sortProp, Sort.Direction direction) {
		return PageRequest.of(this.page - 1, this.size, Sort.by(direction, sortProp));
	}
}
