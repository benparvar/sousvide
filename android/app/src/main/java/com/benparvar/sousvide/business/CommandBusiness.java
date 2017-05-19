package com.benparvar.sousvide.business;

import android.content.Context;
import android.util.Log;

import com.benparvar.sousvide.entity.Pan;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static com.benparvar.sousvide.infrastructure.Constants.PanCommand.HEADER;
import static com.benparvar.sousvide.infrastructure.Constants.PanCommand.SEPARATOR;
import static com.benparvar.sousvide.infrastructure.Constants.PanCommand.STATUS;
import static com.benparvar.sousvide.infrastructure.Constants.PanStatus.STS_OFF;
import static com.benparvar.sousvide.infrastructure.Constants.PanStatus.STS_READY;
import static com.benparvar.sousvide.infrastructure.Constants.PanVerb.PAN_OFF;
import static com.benparvar.sousvide.infrastructure.Constants.PanVerb.PAN_ON;

/**
 * Created by alans on 01/05/2017.
 */

public class CommandBusiness extends BaseBusiness {
    private final String TAG = "CommandBusiness";
    public CommandBusiness(Context mContext) {
        super(mContext);
    }

    public Pan parse(Pan pan, String data) {
        //Log.d(TAG, data);
        Boolean error = Boolean.FALSE;
        String string = "";
        int index = 0;
        List<String> preParsedString = Arrays.asList(data.split(SEPARATOR));
        Log.d(TAG, preParsedString.toString());

        // PAN
        string = preParsedString.get(index);
        if (string.equals(HEADER)) {
            // S
            string = preParsedString.get(index++);
            if (string.equals(STATUS)) {
                // 000
                string = preParsedString.get(index++);
                switch (string) {
                    case PAN_OFF:
                        pan.setStatus(STS_OFF);
                        break;
                    case PAN_ON:
                        pan.setStatus(STS_READY);
                        break;
                    //PAN_TIMER PAN_TEMPERATURE PAN_TIMER_TARGET PAN_TEMPERATURE_TARGET PAN_CURRENT_TIMER PAN_CURRENT_TEMPERATURE PAN_READY PAN_COOK_IN_PROGRESS  PAN_COOK_FINISHED PID_VALUE
                }
            } else {
                error = Boolean.TRUE;
            }
        } else {
            error = Boolean.TRUE;
        }

        // Error
        if (Boolean.TRUE.equals(error)) {
            Log.e(TAG, "Unable to parse: " + string);
        }

        return pan;
    }
}
