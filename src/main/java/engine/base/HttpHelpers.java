package engine.base;

public abstract class HttpHelpers {
    public static abstract class StatusCodes {
        public static final int CONTINUE = 100;

        public static final int OK = 200;
        public static final int CREATED = 201;
        public static final int NO_CONTENT = 204;

        public static final int MOVED_PERMANENTLY = 301;
        public static final int FOUND = 302;
        public static final int NOT_MODIFIED = 304;
        public static final int TEMPORARY_REDIRECT = 307;

        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int CONFLICT = 409;

        public static final int INTERNAL_SERVER_ERROR = 500;

        public static String statusCodeText(int statusCode) {
            String text;

            switch (statusCode) {
                case OK:
                    text = "OK";
                    break;
                case CREATED:
                    text = "Created";
                    break;
                case CONTINUE:
                    text = "Continue";
                    break;
                case NO_CONTENT:
                    text = "No Content";
                    break;
                case MOVED_PERMANENTLY:
                    text = "Moved Permanently";
                    break;
                case FOUND:
                    text = "Found";
                    break;
                case NOT_MODIFIED:
                    text = "Not Modified";
                    break;
                case TEMPORARY_REDIRECT:
                    text = "Temporary Redirect";
                    break;
                case BAD_REQUEST:
                    text = "Bad Request";
                    break;
                case UNAUTHORIZED:
                    text = "Unauthorized";
                    break;
                case FORBIDDEN:
                    text = "Forbidden";
                    break;
                case NOT_FOUND:
                    text = "Not Found";
                    break;
                case CONFLICT:
                    text = "Conflict";
                    break;
                case INTERNAL_SERVER_ERROR:
                    text = "Internal Server Error";
                    break;
                default:
                    text = "OK";
            }
            return text;
        }
    }

    public static abstract class Methods {
        public static final String HEAD = "HEAD";
        public static final String OPTIONS = "OPTIONS";
        public static final String GET = "GET";
        public static final String POST = "POST";
        public static final String PUT = "PUT";
        public static final String PATCH = "PATCH";
        public static final String DELETE = "DELETE";
    }

}
