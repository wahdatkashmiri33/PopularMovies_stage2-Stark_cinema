package emzah.inc.wahdat.popularmovies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import emzah.inc.wahdat.popularmovies.Database.FavouritesModal;
import emzah.inc.wahdat.popularmovies.R;
import emzah.inc.wahdat.popularmovies.UI.FavouritesDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import emzah.inc.wahdat.popularmovies.Database.FavouritesModal;
import emzah.inc.wahdat.popularmovies.UI.FavouritesDetail;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteHolder> {
    private List<FavouritesModal> favouritesmovies;
    private Context context;

    public FavouritesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FavouriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(emzah.inc.wahdat.popularmovies.R.layout.favourite_list,parent,false);
        return new FavouriteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteHolder holder, int position) {
     final FavouritesModal favouritesModal= favouritesmovies.get(position);
        String BASE_URL="http://image.tmdb.org/t/p/";
        String size= "w780/";
        Picasso.get().load(BASE_URL+size+favouritesModal.getPosterPath()).into(holder.posterimage);
        holder.posterimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FavouritesDetail.class);
                intent.putExtra("title",favouritesModal.getOriginalTitle());
                intent.putExtra("backdrop",favouritesModal.getBackdropPath());
                intent.putExtra("overview",favouritesModal.getOverview());
                intent.putExtra("releasedate",favouritesModal.getReleaseDate());
                intent.putExtra("vote",favouritesModal.getVoteAverage());
                intent.putExtra("liked",favouritesModal.isFavourite());
                intent.putExtra("movieid",favouritesModal.getMovieid());


                context.startActivity(intent);
            }
        });
    }


    public void setFavouriteMovies(List<FavouritesModal> favouriteMovies){
    this.favouritesmovies=favouriteMovies;
    notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if (favouritesmovies==null){
            return 0;
        }
        return favouritesmovies.size();
    }

    public class FavouriteHolder extends RecyclerView.ViewHolder{


        @BindView(emzah.inc.wahdat.popularmovies.R.id.poster_image)
        ImageView posterimage;
        public FavouriteHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
