package cn.yixblog.simpledao.interfaces;

/**
 * bean for stoage
 * Created by Yixian on 14-2-6.
 */
public interface StorageBean<D extends DAOBean> {
    public void initValue(D daoBean);
    public D toDAO();
}
