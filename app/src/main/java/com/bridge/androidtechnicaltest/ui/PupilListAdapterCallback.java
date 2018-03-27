package com.bridge.androidtechnicaltest.ui;

/* Contract to perform an action on various list view events */
public interface PupilListAdapterCallback {

    void retryPageLoad();
    void onListItemClick(int position);
    void onDeletePupilFromList(int pos);
}
