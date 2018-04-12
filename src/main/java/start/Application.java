package start;


import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class Application extends NanoHTTPD {
    public static void main(String[] args)  {
        try {
            new Application();
        } catch (IOException ex) {
            System.err.println("We crashed and burned cause of: " + ex);
        }
    }

    private Application() throws IOException {
        super(8080);

        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Rejoice! For we are live on port 8080!");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><p>Remember!</p></html>";
        System.out.println(session.getMethod().toString() + " " + session.getUri());

        return newFixedLengthResponse(msg);
    }

}
