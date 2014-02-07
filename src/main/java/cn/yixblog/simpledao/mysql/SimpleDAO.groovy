package cn.yixblog.simpledao.mysql

import cn.yixblog.simpledao.configReader.DbConfig
import cn.yixblog.simpledao.dao.ISimpleDAO
import org.apache.commons.lang.StringUtils
import org.springframework.jdbc.core.*
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException

/**
 * Created by dyb on 14-1-31.
 */
abstract class SimpleDAO<T> implements ISimpleDAO<T> {

    protected abstract DbConfig getDbConfig();

    protected abstract JdbcTemplate getJdbcTemplate();

    @Override
    T findOne(Object primary) {
        String sql = "select * from ${dbConfig.getTable()} where ${dbConfig.getPrimary()}=?";
        return jdbcTemplate.queryForObject(sql, getQueryRowMapper(), primary);
    }

    @Override
    List<T> list(String fromSql, String orderBy, List<Object> params, int ... pageArgs) {
        String sql = "select * from ${dbConfig.getTable()}";
        if (fromSql != null && fromSql != "") {
            sql += " where $fromSql ";
        }
        if (orderBy != null && orderBy != "") {
            sql += " order by $orderBy "
        }
        if (pageArgs != null && pageArgs.length == 2) {
            sql += " limit ${pageArgs[0]},${pageArgs[1]}";
        }
        return jdbcTemplate.query(sql, params.toArray(), getQueryRowMapper())
    }

    protected abstract RowMapper<T> getQueryRowMapper();

    @Override
    int count(String fromSql, List<Object> params) {
        String sql = "select count(*) from ${dbConfig.getTable()}";
        if (fromSql != null && fromSql != "") {
            sql += " where $fromSql ";
        }
        Number number = jdbcTemplate.queryForObject(sql, params.toArray(), Integer.class);
        return (number != null ? number.intValue() : 0);
    }

    @Override
    T save(T obj) {
        List<String> columns = dbConfig.isPrimaryAutoIncrement() ? dbConfig.listColumnsWithoutPrimary() : dbConfig.listColumns();
        String sql = "insert into ${dbConfig.getTable()} (${StringUtils.join(columns.toArray(), ",")}) values (${StringUtils.repeat("?", ",", columns.size())})"

        if (dbConfig.isPrimaryAutoIncrement()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            int rowCount = jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(sql);
                    columns.eachWithIndex { String col, int index ->
                        StatementCreatorUtils.setParameterValue(ps, index + 1, SqlTypeValue.TYPE_UNKNOWN, getColumnValue(obj, col));
                    }
                    return ps;
                }
            }, keyHolder);
            if (rowCount > 0) {
                obj[dbConfig.getPrimary()] = keyHolder.getKey().intValue();
            }
        } else {
            List<Object> params = [];
            for (String col : columns) {
                params.add(getColumnValue(obj, col))
            }
            jdbcTemplate.update(sql, params.toArray())
        }
        return obj;
    }

    @Override
    void update(T obj) {
        List<String> columns = dbConfig.listColumnsWithoutPrimary()
        String sql = "update ${dbConfig.getTable()} set ${makeUpdateSetter(columns)} where ${dbConfig.getPrimary()}=?";
        List<Object> args = [];
        for (String col : columns) {
            Object columnVal = getColumnValue(obj, col);
            if (columnVal != null) {
                args.add(columnVal);
            }
        }
        args.add(getColumnValue(obj, dbConfig.getPrimary()));
        jdbcTemplate.update(sql, args.toArray())
    }

    private Object getColumnValue(T obj, String column) {
        String field = dbConfig.getFieldByColumnName(column);
        if (field != null) {
            return obj[field];
        }
        return null;
    }

    private String makeUpdateSetter(List<String> columns) {
        StringBuilder builder = new StringBuilder();
        for (String column : columns) {
            if (column != dbConfig.getPrimary()) {
                builder.append(",$column=?")
            }
        }
        builder.deleteCharAt(0);
        return builder.toString();
    }

    @Override
    void delete(Object primary) {
        String sql = "delete from ${dbConfig.getTable()} where ${dbConfig.getPrimary()}=?"
        jdbcTemplate.update(sql, primary);
    }
}
