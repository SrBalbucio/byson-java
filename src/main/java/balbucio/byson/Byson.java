package balbucio.byson;

import lombok.Getter;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Byson {

    @Getter
    private ByteBuffer buffer;
    @Getter
    private JSONObject json;

    public Byson(JSONObject json) {
        this.json = json;
    }

    public Byson(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    protected void serialize() throws IOException {
        BysonParser.serialize(json, false);
    }
}
