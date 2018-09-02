package emzah.inc.wahdat.popularmovies.UI;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import emzah.inc.wahdat.popularmovies.Adapter.Adapter;
import emzah.inc.wahdat.popularmovies.Adapter.FavouritesAdapter;
import emzah.inc.wahdat.popularmovies.BuildConfig;
import emzah.inc.wahdat.popularmovies.Database.FavouritesModal;
import emzah.inc.wahdat.popularmovies.ViewModel.FavViewmodel;
import emzah.inc.wahdat.popularmovies.Model.Model;

import emzah.inc.wahdat.popularmovies.Model.Result;
import emzah.inc.wahdat.popularmovies.MoviesApi.MoviesApi;
import emzah.inc.wahdat.popularmovies.NetworkInfo.Network;
import emzah.inc.wahdat.popularmovies.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import emzah.inc.wahdat.popularmovies.BuildConfig;
import emzah.inc.wahdat.popularmovies.Model.Model;
import emzah.inc.wahdat.popularmovies.MoviesApi.MoviesApi;
import emzah.inc.wahdat.popularmovies.NetworkInfo.Network;
import emzah.inc.wahdat.popularmovies.ViewModel.FavViewmodel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,Adapter.OnItemClickListener{


    @BindView(emzah.inc.wahdat.popularmovies.R.id.recyclerview) RecyclerView recyclerView;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.contentlayout) ConstraintLayout constraintLayout;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.nofav) TextView textView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;
    private Model moviesModal;

    private List<Result> movieResults;

    private FavouritesAdapter favouritesAdapter;

    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;
    //

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    String API_KEY= BuildConfig.GoogleSecAPIKEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(emzah.inc.wahdat.popularmovies.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(emzah.inc.wahdat.popularmovies.R.id.toolbar);
        setSupportActionBar(toolbar);





        DrawerLayout drawer = (DrawerLayout) findViewById(emzah.inc.wahdat.popularmovies.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, emzah.inc.wahdat.popularmovies.R.string.navigation_drawer_open, emzah.inc.wahdat.popularmovies.R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(emzah.inc.wahdat.popularmovies.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        movieResults= new ArrayList<>();
        //Bindingview

        ButterKnife.bind(this);
        settinglayoutmanager();

        boolean isConnected=  Network.getConnectivityStatus(MainActivity.this);
        if (isConnected){
            loadPopularMovies();
        }
        else {

            checkNetworkConnection();
            Snackbar.make(constraintLayout,"Please Check Internet Connection",Snackbar.LENGTH_LONG).show();
        }

//

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save list state
        listState=layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY,listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Retrieve list state and list/item positions
        if (savedInstanceState !=null){
            listState=savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listState !=null){
            layoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void checkNetworkConnection() {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection and then Refresh to continue Or press close and check favourite lists");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void settinglayoutmanager() {

        //int resId = R.anim.layout_animation_fall_down;
        //LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        int noOfColumns= 2;

        layoutManager=new GridLayoutManager(this,noOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(getApplicationContext(), emzah.inc.wahdat.popularmovies.R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setHasFixedSize(true);

        //recyclerView.setLayoutAnimation(animation);
    }

    private void loadPopularMovies() {
        // Creating a retrofit object
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Creating the Api interface
        MoviesApi.Api api= retrofit.create(MoviesApi.Api.class);

        //now making the call object
        Call<Model> call= api.getPopularMovies(API_KEY);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                moviesModal= response.body();
                movieResults=new ArrayList<>();
                movieResults=moviesModal.getResults();
                adapter=new Adapter(movieResults,getApplicationContext());
                adapter.setonItemClickListener(MainActivity.this);

                recyclerView.setAdapter(adapter);

                //animation

                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
                Log.d("MEssage",""+response);


            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Log.d("MEssage",""+t);
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(emzah.inc.wahdat.popularmovies.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public  boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == emzah.inc.wahdat.popularmovies.R.id.nav_popular) {
            // Handle the camera action
            boolean isConnected=  Network.getConnectivityStatus(MainActivity.this);
            if (isConnected){
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadPopularMovies();
            }
            else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout,"Please Check Internet Connection",Snackbar.LENGTH_LONG).show();
            }
        } else if (id == emzah.inc.wahdat.popularmovies.R.id.nav_Top_rated) {

            boolean isConnected=  Network.getConnectivityStatus(MainActivity.this);
            if (isConnected){
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadTopRatedMovies();
            }
            else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout,"Please Check Internet Connection",Snackbar.LENGTH_LONG).show();
            }

        }  else if (id== emzah.inc.wahdat.popularmovies.R.id.favourites){


       
            loadfavouritemovies();


        }
        else if (id==R.id.share){
            String MIMETYPE="text/plain";
            String title="Share Stark Cinema";
            String link="https://play.google.com/store/apps/details?id=emzah.inc.wahdat.popularmovies";
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType(MIMETYPE)
                    .setChooserTitle(title)
                    .setText(link)
                    .startChooser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(emzah.inc.wahdat.popularmovies.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadfavouritemovies() {
        RecyclerView.LayoutManager  layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

         favouritesAdapter=new FavouritesAdapter(this);
         recyclerView.setAdapter(favouritesAdapter);
        SetupViewModel();

    }

    private void SetupViewModel() {
        // Log.d("No onchange test","actively receiving data");
        //we are commenting below line because i am calling getallmoview method in viewmodel class
        // final LiveData<List<FavouritesModal>> listofmovies=mdb.moviesDao().getAllMovies();
        FavViewmodel viewmodel= ViewModelProviders.of(this).get(FavViewmodel.class);
        //now we can retreive our live data objectusing getmovies method from viewmodel
        viewmodel.getFavourites().observe(this, new Observer<List<FavouritesModal>>() {
            @Override
            public void onChanged(@Nullable List<FavouritesModal> favouritesModals) {
                Log.d("Livedata test","Update list of tasks from livedata i viewmodel");
                if (favouritesModals !=null && favouritesModals.size()>0){


                  recyclerView.setVisibility(View.VISIBLE);
                    favouritesAdapter.setFavouriteMovies(favouritesModals);
                    textView.setVisibility(View.GONE);
                }
                else {


                    favouritesAdapter.setFavouriteMovies(null);
                  recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Nothing in the Favourites", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadTopRatedMovies() {
        // Creating a retrofit object
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Creating the Api interface
        MoviesApi.Api api= retrofit.create(MoviesApi.Api.class);

        //now making the call object
        Call<Model> call= api.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {
                moviesModal= response.body();
                movieResults=new ArrayList<>();
                movieResults=moviesModal.getResults();
                adapter=new Adapter(movieResults,getApplicationContext());
                adapter.setonItemClickListener(MainActivity.this);

                recyclerView.setAdapter(adapter);
                //animation

                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
                Log.d("MEssage",""+response);


            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                Log.d("MEssage",""+t);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(emzah.inc.wahdat.popularmovies.R.menu.main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if (id== emzah.inc.wahdat.popularmovies.R.id.refresh){
            boolean isConnected=  Network.getConnectivityStatus(MainActivity.this);
            if (isConnected){
                loadPopularMovies();
            }
            else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout,"Please Check Internet Connection",Snackbar.LENGTH_LONG).show();
            }
        }
        return true;
    }



    @Override
    public void onListItemClick(int position) {

        Intent intent=new Intent(MainActivity.this,MoviesDetails.class);
        intent.putExtra("imageUrl",movieResults.get(position).getBackdropPath());
        intent.putExtra("imagename",movieResults.get(position).getTitle());
        intent.putExtra("descr",movieResults.get(position).getOverview());
        intent.putExtra("ratings",movieResults.get(position).getVoteAverage().toString());
        intent.putExtra("releasedate",movieResults.get(position).getReleaseDate());
        intent.putExtra("movieid",movieResults.get(position).getId().toString());
        intent.putExtra("posterimage", movieResults.get(position).getPosterPath());



        startActivity(intent);

    }
}