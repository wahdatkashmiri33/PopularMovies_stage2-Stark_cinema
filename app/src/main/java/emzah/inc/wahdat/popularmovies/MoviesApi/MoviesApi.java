package emzah.inc.wahdat.popularmovies.MoviesApi;

import emzah.inc.wahdat.popularmovies.Model.Model;
import emzah.inc.wahdat.popularmovies.Model.Review;
import emzah.inc.wahdat.popularmovies.Model.Trailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MoviesApi {




    public interface Api{

        String BASE_URL="http://api.themoviedb.org/3/movie/";

        @GET("popular")
        Call<Model> getPopularMovies(@Query("api_key") String KEY);

        @GET("top_rated")
        Call<Model> getTopRatedMovies(@Query("api_key") String KEY);
        @GET("{movie_id}/videos")
        Call<Trailer> getTrailer (@Path("movie_id") String movieId, @Query("api_key") String apiKey);
        @GET("{movie_id}/reviews")
        Call<Review> getReview(@Path("movie_id")String movieId, @Query("api_key") String apiKey);


    }
}
