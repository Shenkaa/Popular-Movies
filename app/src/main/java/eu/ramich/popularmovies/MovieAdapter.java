package eu.ramich.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import eu.ramich.popularmovies.data.model.Movie;
import eu.ramich.popularmovies.utilities.NetworkUtils;
import eu.ramich.popularmovies.utilities.PopularMoviesUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> movieList;
    private final MovieAdapterOnClickHandler mClickHandler;


    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_movie;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Picasso.with(holder.moviePoster.getContext())
                .load(NetworkUtils.buildPosterURL(movie.getPosterPath(),
                        PopularMoviesUtils.getPosterWidth(true)).toString())
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if (movieList != null) {
            return movieList.size();
        }
        return 0;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder
            implements OnClickListener {

        @BindView(R.id.iv_movie_poster) ImageView moviePoster;

        public MovieAdapterViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }
}
