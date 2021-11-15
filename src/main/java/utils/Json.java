package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class Json {

    private static ObjectMapper objectMapper = defaultObjectMapper();

    /**
     * Loads the object mapper class, and configures it
     * so, app won't crash if a property is found missing
     * */
    private static ObjectMapper defaultObjectMapper(){
        ObjectMapper obj = new ObjectMapper();
        obj.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return obj;
    }

    /**
     * Convert Json String to Json Node
     * @param jsonSrc
     * */
    public static JsonNode parse(String jsonSrc) throws JsonProcessingException {
        return objectMapper.readTree(jsonSrc);
    }

    /**
     * Object mapper maps JsonConfig class to the Json Node
     * @param tClass , generic class
     * @param node
     * @return obj of type T
     * */
    public static <T> T fromJsonClass(JsonNode node, Class<T> tClass) throws JsonProcessingException {
        return objectMapper.treeToValue(node, tClass);
    }

    /**
     * Returns a Json Node
     * @param obj
     * @return JsonNode
     * */
    public static JsonNode toJson(Object obj){
        return objectMapper.valueToTree(obj);
    }

    /**
     * Helps to stringifies Json obj based on whether
     * argument passed to parameter truthy is false or true
     * @param obj
     * @param truthy
     * @return String
     * */
    private static String generateJson(Object obj, boolean truthy) throws JsonProcessingException {
        ObjectWriter objectWriter = objectMapper.writer();

        if(truthy)objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        return objectWriter.writeValueAsString(obj);
    }

    /**
     * returns json as string on a straight line, No indentation
     * @param node
     * @return String
     * */
    public static String stringify(JsonNode node) throws JsonProcessingException {
        return generateJson(node, false);
    }

    /**
     * returns json as string, With the indentation
     * @param node
     * @return String
     * */
    public static String stringifyTruthy(JsonNode node) throws JsonProcessingException {
        return generateJson(node, true);
    }
}
