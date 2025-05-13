package ru.tsavaph;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class TimeTrackerConstant {

    public static final String EMPTY_STRING = "";
    public static final int NO_TIME_THRESHOLD = -1;
    public static final String SPACE = " ";
    public static final String OFFSET_SYMBOL = "|--";

}
