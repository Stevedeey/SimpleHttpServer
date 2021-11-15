package core;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class HttpConnectionThread extends Thread{

    private Socket socket;
    private static String webRoot;
    final static Logger logger = Logger.getLogger(ServerListeningThread.class.getName());

    public HttpConnectionThread(Socket socket, String webRoot) {
        this.socket = socket;
        this.webRoot = webRoot;
    }

    @Override
    public void run() {
        handleClientConnection();
    }

    /**
     * Reads client request information from socket
     * Reads from the file that we want to serve to the client(Html or json file)
     * Then send data(together with some read by socket) to the client via the specified port and webroot,
     * any port or root other than the one specified, it returns Not found 400 ERROR
     * */
    private void handleClientConnection(){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder requestBuilder = new StringBuilder();
            String line;

            while (!(line = br.readLine()).isBlank()) {
                requestBuilder.append(line + "\r\n");
            }

            String request = requestBuilder.toString();
            String[] requestsLines = request.split("\r\n");
            String[] requestLine = requestsLines[0].split(" ");
            String method = requestLine[0];
            String path = requestLine[1];
            String version = requestLine[2];
            String host = requestsLines[1].split(" ")[1];


            List<String> headers = new ArrayList<>();
            for (int h = 2; h < requestsLines.length; h++) {
                String header = requestsLines[h];
                headers.add(header);
            }

            // client's data information
            String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                    socket.toString(), method, path, version, host, headers.toString());
            System.out.println(accessLog);

            Path filePath = getFilePath(path);

            if (Files.exists(filePath)) {
                String contentType = guessContentType(filePath);
                sendResponse(socket, "200 OK", contentType, Files.readAllBytes(filePath));
            } else {
                // 404
                byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
                sendResponse(socket, "404 Not Found", "text/html", notFoundContent);
            }

            logger.info("Connection Processing finished");

        }catch (IOException e){
            System.out.println("Don't crash");
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Responsible for writing data to the client, including Header information
     * @param client
     * @param content
     * @param contentType
     * @param status
     * */
    private void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        String data = "";

        for(byte b: content){
            data += (char) b;
        }

        final String CRLF = "\n\r";
        String response = "HTTP/1.1 "+status+" OK" + CRLF +
                    "Content-length: " + data.getBytes().length + CRLF + "Content-type: " + contentType + CRLF +
                    CRLF + data + CRLF + CRLF;

        clientOutput.write(response.getBytes());
        clientOutput.flush();
        clientOutput.close();
    }

    /**
     * Helps to return the right resource base on the url end-point visited
     * e.g / or /json
     * @param path
     * @return Path
     * */
    private static Path getFilePath(String path) {
        if (webRoot.equals(path)) {
            path = webRoot+"home.html";
        }else if("/json".equals(path)){
            path = webRoot+"books.json";
        }

        return Paths.get("src/main/resources/", path);
    }

    /**
     * Returns the content-type of a particular file
     * e.g application/json or text/html
     * */
    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
}
