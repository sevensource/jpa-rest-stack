package org.sevensource.support.jpa.hibernate.unique;

class ConstraintDescriptor {
	final String field;
	final Object value;
	final String group;
	
	ConstraintDescriptor(String field, Object value, String group) {
		this.field = field;
		this.value = value;
		this.group = group;
	}
}