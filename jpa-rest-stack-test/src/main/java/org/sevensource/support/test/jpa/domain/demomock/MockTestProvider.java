package org.sevensource.support.test.jpa.domain.demomock;
//package org.sevensource.support.jpa.domain.demomock;
//
//import java.util.function.BiConsumer;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//public class MockTestProvider<T> {
//
//	Class<T> domainClass;
//	BiConsumer biconsumer;
//	Supplier supplier;
//	Object supplierValue;
//	Consumer<T> valuesetter;
//	
//	
//	public MockTestProvider(Class<T> domainClass) {
//		this.domainClass = domainClass;
//	}
//	
//	public void function(Function<T, ?> consumer) {
//	}
//	
//	public void supplier(Supplier<?> supplier) {
//	}
//	
//	public <X> MockTestProvider<T> biconsumer(BiConsumer<T, X> biconsumer, Supplier<X> supplier) throws InstantiationException, IllegalAccessException {
//		this.supplier = supplier;
//		this.biconsumer = biconsumer;
//		return this;
//	}
//	
//	public MockTestProvider<T> valuesetter(Consumer<T> consumer) {
//		this.valuesetter = consumer;
//		return this;
//	}
//	
//	public <X> MockTestProvider<T> biconsumer(BiConsumer<T, X> biconsumer, X supplierValue) throws InstantiationException, IllegalAccessException {
//		this.supplierValue = supplierValue;
//		this.biconsumer = biconsumer;
//		return this;
//	}
//	
//	public void run() throws InstantiationException, IllegalAccessException {
//		T instance = this.domainClass.newInstance();
//		if(supplier != null)
//			biconsumer.accept(instance, supplier.get());
//		if(supplierValue != null)
//			biconsumer.accept(instance, supplierValue);
//		if(valuesetter != null)
//			valuesetter.accept(instance);
//	}
//}
