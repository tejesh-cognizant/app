import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SharePointFileUploaderUtil {

    private static final String SITE_URL = "https://your-sharepoint-site-url";
    private static final String FOLDER_PATH = "/sites/your-site-name/Shared Documents/YourFolderName/";
    private static final String USERNAME = "your-username";
    private static final String PASSWORD = "your-password";

    public static void main(String[] args) {
        String filePath = "path-to-your-local-file";
        String fileName = "name-of-the-file-in-sharepoint";
        uploadFile(filePath, fileName);
    }

    public static void uploadFile(String filePath, String fileName) {
        try {
            // Construct URL
            URL url = new URL(SITE_URL + "/_api/web/getfolderbyserverrelativeurl('" + FOLDER_PATH + "')/files/add(url='" + fileName + "', overwrite=true)");

            // Establish connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Set authentication
            String userCredentials = USERNAME + ":" + PASSWORD;
            String basicAuth = "Basic " + new String(java.util.Base64.getEncoder().encode(userCredentials.getBytes()));
            connection.setRequestProperty("Authorization", basicAuth);

            // Set content type
            connection.setRequestProperty("Accept", "application/json;odata=verbose");
            connection.setRequestProperty("Content-Type", "application/json;odata=verbose");

            // Get the output stream
            OutputStream outputStream = connection.getOutputStream();

            // Read the file
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            // Check response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("File uploaded successfully!");
            } else {
                System.out.println("Error uploading file. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
