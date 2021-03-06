package com.aone.menurandomchoice.views.base;

import android.content.Context;

import androidx.annotation.NonNull;


public interface BaseContract {

    interface View {

        @NonNull
        Context getAppContext();

        @NonNull
        Context getActivityContext();

        void showToastMessage(@NonNull String message);

        void showProgressDialog();

        void hideProgressDialog();

    }

    interface Presenter<V extends BaseContract.View> {

        void attachView(@NonNull V view);

        void detachView();

        boolean isAttachView();

    }

}
