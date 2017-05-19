package com.benparvar.sousvide.infrastructure;

/**
 * Created by alans on 09/04/2017.
 */

public class Constants {
    public interface Bluetooth {
        int REQUEST_ENABLE_BT = 0;
        String END_LINE = "\n";
    }

    public interface ErrorCode {
        int NO_ERROR = -1;
        int UNKNOWN = 0;
        int NO_PAIRED_DEVICES = 1;
        int YES_PAIRED_DEVICES = 2;
    }

    // STATUS
    public interface PanStatus {
        String STS_OFF = "0";
        String STS_READY = "1";
        String STS_COOK_IN_PROGRESS = "2";
        String STS_COOK_FINISHED = "3";
    }

    // COMMAND
    public interface PanCommand {
        String HEADER = "PAN";
        String SEPARATOR = ":";
        String VERB = "V";
        String NOUN = "N";
        String STATUS = "S";
    }

    // VERB
    public interface PanVerb {
        String PAN_OFF = "000";
        String PAN_ON = "001";
        String PAN_TIMER = "002";
        String PAN_TEMPERATURE = "003";
        String PAN_TIMER_TARGET = "004";
        String PAN_TEMPERATURE_TARGET = "005";
        String PAN_CURRENT_TIMER = "006";
        String PAN_CURRENT_TEMPERATURE = "007";
        String PAN_READY = "008";
        String PAN_COOK_IN_PROGRESS = "009";
        String PAN_COOK_FINISHED = "010";
        String PID_VALUE = "011";
    }

    // ERROR CODE
    public interface PanErrorCode {
        String INVALID_HEADER = "900";
        String INVALID_VERB = "901";
        String INVALID_NOUN = "902";
        String INVALID_TIMER_TARGET = "903";
        String INVALID_TEMPERATURE_TARGET = "904";
        String INVALID_ALREADY_OFF = "905";
        String INVALID_ALREADY_COOKING = "906";
        String INVALID_ALREADY_FINISHED_COOKING = "907";
        String INVALID_NO_PROGRAMMED = "908";
    }

    public interface SecurityKeys {
        String PREFERENCE_NAME = new String(new char[]{'b', 'e', 'n', 'p', 'a', 'r', 'v', 'a', 'r',
                's', 'r', 'a', 'v', 'r', 'a', 'p', 'n', 'e', 'b' });
    }
}
