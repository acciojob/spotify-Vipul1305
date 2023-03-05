package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        boolean contains = false;
        for (Artist artist: artists){
            String name = artist.getName();
            if(artistName.equals(name)){
                contains = true;
                break;
            }
        }
        if (!contains){
            Artist artist = new Artist(artistName);
            artists.add(artist);
        }
        Album album = new Album(title);
        albums.add(album);
        for (Artist artist: artists) {
            String name = artist.getName();
            if(artistName.equals(name)) {
                List<Album> albumList = new ArrayList<>();
                if (artistAlbumMap.containsKey(artist)) {
                    albumList = artistAlbumMap.get(artist);
                }
                albumList.add(album);
                artistAlbumMap.put(artist, albumList);
            }
        }
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean contains = false;
        for (Album album: albums){
            String name = album.getTitle();
            if (albumName.equals(name)){
                contains = true;
                break;
            }
        }
        if (!contains){
            throw new Exception("Album does not exist");
        }
        Song song = new Song(title,length);
        songs.add(song);
        for (Album album: albums) {
            String name = album.getTitle();
            if (albumName.equals(name)) {
                List<Song> songList = new ArrayList<>();
                if(albumSongMap.containsKey(album)){
                    songList = albumSongMap.get(album);
                }
                songList.add(song);
                albumSongMap.put(album,songList);
            }
        }
        songLikeMap.put(song,new ArrayList<>());
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        boolean contains = false;
        for (User user: users){
            String userMobile = user.getMobile();
            if(mobile.equals(userMobile)){
                contains = true;
                break;
            }
        }
        if (!contains){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> songList = new ArrayList<>();
        for (Song song: songs){
            int songLength = song.getLength();
            if(songLength == length){
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist,songList);
        for (User user: users) {
            String userMobile = user.getMobile();
            if (mobile.equals(userMobile)) {
                creatorPlaylistMap.put(user, playlist);

                List<User> userList = new ArrayList<>();
                if (playlistListenerMap.containsKey(playlist)){
                    userList = playlistListenerMap.get(playlist);
                }
                userList.add(user);
                playlistListenerMap.put(playlist,userList);
            }
        }
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        boolean contains = false;
        for (User user: users){
            String userMobile = user.getMobile();
            if(mobile.equals(userMobile)){
                contains = true;
                break;
            }
        }
        if (!contains){
            throw new Exception("User does not exist");
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        List<Song> songList = new ArrayList<>();
        for (Song song: songs){
            String songT = song.getTitle();
            if(songTitles.contains(songT)){
                songList.add(song);
            }
        }
        playlistSongMap.put(playlist,songList);
        for (User user: users) {
            String userMobile = user.getMobile();
            if (mobile.equals(userMobile)) {
                creatorPlaylistMap.put(user, playlist);

                List<User> userList = new ArrayList<>();
                if (playlistListenerMap.containsKey(playlist)){
                    userList = playlistListenerMap.get(playlist);
                }
                userList.add(user);
                playlistListenerMap.put(playlist,userList);
            }
        }
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        boolean contains = false;
        User user = new User();
        for (User u: users){
            String userMobile = u.getMobile();
            if(mobile.equals(userMobile)){
                contains = true;
                user = u;
                break;
            }
        }
        if (!contains){
            throw new Exception("User does not exist");
        }
        boolean contains1 = false;
        for (Playlist playlist: playlists){
            String playTitle = playlist.getTitle();
            if(playlistTitle.equals(playTitle)){
                contains1 = true;
                if (creatorPlaylistMap.containsKey(user) || playlistListenerMap.get(playlist).contains(user)){
                    break;
                }else {
                    List<User> userList = new ArrayList<>();
                    if(playlistListenerMap.containsKey(playlist)){
                        userList = (playlistListenerMap.get(playlist));
                    }
                    userList.add(user);
                    playlistListenerMap.put(playlist,userList);
                }
            }
        }
        if (!contains1){
            throw new Exception("Playlist does not exist");
        }
        return new Playlist();

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        boolean contains = false;
        User user = new User();
        for (User u: users){
            String userMobile = u.getMobile();
            if(mobile.equals(userMobile)){
                contains = true;
                user = u;
                break;
            }
        }
        if (!contains){
            throw new Exception("User does not exist");
        }
        boolean contains1 = false;
        for (Song song: songs) {
            String s = song.getTitle();
            if (songTitle.equals(s)) {
                contains1 = true;
                if(!songLikeMap.get(song).contains(user)) {
                    List<User> userList = new ArrayList<>();
                    if (songLikeMap.containsKey(song)) {
                        userList = songLikeMap.get(song);
                    }
                    userList.add(user);
                    songLikeMap.put(song, userList);

                    for (Artist artist : artistAlbumMap.keySet()) {
                        List<Album> albumList = artistAlbumMap.get(artist);
                        for (Album album : albumList) {
                            if (albumSongMap.get(album).contains(song)) {
                                artist.setLikes(artist.getLikes() + 1);
                            }
                        }
                    }
                }

            }
        }
        if (!contains1){
            throw new Exception("Song does not exist");
        }
        return new Song();
    }

    public String mostPopularArtist() {
        int max = 0;
        String name = "";
        for (Artist artist: artists){
            int likes = artist.getLikes();
            if(likes > max){
                max = likes;
                name = artist.getName();
            }
        }
        return name;
    }

    public String mostPopularSong() {
        int max  = 0;
        String title = "";
        for (Song song: songLikeMap.keySet()){
            int likes = songLikeMap.get(song).size();
            if(likes > max){
                max = likes;
                title = song.getTitle();
            }
        }
        return title;
    }
}
