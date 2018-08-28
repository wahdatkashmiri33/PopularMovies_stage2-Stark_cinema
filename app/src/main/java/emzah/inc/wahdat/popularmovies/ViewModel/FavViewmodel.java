package emzah.inc.wahdat.popularmovies.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import emzah.inc.wahdat.popularmovies.Database.AppDatabase;
import emzah.inc.wahdat.popularmovies.Database.FavouritesModal;

import java.util.List;

public class FavViewmodel extends AndroidViewModel {


    //Android viewmodel class will have a viewmodel that will receive a paramenter of type applictiom
    //we are going to use this viewmodel to cache the data of favourite model objects

    //this var will be private and will have a public getter
    private LiveData<List<FavouritesModal>> favourites;
    public FavViewmodel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase=AppDatabase.getInstance(this.getApplication());
        Log.d("viewmodel test","retreiving data from database");
       favourites= appDatabase.moviesDao().getAllMovies();
        //and now our viewmodel is ready
    }

    public LiveData<List<FavouritesModal>> getFavourites() {

        return favourites;
    }
}
