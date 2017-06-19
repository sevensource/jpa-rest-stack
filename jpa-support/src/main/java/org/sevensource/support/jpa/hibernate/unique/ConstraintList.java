package org.sevensource.support.jpa.hibernate.unique;

import java.util.ArrayList;

import org.springframework.util.StringUtils;

class ConstraintList extends ArrayList<ConstraintDescriptorGroup>{

	private static final long serialVersionUID = -2851078712901740713L;

	ConstraintDescriptorGroup getConstraintDescriptorGroup(String name) {
		if(name == null || !StringUtils.hasText(name)) {
			return null;
		}
		
		for(ConstraintDescriptorGroup group : this) {
			if(name.equals(group.getName())) {
				return group;
			}
		}
		
		return null;
	}
}
