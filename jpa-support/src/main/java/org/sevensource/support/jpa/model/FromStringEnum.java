package org.sevensource.support.jpa.model;

public interface FromStringEnum {
	
	String stringValue();
		
	@SuppressWarnings("unchecked")
	public static <T extends Enum<?> & FromStringEnum> T getFromStringEnum(Class<T> enumClass, String value) {
		T v = enumClass.getEnumConstants()[0];
		return (T) v.fromStringValue(value);
	}
	
	@SuppressWarnings("unchecked")
	public default Enum<? extends FromStringEnum> fromStringValue(String value) {
		FromStringEnum[] enumValues = getClass().getEnumConstants();
		
		for(FromStringEnum enumValue : enumValues) {
			if(enumValue.stringValue().equals(value)) {
				return (Enum<? extends FromStringEnum>) enumValue;
			}
		}
		
		throw new IllegalArgumentException("UNKNOWN");
	}
	
	
}
