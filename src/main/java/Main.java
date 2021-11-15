import core.ServerListeningThread;
import http_configuration.ConfigurationManager;
import http_configuration.JsonConfig;
import org.apache.log4j.PropertyConfigurator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class Main {

    public static void main(String[] args) throws IOException {
        List<Integer> list = new ArrayList<>();

        PropertyConfigurator.configure("src/main/resources/log4j.properties");
        System.out.println("Server starting...");
        ConfigurationManager.getInstance().loadConfigFile("src/main/resources/http_server.json");

        JsonConfig config = ConfigurationManager.getInstance().getCurrentConfig();

        System.out.println("Using Port: "+ config.getPort());
        System.out.println("Using Web Root: "+ config.getWebroot());

        try{
            ServerListeningThread serverListeningThread = new ServerListeningThread(config.getHost(), config.getPort(), config.getWebroot());
            serverListeningThread.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
