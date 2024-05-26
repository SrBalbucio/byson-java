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

    public static JSONObject deserialize(ByteBuffer buffer, boolean debug) throws IOException {
        if (buffer != null) {
            JSONObject json = new JSONObject();
            byte[] bytes = BysonCompressHelper.decompress(buffer.array());
            debug("Tamanho do buffer de deserialização: " + bytes.length, debug);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            while (data.available() > 0) {
                System.out.println("Faltando: " + data.available());
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

    public static ByteBuffer serialize(JSONObject json, boolean debug) throws IOException {
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
            debug("Tamanho do Buffer após serialize: " + out.size(), debug);
            return ByteBuffer.wrap(BysonCompressHelper.compress(out.toByteArray()));
        }
        return null;
    }

    private static void debug(String msg, boolean debug) {
        if (debug) {
            System.out.println("DEBUG: " + msg);
        }
    }
}
