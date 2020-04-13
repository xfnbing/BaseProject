package com.easyder.wrapper.core.manager;

import com.easyder.wrapper.core.model.BaseVo;
import com.easyder.wrapper.core.network.NetworkManager;
import com.easyder.wrapper.core.network.RequestInfo;
import com.easyder.wrapper.core.network.ResponseInfo;
import com.easyder.wrapper.base.presenter.Callback;
import com.easyder.wrapper.core.result.CacheResult;
import com.easyder.wrapper.core.scheduler.TaskScheduler;
import com.easyder.wrapper.utils.LogUtils;
import com.easyder.wrapper.utils.UIUtils;
import com.easyder.wrapper.base.view.MvpView;

/**
 * 数据管理统管类，负责管理所有服务器的接口数据以及本地SD保存的数据
 *
 * @author 刘琛慧
 *         date 2016/6/20.
 */
public class DataManager {

    private int dataLoadMode = MODE_REMOTE_SERVER;  //数据加载模式
    //从本地数据库加载数据
    public static final int MODE_LOCAL = 0x01;
    //从本地服务器加载数据
    public static final int MODE_LOCAL_SERVER = 0x02;
    //从远程服务器加载数据
    public static final int MODE_REMOTE_SERVER = 0x03;

    private static DataManager instance;

    private DataManager() {
    }

    /**
     * 单利构造方法
     *
     * @return
     */
    public static DataManager getDefault() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    /**
     * 检查请求是否设置了合法的缓存时间，如果是则先从缓存里面请求数据
     * 并将从缓存中加载到的数据解析成Vo对象，并回调presenter的onSuccess方法。
     * 如果解析失败回调presenter的onError方法。
     *
     * @param requestInfo 请求对象
     * @param callback    回调presenter
     */
    public void loadData(final RequestInfo requestInfo, final Callback callback) {
        if (requestInfo.needMockData) {
            ResponseInfo responseInfo = new ResponseInfo(ResponseInfo.SUCCESS);
            //模拟数据需要对象的
            try {
                BaseVo dataVo = requestInfo.getDataClass().newInstance();
                dataVo.doMock();
                responseInfo.setDataVo(dataVo);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            callback.onSuccess(responseInfo);

        } else {
            switch (dataLoadMode) {
                case MODE_LOCAL:
                    TaskScheduler.execute(new Runnable() {
//                    TaskManager.getDefault().post(new Runnable() {
                        @Override
                        public void run() {
//                            loadDataFromLocal(requestInfo.getApi(), requestInfo.getRequestParams(), requestInfo.getDataClass(), callback);
                        }
                    });
                    break;
                case MODE_LOCAL_SERVER:
                case MODE_REMOTE_SERVER:
                    loadDataFromServer(requestInfo, callback);
                    break;
            }
        }
    }

    private void loadDataFromServer(RequestInfo requestInfo, Callback callback) {
        long cacheTime = requestInfo.getDataExpireTime();
        if (cacheTime > 0 && requestInfo.getRequestType() == RequestInfo.REQUEST_GET) {
//            TaskManager.getDefault().post(new CacheDataRunnable(requestInfo, callback));
            TaskScheduler.execute(new CacheDataRunnable(requestInfo, callback));
        } else {
            if (requestInfo.getRequestType() == RequestInfo.REQUEST_GET) {
                NetworkManager.getDefault().doGet(requestInfo, callback);
            } else  if (requestInfo.getRequestType() == RequestInfo.REQUEST_DELETE){
                NetworkManager.getDefault().doDelete(requestInfo, callback);
            }else  if (requestInfo.getRequestType() == RequestInfo.REQUEST_PUT){
                NetworkManager.getDefault().doPut(requestInfo, callback);
            }else {
                NetworkManager.getDefault().doPost(requestInfo, callback);
            }
        }
    }


    /**
     * 将网络请求的结果通过主线程分发
     *
     * @param callback
     * @param responseInfo
     */
    public void postCallback(final Callback callback, final ResponseInfo responseInfo) {
        if (callback == null) {
            return;
        }

        if (UIUtils.isRunInMainThread()) {
            if (responseInfo.getState() == ResponseInfo.SUCCESS) {
                callback.onSuccess(responseInfo);
            } else {
                callback.onError(responseInfo);
            }
            return;
        }

        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                if (responseInfo.getState() == ResponseInfo.SUCCESS) {
                    callback.onSuccess(responseInfo);
                } else {
                    callback.onError(responseInfo);
                }
            }
        });
    }


    /**
     * View层对象生命周期终结，取消所有网络请求级缓存请求。
     *
     * @param detachView
     */
    public void onViewDetach(MvpView detachView) {
        NetworkManager.getDefault().cancellAll(detachView);
    }


    /**
     * APP已关闭退出，释放所有占用资源和线程
     */
    public void onApplicationDestroy() {
        instance = null;
//        TaskManager.getDefault().destory();
        TaskScheduler.getInstance().destory();
        NetworkManager.getDefault().destory();
        CacheManager.getDefault().closeCache();
    }

    private static class CacheDataRunnable implements Runnable {
        RequestInfo requestInfo;
        Callback callback;

        public CacheDataRunnable(RequestInfo requestInfo, Callback callback) {
            this.requestInfo = requestInfo;
            this.callback = callback;
        }

        @Override
        public void run() {
            String key = CacheManager.getDefault().sortUrl(requestInfo.getUrl(), requestInfo.getRequestParams());
            CacheResult cacheResult = CacheManager.getDefault().readFromCache(key, requestInfo.getDataExpireTime());

            if (cacheResult.isValid) {
                ResponseInfo responseInfo;
                try {
                    responseInfo = new ResponseInfo(ResponseInfo.SUCCESS);
                    responseInfo.setDataVo(BaseVo.parseDataVo(cacheResult.cacheData, requestInfo.getDataClass()));
                    DataManager.getDefault().postCallback(callback, responseInfo);
                } catch (Exception ex) {
                    LogUtils.e("解析缓存数据失败：" + cacheResult.cacheData);
                    responseInfo = new ResponseInfo(ResponseInfo.FAILURE);
                    DataManager.getDefault().postCallback(callback, responseInfo);
                }
            } else {
                if (requestInfo.getRequestType() == RequestInfo.REQUEST_GET) {
                    NetworkManager.getDefault().doGet(requestInfo, callback);
                } else {
                    NetworkManager.getDefault().doPost(requestInfo, callback);
                }
            }
        }
    }
}
