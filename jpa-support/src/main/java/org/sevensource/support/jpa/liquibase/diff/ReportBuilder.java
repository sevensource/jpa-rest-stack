package org.sevensource.support.jpa.liquibase.diff;

class ReportBuilder {
	private StringBuilder builder = new StringBuilder();
	
	ReportBuilder appendLine(String s) {
		builder.append(s).append("\n");
		return this;
	}
	
	public String asString() {
		return builder.toString();
	}
}