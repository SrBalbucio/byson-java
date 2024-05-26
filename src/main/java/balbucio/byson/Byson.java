package balbucio.byson;

import lombok.Getter;
import org.json.JSONObject;

import javax.xml.crypto.Data;
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

    public void deserialize(boolean debug) throws IOException {
        if (buffer != null) {
            this.json = new JSONObject();
            byte[] bytes = buffer.array();
            debug("Tamanho do buffer de deserialização: "+bytes.length, debug);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            while (data.available() > 0) {
                String key = data.readUTF();
                short type = data.readShort();
                Object obj = null;

                if (type == 0) {
                    obj = data.readInt();
                } else if (type == 1) {
                    obj = data.readUTF();
                }
                this.json.put(key, obj);
            }
        }
    }

    protected void serialize() throws IOException {
        serialize(false);
    }

    public void serialize(boolean debug) throws IOException {
        if (json != null && !json.isEmpty()) {
            List<byte[]> inputs = new ArrayList<>();

            for (String s : json.keySet()) {
                Object obj = json.get(s);
                ByteArrayOutputStream inputStream = getBytesByType(s, obj);
                if (inputStream != null) {
                    inputStream.flush();
                    inputs.add(inputStream.toByteArray());
                }
            }

            this.buffer = ByteBuffer.allocate(inputs.stream().mapToInt(bytes -> bytes.length).sum());
            inputs.forEach(by -> {
                System.out.println(by.length);
                buffer.put(by);
            });
            debug("Tamanho do Buffer após serialize: " + buffer.capacity(), debug);
        }
    }

    private static ByteArrayOutputStream getBytesByType(String s, Object obj) throws IOException {
        ByteArrayOutputStream inputStream = null;
        if (obj instanceof Integer) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 8 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(0);
            data.writeInt((Integer) obj);
            data.flush();
        } else if (obj instanceof String) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + ((String) obj).getBytes(StandardCharsets.UTF_8).length + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(1);
            data.writeUTF((String) obj);
            data.flush();
        }
        return inputStream;
    }

    private void debug(String msg, boolean debug) {
        if (debug) {
            System.out.println("DEBUG: " + msg);
        }
    }

}
