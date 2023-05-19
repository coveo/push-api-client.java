package com.coveo.pushapiclient;

public interface BaseSource {
    /**
     * Return an instance of {@link PlatformClient}
     *
     * @return
     */
    PlatformClient getPlatformClient();

    /**
     * Returns the unique identifier of the source
     *
     * @return
     */
    String getId();

}
