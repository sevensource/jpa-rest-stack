package org.sevensource.support.test.jdbc;

import net.ttddyy.dsproxy.QueryCountHolder;

public class DataSourceAssertions {

	private DataSourceAssertions() { }

    public static void reset() {
        QueryCountHolder.clear();
    }

    public static long selectCount() {
    	return QueryCountHolder.getGrandTotal().getSelect();
    }

    public static long insertCount() {
    	return QueryCountHolder.getGrandTotal().getInsert();
    }

    public static long updateCount() {
    	return QueryCountHolder.getGrandTotal().getUpdate();
    }

    public static long deleteCount() {
    	return QueryCountHolder.getGrandTotal().getDelete();
    }

    public static long totalCount() {
    	return QueryCountHolder.getGrandTotal().getTotal();
    }
}
