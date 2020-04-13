package com.easyder.wrapper.base.adapter;

import java.util.List;

/**
 * @author 刘琛慧
 * @version 1.0
 * @date 2017/3/4
 */

interface DataHelper<T> {

    List<T> getDataSet();

    void updateAll(List<T> newData);

    void addAll(List<T> newData);

    void clear();

    void addHead(T data);

    void append(T data);

    void replace(int index, T data);

    void replace(T oldData, T newData);

    void insert(int index, T data);

    void remove(int index);

    void remove(T data);

    void removeHead();

    void removeTail();

    void removeAll(List<T> datas);
}

