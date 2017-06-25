package eu.ramich.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.ramich.popularmovies.R;
import eu.ramich.popularmovies.ui.MovieDetailsFragment;
import eu.ramich.popularmovies.utilities.NetworkUtils;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private Cursor mCursor;
    private final TrailerAdapterOnClickHandler mClickHandler;


    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Picasso.with(holder.trailerThumbnail.getContext())
                .load(NetworkUtils.buildVideoThumbnailUrl(
                        mCursor.getString(MovieDetailsFragment.INDEX_TRAILER_KEY)).toString()
                )
                .placeholder(R.drawable.ic_movie)
                .into(holder.trailerThumbnail);

        holder.trailerTitle.setText(mCursor.getString(MovieDetailsFragment.INDEX_TRAILER_NAME));
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

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailerKey);
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.iv_trailer_thumbnail)
        ImageView trailerThumbnail;
        @BindView(R.id.tv_trailer_title)
        TextView trailerTitle;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String trailerKey = mCursor.getString(MovieDetailsFragment.INDEX_TRAILER_KEY);
            mClickHandler.onClick(trailerKey);
        }
    }
}
