package com.benparvar.sousvide.entity;

import static com.benparvar.sousvide.infrastructure.Constants.PanCommand.HEADER;
import static com.benparvar.sousvide.infrastructure.Constants.PanCommand.SEPARATOR;
import static com.benparvar.sousvide.infrastructure.Constants.PanCommand.VERB;

/**
 * Created by alans on 25/04/2017.
 */

public class Command {
    private final String header = HEADER;
    private String type;

    public String stringify() {
        return header.concat(SEPARATOR).concat(VERB);
    }
}
