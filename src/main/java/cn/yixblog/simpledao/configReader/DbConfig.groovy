package cn.yixblog.simpledao.configReader

import cn.yixblog.simpledao.annotations.DbColumn
import cn.yixblog.simpledao.annotations.DbTable

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by dyb on 14-1-31.
 */
class DbConfig {
    String table;
    String primary;
    boolean primaryAutoIncrement;
    /**
     * column reference config,key is column name,value is field name;
     */
    Map<String, String> columnReference;

    String getFieldByColumnName(String columnName) {
        return columnReference[columnName];
    }

    String getColumnByField(String fieldName) {
        String column = null;
        columnReference.find { col, field ->
            if (field == fieldName) {
                column = col;
                return true;
            }
            return false
        }
        return column;
    }

    List<String> listColumns() {
        List<String> columns = [];
        columnReference.each { col, field ->
            columns.add(col);
        }
        return columns;
    }

    List<String> listColumnsWithoutPrimary() {
        List<String> columns = [];
        columnReference.each { col, field ->
            if (col != primary) {
                columns.add(col)
            }
        }
        return columns;
    }

    static DbConfig initFromClass(Class clazz) {
        if (clazz.isAnnotationPresent(DbTable.class)) {
            DbTable tableAnno = clazz.getAnnotation(DbTable.class);
            String tableName = tableAnno.value();
            String primary = tableAnno.primaryKey();
            boolean auto = tableAnno.primaryAuto();
            Map<String, String> references = [:]
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(DbColumn.class)) {
                    DbColumn columnAnno = field.getAnnotation(DbColumn.class);
                    String columnName = columnAnno.value();
                    if (columnName == "") {
                        columnName = field.getName();
                    }
                    references[columnName] = field.getName();
                }
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(DbColumn.class)) {
                    DbColumn columnAnno = method.getAnnotation(DbColumn.class);
                    String fieldName = getGetterFieldName(method.getName());
                    String columnName = columnAnno.value();
                    columnName = columnName == "" ? fieldName : columnName;
                    references[columnName] = fieldName;
                }
            }
            return new DbConfig(table: tableName, primary: primary, columnReference: references, primaryAutoIncrement: auto)
        }
        return null;
    }

    private static String getGetterFieldName(String getterName) {
        if (getterName.startsWith("get")) {
            return getterName.substring(3, 4).toLowerCase() + getterName.substring(4);
        }
        throw new RuntimeException("DbColumn annotation on a method that is not a getter method:$getterName")
    }

}
