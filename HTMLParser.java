package crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLParser {

    public static List<String> getLinks(String text) {

        List<String> links = new ArrayList<>();

        Pattern pattern = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=[\"'](.*?)[\"']", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            links.add(matcher.group(1));
        }

        return links;
    }

    public static String parseTitle(String text) {

        Pattern pattern = Pattern.compile("<title>(.*?)<\\/title>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return " ";
    }

    public static String parseContent(String url) throws IOException {

        final URLConnection URLStream = new URL(url).openConnection();
        URLStream.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
        InputStream inputStream = null;
        if (!URLStream.getContentType().matches(".*text\\/html.*")) {
            throw new IOException("error");
        }
        inputStream = URLStream.getInputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder stringBuilder = new StringBuilder();

        String nextLine;
        while ((nextLine = reader.readLine()) != null) {
            stringBuilder.append(nextLine);
        }

        return stringBuilder.toString();
    }

    public static String parseHost(String rawLink) {
        try {
            return new URI(rawLink).getHost();
        } catch (URISyntaxException e) {
            return rawLink;
        }
    }

    public static String parseURL(String rawLink, String defaultHost) {

        try {
            StringBuilder sb = new StringBuilder();

            if (rawLink.matches("^/+.*")) {
                sb.append(rawLink.replaceAll("^/+", "https://"));
            } else if (!rawLink.matches("^[^/]*https?:.*")) {
                sb.append("https://").append(defaultHost).append("/").append(rawLink);
            } else {
                sb.append(rawLink);
            }

            URI uri = new URI(sb.toString());

            String scheme = uri.getScheme() != null ? uri.getScheme() : "https";
            String host = uri.getHost() != null ? uri.getHost() : defaultHost;
//            String path = !uri.getPath().equals("") ? uri.getPath().replaceAll("([^/])$", "$1/") : "/";
            String path = !uri.getPath().equals("") ? uri.getPath() : "/";
            String port = uri.getPort() != -1 ? ":" + uri.getPort() : "";

            return "http://localhost:25555" + path;
            //return scheme + "://" + host + port + path;
        } catch (URISyntaxException e) {
            return rawLink;
        }
    }
}
