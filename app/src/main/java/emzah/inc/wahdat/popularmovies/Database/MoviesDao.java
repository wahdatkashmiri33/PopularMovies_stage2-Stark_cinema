package emzah.inc.wahdat.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM favourite_movies")
     LiveData<List<FavouritesModal>> getAllMovies();

    @Query("SELECT * FROM favourite_movies WHERE movie_id = :movieid")
    FavouritesModal checkifExists(String movieid);

    @Insert
    void insertFavMovie(FavouritesModal favouritesModal);

   // @Query("DELETE  FROM favourite_movies WHERE id = :id")
   @Query("DELETE FROM favourite_movies WHERE movie_id = :movieid")
    void deleteFavMovie(String movieid);
}
