package com.hiep.video.maker.util;

import com.hiep.video.maker.entity.VideoEntity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hiep on 7/14/2016.
 */
public class Util {
    public static String convertDuration(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;

        StringBuilder b = new StringBuilder();
        b.append(minutes == 0 ? "0" : minutes < 10 ? String.valueOf("" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        return b.toString();
    }

    public static String formatDuration2(int duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long mini=duration%1000;
        String str=String.valueOf(mini);
        str=str.substring(0,1);
        StringBuilder b = new StringBuilder();
        b.append("00:");
        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        b.append(".").append(str+"00");
        return b.toString();
    }
    public static String diffTime(Date date) {
        long time = date.getTime() / 1000;
        long now = new Date().getTime() / 1000;
        long diff = now - time;
        int minutes = (int) diff / (60);
        String des = "";
        if (minutes <= 0) {
            des += " vừa xong";
        } else if (minutes < 60) {
            des += minutes + " phút trước";
        } else if (minutes < 1440) {
            des += (minutes / 60) + " giờ trước";
        } else if (minutes < 43200) {
            int numberOfDays = minutes / 1440;
            if (numberOfDays < 7) {
                if (numberOfDays == 1) {
                    des = "Hôm qua";
                } else
                    des += numberOfDays + " ngày trước";
            } else if (numberOfDays < 14) {
                des = "1 tuần trước";
            } else if (numberOfDays < 21) {
                des = "2 tuần trước";
            } else
                des = "3 tuần trước";
        } else if (minutes < 518400) {
            des += (minutes / 43200) + " tháng trước";
        } else {
            des += (minutes / 518400) + " năm trước";
        }

        return des;
    }

    public static String convertDurationMili(long duration) {
        long seconds = (duration / 1000) % 60;
        long minutes = (duration / (1000 * 60)) % 60;
        long mini=duration%1000;
        String str=String.valueOf(mini);
        str=str.substring(0,1);
        StringBuilder b = new StringBuilder();
        b.append(minutes == 0 ? "0" : minutes < 10 ? String.valueOf("" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        b.append(".").append(str);
        return b.toString();
    }

    public static void sort(ArrayList<VideoEntity> listMyVideos){
        if (listMyVideos==null || listMyVideos .size() ==0){
            return;
        }else {
            for (int i = 0; i < listMyVideos.size(); i++) {
                for (int j = 1; j < (listMyVideos.size() - i); j++) {
                    long  createTime1=listMyVideos.get(j-1).getCreateTime();
                    long  createTime2=listMyVideos.get(j).getCreateTime();
                    if (createTime1 < createTime2) {
                        VideoEntity videoEntity=listMyVideos.get(j-1);
                        listMyVideos.set(j-1,listMyVideos.get(j));
                        listMyVideos.set(j,videoEntity);
                    }
                }
            }
        }
    }
}
