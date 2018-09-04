package com.example.restaurantdemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.common.ApplicationShare;

public class ProfileFragment extends BaseFragment {

    private View view;

    TextView tv_profile_name, tv_profile_tel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitView();
    }

    private void InitView() {
        tv_profile_name = (TextView) getView().findViewById(R.id.tv_profile_name);
        tv_profile_tel = (TextView) getView().findViewById(R.id.tv_profile_tel);

        ApplicationShare app = (ApplicationShare)getActivity().getApplication();
        tv_profile_name.setText(app.getLoginName());
        tv_profile_tel.setText(String.format("연락처: %s, 새내기 회원", app.getLoginTel()));
    }
}
