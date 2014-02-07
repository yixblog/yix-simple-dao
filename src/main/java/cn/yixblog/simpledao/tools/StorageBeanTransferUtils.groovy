package cn.yixblog.simpledao.tools

import cn.yixblog.simpledao.interfaces.DAOBean
import cn.yixblog.simpledao.interfaces.StorageBean

/**
 * transfer utils
 * Created by Yixian on 14-2-6.
 */
class StorageBeanTransferUtils {
    static <S extends StorageBean<D>, D extends DAOBean> S toStorageBean(D daoBean) {
        S emptyStorageBean = new S();
        emptyStorageBean.initValue(daoBean);
        return emptyStorageBean;
    }

    static <S extends StorageBean<D>, D extends DAOBean> List<S> toStorageBeanList(List<D> daoList) {
        List<S> storageList = new ArrayList<>();
        for (D bean : daoList) {
            S item = toStorageBean(bean);
            storageList.add(item);
        }
        return storageList;
    }
}
