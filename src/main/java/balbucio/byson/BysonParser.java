package balbucio.byson;

import balbucio.byson.utils.BysonCompressHelper;
import balbucio.byson.utils.BysonTypeHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BysonParser {

    public static JSONObject deserialize(ByteBuffer buffer) throws IOException {
        if (buffer != null) {
            JSONObject json = new JSONObject();
            byte[] bytes = BysonCompressHelper.decompress(buffer.array());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            while (data.available() > 0) {
                String key = data.readUTF();
                short type = data.readShort();
                Object obj = null;

                obj = BysonTypeHelper.parseObject(type, obj, data);
                json.put(key, obj);
            }
            return json;
        }
        return null;
    }

    public static ByteBuffer serialize(JSONObject json) throws IOException {
        if (json != null && !json.isEmpty()) {
            List<byte[]> inputs = new ArrayList<>();

            for (String s : json.keySet()) {
                Object obj = json.get(s);
                ByteArrayOutputStream inputStream = BysonTypeHelper.keyValueToBinary(s, obj);
                if (inputStream != null) {
                    inputStream.flush();
                    inputs.add(inputStream.toByteArray());
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(inputs.stream().mapToInt(bytes -> bytes.length).sum());
            for (byte[] in : inputs) {
                out.write(in);
            }
            return ByteBuffer.wrap(BysonCompressHelper.compress(out.toByteArray()));
        }
        return null;
    }

}
