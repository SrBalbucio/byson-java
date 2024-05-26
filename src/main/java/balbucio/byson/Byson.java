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

    public JSONObject deserialize(ByteBuffer buffer, boolean debug) throws IOException {
        if (buffer != null) {
            JSONObject json = new JSONObject();
            byte[] bytes = buffer.array();
            debug("Tamanho do buffer de deserialização: "+bytes.length, debug);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            while (data.available() > 0) {
                String key = data.readUTF();
                short type = data.readShort();
                Object obj = null;

                if (type == 0) { // Int
                    obj = data.readInt();
                } else if (type == 1) { // Short
                    obj = data.readShort();
                } else if(type == 2){ // Double
                    obj = data.readDouble();
                } else if(type == 3){ // Float
                    obj = data.readFloat();
                } else if(type == 4){ // Long
                    obj = data.readLong();
                } else if(type == 5){ // Byte
                    obj = data.readByte();
                } else if(type == 6){ // Byte Array
                    int size = data.readInt();
                    byte[] bbs = new byte[size];
                    data.read(bbs);
                    obj = bbs;
                } else if(type == 7){ // UTF String
                    obj = data.readUTF();
                } else if(type == 8){ // boolean
                    obj = data.readBoolean();
                }
                json.put(key, obj);
            }
            return json;
        }
        return null;
    }

    protected void serialize() throws IOException {
        serialize(json, false);
    }

    public ByteBuffer serialize(JSONObject json, boolean debug) throws IOException {
        if (json != null && !json.isEmpty()) {
            List<byte[]> inputs = new ArrayList<>();

            for (String s : json.keySet()) {
                Object obj = json.get(s);
                ByteArrayOutputStream inputStream = convertToBinary(s, obj);
                if (inputStream != null) {
                    inputStream.flush();
                    inputs.add(inputStream.toByteArray());
                }
            }

            ByteBuffer buffer = ByteBuffer.allocate(inputs.stream().mapToInt(bytes -> bytes.length).sum());
            inputs.forEach(by -> {
                System.out.println(by.length); // TODO remover
                buffer.put(by);
            });
            debug("Tamanho do Buffer após serialize: " + buffer.capacity(), debug);
            return buffer;
        }
        return null;
    }

    public static ByteArrayOutputStream convertToBinary(String s, Object obj) throws IOException {
        ByteArrayOutputStream inputStream = null;

        // O tamanho dos dados primitivos é baseado no Java Primite Data Types + 2 (ou 4)
        // https://www.w3schools.com/java/java_data_types.asp
        // key size + object size + type (4)
        if (obj instanceof Integer) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 8 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(0);
            data.writeInt((Integer) obj);
            data.flush();
        } else if(obj instanceof Short){
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 4 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(1);
            data.writeShort((Short) obj);
            data.flush();
        } else if(obj instanceof Double){
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 12 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(2);
            data.writeDouble((Double) obj);
            data.flush();
        } else if(obj instanceof Float){
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 8 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(3);
            data.writeFloat((Float) obj);
            data.flush();
        } else if(obj instanceof Long){
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 12 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(4);
            data.writeLong((Long) obj);
            data.flush();
        } else if(obj instanceof Byte){
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 2 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(5);
            data.writeByte((Byte) obj);
            data.flush();
        } else if (obj instanceof byte[]) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + ((byte[]) obj).length + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(6);
            data.writeInt(((byte[]) obj).length);
            data.write((byte[]) obj);
            data.flush();
        } else if (obj instanceof String) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + ((String) obj).getBytes(StandardCharsets.UTF_8).length + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(7);
            data.writeUTF((String) obj);
            data.flush();
        }else if (obj instanceof Boolean) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 2 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(8);
            data.writeBoolean((boolean) obj);
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
