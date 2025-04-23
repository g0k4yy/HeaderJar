import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.util.ArrayList;

public class HeaderJarHttpHandler implements HttpHandler {

    private final String[] rawString;
    private final ArrayList<String>[] headers;
    private final ArrayList<String>[] headerValues;
    private final Object profilesLock = new Object(); // Lock object for synchronization
    public MontoyaApi api;

    public HeaderJarHttpHandler(HeaderJarUserInterface ui, String[] rawString, MontoyaApi mapi) {
        this.rawString = rawString;
        this.api = mapi;

        int profileCount = rawString.length;
        headers = new ArrayList[profileCount];
        headerValues = new ArrayList[profileCount];

        for (int i = 0; i < profileCount; i++) {
            headers[i] = new ArrayList<>();
            headerValues[i] = new ArrayList<>();
        }

        this.parseProfilesHeaders();
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {
        HttpRequest newRequest = httpRequestToBeSent;

        if (!httpRequestToBeSent.hasHeader("X-HeaderJar")) {
            return RequestToBeSentAction.continueWith(httpRequestToBeSent);
        }

        int profile;
        try {
            profile = Integer.parseInt(httpRequestToBeSent.headerValue("X-HeaderJar"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid HeaderJar header value: " + httpRequestToBeSent.headerValue("HeaderJar"), e);
        }

        synchronized (profilesLock) {
            if (profile < 0 || profile >= headers.length ||
                headers[profile] == null || headerValues[profile] == null) {
                throw new IllegalStateException("Headers or header values for profile " + profile + " are not initialized.");
            }

            for (int i = 0; i < headers[profile].size(); i++) {
                String header = headers[profile].get(i);
                String value = headerValues[profile].get(i);

                if (newRequest.hasHeader(header)) {
                    newRequest = newRequest.withUpdatedHeader(header, value);
                } else {
                    newRequest = newRequest.withAddedHeader(header, value);
                }
            }
        }

        newRequest = newRequest.withRemovedHeader("X-HeaderJar");
        return RequestToBeSentAction.continueWith(newRequest);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }

    public String[] getRawString() {
        return rawString;
    }

    public void parseProfilesHeaders() {
        synchronized (profilesLock) {
            for (int i = 0; i < headers.length; i++) {
                headers[i].clear();
                headerValues[i].clear();
            }

            for (int i = 0; i < rawString.length; i++) {
                if (rawString[i] != null && !rawString[i].isEmpty()) {
                    String[] headerPairs = rawString[i].split("\n");

                    for (String headerPair : headerPairs) {
                        headerPair = headerPair.trim();

                        if (headerPair.contains(":")) {
                            String[] combinedPairs = headerPair.split(" (?=[^:]*:)");

                            for (String pair : combinedPairs) {
                                pair = pair.trim();
                                String[] splitPair = pair.split(":", 2);

                                if (splitPair.length == 2) {
                                    String headerName = splitPair[0].trim();
                                    String headerValue = splitPair[1].trim();

                                    headers[i].add(headerName);
                                    headerValues[i].add(headerValue);
                                } else {
                                    System.out.println("Skipping invalid header pair: " + pair);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<String> getHeadersForProfile(int profileIndex) {
        synchronized (profilesLock) {
            if (profileIndex >= 0 && profileIndex < rawString.length) {
                return new ArrayList<>(headers[profileIndex]); // Defensive copy
            }
            return null;
        }
    }

    public ArrayList<String> getValuesForProfile(int profileIndex) {
        synchronized (profilesLock) {
            if (profileIndex >= 0 && profileIndex < rawString.length) {
                return new ArrayList<>(headerValues[profileIndex]); // Defensive copy
            }
            return null;
        }
    }
}
