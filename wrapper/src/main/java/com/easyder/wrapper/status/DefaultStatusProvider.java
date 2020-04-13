package com.easyder.wrapper.status;

import android.view.View;
import android.view.ViewGroup;

import com.easyder.wrapper.R;


public class DefaultStatusProvider {

    /**
     * 加载中的默认实现
     */
    public static class DefaultLoadingStatusProvider extends StatusProvider {

        public DefaultLoadingStatusProvider() {
            super(DefaultStatus.STATUS_LOADING);
        }

        @Override
        public View getStatusView(ViewGroup containerView) {
            return View.inflate(containerView.getContext(), R.layout.layout_status_loading, null);
        }
    }

    /**
     * 网络异常的默认实现
     */
    public static class DefaultNoNetWorkStatusProvider extends StatusProvider {

        public DefaultNoNetWorkStatusProvider() {
            super(DefaultStatus.STATUS_NO_NETWORK);
        }

        @Override
        public View getStatusView(ViewGroup containerView) {
            return View.inflate(containerView.getContext(), R.layout.layout_status_no_network, null);
        }
    }

    /**
     * 空态页的默认实现
     */
    public static class DefaultEmptyStatusProvider extends StatusProvider {

        public DefaultEmptyStatusProvider() {
            super(DefaultStatus.STATUS_EMPTY);
        }

        @Override
        public View getStatusView(ViewGroup containerView) {
            return View.inflate(containerView.getContext(), R.layout.layout_status_empty, null);
        }
    }

    /**
     * 未登录的默认实现
     */
    public static class DefaultNotLoginStatusProvider extends StatusProvider {

        public DefaultNotLoginStatusProvider() {
            super(DefaultStatus.STATUS_NO_NETWORK);
        }

        @Override
        public View getStatusView(ViewGroup containerView) {
            return View.inflate(containerView.getContext(), R.layout.layout_status_not_login, null);
        }
    }


    /**
     * 网络异常的默认实现
     */
    public static class DefaultLoadErrorStatusProvider extends StatusProvider {

        public DefaultLoadErrorStatusProvider() {
            super(DefaultStatus.STATUS_LOAD_ERROR);
        }

        @Override
        public View getStatusView(ViewGroup containerView) {
            return View.inflate(containerView.getContext(), R.layout.layout_status_not_login, null);
        }
    }
}
