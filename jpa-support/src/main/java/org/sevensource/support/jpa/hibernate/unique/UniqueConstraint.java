package org.sevensource.support.jpa.hibernate.unique;

import org.springframework.util.StringUtils;

class UniqueConstraint {
	final String field;
	final Object value;
	final String group;

	UniqueConstraint(String field, Object value, String group) {
		this.field = field;
		this.value = value;
		this.group = StringUtils.hasLength(group) ? group : null;
	}
}