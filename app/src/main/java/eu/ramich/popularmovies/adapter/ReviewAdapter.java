package eu.ramich.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.ramich.popularmovies.R;
import eu.ramich.popularmovies.ui.MovieDetailsFragment;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private Cursor mCursor;
    private final ReviewAdapterOnClickHandler mClickHandler;


    public ReviewAdapter(ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item_review;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.reviewAuthor.setText(mCursor.getString(MovieDetailsFragment.INDEX_REVIEW_AUTHOR));
        holder.reviewContent.setText(mCursor.getString(MovieDetailsFragment.INDEX_REVIEW_CONTENT));
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

    public interface ReviewAdapterOnClickHandler {
        void onClick(ReviewAdapterViewHolder reviewAdapterViewHolder);
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.tv_review_author)
        TextView reviewAuthor;
        @BindView(R.id.tv_review_content)
        public TextView reviewContent;
        @BindView(R.id.iv_review_arrow)
        public ImageView reviewArrow;

        public boolean isExpanded;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            isExpanded = false;
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(this);
        }
    }
}
