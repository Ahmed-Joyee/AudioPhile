package app.android.audiophile;

public class YoutubePlay {
    private String nameOfSong;
    private String videoId;

    public YoutubePlay(String nameOfSong, String videoId) {
        this.nameOfSong = nameOfSong;
        this.videoId = videoId;
    }

    public String getNameOfSong() {
        return nameOfSong;
    }

    public void setNameOfSong(String nameOfSong) {
        this.nameOfSong = nameOfSong;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
