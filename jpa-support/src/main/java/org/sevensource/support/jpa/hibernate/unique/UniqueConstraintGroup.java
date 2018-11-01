package org.sevensource.support.jpa.hibernate.unique;

import java.util.ArrayList;
import java.util.List;

class UniqueConstraintGroup {
	private final String name;
	private List<UniqueConstraint> constraints = new ArrayList<>(3);
	
	
	public UniqueConstraintGroup(String groupName, UniqueConstraint descriptor) {
		this.name = groupName;
		if(descriptor != null) {
			this.constraints.add(descriptor);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<UniqueConstraint> getConstraints() {
		return constraints;
	}
}
