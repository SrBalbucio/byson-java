import balbucio.byson.BysonParser;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Estes são testes extras para garantir que as respostas de APIs possam se tornar binário sem problema.
 *
 * Testa ambientes talvez não tão propicios.
 * Importante avisar que eu fiz com Jsoup porque é bem fácil mas você poderia fazer sem usar uma biblioteca
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class APISerializeTest {

    @Test
    @DisplayName("Checking a JSON from DummyJSON")
    @Order(1)
    public void dummyJson() throws IOException {
        String msgJson = Jsoup.connect("https://dummyjson.com/user/2")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute().body();
        System.out.println(msgJson);
        JSONObject oJson = new JSONObject(msgJson);
        ByteBuffer jsonBuffer = BysonParser.serialize(oJson);
        JSONObject generated = BysonParser.deserialize(jsonBuffer);
        System.out.println(generated.toString());
        checkJsonEquals(oJson, generated);
    }

    public void checkJsonEquals(JSONObject or, JSONObject gn){
        for(String key : or.keySet()){
            System.out.println(key+" >> "+gn.has(key));
            assertTrue(gn.has(key));
            if(or.get(key) instanceof JSONObject){
                checkJsonEquals(or.getJSONObject(key), gn.getJSONObject(key));
            } else{
                assertEquals(or.get(key), gn.get(key));
            }
        }
    }
}
