import Exceptions.HttpConfigurationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import http_configuration.ConfigurationManager;
import http_configuration.JsonConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Json;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationManagerTest {
    JsonConfig config = null;
    private String path = "src/main/resources/http_server.json";
    @BeforeEach
    public void setup(){
        ConfigurationManager.getInstance().loadConfigFile(path);
        config = ConfigurationManager.getInstance().getCurrentConfig();
    }

    @Test
    public void getPort(){
        assertEquals(8080, config.getPort());
        assertNotEquals("/", config.getPort()+"");
        assertNotEquals("localhost", config.getPort()+"");
    }

    @Test
    public void getWebRoot(){
        assertEquals("/", config.getWebroot());
        assertNotEquals("8080", config.getWebroot());
        assertNotEquals("localhost", config.getWebroot());
    }

    @Test
    public void getHost(){
        assertEquals("localhost", config.getHost());
        assertNotEquals("8080", config.getHost());
        assertNotEquals("/", config.getHost());
    }

    @Test
    public void JsonParser(){

        String data = "{\n" +
                "  \"port\": 8080,\n" +
                "  \"webroot\": \"/\",\n" +
                "  \"host\": \"localhost\"\n" +
                "}";
        assertEquals(data, readJsonFile().toString());
    }

    @Test
    public void toJsonNode(){
        JsonNode config = null;
        try{
            config = Json.parse(readJsonFile().toString());
        }catch (IOException e){
            throw new HttpConfigurationException("Error while parsing file", e);
        }

        String data = "{\"port\":8080,\"webroot\":\"/\",\"host\":\"localhost\"}";
        assertEquals(data, config.toString());
    }

    @Test
    public void JsonNodeConfigClass() throws JsonProcessingException {
        JsonNode config = null;
        JsonConfig currentConfig = null;
        try{
            config = Json.parse(readJsonFile().toString());
        }catch (IOException e){
            throw new HttpConfigurationException("Error while parsing file", e);
        }

        try{
            currentConfig = Json.fromJsonClass(config, JsonConfig.class);
        }catch (IOException e){
            throw new HttpConfigurationException("Error while parsing file, internal", e);
        }

        assertEquals("localhost", currentConfig.getHost());
        assertEquals("/", currentConfig.getWebroot());
        assertEquals(8080, currentConfig.getPort());

        String stringify = Json.stringify(config);
        String stringifyTruthy = Json.stringifyTruthy(config);

        //convert back to node
        JsonNode node = Json.toJson(stringify);

        String data = "{\n" +
                "  \"port\" : 8080,\n" +
                "  \"webroot\" : \"/\",\n" +
                "  \"host\" : \"localhost\"\n" +
                "}";

        String data2 = "{\"port\":8080,\"webroot\":\"/\",\"host\":\"localhost\"}";

        assertNotEquals(data2, stringifyTruthy);
        assertEquals(data2, stringify);
        assertEquals(data, stringifyTruthy);
        assertNotEquals(data, stringify);
        assertEquals("\"{\\\"port\\\":8080,\\\"webroot\\\":\\\"/\\\",\\\"host\\\":\\\"localhost\\\"}\"", node.toString());

    }

    public StringBuffer readJsonFile(){
        FileReader fileReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        try{
            fileReader = new FileReader(path);
        }catch (FileNotFoundException e){
            throw new HttpConfigurationException(e);
        }

        int i;
        try {
            while((i =  fileReader.read()) != -1){
                stringBuffer.append((char) i);
            }
        }catch (IOException e){
            throw new HttpConfigurationException(e);
        }

        return stringBuffer;
    }
}
