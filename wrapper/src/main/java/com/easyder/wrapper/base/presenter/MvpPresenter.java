package com.easyder.wrapper.base.presenter;


import com.easyder.wrapper.base.view.MvpView;

import java.lang.ref.WeakReference;

/**
 * @author 刘琛慧
 * @date 2015/8/10.
 */
public abstract class MvpPresenter<V extends MvpView> implements Presenter<V> {
    private WeakReference<V> viewReference; //MvpView的子类的弱引用
    protected String viewClassName; //类名 Tag


    @Override
    public void attachView(V view) {
        viewClassName = view.getClass().getSimpleName();
        viewReference = new WeakReference<>(view);
    }

    /**
     * 检查Activity或者Fragment是否已经绑定到了Presenter层
     *
     * @return 是否已经绑定
     */
    public boolean isViewAttached() {
        return viewReference != null && viewReference.get() != null;
    }


    /**
     * @return 获取实现了MvpView接口的Activity或者Fragment的引用用来实现回调
     */
    public V getView() {
        return viewReference == null ? null : viewReference.get();
    }


    @Override
    public void detachView() {
        if (viewReference != null) {
            viewReference.clear();
            viewReference = null;
        }
    }

}
