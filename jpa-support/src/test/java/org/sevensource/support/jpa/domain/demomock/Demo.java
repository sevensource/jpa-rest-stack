package org.sevensource.support.jpa.domain.demomock;
public  class Demo {
	private String name = "yop";
	
	public void setName(String name) {
		System.out.println("Setting name to " + name);
	}
	
	public void setName(Object object) {
		System.out.println("Setting OBJECGT to " + object);
	}
	
	public String getName() {
		return name;
	}
}