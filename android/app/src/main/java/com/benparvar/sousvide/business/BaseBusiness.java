package com.benparvar.sousvide.business;

import android.content.Context;

/**
 * Created by alans on 07/04/2017.
 */

public abstract class BaseBusiness {
    protected Context mContext;

    private BaseBusiness() {
    }

    public BaseBusiness(Context mContext) {
        this.mContext = mContext;
    }
}
