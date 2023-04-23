package com.github.wohaopa.zeropointwrapper.update;

import java.util.Map;

public interface Updater {

    String getVersion();

    String getTargetVersion();

    Map<String, String> getDownloads();

    void preExecute();

    void postExecute();
}
