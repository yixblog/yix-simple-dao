package cn.yixblog.simpledao.dao;

import java.util.List;

/**
 * a simple crud dao for all basic classes
 * Created by dyb on 14-1-31.
 */
public interface ISimpleDAO<T> {
    public T findOne(Object primary);

    public List<T> list(String fromSql, String orderBy, List<Object> params, int... pageArgs);

    public int count(String fromSql, List<Object> params);

    public T save(T obj);

    public void update(T obj);

    public void delete(Object primary);
}
