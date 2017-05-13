package com.benparvar.sousvide.infrastructure;

import static com.benparvar.sousvide.infrastructure.Constants.ErrorCode.NO_ERROR;

/**
 * Created by alans on 07/05/2017.
 */
public class OperationResult<T> {
    private T mResult;
    private int mError = NO_ERROR;

    public int getError() {
        return mError;
    }

    public void setError(int error) {
        mError = error;
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }
}

