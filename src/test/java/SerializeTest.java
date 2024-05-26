import balbucio.byson.Byson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SerializeTest {

    private Byson byson;
    private JSONObject json;

    @BeforeAll
    public void init(){
        JSONObject json = new JSONObject();
        json.put("key-1", 25);
        json.put("key-2", 35L);
        json.put("key-3", 45D);
        json.put("key-4", "value");
        json.put("key-5", "value-2");
        json.put("key-6", true);
        json.put("key-7", new JSONArray(Arrays.asList(1,2,3,4,5,6,7,8,9)));
        json.put("key-8", new JSONArray(Arrays.asList("a", "b", "c", "d", "e", "f")));
        System.out.println("Tamanho do JSON após convertido para String: "+json.toString().getBytes(StandardCharsets.UTF_8).length);
        this.byson = new Byson(json);
    }

    @Test
    @DisplayName("Serializing JSON")
    @Order(0)
    public void convert() throws IOException {
        byson.serialize(true);
    }

    @Test
    @DisplayName("Deserialize")
    @Order(1)
    public void deserialize() throws IOException{
        byson.deserialize(true);
        this.json = byson.getJson();
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
    @Disabled
    @Order(3)
    public void getKey2(){
        assertEquals(25, json.getInt("key-1"));
    }
    @Test
    @DisplayName("Get key-3")
    @Disabled
    @Order(4)
    public void getKey3(){
        assertEquals(25, json.getInt("key-1"));
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

}
