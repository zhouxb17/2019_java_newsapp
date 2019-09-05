package com.java.ZhouXuanBai.ui.shield;

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

public class ShieldFragment extends Fragment {

    private ShieldViewModel shieldViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shieldViewModel =
                ViewModelProviders.of(this).get(ShieldViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shield, container, false);
        final TextView textView = root.findViewById(R.id.text_sheild);
        shieldViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}