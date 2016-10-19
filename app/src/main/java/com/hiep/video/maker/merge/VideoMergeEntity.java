package com.hiep.video.maker.merge;

import java.io.Serializable;

/**
 * Created by Hiep on 7/25/2016.
 */
public class VideoMergeEntity implements Serializable {
    private String filePath;
    private String key;

    public VideoMergeEntity(String key, String filePath) {
        this.filePath = filePath;
        this.key = key;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
