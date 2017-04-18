import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by XMBomb on 07.04.2017.
 */
public class App {
    private static final int API_WAIT_TIME = 100;
    private static final String ACCESS_TOKEN_PATH = "res" + File.separator + "auth-key.txt";
    private static final String OUTPUT_PATH = "res" + File.separator + "artists.txt";
    private static final String BASE_REQUEST_URL ="https://api.spotify.com/v1/me/following";
    private static final int MAX_LIMIT = 50;
    private List<String> allArtists = new ArrayList<>();

    public App() {
        super();
    }

    public static void main(String... args) throws IOException, UnirestException, InterruptedException {
        App app = new App();
        app.init();
    }

    private void init() throws IOException, UnirestException, InterruptedException {
        final String oAuthToken = FileUtils.readFileToString(new File(ACCESS_TOKEN_PATH), Charset.forName("UTF-8"));
        Map<String, String> headers = buildHttpHeaders(oAuthToken);


        String lastArtistId = null;

        while (true){
            try {
                int lastLoopArtistSize = allArtists.size();
                HttpResponse<JsonNode> response = getRequestedArtists(BASE_REQUEST_URL, headers, MAX_LIMIT, lastArtistId);
                JSONObject artists = (JSONObject) response.getBody().getObject().get("artists");
                JSONArray items = ((JSONArray) artists.get("items"));

                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    allArtists.add(item.get("name").toString());
                    lastArtistId = item.get("id").toString();
                }

                if (allArtists.size() == lastLoopArtistSize){
                    break;
                }

                Thread.sleep(API_WAIT_TIME);
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
        }

        String allArtistsStr = allArtists.stream().sorted().collect(Collectors.joining("\n"));
        FileUtils.writeStringToFile(new File(OUTPUT_PATH), allArtistsStr , Charset.forName("UTF-8"));
    }

    private Map<String, String> buildHttpHeaders(String oAuthToken){
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + oAuthToken);
        return headers;
    }

    private HttpResponse<JsonNode> getRequestedArtists(String requestUrl, Map<String, String> headers, int limit, String afterArtistId) throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        if (afterArtistId != null) {
            jsonResponse = Unirest.get(requestUrl).headers(headers)
                    .queryString("type", "artist")
                    .queryString("limit", limit)
                    .queryString("after", afterArtistId)
                    .asJson();
        }
        else{
            jsonResponse = Unirest.get(requestUrl).headers(headers)
                    .queryString("type", "artist")
                    .queryString("limit", limit)
                    .asJson();
        }

        return jsonResponse;
    }

}
