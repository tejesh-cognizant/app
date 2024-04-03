import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SharePointMultiFileUploader {
    public static void main(String[] args) throws IOException {
        String siteUrl = "Your SharePoint Site URL";
        String username = "Your Username";
        String password = "Your Password";
        String folderUrl = "Your SharePoint Folder URL";
        List<String> filePaths = List.of("Path to your local file 1", "Path to your local file 2", "Path to your local file 3");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (String filePath : filePaths) {
                HttpPost httpPost = new HttpPost(siteUrl + "/_api/web/getfolderbyserverrelativeurl('" + folderUrl + "')/files/add(url='" + new File(filePath).getName() + "', overwrite=true)");

                // Set authentication
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                httpPost.setHeader(HttpHeaders.AUTHORIZATION, authHeader);

                // Set file content
                File file = new File(filePath);
                InputStream inputStream = new FileInputStream(file);
                HttpEntity entity = MultipartEntityBuilder.create()
                        .addBinaryBody("file", inputStream, ContentType.create("application/octet-stream"), file.getName())
                        .build();
                httpPost.setEntity(entity);

                // Execute the request
                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    System.out.println("File '" + file.getName() + "' uploaded successfully.");
                } else {
                    System.err.println("Failed to upload file '" + file.getName() + "'. Status code: " + statusCode);
                }
            }
        }
    }
}
