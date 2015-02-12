# rtsp-client


First start up ffplay to act as the server:
ffplay -rtsp_flags listen rtsp://localhost:8888/video.sdp?tcp

Then run the RtspClientApp.
