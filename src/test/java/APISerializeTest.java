import balbucio.byson.BysonParser;
import org.json.JSONArray;
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
        System.out.println(jsonBuffer.capacity());
        JSONObject generated = BysonParser.deserialize(jsonBuffer);
        System.out.println(generated.toString());
        checkJsonEquals(oJson, generated);
    }

    @Test
    @DisplayName("Checking a JSON from FakeStore")
    @Order(2)
    public void fakeStore() throws IOException {
        String msgJson = Jsoup.connect("https://fakestoreapi.com/products?limit=10")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute().body();
        System.out.println(msgJson);
        JSONObject j = new JSONObject();
        JSONArray oJson = new JSONArray(msgJson);
        j.put("lista", oJson);
        ByteBuffer jsonBuffer = BysonParser.serialize(j);
        System.out.println(jsonBuffer.capacity());
        JSONObject generated = BysonParser.deserialize(jsonBuffer);
        System.out.println(generated.toString());
        checkJsonEquals(j, generated);
    }

    public void checkJsonEquals(JSONObject or, JSONObject gn){
        for(String key : or.keySet()){
            System.out.println(key+" >> "+gn.has(key) +" >> "+or.get(key).getClass().getName());
            assertTrue(gn.has(key));
            if(or.get(key) instanceof JSONObject){
                checkJsonEquals(or.getJSONObject(key), gn.getJSONObject(key));
            } else if(or.get(key) instanceof JSONArray){
                // FORMA BEM PORCA de checar o JSONArray
                for (int i = 0; i < ((JSONArray) or.get(key)).toList().size(); i++) {
                    if(((JSONArray) or.get(key)).get(i) instanceof JSONObject){
                        checkJsonEquals(or.getJSONArray(key).getJSONObject(i), gn.getJSONArray(key).getJSONObject(i));
                    }
                }
            } else{
                assertEquals(or.get(key), gn.get(key));
            }
        }
    }
}
