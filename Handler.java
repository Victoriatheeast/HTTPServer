import java.net.*;
import java.io.*;
import java.util.*;


/**
 * Handler class handles concurrent requests from clients based on multi-threading
 */
public class Handler implements Runnable {

    //The accepted socket from the web server
    private Socket socket;

    //BufferedReader that reads input from clients
    private BufferedReader reader;

    //DataOutputStream that sends out messages from the server
    private DataOutputStream OStream;

    private Map<String, byte[]> fileMap;
    private Map<String,String> redirectMap;

    //Carriage Return and new Line Feed
    private static final String CRLF = "\r\n";

    /**
     * Constructs a Handler
     * @param client client socket
     */
    public Handler(Socket client){
        this.socket = client;
    }

    /**
     * Implement the run() method of the Runnable interface
     */
    public void run(){
        processRequest();
    }

    /**
     * Process request
     */
    private void processRequest(){

        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.OStream = new DataOutputStream((socket.getOutputStream()));

            //Declare and initialize an array list to store request message
            ArrayList<String> requestMessage = new ArrayList<>();
            String requestLine;
            do {
                //Read, print, and add each line of the request message to the list
                requestLine = this.reader.readLine();
                requestMessage.add(requestLine);
                System.out.println(requestLine);

            } while (requestLine != null && requestLine.length() > 0);

            //Declare and initialize a http request
            Request newRequest = new Request(requestMessage);

            if(newRequest.getMethod() == Request.Method.GET || newRequest.getMethod() == Request.Method.HEAD) {

                redirectMap = WebServer.redirectMap;
                fileMap = WebServer.fileMap;

                //If the requested URL is an URL in the redirect file, return 301
                if (redirectMap.containsKey(newRequest.getURL())) {
                    return301(newRequest, redirectMap.get(newRequest.getURL()));
                    //If not then if the file is not in the file structure, return 404
                } else if (!fileMap.containsKey(newRequest.getURL())) {
                    return404(newRequest);
                    //Then return 200
                } else {
                    byte[] data = fileMap.get(newRequest.getURL());
                    return200(newRequest, data);
                }
            }
            else{
                //Return 403 to all other methods except GET and HEAD, POST is unsupported so it also returns a 403
                return403(newRequest);
            }
        }

        catch (Exception e){
            System.err.println("Client socket is disconnected");
        }

        // Close streams and socket.
        finally {

            try {
                reader.close();
                OStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Return a 403 Status code to the malformed request or unknown request method
     * @param request http request
     */
    private void return403(Request request){

        //Create response message
        String response = new String();
        response += request.getProtocolVersion()+" 403 Forbidden"+CRLF
                +  "Server: MyServer"+CRLF
                +  "Date:"+new Date()+CRLF
                + "Content-Type: "+contentType(request.getURL())+CRLF
                + "Connection: close"+CRLF;

        try {
            OStream.writeBytes(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Return a 404 Status code to request for the files not found
     * @param request http request
     */
    private void return404(Request request){

        //Create html body
        String messageBody = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD>" + CRLF
                + "<BODY><H1>404 Not Found </H1>"+ CRLF
                + request.getURL()+" is not found on this server"+ CRLF
                +"</BODY></HTML>"+CRLF;

        //Create response message
        String response = "HTTP/1.1 404 Not Found"+CRLF
                + "Server: MyServer"+CRLF
                + "Date:"+new Date()+CRLF
                + "Content-Type: text/html"+CRLF
                + "Connection: close"+CRLF
                + "Content-Length: " + messageBody.length()+CRLF;
        if (request.getMethod() == Request.Method.GET) {
            response += CRLF + messageBody + CRLF;
        }

            try {
            OStream.writeBytes(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Return 301 for requests that has been moved, and redirect to the new URL
     * @param request HTTP request
     * @param redirectURl new URL redirected to
     */
    private void return301(Request request, String redirectURl){

        String response = request.getProtocolVersion()+" 301 Moved Permanently"+CRLF
                + "Server: MyServer"+CRLF
                + "Date:"+new Date()+CRLF
                + "Location: "+redirectURl+CRLF
                + "Content-Type: "+contentType(request.getURL())+CRLF
                + "Connection: close"+CRLF;

        try {
            OStream.writeBytes(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return 200 to GET or Head method
     * @param request HTTP request
     */
    private void return200(Request request, byte[] data){
        String response = request.getProtocolVersion()+" 200 OK"+CRLF
                + "Content-Type: " + contentType(request.getURL())+CRLF
                + "Server: MyServer"+CRLF
                + "Date:"+new Date()+CRLF
                + "Connection: close"+CRLF
                + String.format("Content-Length: %d"+CRLF, data.length);
        try {
            OStream.writeBytes(response);
            if (request.getMethod() == Request.Method.GET) {
                //Blank line
                OStream.writeBytes(CRLF);
                OStream.write(data);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Get the content type of the file
     * @param fileName the name of file
     * @return the content type of the file
     */
    public static String contentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".txt")) {
            return "text/plain";
        } else if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpg";
        } else if (fileName.endsWith(".gif")) {
            return "image/png";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }
}



