package json;

import java.util.List;

/**
 * Created by XMBomb on 07.04.2017.
 */
public class SpotifyArtistsResponse {
    List<Artist> artists;

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
}
