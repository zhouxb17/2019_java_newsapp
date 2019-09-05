package com.java.ZhouXuanBai.ui.collection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.java.ZhouXuanBai.R;

public class CollectionFragment extends Fragment {

    private CollectionViewModel collectionViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        collectionViewModel =
                ViewModelProviders.of(this).get(CollectionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_collection, container, false);
        final TextView textView = root.findViewById(R.id.text_collection);
        collectionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}