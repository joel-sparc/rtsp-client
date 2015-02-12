
import org.bytedeco.javacv.*;

import static org.bytedeco.javacpp.opencv_core.*;

public class RtspClient {

    private String ffmpeg_link = "rtsp://localhost:8888/video.sdp";

    private volatile FFmpegFrameRecorder recorder;
    private int sampleAudioRateInHz = 44100;
    private int imageWidth = 320;
    private int imageHeight = 240;
    private int frameRate = 30;


    private void initRecorder() {

        recorder = new FFmpegFrameRecorder(ffmpeg_link, imageWidth, imageHeight, 1);
        recorder.setFormat("flv");
        recorder.setSampleRate(sampleAudioRateInHz);
        // Set in the surface changed method
        recorder.setFrameRate(frameRate);

        recorder.setFormat("rtsp");
        recorder.setVideoOption("rtsp_transport", "tcp");

    }

    private void start(){
        //Create canvas frame for displaying video.
        CanvasFrame canvas = new CanvasFrame("VideoCanvas");

        //Set Canvas frame to close on exit
        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        //Declare FrameGrabber to import video from "video.mp4"
        FrameGrabber grabber = new OpenCVFrameGrabber("video.mp4");

        try {

            final long startTime = System.currentTimeMillis();

            grabber.start();

            recorder.start();

            IplImage img;

            while (true) {
                img = grabber.grab();

                //Set canvas size as per dimentions of video frame.
                canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());

                if (img != null) {
                    //Show video frame in canvas
                    canvas.showImage(img);

                    try {
                        long t = 1000 * (System.currentTimeMillis() - startTime);
                        if (t > recorder.getTimestamp()) {
                            recorder.setTimestamp(t);
                        }
                        recorder.record(img);
                    } catch (FFmpegFrameRecorder.Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        RtspClient client = new RtspClient();
        client.initRecorder();
        client.start();
    }
}
