package org.sevensource.support.test.jdbc;

import java.util.List;

import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;

public class SimpleQueryLogEntryCreator extends DefaultQueryLogEntryCreator {
	
	private final Formatter formatter = FormatStyle.BASIC.getFormatter();
	
	private final boolean useHibernateQueryFormatter;
	
	public SimpleQueryLogEntryCreator(boolean useHibernateQueryFormatter) {
		this.useHibernateQueryFormatter = useHibernateQueryFormatter;
	}
	
	@Override
	protected String formatQuery(String query) {
		return useHibernateQueryFormatter ? this.formatter.format(query) : super.formatQuery(query);
	}
	
	@Override
	protected void writeResultEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
	}
	
	@Override
	protected void writeTypeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
	}
	
	@Override
	protected void writeBatchEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
	}
	
	@Override
	protected void writeQuerySizeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
	}
	
	@Override
	protected void writeBatchSizeEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
	}
}
