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

            boolean complexSerialization = data.readBoolean();
            while (data.available() > 0) {
                try {
                    if(complexSerialization){
                        data.readInt();
                        data.readInt();
                    }
                    String key = data.readUTF();
                    short type = data.readShort();
                    Object obj = null;

                    obj = BysonTypeHelper.parseObject(type, obj, data, complexSerialization);
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

            ByteArrayOutputStream mtout = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(mtout);
            data.writeBoolean(false);

            for (String s : json.keySet()) {
                Object obj = json.get(s);
                ByteArrayOutputStream inputStream = BysonTypeHelper.keyValueToBinary(s, obj, false);
                if (inputStream != null) {
                    inputStream.flush();
                    inputs.add(inputStream.toByteArray());
                    inputStream.close();
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(inputs.stream().mapToInt(bytes -> bytes.length).sum());
            out.write(mtout.toByteArray());
            for (byte[] in : inputs) {
                out.write(in);
            }
            ByteBuffer buff = ByteBuffer.wrap(BysonCompressHelper.compress(out.toByteArray()));
            out.close();
            data.close();
            mtout.close();
            return buff;
        }
        return null;
    }

}
