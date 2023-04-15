package com.example.demo.common.response;

import java.util.HashMap;

public class HTTPResponseObjectBuilder {
    private String msg;

    private HashMap<String, Object> data;


    public HTTPResponseObjectBuilder withMessage(String message) {
        this.msg = message;

        return this;
    }

    public HTTPResponseObjectBuilder withData(HashMap<String, Object> res) {
        data = res;

        return this;
    }

    public HTTPResponseObjectBuilder addData(String key, Object value) {
        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);

        return this;
    }

    public HTTPResponseObject build() {
        HTTPResponseObject resp = new HTTPResponseObject();
        resp.setMessage(msg);
        resp.setBody(data);

        return resp;
    }
}
