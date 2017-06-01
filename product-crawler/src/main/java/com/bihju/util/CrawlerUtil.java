package com.bihju.util;

import org.jsoup.nodes.Element;

public class CrawlerUtil {

    public static int getResultIndex(Element element) {
        String idString = element.attr("id");
        if (idString == null || idString.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(idString.substring(idString.indexOf("_") + 1));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
