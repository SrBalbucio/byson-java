package balbucio.byson;

import balbucio.byson.utils.BysonCompressHelper;
import balbucio.byson.utils.BysonTypeHelper;
import balbucio.byson.utils.DynamicByteBuffer;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

/**
 * This is the Byson object, you can use it to store values in the same way as in JSONObject, but here you store them directly as binary.
 *
 * Byson automatically resizes (if necessary) whenever a new item is added.
 *
 * You can choose to decide the initial size, avoiding spending too many bytes.
 */
public class Byson {

    private DynamicByteBuffer buffer;
    private Map<String, Integer> index;

    /**
     * Creates an empty Byson with an initial size of 550 bytes.
     */
    public Byson(){
        this.buffer = new DynamicByteBuffer(550);
    }

    /**
     * Creates a Byson from a ByteBuffer.
     * @param bff Byson's ByteBuffer
     */
    public Byson(ByteBuffer bff){
        this.buffer = new DynamicByteBuffer(bff);
    }

    /**
     * Pre-locates all keys, this speeds up the search and may be necessary for some high-level methods.
     *
     * If Byson is very large or in an environment with few resources, avoid indexing, this stores all the keys and their positions.
     */
    public void index() {
        try {
            this.index = BysonTypeHelper.indexTable(buffer.getBuffer());
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Unable to index this Byson: "+e.getMessage());
        }
    }

    public Optional<Object> opt(String key){
        try {
            if (index.containsKey(key)) {
                return Optional.of(BysonTypeHelper.getObjectByPosition(buffer.getBuffer(), index.get(key)));
            } else {
                return Optional.empty();
            }
        } catch (Exception e){
            throw new RuntimeException("Unable to get value this Byson: "+e.getMessage());
        }
    }

    /**
     * Put an object on Byson.
     * @param key key
     * @param value value
     */
    public void put(String key, Object value){
        try {
            putThrow(key, value);
        } catch (Exception e){
            throw new RuntimeException("Não foi possível adicionar o valor ao Byson["+key+"]: "+e.getMessage());
        }
    }

    /**
     * Put an object in Byson but you can control the throw if there is one.
     * @param key key
     * @param value value
     * @throws IOException If it is not possible to add the value and key.
     */
    public void putThrow(String key, Object value) throws IOException{
        ByteArrayOutputStream out = BysonTypeHelper.keyValueToBinary(key, value);
        buffer.put(out.toByteArray());
    }

    /**
     * For JSONObject.
     * @return
     */
    public JSONObject toJSONObject() {
        try {
            return BysonParser.deserialize(buffer.getBuffer());
        } catch (Exception e){
            throw new RuntimeException("Unable to retrieve JSON: "+e.getMessage());
        }
    }

    /**
     * For compressed ByteBuffer.
     * @return
     */
    public ByteBuffer toByteBuffer(){
        try {
            return BysonCompressHelper.compress(buffer.getBuffer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * For byte array
     * @return
     */
    public byte[] toByteArray(){
        return buffer.getBuffer().array();
    }
}
