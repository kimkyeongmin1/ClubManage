package com.club.management.clubmanage.CORE;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;



/**
 * Created by Andrew on 2017-05-25.
 */

public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {


    @Override
    protected String doInBackground(Map<String, String>... params) {
        String url = "HTTP://192.168.0.4:8080" + params[0].get("url");
        // HTTP 요청 준비 작업
        HttpClient.Builder http = new HttpClient.Builder("POST", url);

        Log.d("url",url);
        // 파라미터 전송
        http.addAllParameters(params[0]);

        // HTTP 요청 전송
        HttpClient post = http.create();
        post.request();

        // 응답 상태코드 가져오기
        int statusCode = post.getHttpStatusCode();

            // 응답 본문
        String body = post.getBody();

        return body;

    }

    /**
     * doInBackground 이전
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * doInBackground 이후
     * @param s
     */
    @Override
    protected void onPostExecute(String s) {
        Log.d("HTTP_RESULT", s);
    }
}
