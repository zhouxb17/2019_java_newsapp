package com.example.ZhouXuanBai.ui.night;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NightViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NightViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is night fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}