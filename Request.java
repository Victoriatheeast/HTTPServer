import java.util.ArrayList;

/**
 * The Request class renders and updates a HTTP Request
 */
public class Request{

    //Methods of the HTTP Request
    enum Method{
        GET,
        HEAD,
        POST,
        OTHER,
    }

    //HTTP Request Method
    private Method method;

    //file URL of the HTTP Request
    private String URL;

    //version of the HTTP Request
    private String protocolVersion;

    /**
     * Constructs a HTTP request
     * @param requestMessage list of lines in the request Message
     */
    public Request(ArrayList<String> requestMessage){
        if (requestMessage.size() == 0){
            throw new IllegalArgumentException("The request message is empty");
        }

        //Parse the firstLine by space
        String[] tokens = requestMessage.get(0).split("\\s+");

        if(tokens.length != 3) {
            throw new IllegalArgumentException("The request line is not in correct format!");
        }

            try {
                this.method = Method.valueOf(tokens[0]);
            } catch (IllegalArgumentException e) {
                //Mark illegal HTTP request
                System.out.println("Invalid HTTP request method:" + tokens[0]);
                this.method = Method.OTHER;
            }

            this.URL = tokens[1];
            this.protocolVersion = tokens[2];

    }

    /**
     * Get the method of the HTTP Request
     * @return the method of the HTTP Request
     */
    public Method getMethod(){
        return this.method;
    }

    /**
     * Get the protocol version of the HTTP Request
     * @return the protocol version of the HTTP Request
     */
    public String getProtocolVersion(){

        return this.protocolVersion;
    }

    /**
     * Get the file URL of the HTTP Request
     * @return the file URL of the HTTP Request
     */
    public String getURL(){

        return this.URL;
    }

}
