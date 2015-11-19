package io.programminglife.androidvideostreamserver.websocket;

import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.programminglife.androidvideostreamserver.util.FileUtil;

/**
 * Created by andreivisan on 11/4/15.
 */
public class WebSocketServer {

    private static final String tag = WebSocketServer.class.getSimpleName();

    private FileUtil fileUtil;

    public WebSocketServer() {
        fileUtil = new FileUtil();
    }

    public void createWebSocketServer() {
        AsyncHttpServer httpServer = new AsyncHttpServer();
        AsyncServer mAsyncServer = new AsyncServer();
        final List<WebSocket> _sockets = new ArrayList<WebSocket>();

        httpServer.websocket("/stream", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
                Log.i("WebSocket", "Connecting");


                //Use this to clean up any references to your websocket
                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        try {
                            if (ex != null)
                                Log.e("WebSocket", "Error");
                        } finally {
                            _sockets.remove(webSocket);
                        }
                    }
                });

                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    @Override
                    public void onStringAvailable(String s) {
                        if ("test".equals(s)) {
                            byte[] bImage = fileUtil.getImageFile("");
                            Log.i("WebSocket", "image size " + bImage.length);
                            webSocket.send(bImage, 100, 2903784);
                        }
                    }
                });

                _sockets.add(webSocket);
            }
        });

        httpServer.listen(mAsyncServer, 8080);
    }

    public void createHttpServer() {
        AsyncHttpServer httpServer = new AsyncHttpServer();
        AsyncServer mAsyncServer = new AsyncServer();

        httpServer.get("/get-media", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    Log.i(tag, "Request for file system received!");
                    JSONArray mediaFilesArray = new FileUtil().getMediaFilesList();
                    JSONObject mediaFiles = new JSONObject();
                    mediaFiles.put("files", mediaFilesArray);
                    Log.i(tag, "Response object: " + mediaFiles);
                    response.send(mediaFiles);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        httpServer.get("/get-image", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Log.i(tag, "Request for image received!");
                String fileName = request.getQuery().getString("name");
                response.setContentType("image/jpeg");
                response.send(fileUtil.base64EncodedImage(fileName));
            }
        });

        httpServer.get("/get-video", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Log.i(tag, "Request for video received!");
                String fileName = request.getQuery().getString("name");
                response.setContentType("video/mp4");
                byte[] movieBytes = fileUtil.getFile(fileName);
                InputStream inputStream = new ByteArrayInputStream(movieBytes);
                //response.sendStream(inputStream, movieBytes.length);
                response.send(fileUtil.getFileString(fileName));
            }
        });

        httpServer.listen(mAsyncServer, 8080);
    }

}
