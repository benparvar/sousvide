package com.benparvar.sousvide.infrastructure;

/**
 * Created by alans on 07/05/2017.
 */
public abstract class OperationListener<T> {
    public void onSuccess(T result){}
    public void onError(int error){}
    public void onCancel(){}

    @SuppressWarnings({"UnusedParameters", "EmptyMethod"})
    public void onProgressUpdate(int progress){}
}