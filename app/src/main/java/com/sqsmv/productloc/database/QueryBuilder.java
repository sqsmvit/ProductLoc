package com.sqsmv.productloc.database;

public class QueryBuilder
{
    public static String buildInsertQuery(String tableName, String[] tableColumns)
    {
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT OR REPLACE INTO ");
        sb.append(tableName);
        sb.append("(");

        sb.append(buildColumnList(tableColumns));
        
        sb.append(")");
        
        sb.append(" VALUES(");
        
        for(int count = 0; count < tableColumns.length; count++)
        {
            sb.append("?");
            if(count != tableColumns.length - 1)
            {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }
    
    public static String buildSelectQuery(String tableName, String[] selectColumns, String whereColumns[])
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT ");

        sb.append(buildColumnList(selectColumns));
        sb.append(" FROM ");
        sb.append(tableName);
        
        if(whereColumns.length > 0)
        {
            sb.append(" WHERE ");
            sb.append(buildWhereClause(whereColumns));
        }
        return sb.toString();
    }

    private static String buildColumnList(String[] columns)
    {
        StringBuilder sb = new StringBuilder();
        for(int count = 0; count < columns.length; count++)
        {
            sb.append(columns[count]);
            if(count != columns.length - 1)
            {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static String buildWhereClause(String[] whereColumns)
    {
        StringBuilder sb = new StringBuilder();
        for(int count = 0; count < whereColumns.length; count++)
        {
            sb.append(whereColumns[count]);
            sb.append(" = ?");
            if(count != whereColumns.length - 1)
            {
                sb.append(" AND ");
            }
        }
        return sb.toString();
    }
}
