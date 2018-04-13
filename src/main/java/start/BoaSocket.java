package start;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BoaSocket implements AutoCloseable {

    private Socket socket;

    @Getter @Setter
    private Map<String, String> responseHeader = new HashMap<>();

    @Getter
    private Map<String, String> requestHeader = new HashMap<>();

    @Getter
    private String body;

    private String responseBody = "";

    @Getter
    private String method;

    @Getter
    private String path;

    @Getter
    private String httpProtocol;

    public BoaSocket(Socket socket) {
        this.socket = socket;

        responseHeader.put("Server", "Boa/0.0.1");
        responseHeader.put("Content-Type", "text/html");
        responseHeader.put("Date", new Date().toString());
        responseHeader.put("Connection", "Keep-Alive");
        responseHeader.put("Content-Length", "0");

        this.parseRequest();
    }

    public void respond(Object object) {
        addBody(object);
        respond();
    }

    public void addBody(Object object) {
        if (object != null) {
            Gson g = new Gson();
            String json = g.toJson(object);
            addBody(json, "application/json");
        }
    }

    private void addBody(byte[] raw, String encoding) {
        if (raw.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (byte aRaw : raw) {
                sb.append((char) aRaw);
            }

            addBody(sb.toString(), encoding);
        }
    }

    private void addBody(String raw, String encoding) {
        if (raw.length() > 0) {
            if (encoding.equals("json") || encoding.equals("application/json")) {
                encoding = "application/json";
            }

            String contentLength = String.valueOf(raw.length());
            responseHeader.replace("Content-Type", encoding);
            responseHeader.replace("Content-Length", contentLength);

            responseBody = raw;
        }
    }

    private String compileResponseHeader() {
        StringBuilder sb = new StringBuilder();

        getResponseHeader().forEach((key, value) -> sb.append(key).append(": ").append(value).append("\r\n"));

        return sb.toString();
    }

    public void respond(int statusCode, String body) {
        try {
            addBody(body, "text/html");
            String headers = compileResponseHeader();
            String statusCodeText = HttpHelpers.statusCodeText(statusCode);

            String httpResponse = String.format(
                    "HTTP/1.1 %s %s\r\n%s\r\n%s",
                    statusCode,
                    statusCodeText,
                    headers,
                    body
            );

            socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            socket.close();
        } catch (IOException ioe) {
            System.err.println("Mayday: " + ioe);
        }
    }

    public void respond(int statusCode) {
        respond(statusCode, responseBody);
    }

    public void respond() {
        respond(200);
    }

    public void respond(String body) {
        respond(200, body);
    }

    private void parseRequest() {
        try {
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            String line = reader.readLine();

            while (!line.isEmpty()) {
                if (line.contains(": ")) {
                    String[] pairs = line.split(":\\s{1}");
                    if (pairs.length == 2) {
                        requestHeader.put(pairs[0], pairs[1]);
                    }
                } else if (line.contains("HTTP/")) {
                    String[] headerInfo = line.split("\\s");
                    method = headerInfo[0];
                    path = headerInfo[1];
                    httpProtocol = headerInfo[2];
                }

                line = reader.readLine();
            }

            parseBody(reader);
        } catch (IOException ioe) {
            System.err.println("Mayday: " + ioe);
        }
    }

    private void parseBody(BufferedReader reader) {
        String contentLength = requestHeader.getOrDefault("Content-Length", "0");
        long contentSize = Long.parseLong(contentLength);

        if (contentSize > 0) {
            StringBuilder sb = new StringBuilder();
            try {
                for (int i = 0; i < contentSize; i++) {
                    sb.append((char) reader.read());
                }

                body = sb.toString();
            } catch (IOException ioe) {
                System.err.println("Mayday: " + ioe);
            }
        }
    }

    @Override
    public void close() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println("Could not close socket!");
            }
        }
    }
}
