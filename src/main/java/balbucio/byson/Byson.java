package balbucio.byson;

import balbucio.byson.utils.BysonCompressHelper;
import balbucio.byson.utils.BysonTypeHelper;
import balbucio.byson.utils.DynamicByteBuffer;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the Byson object, you can use it to store values in the same way as in JSONObject, but here you store them directly as binary.
 * <p>
 * Byson automatically resizes (if necessary) whenever a new item is added.
 * <p>
 * You can choose to decide the initial size, avoiding spending too many bytes.
 */
public class Byson {

    private DynamicByteBuffer buffer;
    private Map<String, Integer> index = new ConcurrentHashMap<>();
    @Getter
    private boolean complexSerialization = false;

    /**
     * Creates an empty Byson with an initial size of 550 bytes.
     */
    public Byson() {
        this.buffer = new DynamicByteBuffer(550);
        setComplexSerialization(false);
    }

    /**
     * Creates a Byson from a ByteBuffer.
     *
     * @param bff Byson's ByteBuffer
     */
    public Byson(ByteBuffer bff) {
        this.buffer = new DynamicByteBuffer(bff);
    }

    public void setComplexSerialization(boolean complexSerialization) {
        try {
            ByteArrayOutputStream mtout = new ByteArrayOutputStream();
            DataOutputStream data = new DataOutputStream(mtout);
            data.writeBoolean(complexSerialization);
            buffer.putFlip(mtout.toByteArray());

            data.close();
            mtout.close();
            this.complexSerialization = complexSerialization;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Activates the mode in which extra keys are added to bytes, to facilitate some processes and methods.
     *
     * In version 0.0.2 there is really no use for this.
     * @return the object
     */
    public Byson enableComplexSerialization() {
        setComplexSerialization(true);
        return this;
    }

    /**
     * Pre-locates all keys, this speeds up the search and may be necessary for some high-level methods.
     * <p>
     * If Byson is very large or in an environment with few resources, avoid indexing, this stores all the keys and their positions.
     * @return the object
     */
    public Byson index() {
        try {
            this.index = BysonTypeHelper.indexTable(buffer.getBuffer());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to index this Byson: " + e.getMessage());
        }
        return this;
    }

    public String getString(String key){
        return (String) opt(key).get();
    }

    public Integer getInt(String key){
        return (Integer) opt(key).get();
    }

    public Long getLong(String key){
        return (Long) opt(key).get();
    }

    public Float getFloat(String key){
        return (Float) opt(key).get();
    }

    public Short getShort(String key){
        return (Short) opt(key).get();
    }

    public byte[] getByteArray(String key){
        return (byte[]) opt(key).get();
    }

    public byte getByte(String key){
        return (byte) opt(key).get();
    }

    public boolean getBoolean(String key){
        return (boolean) opt(key).get();
    }

    public JSONArray getJSONArray(String key){
        return (JSONArray) opt(key).get();
    }

    public BigDecimal getBigDecimal(String key){
        return (BigDecimal) opt(key).get();
    }

    public BigInteger getBigInteger(String key){
        return (BigInteger) opt(key).get();
    }

    public Object get(String key){
        return opt(key).get();
    }

    /**
     * Searches for a value by key, either in the index table or via linear search.
     * @param key key
     * @return value
     */
    public Optional<Object> opt(String key) {
        try {
            if (index.containsKey(key)) {
                return Optional.of(BysonTypeHelper.getObjectByPosition(buffer.getBuffer(), index.get(key), complexSerialization));
            } else {
                Object searchObj = BysonTypeHelper.findByKey(buffer.getBuffer(), key);
                if (searchObj == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(searchObj);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to get value this Byson: " + e.getMessage());
        }
    }

    /**
     * Put an object on Byson.
     *
     * @param key   key
     * @param value value
     */
    public void put(String key, Object value) {
        try {
            putThrow(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível adicionar o valor ao Byson[" + key + "]: " + e.getMessage());
        }
    }

    /**
     * Put an object in Byson but you can control the throw if there is one.
     *
     * @param key   key
     * @param value value
     * @throws IOException If it is not possible to add the value and key.
     */
    public void putThrow(String key, Object value) throws IOException {
        ByteArrayOutputStream out = BysonTypeHelper.keyValueToBinary(key, value, complexSerialization);
        buffer.put(out.toByteArray());
    }

    /**
     * For JSONObject.
     *
     * @return
     */
    public JSONObject toJSONObject() {
        try {
            return BysonParser.deserialize(buffer.getBuffer());
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve JSON: " + e.getMessage());
        }
    }

    /**
     * For compressed ByteBuffer.
     *
     * @return
     */
    public ByteBuffer toByteBuffer() {
        try {
            return BysonCompressHelper.compress(buffer.getBuffer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * For byte array
     *
     * @return
     */
    public byte[] toByteArray() {
        return buffer.getBuffer().array();
    }
}
