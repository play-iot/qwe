package io.zero88.qwe.file;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * File utils for a {@code binary file}, in other words, it is kind of zip file, large file
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
//TODO implement it
public class BinaryFile extends AsyncFileHelper {

    @Default
    protected final int maxSize = 1024;

}
