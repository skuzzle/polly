package de.skuzzle.polly.installer.util;

import java.io.File;
import java.io.IOException;


public interface FileAction {

    public abstract void exec(File file) throws IOException;
}