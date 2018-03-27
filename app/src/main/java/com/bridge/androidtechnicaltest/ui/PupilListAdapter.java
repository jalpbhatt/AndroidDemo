package com.bridge.androidtechnicaltest.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bridge.androidtechnicaltest.R;
import com.bridge.androidtechnicaltest.imagecache.ImageLoader;
import com.bridge.androidtechnicaltest.model.PupilDetails;


import java.util.ArrayList;
import java.util.List;

public class PupilListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<PupilDetails> pupilDetails;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PupilListAdapterCallback mCallback;
    private ImageLoader mImgLoader;

    private String errorMsg;

    public PupilListAdapter(Context context, PupilListAdapterCallback listener) {
        this.context = context;
        this.mCallback = listener;
        pupilDetails = new ArrayList<>();
        mImgLoader = new ImageLoader(context.getApplicationContext());
    }

    public List<PupilDetails> getPupilDetails() {
        return pupilDetails;
    }

    public void setPupilDetails(List<PupilDetails> pupilDetails) {
        this.pupilDetails = pupilDetails;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.pupil_list, parent, false);
        viewHolder = new PupilDetailsVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        PupilDetails objPupilDetails = pupilDetails.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final PupilDetailsVH pupilVH = (PupilDetailsVH) holder;

                pupilVH.mPupilName.setText(objPupilDetails.getName());
                pupilVH.mPupilCountry.setText(objPupilDetails.getCountry());

                pupilVH.mLatitude.setText(context.getString(R.string.latitude, objPupilDetails.getLatitude()));
                pupilVH.mLongitude.setText(context.getString(R.string.longitude, objPupilDetails.getLongitude()));

                String imgURL = objPupilDetails.getImage();

                // Load image from URL to dedicated list view item
                mImgLoader.displayImage(imgURL, pupilVH.mPupilProfileImg);
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }

                break;
        }

    }

    @Override
    public int getItemCount() {
        return pupilDetails == null ? 0 : pupilDetails.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == pupilDetails.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    public void add(PupilDetails r) {
        pupilDetails.add(r);
        notifyItemInserted(pupilDetails.size() - 1);
    }

    public void addAll(List<PupilDetails> movePupilDetailss) {
        for (PupilDetails PupilDetails : movePupilDetailss) {
            add(PupilDetails);
        }
    }

    public void remove(PupilDetails r) {
        int position = pupilDetails.indexOf(r);
        if (position > -1) {
            pupilDetails.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new PupilDetails());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = pupilDetails.size() - 1;
        PupilDetails PupilDetails = getItem(position);

        if (PupilDetails != null) {
            pupilDetails.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PupilDetails getItem(int position) {
        return pupilDetails.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(pupilDetails.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    /**
     * Main list's content ViewHolder
     */
    protected class PupilDetailsVH extends RecyclerView.ViewHolder {
        private TextView mPupilName;
        private TextView mPupilCountry;
        private TextView mLatitude;
        private TextView mLongitude;
        private ImageView mPupilProfileImg;
        private ProgressBar mProgress;
        private FrameLayout mListContainer;
        private ImageButton mBtnDelete;

        public PupilDetailsVH(View itemView) {
            super(itemView);

            mPupilName = (TextView) itemView.findViewById(R.id.pupil_name);
            mPupilCountry = (TextView) itemView.findViewById(R.id.pupil_country);
            mLatitude = (TextView) itemView.findViewById(R.id.latitude);
            mLongitude = (TextView) itemView.findViewById(R.id.longitude);
            mPupilProfileImg = (ImageView) itemView.findViewById(R.id.pupil_image);
            mProgress = (ProgressBar) itemView.findViewById(R.id.list_progress);
            mListContainer = (FrameLayout) itemView.findViewById(R.id.list_container);
            mBtnDelete = (ImageButton) itemView.findViewById(R.id.btnDelete);

            mListContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onListItemClick(getAdapterPosition());
                }
            });

            mBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onDeletePupilFromList(getAdapterPosition());
                }
            });
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }

}
