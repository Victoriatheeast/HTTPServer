import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * The WebServer Class establishes a Server
 */
public class WebServer{

    //Redirect file name
    private static final String REDIRECT_FILE = "/redirect.defs";

    //Current Directory
    private static final String ROOT_DIRECTORY = "./www";

    //Map which stores all the files in the serving tree
    public static Map<String, byte[]> fileMap;

    //Map which stores all the redirected URL
    public static Map<String,String> redirectMap;


    /**
     * Get all the files in a directory
     * @param directory the directory file
     * @return a list of files in a directory
     */
    private static ArrayList<File> getFilesInDirectory(File directory) {
        ArrayList<File> files = new ArrayList<>();
        File[] allFiles = directory.listFiles();
        for (File file : allFiles) {
            if (file.isDirectory()) {
                files.addAll(getFilesInDirectory(file));
            } else if (file.isFile()) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Read file and get the data in each file
     * @param file
     * @return the content/data in the file in bytes
     * @throws IOException
     */
    private static byte[] readFile(File file) {
        int fileLength = (int) file.length();
        //An array of bytes storing the data in the file
        byte[] fileContent = new byte[fileLength];
        try {
            FileInputStream fis = new FileInputStream(file);
            //Reads fileContent.length bytes of data from this input stream into an array of bytes.
            fis.read(fileContent);

        } catch (IOException e) {
            e.printStackTrace();

        }
        return fileContent;
    }

    /**
     * Load all the files in the serving tree
     */
    public static void loadFiles(){

        fileMap = new HashMap<>();

        //The Root Directory of the Server
        System.out.println("Root = " + ROOT_DIRECTORY);
        ArrayList<File> filesInDirectory = getFilesInDirectory(new File(ROOT_DIRECTORY));


        for (File file : filesInDirectory) {
            //Use all the relative path of the file as the key for the fileMap
            String relativePath = file.getPath().replaceFirst(ROOT_DIRECTORY, "");
            if (!relativePath.equals(REDIRECT_FILE)) {
                System.out.println(relativePath);
                fileMap.put(relativePath, readFile(file));
            }
        }

    }

    /**
     * Load all the redirect urls in the redirect file, and add them in a map
     */
    public static void loadRedirect(){

        redirectMap = new HashMap<>();

        String redirectPath = ROOT_DIRECTORY + REDIRECT_FILE;
        System.out.println("Redirect_File = " + redirectPath);
        File redirectFile = new File(redirectPath);
        if (!redirectFile.exists()) {
            System.out.println("Redirect file is not found");
        } else {
            String fileContent = new String(readFile(redirectFile));
            String[] lines = fileContent.split("\n");
            for (String line : lines) {
                System.out.println(line);
                String[] split = line.split("\\s+");
                if (split.length == 2) {
                    redirectMap.put(split[0], split[1]);
                }
            }
        }
    }



    /**
     * The main method which starts the server and binds to a TCP port
     * @param argv command-line flag
     */
    public static void main(String[] argv) {
        // Server port.
        int portNumber = -1;

        // Process command-line arguments.
        for (String arg : argv) {
            String[] splitArg = arg.split("=");
            if (splitArg.length == 2 && splitArg[0].equals("--serverPort")) {
                portNumber = Integer.parseInt(splitArg[1]);
            } else {
                System.err.println("Usage: java WebServer --serverPort=<port>");
                return;
            }
        }

        // Check port number.
        if (portNumber == -1) {
            System.err.println("Must specify port number with --serverPort");
            return;
        }
        if (portNumber <= 1024) {
            System.err.println("Avoid potentially reserved port number: " + portNumber + " (should be > 1024)");
            return;
        }

        try {
            // Bind to new server socket to server port.
            ServerSocket serverSocket = new ServerSocket(portNumber);

            //Load files and redirect in the serving tree
            loadFiles();
            loadRedirect();


            // Accept requests indefinitely.
            while (true) {
                System.out.println("Web server listening on port " + portNumber + " ...");

                Socket socket = serverSocket.accept();

                //Handle concurrent requests
                Handler requestHandler = new Handler(socket);
                Thread thread = new Thread(requestHandler);
                thread.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
