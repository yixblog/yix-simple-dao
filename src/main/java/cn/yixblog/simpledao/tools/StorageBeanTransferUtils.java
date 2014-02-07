package cn.yixblog.simpledao.tools;

import cn.yixblog.simpledao.interfaces.DAOBean;
import cn.yixblog.simpledao.interfaces.StorageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * transfer utils
 * Created by Yixian on 14-2-6.
 */
public class StorageBeanTransferUtils {
    public static <D extends DAOBean, S extends StorageBean<D>> S toStorageBean(D daoBean, Class<S> storageClass) {
        try {
            S emptyStorageBean = storageClass.newInstance();
            emptyStorageBean.initValue(daoBean);
            return emptyStorageBean;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <D extends DAOBean, S extends StorageBean<D>> List<S> toStorageBeanList(List<D> daoList, Class<S> storageClass) {
        List<S> storageList = new ArrayList<>();
        for (D bean : daoList) {
            S item = toStorageBean(bean, storageClass);
            storageList.add(item);
        }
        return storageList;
    }
}