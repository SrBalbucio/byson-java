import balbucio.byson.Byson;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BysonIndexedTest {

    private Byson byson;

    @BeforeAll
    public void initByson(){
        this.byson = new Byson().enableComplexSerialization();
    }

    @Test
    @DisplayName("Insert values")
    @Order(0)
    public void values(){
        byson.put("key-1", 0);
        byson.put("key-2", 1D);
        byson.put("key-3", 1F);
        byson.put("key-4", "não deixa a vibe cair");
        byson.put("key-5", true);
    }

    @Test
    @DisplayName("Print JSON")
    @Order(1)
    public void print(){
        JSONObject json = byson.toJSONObject();
        System.out.println(json.toString());
    }

    @Test
    @DisplayName("Index")
    @Order(1)
    public void indexing(){
        byson.index();
    }

    @Test
    @DisplayName("Get key-1")
    @Order(2)
    public void key1(){
        int v = (Integer) byson.opt("key-1").get();
        System.out.println(v);
        assertEquals(0, v);
    }
    @Test
    @DisplayName("Get key-2")
    @Order(3)
    public void key2(){
        double v = (double) byson.opt("key-2").get();
        System.out.println(v);
        assertEquals(1D, v);
    }
    @Test
    @DisplayName("Get key-3")
    @Order(4)
    public void key3(){
        float v = (float) byson.opt("key-3").get();
        System.out.println(v);
        assertEquals(1F, v);
    }
    @Test
    @DisplayName("Get key-4")
    @Order(5)
    public void key4(){
        String v = (String) byson.opt("key-4").get();
        System.out.println(v);
        assertEquals("não deixa a vibe cair", v);
    }
    @Test
    @DisplayName("Get key-5")
    @Order(6)
    public void key5(){
        boolean v = (boolean) byson.opt("key-5").get();
        System.out.println(v);
        assertEquals(true, v);
    }
}
