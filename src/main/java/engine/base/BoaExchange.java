package engine.base;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

public class BoaExchange {

	@Getter
    private Socket socket;

    @Getter @Setter
    private Map<String, String> responseHeader = new HashMap<>();

    @Getter
    private Map<String, String> requestHeader = new HashMap<>();

    @Getter
    private String body;

    @Getter
    private int statusCode = 0;

    @Getter
    private Map<String, String> params = new HashMap<>();

    private String responseBody = "";

    @Getter
    private String method;

    @Getter
    private String path;

    @Getter
    private String httpProtocol;

    public BoaExchange(Socket socket) {
        this.socket = socket;

        responseHeader.put("Server", "Boa/0.0.2");
        responseHeader.put("Connection", "Keep-Alive");
        responseHeader.put("Date", new Date().toString());
        responseHeader.put("Content-Type", "text/html");
        responseHeader.put("Content-Length", "0");

        this.parseRequest();
    }

    public void setContentType(String type) {
        responseHeader.replace("Content-Type", type);
    }

    public void setContentLength(int length) {
        responseHeader.replace("Content-Length", String.valueOf(length));
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public void addBody(byte[] raw, String encoding) {
        if (raw.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (byte aRaw : raw) {
                sb.append((char) aRaw);
            }

            addBody(sb.toString(), encoding);
        }
    }

    public void addBody(String raw, String encoding) {
        if (raw.length() > 0) {
            if (encoding.equals("json") || encoding.equals("application/json")) {
                encoding = "application/json";
            }

            setContentType(encoding);
            setContentLength(raw.length());

            responseBody = raw;
        }
    }

    public void addBody(Object object) {
        if (object != null) {
            Gson g = new Gson();
            String json = g.toJson(object);
            addBody(json, "application/json");
        }
    }

    private String compileResponseHeader() {
        StringBuilder sb = new StringBuilder();

        getResponseHeader().forEach((key, value) -> sb.append(key).append(": ").append(value).append("\r\n"));

        return sb.toString();
    }

    public void respond() {
        respond(200);
    }

    public void respond(int statusCode) {
        respond(statusCode, responseBody);
    }

    public void respond(String body) {
        respond(200, body);
    }

    public void respond(int statusCode, String body) {
        try {
            addBody(body, "text/html");
            String headers = compileResponseHeader();
            String statusCodeText = HttpHelpers.StatusCodes.statusCodeText(statusCode);

            String httpResponse = String.format(
                    "HTTP/1.1 %s %s\r\n%s\r\n%s",
                    statusCode,
                    statusCodeText,
                    headers,
                    body
            );

            socket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            this.statusCode = statusCode;
            this.close();
        } catch (IOException ioe) {
            System.err.println("Mayday: " + ioe);
        }
    }

    public void respond(Object object) {
        addBody(object);
        respond();
    }

    public void render(String view) {
        render(view, new HashMap<>());
    }

    public void render(String view, Map<String, String> vars) {
        render(200, view, vars);
    }

    public void render(int statusCode, String view, Map<String, String> vars) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("views/" + view + ".twig");
        JtwigModel model = JtwigModel.newModel();

        vars.putAll(this.getParams());
        vars.forEach(model::with);

        String html = template.render(model);

        respond(statusCode, html);
    }

    private void parseRequest() {
        try {
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            String line = reader.readLine();

            while (!line.isEmpty()) {
                if (line.contains(": ")) {
                    String[] pairs = line.split(":\\s");
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

    public Object parseBodyToType(Class<?> type) {
        if (body == null) {
            return null;
        }

        Gson g = new Gson();
        return g.fromJson(body, type);
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

    public void close() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println("Could not close socket!" + e);
            }
        }
    }
}
