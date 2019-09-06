package com.java.ZhouXuanBai;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NetConnection {
    public static String httpRequest(String params) {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL("https://api2.newsminer.net/svc/news/queryNewsList?" + params);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();

            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public static String httpRequest() {
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL("https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2019-07-01&endDate=2019-07-03&words=特朗普&categories=科技");
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();

            InputStream inputStream = httpUrlConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            httpUrlConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}