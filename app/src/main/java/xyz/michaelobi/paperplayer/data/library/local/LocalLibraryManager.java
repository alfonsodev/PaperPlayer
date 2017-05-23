package xyz.michaelobi.paperplayer.data.library.local;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import xyz.michaelobi.paperplayer.data.library.LibraryManager;
import xyz.michaelobi.paperplayer.data.model.Album;
import xyz.michaelobi.paperplayer.data.model.Artist;
import xyz.michaelobi.paperplayer.data.model.Song;

import java.util.List;
import java.util.Locale;

import rx.Observable;
import xyz.michaelobi.paperplayer.data.model.Album;
import xyz.michaelobi.paperplayer.data.model.Song;

/**
 * PaperPlayer Michael Obi 22 10 2016 12:29 PM
 */

public class LocalLibraryManager implements LibraryManager {
    private final Context context;

    public LocalLibraryManager(Context context) {
        this.context = context;
    }


    private static Observable<Cursor> create(Cursor cursor) {
        return Observable.create(sub -> {
            if (sub.isUnsubscribed()) return;

            try {
                while (cursor.moveToNext()) {
                    if (sub.isUnsubscribed()) return;
                    sub.onNext(cursor);
                }
                sub.onCompleted();
            } catch (Exception e) {
                sub.onError(e);
            }
        });
    }

    @Override
    public Observable<List<Song>> fetchAllSongs() {
        QueryBuilder builder = new QueryBuilder(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                .where(MediaStore.Audio.Media.IS_MUSIC + "=1")
                .orderBy(MediaStore.Audio.Media.TITLE);
        Cursor cursor = builder.query();

        return create(cursor)
                .map(Song.Companion::from)
                .doOnCompleted(cursor::close)
                .toList();
    }


    @Override
    public Observable<List<Album>> fetchAllAlbums() {
        QueryBuilder builder = new QueryBuilder(context, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
                .orderBy(MediaStore.Audio.Albums.ALBUM);
        Cursor cursor = builder.query();
        return create(cursor)
                .map(Album.Companion::from)
                .doOnCompleted(cursor::close)
                .toList();
    }

    @Override
    public Observable<Song> getSong(long id) {
        return null;
    }

    @Override
    public Observable<Album> getAlbum(long id) {
        QueryBuilder builder = new QueryBuilder(context, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI)
                .where(String.format(Locale.getDefault(), "%s=%d", MediaStore.Audio.Albums._ID, id))
                .orderBy(MediaStore.Audio.Albums.ALBUM);
        Cursor cursor = builder.query();
        return create(cursor)
                .take(1)
                .map(Album.Companion::from)
                .doOnCompleted(cursor::close)
                .first();
    }

    @Override
    public Observable<List<Artist>> fetchAllArtists() {
        QueryBuilder builder = new QueryBuilder(context, MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI)
                .orderBy(MediaStore.Audio.Artists.ARTIST);
        Cursor cursor = builder.query();
        return create(cursor)
                .map(Artist.Companion::from)
                .doOnCompleted(cursor::close)
                .toList();
    }

}
