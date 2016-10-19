package com.hiep.video.maker.entity;

import java.io.Serializable;

/**
 * Created by Hiep on 7/21/2016.
 */
public class AudioEntity implements Serializable{
    private String audioPath;
    private String audioTitle;
    private String audioSize;
    private boolean isPlay=false;

    public AudioEntity() {
        super();
    }

    public AudioEntity(String audioPath, String audioTitle, String audioSize) {
        this.audioPath = audioPath;
        this.audioTitle = audioTitle;
        this.audioSize = audioSize;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public String getAudioSize() {
        return audioSize;
    }

    public void setAudioSize(String audioSize) {
        this.audioSize = audioSize;
    }
}
