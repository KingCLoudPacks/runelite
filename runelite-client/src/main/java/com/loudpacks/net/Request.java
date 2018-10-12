package com.loudpacks.net;

import com.google.gson.Gson;
import java.util.LinkedHashMap;

public class Request {

    private final RequestType type;
    private final Gson gson;
    private LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();

    public Request(RequestType type) {
        this.type = type;
        gson = new Gson();
    }

    public String getJsonData() {
        return gson.toJson(new RequestData(type, parameters));
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }

    public LinkedHashMap<String, String> getParameters() {
        return parameters;
    }

    public RequestType getType() {
        return type;
    }

    private class RequestData {

        private final int type;
        private final LinkedHashMap<String, String> parameters;

        public RequestData(RequestType type, LinkedHashMap<String, String> parameters) {
            this.type = type.getId();
            this.parameters = parameters;
        }

    }
}
