package hcmute.spkt.mssv19110218.chatappzalo.Controller;
import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder; //khởi tạo MediaRecorder

    //tạo hàm để set những thứ cần thiết của mediarecorder
    private void initMediaRecorder(){
        mediaRecorder = new MediaRecorder(); //Khởi tạo mediarecorder
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //xác định nguồn âm thanh
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //xác định file Output
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //Xác định mã hóa âm thanh
    }

    //hàm khi bắt đầu
    public void start(String filePath) throws IOException {
        //nếu mediaRecorder rỗng thì chạy hàm initMediaRecorder
        if (mediaRecorder == null) {
            initMediaRecorder();
        }
        mediaRecorder.setOutputFile(filePath); //set outputFile bằng filepath
        mediaRecorder.prepare(); //Chuẩn bị cho máy ghi bắt đầu ghi và mã hóa dữ liệu.
        mediaRecorder.start(); //bắt đầu ghi âm
    }

    //hàm khi dừng
    public void stop() {
        //chạy hàm mediaRecorder.stop() để dừng ghi âm
        try {
            mediaRecorder.stop();
            destroyMediaRecorder();
        }
        //nếu có lỗi sẽ catch Exception và in lỗi
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //hủy âm thanh đã ghi âm
    private void destroyMediaRecorder() {
        //giải phòng tài nguyên của mediaRecorder
        //Phương thức này được gọi khi đã sử dụng xong MediaRecorder
        mediaRecorder.release();
        mediaRecorder = null;
    }
}