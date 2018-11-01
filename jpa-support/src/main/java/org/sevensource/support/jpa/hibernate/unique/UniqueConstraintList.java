package org.sevensource.support.jpa.hibernate.unique;

import java.util.ArrayList;

import org.springframework.util.StringUtils;

class UniqueConstraintList extends ArrayList<UniqueConstraintGroup>{

	private static final long serialVersionUID = -2851078712901740713L;

	public void addUniqueConstraint(UniqueConstraint uniqueConstraint) {
		final UniqueConstraintGroup group = getConstraintDescriptorGroup(uniqueConstraint.group);
		if(group == null) {
			add(new UniqueConstraintGroup(uniqueConstraint.group, uniqueConstraint));
		} else {
			group.getConstraints().add(uniqueConstraint);
		}
	}


	UniqueConstraintGroup getConstraintDescriptorGroup(String name) {
		if(!StringUtils.hasText(name)) {
			return null;
		}

		return stream()
				.filter(group -> name.equals(group.getName()))
				.findFirst()
				.orElse(null);
	}
}
