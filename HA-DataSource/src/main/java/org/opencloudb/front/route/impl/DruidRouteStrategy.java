package org.opencloudb.front.route.impl;

import java.sql.SQLSyntaxErrorException;

import org.opencloudb.front.cache.CachePool;
import org.opencloudb.front.route.RouteStrategy;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

public class DruidRouteStrategy implements RouteStrategy {

	@Override
	public String route(String sqlStmt) throws SQLSyntaxErrorException {

		MySqlStatementParser parser = new MySqlStatementParser(sqlStmt);
		SQLStatement statement = parser.parseStatement();
		String tableName = null;

		if (statement instanceof SQLSelectStatement) {
			SQLSelectQuery selectQuery = ((SQLSelectStatement) statement)
					.getSelect().getQuery();
			tableName = SelectQuery(selectQuery);
		} else if (statement instanceof MySqlInsertStatement) {
			tableName = ((MySqlInsertStatement) statement).getTableName()
					.toString();
		} else if (statement instanceof MySqlDeleteStatement) {
			tableName = ((MySqlDeleteStatement) statement).getTableName()
					.toString();
		} else if (statement instanceof MySqlCreateTableStatement) {
			tableName = ((MySqlCreateTableStatement) statement).getName()
					.toString();
		} else if (statement instanceof MySqlUpdateStatement) {
			tableName = ((MySqlUpdateStatement) statement).getTableName()
					.toString();
		} else if (statement instanceof MySqlAlterTableStatement) {
			tableName = ((MySqlAlterTableStatement) statement).getName()
					.toString();
		}

		// 检验unsupported statement
		checkUnSupportedStatement(statement);

		// 无法找到表名
		if (tableName == null || "".equals(tableName)) {
			throw new SQLSyntaxErrorException(
					"can't find table name form the sql statement,please chek the statement");
		}

		return tableName;
	}

	private void checkUnSupportedStatement(SQLStatement statement)
			throws SQLSyntaxErrorException {
		if (statement instanceof MySqlReplaceStatement) {
			throw new SQLSyntaxErrorException(
					" ReplaceStatement can't be supported,use insert into ...on duplicate key update... instead ");
		}
	}

	private String SelectQuery(SQLSelectQuery selectQuery) {
		if (selectQuery instanceof SQLSelectQueryBlock) {
			return ((SQLSelectQueryBlock) selectQuery).getFrom().toString();
		} else if (selectQuery instanceof SQLUnionQuery) {
			return SelectQuery(((SQLUnionQuery) selectQuery).getLeft());
		}
		return "";
	}

}
