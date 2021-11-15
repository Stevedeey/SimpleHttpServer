package core;

import org.apache.log4j.PropertyConfigurator;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ServerListeningThread extends Thread{
    private int port;
    private String webroot;
    private ServerSocket serverSocket;
    final static Logger logger = Logger.getLogger(ServerListeningThread.class.getName());
    private static String loggerPath = "src/main/resources/log4j.properties";


    public ServerListeningThread(String host, int port, String webroot) throws IOException {
        this.port = port;
        this.webroot = webroot;

        PropertyConfigurator.configure(loggerPath);

        if(host == "localhost")
            this.serverSocket = new ServerSocket(this.port, 0, InetAddress.getLocalHost());
        else
            this.serverSocket = new ServerSocket(this.port, 0, InetAddress.getByName(host));
    }

    /**
     * A separate thread that listen to connection
     * Creates a socket connection
     * Passes it to thread(s) that needed it,
     * Here we can have more than a thread that want to use that socket connection
     * */
    @Override
    public void run() {
        try{
            while (serverSocket.isBound() && !serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                logger.info("Connection accepted: "+socket.getInetAddress());

                HttpConnectionThread workerThread = new HttpConnectionThread(socket, webroot);

                workerThread.start();
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
