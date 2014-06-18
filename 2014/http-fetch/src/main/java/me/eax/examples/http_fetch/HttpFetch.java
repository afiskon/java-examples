package me.eax.examples.http_fetch;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.commons.io.*;
import org.apache.commons.lang3.tuple.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class HttpFetch {
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                                                        "(KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

    public static void main(String[] args) {
        if(args.length == 3) {
            try {
                process(args[0], args[1], args[2]);
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage: " + executableName() + " <domain> <YYYY-MM-DD> <number>");
        }
    }

    private static void process(String domainStr, String dateStr, String numberStr) throws IOException {
        String s = getUrl("http://www.liveinternet.ru/stat/" + domainStr +
                "/pages.html?date=" + dateStr +
                "&period=month&total=yes&per_page=100");
        ArrayList< Pair<String, Integer> > pages = parseLiveinternatStat(domainStr, s);
        int maxNumber = Integer.parseInt(numberStr);
        int currentNumber = 0;
        System.out.println("<ul>");
        for(Pair<String, Integer> p : pages) {
            String url = p.getLeft();
            int views = p.getRight();
            String title = getPageTitle(url);
            String end = ending(views);
            System.out.printf("<li><a href=\"%s\">%s</a> %d просмотр%s за месяц</a></li>\n", url, title, views, end);

            currentNumber++;
            if(currentNumber >= maxNumber) break;
        }
        System.out.println("</ul>");
    }

    private static String getPageTitle(String url) throws IOException {
        String s = getUrl(url);
        Pattern pattern = Pattern.compile("<h2>(.*?)</h2>");
        Matcher m = pattern.matcher(s);
        if(!m.find()) throw new IOException("Failed to find page title");
        return m.group(1);
    }

    private static ArrayList< Pair<String, Integer> > parseLiveinternatStat(String domainStr, String s) {
        Pattern pattern = Pattern.compile("(?s)for=\"id_\\d+\"><a href=\"([^\"]+)\"[^>]*>.*?<td>([\\d,]+)</td>");
        Matcher matcher = pattern.matcher(s);
        ArrayList< Pair<String, Integer>> result = new ArrayList<>();
        String rootUrl = "http://" + domainStr + "/";
        String pageUrlStart = "http://" + domainStr + "/page/";
        String tagUrlStart = "http://" + domainStr + "/tag/";
        while(matcher.find()) {
            String url = matcher.group(1);
            if(url.equals(rootUrl)) continue;
            if(url.startsWith(pageUrlStart)) continue;
            if(url.startsWith(tagUrlStart)) continue;
            String numStr = matcher.group(2);
            Integer num = Integer.parseInt(numStr.replace(",", ""));
            result.add(Pair.of(url, num));
        }
        return result;
    }

    private static String getUrl(String uri) throws IOException {
        HttpGet req = new HttpGet(uri);
        req.setHeader("User-Agent", DEFAULT_USER_AGENT);
        try ( CloseableHttpClient client = HttpClients.createDefault();
              CloseableHttpResponse response = client.execute(req) ) {
            InputStream inputStream = response.getEntity().getContent();
            return IOUtils.toString(inputStream);
        }
    }

    private static String ending(int views) {
        int rem100 = views % 100;
        if(rem100 >= 5 && rem100 <= 20) {
            return "ов";
        } else {
            int rem10 = views % 10;
            if(rem10 == 1) {
                return "";
            } else if (rem10 >= 2 && rem10 <= 4) {
                return "а";
            } else {
                return "ов";
            }
        }
    }

    private static String executableName() {
        return System.getProperty("sun.java.command");
    }

}
