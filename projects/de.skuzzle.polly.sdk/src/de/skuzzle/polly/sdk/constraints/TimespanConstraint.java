package de.skuzzle.polly.sdk.constraints;

import de.skuzzle.polly.sdk.Types;
import de.skuzzle.polly.sdk.Types.TimespanType;


class TimespanConstraint implements AttributeConstraint {

    @Override
    public boolean accept(Types type) {
        return type instanceof TimespanType;
    }
}
