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
        json.put("key-10", new JSONObject()
                .put("fuub", Arrays.asList("Varanda", "Chico", "Inicial na Unha"))
                .put("TLP", Arrays.asList("jv", "balb", "flashh", "swat", "gongo"))
                .put("repositorios", 107));
        System.out.println("Tamanho do JSON após convertido para String: "+json.toString().getBytes(StandardCharsets.UTF_8).length);
    }

    @Test
    @DisplayName("Serializing JSON")
    @Order(0)
    public void convert() throws IOException {
        this.buffer = BysonParser.serialize(json);
        System.out.println("Tamanho do buffer serializado e comprimido: "+buffer.capacity());
    }

    @Test
    @DisplayName("Deserialize")
    @Order(1)
    public void deserialize() throws IOException{
        this.json = BysonParser.deserialize(buffer);
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
    @Order(7)
    public void getKey6(){
        Boolean s = json.getBoolean("key-6");
        System.out.println(s);
        assertEquals(true, s);
    }
    @Test
    @DisplayName("Get key-7")
    @Order(8)
    public void getKey7(){
        JSONArray s = json.getJSONArray("key-7");
        System.out.println(s);
        assertTrue(Arrays.asList(1,2,3,4,5,6,7,8,9).containsAll(s.toList()));
    }
    @Test
    @DisplayName("Get key-8")
    @Order(9)
    public void getKey8(){
        JSONArray s = json.getJSONArray("key-8");
        System.out.println(s);
        assertTrue(Arrays.asList("a", "b", "c", "d", "e", "f").containsAll(s.toList()));
    }
    @Test
    @DisplayName("Get key-9")
    @Order(10)
    public void getKey9(){
        JSONArray s = json.getJSONArray("key-9");
        JSONObject json1 = s.getJSONObject(0);
        JSONObject json2 = s.getJSONObject(1);
        System.out.println(json1);
        System.out.println(json2);
        assertEquals("java", json1.getString("lang-1"));
        assertEquals("javascript", json2.getString("lang-2"));
    }
    @Test
    @DisplayName("Get key-10")
    @Order(11)
    public void getKey10(){
        JSONObject s = json.getJSONObject("key-10");
        JSONArray TLP = s.getJSONArray("TLP");
        JSONArray musics = s.getJSONArray("fuub");
        int repositores = s.getInt("repositorios");
        System.out.println(s.toString());
        assertEquals(107, repositores);
        assertTrue(Arrays.asList("Varanda", "Chico", "Inicial na Unha").containsAll(musics.toList()));
        assertTrue(Arrays.asList("jv", "balb", "flashh", "swat", "gongo").containsAll(TLP.toList()));
    }

}
