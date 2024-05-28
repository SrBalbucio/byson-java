package balbucio.byson.utils;

import balbucio.byson.BysonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class with utilities to transform JSON types into binary.
 * <p>
 * There is a lot of recursive code and some is needed for now.
 */
public class BysonTypeHelper {

    /*
    Agora em portugues, alguns podem perguntar porque do código duplicado e é por causa da adição (ou remoção) do key.
    Seria possível transforma-lo em um unico, mas o gasto de tempo não vale o esforço, além que alguns tipos não serão suportados dentro da lista.
     */

    /**
     * Creates a ByteArray from a Key:Value
     *
     * @param s   Key
     * @param obj Value
     * @return ByteArray in OutputStream
     * @throws IOException If he can't write or convert.
     */
    public static ByteArrayOutputStream keyValueToBinary(String s, Object obj, boolean complexSerialization) throws IOException {
        ByteArrayOutputStream inputStream = null;

        // O tamanho dos dados primitivos é baseado no Java Primite Data Types + 2 (ou 4)
        // https://www.w3schools.com/java/java_data_types.asp
        // key size + object size + type (4)
        int size = 0;
        int keySize = 0;
        size += (complexSerialization ? 8 : 0);
        DataOutputStream data = null;
        if (obj instanceof Integer) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 4 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(0);
            data.writeInt((Integer) obj);
        } else if (obj instanceof Short) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 2 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(1);
            data.writeShort((Short) obj);
        } else if (obj instanceof Double) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 8 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(2);
            data.writeDouble((Double) obj);
        } else if (obj instanceof Float) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 4 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(3);
            data.writeFloat((Float) obj);
        } else if (obj instanceof Long) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 8 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(4);
            data.writeLong((Long) obj);
        } else if (obj instanceof Byte) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 1 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(5);
            data.writeByte((Byte) obj);
        } else if (obj instanceof byte[]) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + ((byte[]) obj).length + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(6);
            data.writeInt(((byte[]) obj).length);
            data.write((byte[]) obj);
        } else if (obj instanceof String) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            int textSize = ((String) obj).getBytes(StandardCharsets.UTF_8).length;
            size += keySize + textSize + 2;
            size += (complexSerialization ? 4 : 0);
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(7);
            if(complexSerialization){ // precisa ser depois :C
                data.writeInt(textSize); // tamanho do UTF
            }
            data.writeUTF((String) obj);
        } else if (obj instanceof Boolean) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            size += keySize + 1 + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(8);
            data.writeBoolean((boolean) obj);
        } else if (obj instanceof Iterable) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            Iterable array = (Iterable) obj;
            List<byte[]> inputs = listToBinary(array, complexSerialization);
            size += keySize + 6 + (4 * inputs.size()) + inputs.stream().mapToInt(bytes -> bytes.length).sum();
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(9);
            data.writeInt(inputs.size());
            for (byte[] input : inputs) {
                data.writeInt(input.length);
                data.write(input);
            }
        } else if (obj instanceof JSONObject) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            List<byte[]> inputs = mapToBinary(((JSONObject) obj).toMap(), complexSerialization);
            size += keySize + 6 + (4 * inputs.size()) + inputs.stream().mapToInt(bytes -> bytes.length).sum();
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(10);
            data.writeInt(inputs.size());
            for (byte[] input : inputs) {
                data.writeInt(input.length);
                data.write(input);
            }
        } else if (obj instanceof Map) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            List<byte[]> inputs = mapToBinary((Map<String, Object>) obj, complexSerialization);
            size = keySize + 6 + (4 * inputs.size()) + inputs.stream().mapToInt(bytes -> bytes.length).sum();
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(10);
            data.writeInt(inputs.size());
            for (byte[] input : inputs) {
                data.writeInt(input.length);
                data.write(input);
            }
        } else if (obj instanceof BigDecimal) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            BigDecimal decimal = (BigDecimal) obj;
            String dec = decimal.toPlainString();
            size += keySize + dec.getBytes(StandardCharsets.UTF_8).length + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(11);
            data.writeUTF(dec);
        } else if (obj instanceof BigInteger) {
            keySize = s.getBytes(StandardCharsets.UTF_8).length;
            BigInteger decimal = (BigInteger) obj;
            String dec = decimal.toString();
            size += keySize + dec.getBytes(StandardCharsets.UTF_8).length + 2;
            inputStream = new ByteArrayOutputStream(size);
            data = new DataOutputStream(inputStream);
            if(complexSerialization){
                data.writeInt(size);
                data.writeInt(keySize);
            }
            data.writeUTF(s);
            data.writeShort(12);
            data.writeUTF(dec);
        }
        data.flush();
//        data.close();
        return inputStream;
    }

    /**
     * Transforms a list into a binary array.
     * <p>
     * The binary generated for each type has fewer parameters.
     *
     * @param array List
     * @return ByteArray
     * @throws IOException If he can't write or convert.
     */
    public static List<byte[]> listToBinary(Iterable array, boolean complexSerialization) throws IOException {
        List<byte[]> inputs = new ArrayList<>();
        for (Object obj : array) {
            ByteArrayOutputStream inputStream = null;
            if (obj instanceof Integer) {
                inputStream = new ByteArrayOutputStream(4 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(0);
                data.writeInt((Integer) obj);
                data.flush();
            } else if (obj instanceof Short) {
                inputStream = new ByteArrayOutputStream(2 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(1);
                data.writeShort((Short) obj);
                data.flush();
            } else if (obj instanceof Double) {
                inputStream = new ByteArrayOutputStream(8 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(2);
                data.writeDouble((Double) obj);
                data.flush();
            } else if (obj instanceof Float) {
                inputStream = new ByteArrayOutputStream(4 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(3);
                data.writeFloat((Float) obj);
                data.flush();
            } else if (obj instanceof Long) {
                inputStream = new ByteArrayOutputStream(8 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(4);
                data.writeLong((Long) obj);
                data.flush();
            } else if (obj instanceof Byte) {
                inputStream = new ByteArrayOutputStream(1 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(5);
                data.writeByte((Byte) obj);
                data.flush();
            } else if (obj instanceof byte[]) {
                inputStream = new ByteArrayOutputStream(((byte[]) obj).length + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(6);
                data.writeInt(((byte[]) obj).length);
                data.write((byte[]) obj);
                data.flush();
            } else if (obj instanceof String) {
                inputStream = new ByteArrayOutputStream(((String) obj).getBytes(StandardCharsets.UTF_8).length + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(7);
                data.writeUTF((String) obj);
                data.flush();
            } else if (obj instanceof Boolean) {
                inputStream = new ByteArrayOutputStream(1 + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(8);
                data.writeBoolean((boolean) obj);
                data.flush();
            } else if (obj instanceof Iterable) {
                Iterable arr = (Iterable) obj;
                List<byte[]> ins = listToBinary(arr, complexSerialization);
                inputStream = new ByteArrayOutputStream(6 + (4 * ins.size()) + ins.stream().mapToInt(bytes -> bytes.length).sum());
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(9);
                data.writeInt(inputs.size());
                for (byte[] input : ins) {
                    data.writeInt(input.length);
                    data.write(input);
                }
            } else if (obj instanceof JSONObject) {
                List<byte[]> ins = mapToBinary(((JSONObject) obj).toMap(), complexSerialization);
                inputStream = new ByteArrayOutputStream(6 + (4 * ins.size()) + ins.stream().mapToInt(bytes -> bytes.length).sum());
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(10);
                data.writeInt(ins.size());
                for (byte[] input : ins) {
                    data.writeInt(input.length);
                    data.write(input);
                }
            } else if (obj instanceof Map) {
                List<byte[]> ins = mapToBinary((Map<String, Object>) obj, complexSerialization);
                inputStream = new ByteArrayOutputStream(6 + (4 * ins.size()) + ins.stream().mapToInt(bytes -> bytes.length).sum());
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(10);
                data.writeInt(ins.size());
                for (byte[] input : ins) {
                    data.writeInt(input.length);
                    data.write(input);
                }
            } else if (obj instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) obj;
                String dec = decimal.toPlainString();
                inputStream = new ByteArrayOutputStream(dec.getBytes(StandardCharsets.UTF_8).length + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(11);
                data.writeUTF(dec);
                data.flush();
            } else if (obj instanceof BigInteger) {
                BigInteger decimal = (BigInteger) obj;
                String dec = decimal.toString();
                inputStream = new ByteArrayOutputStream(dec.getBytes(StandardCharsets.UTF_8).length + 2);
                DataOutputStream data = new DataOutputStream(inputStream);
                data.writeShort(12);
                data.writeUTF(dec);
                data.flush();
            }
            if (inputStream != null) {
                inputs.add(inputStream.toByteArray());
            }
        }
        return inputs;
    }

    /**
     * Transforms a map into a binary array.
     *
     * @param map Map
     * @return ByteArray
     * @throws IOException If he can't write or convert.
     */
    public static List<byte[]> mapToBinary(Map<String, Object> map, boolean complexSerialziation) throws IOException {
        List<byte[]> inputs = new ArrayList<>();
        ByteArrayOutputStream mtout = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(mtout);
        data.writeBoolean(complexSerialziation);

        inputs.add(mtout.toByteArray());

        for (String s : map.keySet()) {
            Object obj = map.get(s);
            // TODO ver se aqui vai ter complexSerialization
            ByteArrayOutputStream inputStream = keyValueToBinary(s, obj, complexSerialziation);
            if (inputStream != null) {
                inputStream.flush();
                inputs.add(inputStream.toByteArray());
            }
        }

        data.close();
        mtout.close();

        return inputs;
    }

    /**
     * Transforms the binary into an object, using a parameter in Short format.
     *
     * @param type short type number
     * @param obj  where it should be set.
     * @param data
     * @return returns the object itself (obj parameter)
     * @throws IOException If he can't write or convert.
     */
    public static Object parseObject(short type, Object obj, DataInputStream data, boolean complexSerialization) throws IOException {
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
            if(complexSerialization){ data.readInt(); }
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
                ((JSONArray) obj).put(parseObject(tObj.readShort(), null, tObj, false));
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
            obj = BysonParser.deserialize(buffer);
        } else if (type == 11) {
            obj = new BigDecimal(data.readUTF());
        } else if (type == 12) {
            obj = new BigInteger(data.readUTF());
        }
        return obj;
    }

    public static Map<String, Integer> indexTable(ByteBuffer buffer) throws IOException {
        if (buffer != null) {
            Map<String, Integer> map = new ConcurrentHashMap<>();
            byte[] bytes = buffer.array();
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
                    int pos = bytes.length - data.available();
                    // PULAR OS BYTES
                    short type = data.readShort();
                    // TODO verificar o complex
                    BysonTypeHelper.parseObject(type, null, data, complexSerialization);
                    map.put(key, pos);
                } catch (Exception ignored) {
                    break;
                }
            }
            return map;
        }
        return null;
    }

    public static Object findByKey(ByteBuffer buffer, String key) throws IOException {
        if (buffer != null) {
            Object obj = null;
            byte[] bytes = buffer.array();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            DataInputStream data = new DataInputStream(inputStream);

            boolean complexSerialization = data.readBoolean();

            while (data.available() > 0) {
                try {
                    int len = 0;
                    int keySize = 0;
                    if(complexSerialization){
                        len = data.readInt();
                        keySize = data.readInt();
                    }

                    String k = data.readUTF();
                    short type = data.readShort();
                    obj = BysonTypeHelper.parseObject(type, null, data, complexSerialization);
                    if(key.equals(k)){
                        break;
                    }
                } catch (Exception ignored) {
                    break;
                }
            }
            return obj;
        }
        return null;
    }

    public static Object getObjectByPosition(ByteBuffer buffer, int pos, boolean complexSerialization) throws IOException {
        byte[] bytes = buffer.array();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        DataInputStream data = new DataInputStream(inputStream);

        data.skipBytes(pos);
        short type = data.readShort();
        // TODO verificar o complex
        return BysonTypeHelper.parseObject(type, null, data, complexSerialization);
    }

}
