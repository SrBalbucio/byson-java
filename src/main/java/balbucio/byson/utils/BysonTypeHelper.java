package balbucio.byson.utils;

import balbucio.byson.BysonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BysonTypeHelper {

    public static ByteArrayOutputStream keyValueToBinary(String s, Object obj) throws IOException {
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
        } else if (obj instanceof Short) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 4 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(1);
            data.writeShort((Short) obj);
            data.flush();
        } else if (obj instanceof Double) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 12 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(2);
            data.writeDouble((Double) obj);
            data.flush();
        } else if (obj instanceof Float) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 8 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(3);
            data.writeFloat((Float) obj);
            data.flush();
        } else if (obj instanceof Long) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 12 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(4);
            data.writeLong((Long) obj);
            data.flush();
        } else if (obj instanceof Byte) {
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
        } else if (obj instanceof Boolean) {
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 2 + 4);
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(8);
            data.writeBoolean((boolean) obj);
            data.flush();
        } else if (obj instanceof Iterable) {
            Iterable array = (Iterable) obj;
            List<byte[]> inputs = listToBinary(array);
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 8 + (4 * inputs.size()) + inputs.stream().mapToInt(bytes -> bytes.length).sum());
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(9);
            data.writeInt(inputs.size());
            for (byte[] input : inputs) {
                data.writeInt(input.length);
                data.write(input);
            }
        } else if (obj instanceof JSONObject) {
            List<byte[]> inputs = mapToBinary(((JSONObject) obj).toMap());
            inputStream = new ByteArrayOutputStream(s.getBytes(StandardCharsets.UTF_8).length + 8 + (4 * inputs.size()) + inputs.stream().mapToInt(bytes -> bytes.length).sum());
            DataOutputStream data = new DataOutputStream(inputStream);
            data.writeUTF(s);
            data.writeShort(10);
            data.writeInt(inputs.size());
            for (byte[] input : inputs) {
                data.writeInt(input.length);
                data.write(input);
            }
        }
        return inputStream;
    }

    public static List<byte[]> listToBinary(Iterable array) throws IOException {
        List<byte[]> inputs = new ArrayList<>();
        for (Object obj : array) {
            ByteArrayOutputStream inputStream = null;
            if (obj instanceof Integer) {
                inputStream = new ByteArrayOutputStream(8 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(0);
                data.writeInt((Integer) obj);
                data.flush();
            } else if (obj instanceof Short) {
                inputStream = new ByteArrayOutputStream(4 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(1);
                data.writeShort((Short) obj);
                data.flush();
            } else if (obj instanceof Double) {
                inputStream = new ByteArrayOutputStream(12 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(2);
                data.writeDouble((Double) obj);
                data.flush();
            } else if (obj instanceof Float) {
                inputStream = new ByteArrayOutputStream(8 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(3);
                data.writeFloat((Float) obj);
                data.flush();
            } else if (obj instanceof Long) {
                inputStream = new ByteArrayOutputStream(12 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(4);
                data.writeLong((Long) obj);
                data.flush();
            } else if (obj instanceof Byte) {
                inputStream = new ByteArrayOutputStream(2 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(5);
                data.writeByte((Byte) obj);
                data.flush();
            } else if (obj instanceof byte[]) {
                inputStream = new ByteArrayOutputStream(((byte[]) obj).length + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(6);
                data.writeInt(((byte[]) obj).length);
                data.write((byte[]) obj);
                data.flush();
            } else if (obj instanceof String) {
                inputStream = new ByteArrayOutputStream(((String) obj).getBytes(StandardCharsets.UTF_8).length + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(7);
                data.writeUTF((String) obj);
                data.flush();
            } else if (obj instanceof Boolean) {
                inputStream = new ByteArrayOutputStream(2 + 4);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(8);
                data.writeBoolean((boolean) obj);
                data.flush();
            } else if (obj instanceof Iterable) {
                Iterable arr = (Iterable) obj;
                List<byte[]> ins = listToBinary(arr);
                inputStream = new ByteArrayOutputStream(8 + (4 * ins.size()) + ins.stream().mapToInt(bytes -> bytes.length).sum());
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(9);
                data.writeInt(inputs.size());
                for (byte[] input : ins) {
                    data.writeInt(input.length);
                    data.write(input);
                }
            } else if (obj instanceof JSONObject) {
                List<byte[]> ins = mapToBinary(((JSONObject) obj).toMap());
                inputStream = new ByteArrayOutputStream(8 + (4 * ins.size()) + ins.stream().mapToInt(bytes -> bytes.length).sum());
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(10);
                data.writeInt(ins.size());
                for (byte[] input : ins) {
                    data.writeInt(input.length);
                    data.write(input);
                }
            }
            if (inputStream != null) {
                inputs.add(inputStream.toByteArray());
            }
        }
        return inputs;
    }

    public static List<byte[]> mapToBinary(Map<String, Object> map) throws IOException {
        List<byte[]> inputs = new ArrayList<>();

        for (String s : map.keySet()) {
            Object obj = map.get(s);
            ByteArrayOutputStream inputStream = keyValueToBinary(s, obj);
            if (inputStream != null) {
                inputStream.flush();
                inputs.add(inputStream.toByteArray());
            }
        }
        return inputs;
    }

    public static Object parseObject(short type, Object obj, DataInputStream data) throws IOException {
        if (type == 0) { // Int
            obj = data.readInt();
        } else if (type == 1) { // Short
            obj = data.readShort();
        } else if (type == 2) { // Double
            obj = data.readDouble();
        } else if (type == 3) { // Float
            obj = data.readFloat();
        } else if (type == 4) { // Long
            obj = data.readLong();
        } else if (type == 5) { // Byte
            obj = data.readByte();
        } else if (type == 6) { // Byte Array
            int size = data.readInt();
            byte[] bbs = new byte[size];
            data.read(bbs);
            obj = bbs;
        } else if (type == 7) { // UTF String
            obj = data.readUTF();
        } else if (type == 8) { // boolean
            obj = data.readBoolean();
        } else if (type == 9) { // array
            obj = new JSONArray();
            int size = data.readInt();
            for (int i = 0; i < size; i++) {
                int len = data.readInt();
                byte[] tby = new byte[len];
                data.read(tby);
                DataInputStream tObj = new DataInputStream(new ByteArrayInputStream(tby));
                ((JSONArray) obj).put(parseObject(tObj.readShort(), null, tObj));
            }
        } else if (type == 10) { // json map
            obj = new JSONObject();
            int size = data.readInt();
            List<byte[]> byarrs = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                int len = data.readInt();
                byte[] tby = new byte[len];
                data.read(tby);
                byarrs.add(tby);
            }
            ByteBuffer buffer = ByteBuffer.allocate(byarrs.stream().mapToInt(bytes -> bytes.length).sum());
            byarrs.forEach(buffer::put);
            obj = BysonParser.deserialize(buffer, false);
        }
        return obj;
    }

}
