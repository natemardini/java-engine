package engine.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Route {

    @Getter @Setter
    private String path = "^/////";

    @Getter
    private List<String> methods = new ArrayList<>();

    @Getter
    private Controller controller;

    @Getter
    private List<String> paramNames = new ArrayList<>();

    public Route(String path, List<String> methods, Controller controller) {
        this.path = processPath(path);
        this.methods = methods;
        this.controller = controller;
    }

    public Route(String path, String method, Controller controller) {
        this(path, Collections.singletonList(method), controller);
    }

    private String processPath(String path) {
        int i = 2;

        while (path.contains("{") && path.contains("}")) {
            int startPos = path.indexOf("{");
            int endPos = path.indexOf("}");
            String name = path.substring(startPos + 1, endPos);

            if (paramNames.contains(name)) {
                name = name + String.valueOf(i);
                i++;
            } else {
                i = 2;
            }

            path = path.substring(0, startPos) + "(?<" + name + ">\\w+)" + path.substring(endPos + 1);
            paramNames.add(name);
        }
        return path;
    }
}
