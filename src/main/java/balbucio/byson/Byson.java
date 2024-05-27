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

public class Byson {

    private DynamicByteBuffer buffer;
    private Map<String, Integer> index;

    public Byson(){
        this.buffer = new DynamicByteBuffer(550);
    }

    public Byson(ByteBuffer bff){
        this.buffer = new DynamicByteBuffer(bff);
    }

    public void index() {
        try {
            this.index = BysonParser.indexTable(buffer.getBuffer());
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Unable to index this Byson: "+e.getMessage());
        }
    }

    public Optional<Object> opt(String key){
        try {
            if (index.containsKey(key)) {
                return Optional.of(BysonParser.getObjectByPosition(buffer.getBuffer(), index.get(key)));
            } else {
                return Optional.empty();
            }
        } catch (Exception e){
            throw new RuntimeException("Unable to get value this Byson: "+e.getMessage());
        }
    }

    public void put(String key, Object value){
        try {
            putThrow(key, value);
        } catch (Exception e){
            throw new RuntimeException("Não foi possível adicionar o valor ao Byson["+key+"]: "+e.getMessage());
        }
    }

    public void putThrow(String key, Object value) throws IOException{
        ByteArrayOutputStream out = BysonTypeHelper.keyValueToBinary(key, value);
        buffer.put(out.toByteArray());
    }

    public JSONObject toJSONObject() {
        try {
            return BysonParser.deserialize(buffer.getBuffer());
        } catch (Exception e){
            throw new RuntimeException("Unable to retrieve JSON: "+e.getMessage());
        }
    }

    public ByteBuffer toByteBuffer(){
        try {
            return BysonCompressHelper.compress(buffer.getBuffer());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toByteArray(){
        return buffer.getBuffer().array();
    }
}
