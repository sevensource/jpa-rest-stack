package org.sevensource.support.rest.dto;

import java.util.List;

import org.springframework.hateoas.PagedResources.PageMetadata;

public class PagedCollectionResourceDTO<DTO extends IdentifiableDTO<?>> {

	private final List<DTO> data;
	private final PageMetadata page;

	public PagedCollectionResourceDTO(List<DTO> data, PageMetadata page) {
		this.data = data;
		this.page = page;
	}

	public List<DTO> getData() {
		return data;
	}
	public PageMetadata getPage() {
		return page;
	}
}
