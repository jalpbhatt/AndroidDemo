package com.bridge.androidtechnicaltest.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bridge.androidtechnicaltest.R;
import com.bridge.androidtechnicaltest.api.PupilService;
import com.bridge.androidtechnicaltest.api.PupilsApi;
import com.bridge.androidtechnicaltest.db.DatabaseHelper;
import com.bridge.androidtechnicaltest.db.PupilsDatabaseLoader;
import com.bridge.androidtechnicaltest.model.PupilDetails;
import com.bridge.androidtechnicaltest.model.Pupils;
import com.bridge.androidtechnicaltest.utils.NetworkUtils;
import com.bridge.androidtechnicaltest.utils.UiUtils;

import java.io.Console;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PupilListFragment extends Fragment implements PupilListAdapterCallback,
        LoaderManager.LoaderCallbacks<List<PupilDetails>> {

	/* LOG_TAG */
	private static final String TAG = PupilListFragment.class.getName();
	private static final String BUNDLE_KEY_PAGE_COUNT = "BUNDLE_KEY_PAGE_COUNT";
    public static final String BUNDLE_KEY_PUPIL_DETAILS = "BUNDLE_KEY_PUPIL_DETAILS";
	private static final boolean DEBUG = true;

	/* UI Elements */
	private PupilListAdapter mPupilAdapter;
	private RecyclerView recyclerView;
	private LinearLayoutManager linearLayoutManager;
	private ProgressBar progressBar;
	private LinearLayout errorLayout;
	private Button btnRetry;
	private TextView txtError;

	private static final int PAGE_START = 1;

	private boolean isLoading = false;
	private boolean isLastPage = false;

	/* Note: Taking assumption that page has minimum 5 page for initialization purpose */
    private static final int DEFAULT_TOTAL_PAGE = 5;
	private int TOTAL_PAGES = DEFAULT_TOTAL_PAGE;

	private int currentPage = PAGE_START;

	/* Web-Api Service */
	private PupilService mPupilService;

    /* The Loader's id (this id is specific to Fragment's LoaderManager) */
    private static final int LOADER_ID = 1;

    private DatabaseHelper mDBHelper;
    private Thread mDBThread;
    private boolean isThreadStopped = false;
    private Handler mUiHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mDBHelper = new DatabaseHelper(getActivity().getApplicationContext());
        mUiHandler = new Handler();

        if (savedInstanceState != null) {
            TOTAL_PAGES = (int) savedInstanceState.get(BUNDLE_KEY_PAGE_COUNT);
        }
    }

    @Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_pupillist, container, false);
		return view;
	}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.pupil_list);

        progressBar = (ProgressBar) view.findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);
        btnRetry = (Button) view.findViewById(R.id.error_btn_retry);
        txtError = (TextView)view. findViewById(R.id.error_txt_cause);

        mPupilAdapter = new PupilListAdapter(getActivity(), this);

        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mPupilAdapter);

        recyclerView.addOnScrollListener(new PupilListScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        mPupilService = PupilsApi.getClient().create(PupilService.class);

        /*
         * Note: If we have network availability then we will fetch data from
         *       Bridge API otherwise we will use the data store in database.
         *
         * Assumption: User must be online to fetch the data once.
         */
        if (NetworkUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
            loadFirstPage();
        } else {

            if (mDBHelper != null & !mDBHelper.hasData()) {
                showErrorView(getString(R.string.error_msg_no_internet));
            }

            if (DEBUG) {
                Log.i(TAG, "+++ Calling initLoader()! +++");
                if (getLoaderManager().getLoader(LOADER_ID) == null) {
                    Log.i(TAG, "+++ Initializing the new Loader... +++");
                } else {
                    Log.i(TAG, "+++ Reconnecting with existing Loader (id '1')... +++");
                }
            }

            // Initialize a Loader with id '1'. If the Loader with this id already
            // exists, then the LoaderManager will reuse the existing Loader.
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();

        fetchPupilList().enqueue(new Callback<Pupils>() {
            @Override
            public void onResponse(Call<Pupils> call, Response<Pupils> response) {

                if (response.isSuccessful() && response.body() != null) {

                    hideErrorView();

                    List<PupilDetails> results = fetchResults(response);
                    progressBar.setVisibility(View.GONE);
                    mPupilAdapter.addAll(results);

                    // Insert Data to Database
                    savePupilsData(results);

                    if (currentPage <= TOTAL_PAGES) {
                        mPupilAdapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }
                } else {
                    showErrorView(getResources().getString(R.string.error_msg_unknown));
                }

            }

            @Override
            public void onFailure(Call<Pupils> call, Throwable t) {
                Log.e(TAG, "Pupils API Call failed = " + t.getLocalizedMessage());
                showErrorView(t);
            }
        });
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        fetchPupilList().enqueue(new Callback<Pupils>() {
            @Override
            public void onResponse(Call<Pupils> call, Response<Pupils> response) {

                if (response.isSuccessful() && response.body() != null) {
                    mPupilAdapter.removeLoadingFooter();
                    isLoading = false;

                    List<PupilDetails> results = fetchResults(response);
                    mPupilAdapter.addAll(results);

                    // Insert Data to Database
                    savePupilsData(results);

                    if (currentPage != TOTAL_PAGES) {
                        mPupilAdapter.addLoadingFooter();
                    } else {
                        isLastPage = true;
                    }
                } /*else {
                    String errorMsg = "";
                    int errorCode = -1;
                    if (response.errorBody() != null) {
                        errorCode = response.errorBody().
                    }
                }*/
            }

            @Override
            public void onFailure(Call<Pupils> call, Throwable t) {

                if (DEBUG) {
                    Log.e(TAG, t.getMessage());
                }
                mPupilAdapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private void savePupilsData(List<PupilDetails> data) {

        if (!isThreadStopped) {
            mDBThread = new DBThread(DBThread.ACTION_INSERT, null, data);
            mDBThread.start();
        }

    }

    /**
     * @param response extracts List<{@link com.bridge.androidtechnicaltest.model.PupilDetails>} from response
     * @return
     */
    private List<PupilDetails> fetchResults(Response<Pupils> response) {
        Pupils pupilsInfo = response.body();
        TOTAL_PAGES = (pupilsInfo.getTotalPages() != null) ? pupilsInfo.getTotalPages() : DEFAULT_TOTAL_PAGE;
        return pupilsInfo.getPupilDetails();
    }

    /**
     * Performs a Retrofit call to pupil's API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PupilListScrollListener} to load next page.
     */
    private Call<Pupils> fetchPupilList() {
        return mPupilService.getPupilsInfo(currentPage);
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!NetworkUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    private void showErrorView(String errorMsg) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            txtError.setText(errorMsg);
        }
    }

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

	@Override
	public void retryPageLoad() {
        loadNextPage();
	}

    @Override
    public void onListItemClick(int position) {
        if (DEBUG) {
            Log.d(TAG, "Position = " + position);
        }
        if (mPupilAdapter != null) {
            PupilDetails pupilInfo = mPupilAdapter.getItem(position);
            if (pupilInfo != null) {
                Intent pupilDetailsActivity = new Intent(getActivity(), PupilDetailActivity.class);
                pupilDetailsActivity.putExtra(BUNDLE_KEY_PUPIL_DETAILS, pupilInfo);
                startActivity(pupilDetailsActivity);
            }
        }
    }

    @Override
    public void onDeletePupilFromList(int pos) {
        if (DEBUG) {
            Log.d(TAG, "Position = " + pos);
        }

        /* By default init */
        int pupilId = -1;
        PupilDetails pupilInfo =  null;
        if (mPupilAdapter != null) {
            pupilInfo = mPupilAdapter.getItem(pos);
            if (pupilInfo != null) {
                pupilId = pupilInfo.getPupilId();
            }
        }

        /* Assumption: If user is online then we will delete pupil via web api otherwise
         *              will do it from offline storage
         */
        if (NetworkUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
            deletePupilFromApi(pupilId, pupilInfo);
        } else {
            deletePupilFromDatabase(pupilInfo);
        }


    }

    /* Delete pupil through API request when user is online */
    private void deletePupilFromApi(final int pupilId, final PupilDetails pupilInfo) {

        mPupilService.deletePupil(pupilId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (DEBUG) {
                    Log.e(TAG, "Request status = " + response.isSuccessful() + "with status code = "
                            + response.code() + "with message = " + response.message());
                }

                if (response.isSuccessful()) {

                    /* TODO: Can move to common static constants of HTTP but have different http code for
                     *       different request like PUT/GET/DEL etc so let it be for now
                     */
                    if (response.code() == 204) {

                        if (DEBUG) {
                            Log.d(TAG, "Request Successful..");
                        }

                        UiUtils.showToast(getActivity().getApplicationContext(), R.string.msg_pupil_del_success);

                        // Clear current data & update the list view
                        if (mPupilAdapter !=null && mPupilAdapter.getItemCount() > 0) {
                            mPupilAdapter.remove(pupilInfo);
                            mPupilAdapter.notifyDataSetChanged();
                        }

                        if (DEBUG) {
                            Log.d(TAG, "Refreshing the list..");
                        }
                    }
                } else {
                    String errorMsg = "Unable to process request due to " +
                            response.message() + " with code " + response.code();
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (DEBUG) {
                    Log.e(TAG, t.getMessage());
                }
                //TODO: Should we do anything here
            }
        });
    }

    /* Delete pupil from database when user is offline */
    private void deletePupilFromDatabase(PupilDetails details) {

        if (!isThreadStopped) {
            mDBThread = new DBThread(DBThread.ACTION_DELETE, details, null);
            mDBThread.start();
        }
    }

    private void resetUIList() {
        if (mPupilAdapter !=null && mPupilAdapter.getItemCount() > 0) {
            List<PupilDetails> list =  mPupilAdapter.getPupilDetails();
            if (list != null) {
                list.clear();
                if (DEBUG) {
                    Log.d(TAG, "Prev data is clear..");
                }
            }
            mPupilAdapter.notifyDataSetChanged();
        }

        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_PAGE_COUNT, TOTAL_PAGES);
    }

    @Override
    public void onStop() {
        super.onStop();

        /* Release Resources */
        if (mDBHelper != null) {
            mDBHelper.close();
        }

        // stop DB thread
        isThreadStopped = true;
    }

    /* Loader implementation for asynchronous data fetching from DB*/

    @Override
    public Loader<List<PupilDetails>> onCreateLoader(int id, Bundle args) {
        if (DEBUG) Log.i(TAG, "+++ onCreateLoader() called! +++");
        progressBar.setVisibility(View.GONE);
        return new PupilsDatabaseLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<PupilDetails>> loader, List<PupilDetails> data) {
        if (DEBUG) Log.i(TAG, "+++ onLoadFinished() called! +++");
        mPupilAdapter.setPupilDetails(data);
        mPupilAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<PupilDetails>> loader) {
        if (DEBUG) Log.i(TAG, "+++ onLoadReset() called! +++");
        mPupilAdapter.setPupilDetails(null);
    }

    /* Use this class to perform database operation like insert/delete in background thread*/
    private class DBThread extends Thread {

        private static final int ACTION_INSERT = 0;
        private static final int ACTION_DELETE = 1;

        private int mAction = 0;
        private PupilDetails mDetails;
        private List<PupilDetails> mData;

        public DBThread(int action, PupilDetails details, List<PupilDetails> data) {
            mAction = action;
            mDetails = details;
            mData = data;
        }

        @Override
        public void run() {

            if (!isThreadStopped) {
                switch (mAction) {
                    case DBThread.ACTION_INSERT:

                        for (PupilDetails info : mData) {
                            mDBHelper.addPupil(info);
                        }
                        break;

                    case DBThread.ACTION_DELETE:
                        boolean isDeleted = mDBHelper.deletePupil(mDetails);

                        /* If deletion is successful then update the UI list */
                        if (isDeleted) {
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Clear current data & update the list view
                                    resetUIList();
                                }
                            });
                        }
                        break;
                }
            }
        }
    }
}
