package io.programminglife.androidvideostreamserver.websocket;

import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONObject;

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

        httpServer.get("/get-image", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Log.i(tag, "Request received!");
                //try {
                response.setContentType("image/jpeg");
                response.send(fileUtil.base64EncodedImage());
//                    response.sendStream(fileUtil.getFile(),
//                            fileUtil.getFile().length());
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                //response.send("Hello!!!");
            }
        });

        httpServer.get("/get-media", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                JSONObject mediaFiles = new FileUtil().getMediaFilesList();
                response.setContentType("application/json");
                response.send(mediaFiles);
            }
        });

        httpServer.listen(mAsyncServer, 8080);
    }

}
