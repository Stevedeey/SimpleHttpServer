package http_configuration;

import Exceptions.HttpConfigurationException;
import com.fasterxml.jackson.databind.JsonNode;
import utils.Json;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *  A configuration singleton class, that reads data from the http_server
 *  .json file with the help of (Json class) from utils
 * */
public class ConfigurationManager {
    private static ConfigurationManager configurationManager;
    private static JsonConfig currentConfig;

    private ConfigurationManager() {}

    public static ConfigurationManager getInstance(){
        if(configurationManager == null) configurationManager = new ConfigurationManager();
        return configurationManager;
    }

    /**
     * This method reads the json config file and load it inside a Json Node
     * @param path
     * */
    public void loadConfigFile(String path){
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

        JsonNode config = null;
        try{
            config = Json.parse(stringBuffer.toString());
        }catch (IOException e){
            throw new HttpConfigurationException("Error while parsing file", e);
        }

        try{
            currentConfig = Json.fromJsonClass(config, JsonConfig.class);
        }catch (IOException e){
            throw new HttpConfigurationException("Error while parsing file, internal", e);
        }

    }

    /**
     * @return JsonConfig
     * */
    public JsonConfig getCurrentConfig(){
        if(currentConfig ==  null) throw new HttpConfigurationException("No Current exception setup");
        return currentConfig;
    }
}
