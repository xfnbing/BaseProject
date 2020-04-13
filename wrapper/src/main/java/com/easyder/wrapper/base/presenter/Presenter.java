package com.easyder.wrapper.base.presenter;

/**
 * Presenter基本方法
 */
public interface Presenter<V> {

    /**
     * 将Activity或者Fragment绑定到Presenter
     */
    void attachView(V view);


    /**
     * 将Activity或者Fragment从Presenter中分离出来
     */
    void detachView();

}
