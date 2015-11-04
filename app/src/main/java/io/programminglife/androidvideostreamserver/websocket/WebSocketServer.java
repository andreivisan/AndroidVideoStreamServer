package io.programminglife.androidvideostreamserver.websocket;

import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreivisan on 11/4/15.
 */
public class WebSocketServer {

    public void createWebSocketServer() {
        AsyncHttpServer httpServer = new AsyncHttpServer();
        final List<WebSocket> _sockets = new ArrayList<WebSocket>();

        httpServer.websocket("/stream", new AsyncHttpServer.WebSocketRequestCallback() {
            @Override
            public void onConnected(final WebSocket webSocket, AsyncHttpServerRequest request) {
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
                        if("test".equals(s)) {
                            webSocket.send("Success!");
                        }
                    }
                });

                _sockets.add(webSocket);
            }
        });

        httpServer.listen(8080);
    }

}
