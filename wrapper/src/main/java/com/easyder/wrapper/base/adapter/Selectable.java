package com.easyder.wrapper.base.adapter;

import java.util.List;

/**
 * Auther:  winds
 * Data:    2017/12/21
 * Desc:    适配器选择方法接口
 */

public interface Selectable<T> {

    boolean isSelected(T item);

    void toggleSelection(T item);

    void clearSelection();

    int getSelectedCount();

    List<T> getSelection();
}
