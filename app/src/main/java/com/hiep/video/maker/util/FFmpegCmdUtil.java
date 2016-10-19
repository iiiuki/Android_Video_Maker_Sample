package com.hiep.video.maker.util;

import android.os.Environment;

/**
 * Created by Hiep on 7/14/2016.
 */
public class FFmpegCmdUtil {
    public static final String linkBorder=FileUtil.getImageBorder()+"/border_video.png";;
    public static String[] cmdCreateVideo(int frame,String videoSource, String outPut){
        float duration_frame=1.0F;
        if (frame<= 20) {
            duration_frame = 1.0F;
        }else {
            duration_frame = 0.6F;
        }
        String str5 = "-framerate 1/" + duration_frame + " -start_number 0 -i " + videoSource + " -vcodec mpeg4 -q:v 3 -r 20 -vf scale=480x480 " + outPut;
        return str5.split(" ");
    }

    public static String[] cmdAddAudiotoVideo(String linkvideo, String linkAudio,String linkOutput){
        String cmd="-i@#" + linkvideo + "@#-i@#" + linkAudio + "@#-vcodec@#" + "mpeg4@#" + "-q:v@#" + "3" + "@#-c:v@#" + "copy@#" + "-c:a@#" + "aac@#" + "-strict@#" + "experimental@#" + "-shortest@#" + linkOutput;
        String[] command = cmd.split("@#");
        return command;
    }
    public static String[] cmdAddVideoToVideo(String linkvideo,String linkVideoEffect,String linkOutput){
        String cmd="-i " + linkvideo + " -i " + linkVideoEffect + " -filter_complex [0:v][1:v]blend=shortest=1:all_mode='overlay':c0_opacity=0.1 -vcodec mpeg4 -q:v 3 -strict experimental -shortest " + linkOutput;
        String[] command = cmd.split(" ");
        return command;
    }

    public static String[] cmdAddBorderToVideo(String linkvideo,String linkOutput){
        String cmd="-i " + linkvideo + " -i " + linkBorder + " -filter_complex [0:v][1:v]overlay=0:0 -vcodec mpeg4 -q:v 3 -acodec copy " + linkOutput;
        String[] command = cmd.split(" ");
        return command;
    }

    public static String[] cmdAddIMageToVideo(String linkvideo,String linkOutput,int left,int top){
        String linkImage=FileUtil.getVideoEffect()+"/gif_money.gif";
        Logger.d(linkImage);
        String cmd="-i " + linkvideo + " -i " + linkImage + " -filter_complex [0:v][1:v]overlay="+left+":"+top+" -vcodec mpeg4 -q:v 3 -acodec copy " + linkOutput;
        String[] command = cmd.split(" ");
        return command;
    }

    public static String[] cmdAddsticker(String linkvideo,String linkOutput,String linkSticker,int left,int top,int startTime,int endTime){

        Logger.d(linkSticker);
        String cmd="-i " + linkvideo + " -i " + linkSticker + " -filter_complex [0:v][1:v]overlay="+left+":"+top+":enable=between(t\\,"+startTime+"\\,"+endTime+") -codec:a copy  " + linkOutput;
        String[] command = cmd.split(" ");
        return command;
    }
}
