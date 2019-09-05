package com.java.ZhouXuanBai.ui.shield;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShieldViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShieldViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is shield fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}