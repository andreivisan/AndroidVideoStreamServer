package io.programminglife.androidvideostreamserver.websocket;

import android.util.Log;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.programminglife.androidvideostreamserver.util.FileUtil;

/**
 * Created by andreivisan on 11/4/15.
 */
public class HttpServer {

    private static final String tag = HttpServer.class.getSimpleName();

    private FileUtil fileUtil;

    public HttpServer() {
        fileUtil = new FileUtil();
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
                response.send(fileUtil.base64EncodedVideo(fileName));
            }
        });

        httpServer.listen(mAsyncServer, 8080);
    }

}
