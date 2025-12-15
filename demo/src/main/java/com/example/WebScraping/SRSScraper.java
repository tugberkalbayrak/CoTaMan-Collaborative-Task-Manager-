package com.example.WebScraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import com.example.Entity.*;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SRSScraper implements IDataFetcher {

    private static final String STARS_BASE_URL = "https://stars.bilkent.edu.tr";
    private static final String SRS_LOGIN_URL = STARS_BASE_URL + "/accounts/login";

    private HttpClient client;
    private CookieManager cookieManager;
    private boolean isLoggedIn = false;

    public SRSScraper() {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.client = HttpClient.newBuilder().cookieHandler(this.cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public boolean sendLoginRequest(String ID, String password) {

        try {
            System.out.println("Fetching login token from SRS...");

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SRS_LOGIN_URL))
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            String loginPageHtml = getResponse.body();

            Document doc = Jsoup.parse(loginPageHtml);
            Element tokenInput = doc.selectFirst("input[name=LoginForm[username]]");
            Element viewStateElement = doc.selectFirst("input[name=__VIEWSTATE]");
            Element eventValidationElement = doc.selectFirst("input[name=__EVENTVALIDATION]");

            if (tokenInput == null) {
                System.err.println("Could not find login token. Moodle structure might have changed.");
                return false;
            }

            // 4. Get the value inside the tags
            String viewState = viewStateElement.val();
            String eventValidation = eventValidationElement.val();
            String loginToken = tokenInput.val();
            System.out.println("Token found: " + loginToken.substring(0, 10) + "...");

            // Step B: POST the credentials + token
            // We need to format data as: username=abc&password=123&logintoken=xyz
            Map<String, String> formData = new HashMap<>();
            formData.put("username", ID);
            formData.put("password", password);
            formData.put("logintoken", loginToken);
            formData.put("__VIEWSTATE", viewState);
            formData.put("__EVENTVALIDATION", eventValidation);

            String formBody = buildFormData(formData);

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(SRS_LOGIN_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            System.out.println("Sending login request...");
            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

            // Step C: Verify success
            // If login fails, Moodle usually keeps you on the login page containing "Invalid login"
            if (postResponse.body().contains("SMS Verification")) {
                this.isLoggedIn = true;
                System.out.println("Login Successful! User is authenticated.");
                return true;
            } else {
                System.err.println("Login Failed. Check credentials.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }

    // Helper to turn a Map into "key=value&key2=value2" string
    private String buildFormData(Map<String, String> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0)
                builder.append("&");
            builder.append(java.net.URLEncoder.encode(entry.getKey(), java.nio.charset.StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(java.net.URLEncoder.encode(entry.getValue(), java.nio.charset.StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

}
