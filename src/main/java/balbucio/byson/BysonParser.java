package balbucio.byson;

import balbucio.byson.utils.BysonCompressHelper;
import balbucio.byson.utils.BysonTypeHelper;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class allows you to directly deserialize and serialize JSON.
 */
public class BysonParser {

    /**
     * Converts the binary to JSONObject again. The binary is already compressed.
     *
     * @param buffer json in binary format
     * @return JSONObject
     * @throws IOException If it is impossible to read the bytes or they are poorly aligned, this exception will appear.
     */
    public static JSONObject deserialize(ByteBuffer buffer) throws IOException {
        if (buffer != null) {
            JSONObject json = new JSONObject();
            byte[] bytes = BysonCompressHelper.decompress(buffer.array());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            while (data.available() > 0) {
                try {
                    String key = data.readUTF();
                    short type = data.readShort();
                    Object obj = null;

                    obj = BysonTypeHelper.parseObject(type, obj, data);
                    json.put(key, obj);
                } catch (Exception e){
                    break;
                }
            }
            return json;
        }
        return null;
    }

    /**
     * Converts JSON to compressed binary using ByteBuffer to store.
     *
     * @param json JSONObject
     * @return json in binary format
     * @throws IOException If it is impossible to read the bytes or they are poorly aligned, this exception will appear.
     */
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

    public static Map<String, Integer> indexTable(ByteBuffer buffer) throws IOException {
        if (buffer != null) {
            Map<String, Integer> map = new ConcurrentHashMap<>();
            byte[] bytes = buffer.array();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            while (data.available() > 0) {
                try {
                    String key = data.readUTF();
                    int pos = bytes.length - data.available();
                    // PULAR OS BYTES
                    short type = data.readShort();
                    BysonTypeHelper.parseObject(type, null, data);
                    map.put(key, pos);
                } catch (Exception ignored) {
                    break;
                }
            }
            return map;
        }
        return null;
    }

    public static Object getObjectByPosition(ByteBuffer buffer, int pos) throws IOException {
        byte[] bytes = buffer.array();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        DataInputStream data = new DataInputStream(inputStream);

        data.skipBytes(pos);
        short type = data.readShort();
        return BysonTypeHelper.parseObject(type, null, data);
    }

}
