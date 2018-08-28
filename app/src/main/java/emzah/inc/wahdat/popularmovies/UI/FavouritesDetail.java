package emzah.inc.wahdat.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import emzah.inc.wahdat.popularmovies.Database.AppDatabase;
import emzah.inc.wahdat.popularmovies.R;
import emzah.inc.wahdat.popularmovies.utils.AppExecutors;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouritesDetail extends AppCompatActivity {
    @BindView(emzah.inc.wahdat.popularmovies.R.id.rootLayout)  CoordinatorLayout coordinatorLayout;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.collapsing)  CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.img_poster)  ImageView backdrop;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.movie_title) TextView title;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.movie_overview) TextView overview;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.release_date) TextView releasedate;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.movie_ratings) TextView ratings;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.poster) ImageView circlebackdrop;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.toolbar)  Toolbar toolbar;
    @BindView(emzah.inc.wahdat.popularmovies.R.id.fav_btn) FloatingActionButton favbtn;


    private AppDatabase mdb;
    boolean isFavourite;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(emzah.inc.wahdat.popularmovies.R.layout.activity_favourites_detail);



        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(emzah.inc.wahdat.popularmovies.R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(emzah.inc.wahdat.popularmovies.R.style.CollapsedAppbar);
//this line shows back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mdb=AppDatabase.getInstance(this);
        Intent intent=getIntent();
        String movietitle=intent.getStringExtra("title");
       String backdropImage =intent.getStringExtra("backdrop");
       String ovewviewMovie=intent.getStringExtra("overview");
       String releasemvieDate=intent.getStringExtra("releasedate");
       String voterating=intent.getStringExtra("vote");
       boolean fav= intent.getBooleanExtra("liked",true);


       title.setText(movietitle);
        String BASE_URL="http://image.tmdb.org/t/p/";
        String size= "w780/";
        Picasso.get().load(BASE_URL+size+backdropImage).into(backdrop);
        overview.setText(ovewviewMovie);
        releasedate.setText(releasemvieDate);
        ratings.setText(voterating);
        Picasso.get().load(BASE_URL+size+backdropImage).into(circlebackdrop);


        favbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isFavourite){
                   isFavourite=false;
                   favbtn.setImageDrawable(ContextCompat.getDrawable(FavouritesDetail.this, emzah.inc.wahdat.popularmovies.R.drawable.ic_favorite_red_24dp));

               }
               else {
                  isFavourite =true;
                  deletefromdatabase();
                  favbtn.setImageDrawable(ContextCompat.getDrawable(FavouritesDetail.this, emzah.inc.wahdat.popularmovies.R.drawable.ic_favorite_border_black_24dp));
                   finish();
               }
            }
        });



    }



    private void deletefromdatabase() {
        Intent intent=getIntent();

        final   String movieId=intent.getStringExtra("movieid");
        AppExecutors.getInstance().diskIO().execute(new Runnable() {

            @Override
            public void run() {
                mdb.moviesDao().deleteFavMovie(movieId);


            }
        });
    }
}
