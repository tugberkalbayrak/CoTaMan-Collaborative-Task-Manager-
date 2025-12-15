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

    public void sendLoginRequest(String ID, String password) {

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

            if (tokenInput == null) {
                System.err.println("Could not find login token. Moodle structure might have changed.");
                return false;
            }
            

        }
    }
    
}
