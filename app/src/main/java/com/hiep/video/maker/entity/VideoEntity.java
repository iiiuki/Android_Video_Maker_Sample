package com.hiep.video.maker.entity;

import java.io.Serializable;

/**
 * Created by Hiep on 7/14/2016.
 */
public class VideoEntity implements Serializable{
    private int id = -1;
    private long createTime = -1;
    private String filePath = null;
    private String fileName = null;
    private boolean isSelect = false;
    private long duration=-1;

    public VideoEntity() {
        super();
    }

    public VideoEntity(int id, long createTime, String filePath, String fileName, boolean isSelect) {
        this.id = id;
        this.createTime = createTime;
        this.filePath = filePath;
        this.fileName = fileName;
        this.isSelect = isSelect;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
