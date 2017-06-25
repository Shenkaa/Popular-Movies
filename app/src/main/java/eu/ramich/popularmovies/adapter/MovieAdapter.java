package eu.ramich.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.ramich.popularmovies.R;
import eu.ramich.popularmovies.ui.MoviesFragment;
import eu.ramich.popularmovies.utilities.NetworkUtils;
import eu.ramich.popularmovies.utilities.PopularMoviesUtils;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Cursor mCursor;
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
        mCursor.moveToPosition(position);
        Picasso.with(holder.moviePoster.getContext())
                .load(NetworkUtils.buildPosterUrl(mCursor.getString(MoviesFragment.INDEX_POSTER_PATH),
                        PopularMoviesUtils.getPosterWidth(true)).toString())
                .placeholder(R.drawable.ic_image)
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(int movieId);
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
            mCursor.moveToPosition(adapterPosition);
            int movieId = mCursor.getInt(MoviesFragment.INDEX_MOVIE_ID);
            mClickHandler.onClick(movieId);
        }
    }
}
