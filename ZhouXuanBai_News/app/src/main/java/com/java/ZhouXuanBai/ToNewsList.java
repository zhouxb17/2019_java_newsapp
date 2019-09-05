package com.java.ZhouXuanBai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class ToNewsList {
    public static Map[] toNewsList(String news) {
        int num = 0;
        for (int i = 0; i < news.length(); i++) {
            if (news.charAt(i) > 47 && news.charAt(i) < 58)
                num = num * 10 + (int) news.charAt(i);
            else
                break;
        }
        Map[] maps = new HashMap[num];
        ArrayList<Map> array = new ArrayList<Map>();
        //get titles
        String pattern1 = "\"title\":\"[0-9[一-龥][（][）][《][》][——][；][，][。][“][”][<] [>][！][？][：][【][】][·]]*";
        Pattern p1 = Pattern.compile(pattern1);
        Matcher m1 = p1.matcher(news);
        int n = 0;
        while (m1.find())
            maps[n++].put("title", m1.group().substring(9));

        //get content
        String pattern2 = "\"content\":\".*\",\"url";
        Pattern p2 = Pattern.compile(pattern2);
        Matcher m2 = p2.matcher(news);
        n = 0;
        while(m2.find())
            maps[n++].put("content",m2.group().substring(11).replaceAll("\",\"url", ""));

        return maps;
    }
}
