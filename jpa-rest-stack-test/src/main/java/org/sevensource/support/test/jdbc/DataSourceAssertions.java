package org.sevensource.support.test.jdbc;

import net.ttddyy.dsproxy.QueryCountHolder;

public class DataSourceAssertions {

	private DataSourceAssertions() { }
	
    public static void reset() {
        QueryCountHolder.clear();
    }
    
    public static int selectCount() {
    	return QueryCountHolder.getGrandTotal().getSelect();
    }
    
    public static int insertCount() {
    	return QueryCountHolder.getGrandTotal().getInsert();
    }
    
    public static int updateCount() {
    	return QueryCountHolder.getGrandTotal().getUpdate();
    }
    
    public static int deleteCount() {
    	return QueryCountHolder.getGrandTotal().getDelete();
    }
    
    public static int totalCount() {
    	return QueryCountHolder.getGrandTotal().getTotal();
    }
}
