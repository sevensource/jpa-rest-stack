package org.sevensource.support.jpa.hibernate.unique;

import java.util.ArrayList;
import java.util.List;

class ConstraintDescriptorGroup {
	private final String name;
	private List<ConstraintDescriptor> constraints = new ArrayList<>(3);
	
	public ConstraintDescriptorGroup(String groupName) {
		this.name = groupName;
	}
	
	public ConstraintDescriptorGroup(String groupName, ConstraintDescriptor descriptor) {
		this.name = groupName;
		if(descriptor != null) {
			this.constraints.add(descriptor);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<ConstraintDescriptor> getConstraints() {
		return constraints;
	}
}
