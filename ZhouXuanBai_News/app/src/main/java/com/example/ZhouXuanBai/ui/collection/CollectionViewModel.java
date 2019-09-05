package com.example.ZhouXuanBai.ui.collection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CollectionViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CollectionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Collection fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}