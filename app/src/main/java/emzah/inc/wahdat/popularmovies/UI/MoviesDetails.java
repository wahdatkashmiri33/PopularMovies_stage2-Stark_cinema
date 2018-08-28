package emzah.inc.wahdat.popularmovies.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import emzah.inc.wahdat.popularmovies.Adapter.ReviewAdapter;
import emzah.inc.wahdat.popularmovies.Adapter.TrailerAdapter;
import emzah.inc.wahdat.popularmovies.BuildConfig;
import emzah.inc.wahdat.popularmovies.Database.AppDatabase;
import emzah.inc.wahdat.popularmovies.Database.FavouritesModal;
import emzah.inc.wahdat.popularmovies.Model.ResultReview;
import emzah.inc.wahdat.popularmovies.Model.ResultTrailer;
import emzah.inc.wahdat.popularmovies.Model.Review;
import emzah.inc.wahdat.popularmovies.Model.Trailer;
import emzah.inc.wahdat.popularmovies.MoviesApi.MoviesApi;
import emzah.inc.wahdat.popularmovies.R;
import emzah.inc.wahdat.popularmovies.utils.AppExecutors;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import emzah.inc.wahdat.popularmovies.BuildConfig;
import emzah.inc.wahdat.popularmovies.Model.ResultReview;
import emzah.inc.wahdat.popularmovies.Model.ResultTrailer;
import emzah.inc.wahdat.popularmovies.Model.Review;
import emzah.inc.wahdat.popularmovies.MoviesApi.MoviesApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesDetails extends AppCompatActivity {
    @BindView(emzah.inc.wahdat.popularmovies.R.id.rootLayout) CoordinatorLayout coordinatorLayout;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.collapsing) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.img_poster) ImageView backdrop;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.movie_title) TextView title;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.movie_overview) TextView overview;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.release_date) TextView releasedate;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.movie_ratings) TextView ratings;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.poster) ImageView circlebackdrop;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.toolbar) Toolbar toolbar;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.recyclerviewtrailer) RecyclerView recyclerViewtrailer;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.recyclerviewreview) RecyclerView recyclerViewReview;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.fav_btn) FloatingActionButton favbtn;




    RecyclerView.LayoutManager layoutManager;
    String API_KEY= BuildConfig.GoogleSecAPIKEY;

    boolean isFavourite ;
    private AppDatabase mdb;
    FavouritesModal favouritesModal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(emzah.inc.wahdat.popularmovies.R.layout.activity_movies_details);

        ButterKnife.bind(this);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(emzah.inc.wahdat.popularmovies.R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(emzah.inc.wahdat.popularmovies.R.style.CollapsedAppbar);

        setSupportActionBar(toolbar);

//initialize mdb
        mdb= AppDatabase.getInstance(getApplicationContext());
//this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

isfavourite();

        Intent intent=getIntent();
        String BASE_URL="http://image.tmdb.org/t/p/";
        String size= "w780/";
       String image= intent.getStringExtra("imageUrl");


        String imagename= intent.getStringExtra("imagename");
        String description=intent.getStringExtra("descr");
        String release=intent.getStringExtra("releasedate");
        String average=intent.getStringExtra("ratings");





       

        Picasso.get().load(BASE_URL+size+image).placeholder(emzah.inc.wahdat.popularmovies.R.drawable.imageload)
                .into(backdrop);

       overview.setText(description);
       title.setText(imagename);
        DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = null;
        try {
            date1 = inputFormatter1.parse(release);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat outputFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
        String output1 = outputFormatter1.format(date1);
        releasedate.setText(output1);
       ratings.setText(average);
        Picasso.get().load(BASE_URL+size+image).into(circlebackdrop);




        layoutManagerTrailers();
        loadTrailers();
        layoutManagerReview();
        loadReviews();




        //favourite btn
        favbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFavourite){

                    removeMovieFromList();
                   isFavourite=false;

                    favbtn.setImageDrawable(ContextCompat.getDrawable(MoviesDetails.this, emzah.inc.wahdat.popularmovies.R.drawable.ic_favorite_border_black_24dp));

                    Toast.makeText(MoviesDetails.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
                else  {




                    addmoviestofavouritelist();


                   isFavourite=true;
                    favbtn.setImageDrawable(ContextCompat.getDrawable(MoviesDetails.this, emzah.inc.wahdat.popularmovies.R.drawable.ic_favorite_red_24dp));
                    Toast.makeText(MoviesDetails.this, "Added to favurites", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }



    private void removeMovieFromList() {
        Intent intent=getIntent();

        final String movieid=intent.getStringExtra("movieid");



        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mdb.moviesDao().deleteFavMovie(movieid);
            }
        });
    }


    private void addmoviestofavouritelist() {

        Intent intent=getIntent();
        String BASE_URL="http://image.tmdb.org/t/p/";
        String size= "w780/";
        String image= intent.getStringExtra("imageUrl");
        final String movieid=intent.getStringExtra("movieid");
        String imagename= intent.getStringExtra("imagename");
        String description=intent.getStringExtra("descr");
        String release=intent.getStringExtra("releasedate");
        String average=intent.getStringExtra("ratings");
        String poster=intent.getStringExtra("posterimage");

        final  FavouritesModal favouritesModal=new FavouritesModal(movieid,average,imagename,poster,image,description,release,true);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (favouritesModal.isFavourite()){
                    mdb.moviesDao().insertFavMovie(favouritesModal);

                    isFavourite=true;


                    }





                }



        });
    }

    private void isfavourite() {
        final String movieid = getIntent().getStringExtra("movieid");
      final FavouritesModal[] movieResponse = new FavouritesModal[1];

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieResponse[0] = mdb.moviesDao().checkifExists(movieid);
                //If the movie belongs to user favourites then it will be shown as liked
                if (movieResponse[0] != null) {
                    favbtn.setImageDrawable(ContextCompat.getDrawable(MoviesDetails.this, emzah.inc.wahdat.popularmovies.R.drawable.ic_favorite_red_24dp));
                    isFavourite = true;
                } else {
                    favbtn.setImageDrawable(ContextCompat.getDrawable(MoviesDetails.this, emzah.inc.wahdat.popularmovies.R.drawable.ic_favorite_border_black_24dp));
                    isFavourite = false;
                }
            }
        });
    }
    private void loadReviews() {
        Intent intent=getIntent();
      String movieId = intent.getStringExtra("movieid");
      Retrofit retrofit=new Retrofit.Builder()
              .baseUrl(MoviesApi.Api.BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .build();
      MoviesApi.Api api= retrofit.create(MoviesApi.Api.class);
      Call<Review> call=api.getReview(movieId,API_KEY);
      call.enqueue(new Callback<Review>() {
          @Override
          public void onResponse(Call<Review> call, Response<Review> response) {
              if (response.body() !=null){

                  assert response.body() != null;
                  List<ResultReview> resultReviews= response.body().getResults();
                  recyclerViewReview.setHasFixedSize(true);
                  recyclerViewReview.setAdapter(new ReviewAdapter(resultReviews,getApplicationContext()));

              }
              else {
                  Toast.makeText(MoviesDetails.this, "null pointer is here", Toast.LENGTH_SHORT).show();
              }
          }

          @Override
          public void onFailure(Call<Review> call, Throwable t) {

          }
      });
    }

    private void layoutManagerReview(){
        layoutManager=new LinearLayoutManager(this);
        recyclerViewReview.setLayoutManager(layoutManager);
    }
    private void layoutManagerTrailers() {
        layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewtrailer.setLayoutManager(layoutManager);
    }

    private void loadTrailers() {
        Intent intent=getIntent();
        String movieId=intent.getStringExtra("movieid");
        // Creating a retrofit object
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Creating the Api interface
        MoviesApi.Api api=retrofit.create(MoviesApi.Api.class);
        //now making the call object
        Call<Trailer> call=api.getTrailer(movieId,API_KEY);
        call.enqueue(new Callback<Trailer>() {
            @Override
            public void onResponse(Call<Trailer> call, @NonNull Response<Trailer> response) {
                if (response.body() !=null){
                    assert response.body() != null;

                    List<ResultTrailer> trailerList=response.body().getResults();

                    recyclerViewtrailer.setHasFixedSize(true);
                    recyclerViewtrailer.setAdapter(new TrailerAdapter(trailerList,getApplicationContext()));


                }
                else {
                    Toast.makeText(MoviesDetails.this, "null pointer is here", Toast.LENGTH_SHORT).show();
                }
                }

            @Override
            public void onFailure(Call<Trailer> call, Throwable t) {
                Log.d("onfaliure", ""+t);
            }
        });



    }


}
