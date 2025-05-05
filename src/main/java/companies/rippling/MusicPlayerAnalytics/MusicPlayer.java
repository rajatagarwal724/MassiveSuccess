package companies.rippling.MusicPlayerAnalytics;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class MusicPlayer {
    private static Map<String, Song> songs = new HashMap<>();
    private static Map<String, User> users = new HashMap<>();
    private static Map<String, Long> mostPlayedSongs = new HashMap<>();
    private static Queue<Map.Entry<String, Long>> maxHeap = new PriorityQueue<>(
            (o1, o2) -> (int) (o2.getValue() - o1.getValue())
    );

    void playSong(String songId, String userId) {
        mostPlayedSongs.put(songId, mostPlayedSongs.getOrDefault(songId, 0L) + 1L);
    }

    void addSong(String songId, String title) {
        songs.put(songId, new Song(songId, title));
    }

    void printAnalytics() {
        maxHeap.addAll(mostPlayedSongs.entrySet());
        while (!maxHeap.isEmpty()) {
            var playedSongEntry = maxHeap.poll();
            System.out.println("Song: " + songs.get(playedSongEntry.getKey()).getTitle() + " No Of Times Played: " + playedSongEntry.getValue());
        }
    }

    public static void main(String[] args) {
        var player = new MusicPlayer();
        player.addSong("1", "test1");
        player.playSong("1", "ss");

        player.addSong("2", "test2");
        player.playSong("2", "ss");
        player.playSong("2", "ss");
        player.playSong("2", "ss");

        player.printAnalytics();
    }
}
