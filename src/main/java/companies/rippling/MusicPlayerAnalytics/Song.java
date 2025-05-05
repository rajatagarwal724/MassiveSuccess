package companies.rippling.MusicPlayerAnalytics;

import lombok.Data;

@Data
public class Song {
    private String songId;
    private String title;

    public Song(String songId, String title) {
        this.songId = songId;
        this.title = title;
    }
}
