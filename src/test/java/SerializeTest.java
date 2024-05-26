import balbucio.byson.Byson;
import balbucio.byson.BysonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerializeTest {

    private ByteBuffer buffer;
    private JSONObject json;

    @BeforeAll
    public void init(){
        this.json = new JSONObject();
        json.put("key-1", 25);
        json.put("key-2", 35L);
        json.put("key-3", 45D);
        json.put("key-4", "value");
        json.put("key-5", "value-2");
        json.put("key-6", true);
        json.put("key-7", new JSONArray(Arrays.asList(1,2,3,4,5,6,7,8,9)));
        json.put("key-8", new JSONArray(Arrays.asList("a", "b", "c", "d", "e", "f")));
        json.put("key-9", new JSONArray(Arrays.asList(
                new JSONObject().put("lang-1", "java"),
                new JSONObject().put("lang-2", "javascript")
        )));
        System.out.println("Tamanho do JSON após convertido para String: "+json.toString().getBytes(StandardCharsets.UTF_8).length);
    }

    @Test
    @DisplayName("Serializing JSON")
    @Order(0)
    public void convert() throws IOException {
        this.buffer = BysonParser.serialize(json, true);
    }

    @Test
    @DisplayName("Deserialize")
    @Order(1)
    public void deserialize() throws IOException{
        this.json = BysonParser.deserialize(buffer, true);
    }

    @Test
    @DisplayName("Get key-1")
    @Order(2)
    public void getKey1(){
        int i = json.getInt("key-1");
        System.out.println(i);
        assertEquals(25, i);
    }

    @Test
    @DisplayName("Get key-2")
    @Order(3)
    public void getKey2(){
        Long l = json.getLong("key-2");
        System.out.println(l);
        assertEquals(35L, l);
    }
    @Test
    @DisplayName("Get key-3")
    @Order(4)
    public void getKey3(){
        Double d = json.getDouble("key-3");
        System.out.println(d);
        assertEquals(45D, d);
    }

    @Test
    @DisplayName("Get key-4")
    @Order(5)
    public void getKey4(){
        String s = json.getString("key-4");
        System.out.println(s);
        assertEquals("value", s);
    }
    @Test
    @DisplayName("Get key-5")
    @Order(6)
    public void getKey5(){
        String s = json.getString("key-5");
        System.out.println(s);
        assertEquals("value-2", s);
    }
    @Test
    @DisplayName("Get key-6")
    @Order(6)
    public void getKey6(){
        Boolean s = json.getBoolean("key-6");
        System.out.println(s);
        assertEquals(true, s);
    }
    @Test
    @DisplayName("Get key-7")
    @Order(6)
    public void getKey7(){
        JSONArray s = json.getJSONArray("key-7");
        System.out.println(s);
        assertTrue(Arrays.asList(1,2,3,4,5,6,7,8,9).containsAll(s.toList()));
    }
    @Test
    @DisplayName("Get key-8")
    @Order(6)
    public void getKey8(){
        JSONArray s = json.getJSONArray("key-8");
        System.out.println(s);
        assertTrue(Arrays.asList("a", "b", "c", "d", "e", "f").containsAll(s.toList()));
    }

}
