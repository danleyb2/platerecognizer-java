import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;


public class Main {

    static String charset = "UTF-8";
    static String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
    static String CRLF = "\r\n"; // Line separator required by multipart/form-data.

    public static void main(String[] args) {

        // Get api key from https://app.platerecognizer.com/start/ and replace MY_API_KEY
        String token = "77c2fabd94b7d0c9b6ac63c###################";

        String file = "C:\\Users\\danleyb2\\Downloads\\pic1.jpg";
        // String file = "/home/danleyb2/Pictures/mrc.jpg";

        try {
            File binaryFile = new File(file);

            URL obj = new URL("https://api.platerecognizer.com/v1/plate-reader/");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Token " + token);
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setRequestProperty("Accept", "application/json");

            // For POST only - START
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            // os.write(POST_PARAMS.getBytes());
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, charset), true);

            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"upload\"; filename=\"" + binaryFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(binaryFile.toPath(), os);
            os.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
            os.close();
            // For POST only - END

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // print result
                System.out.println(response.toString());
            } else {
                System.out.println("POST request failed");
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}

