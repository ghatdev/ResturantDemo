package com.example.restaurantdemo.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.restaurantdemo.R;

public class BookingFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private Button btnBookTable, btnMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booking, container, false);
        return view;
    }

    //https://www.google.co.in/maps/place/Evince+Development+Pvt.+Ltd./@23.0751887,72.5256887,15z/data=!4m2!3m1!1s0x0:0xdc1486269f4bd806?sa=X&ved=0ahUKEwjDs6zYiY3UAhUBvo8KHdLYAn0Q_BIIhgEwCw

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        InitView();
    }

    private void InitView() {

        btnBookTable = (Button) getView().findViewById(R.id.booking_btnCall);
        btnMap = (Button) getView().findViewById(R.id.booking_btnLocation);
        btnBookTable.setOnClickListener(this);
        btnMap.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == btnBookTable) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + getResources().getString(R.string.contact_no)));
            startActivity(callIntent);
        } else if (v == btnMap) {
            String uri = "https://www.google.com/maps/place/1210.+롯데월드타워(잠실역2번출구+쪽)/@37.5102233,127.0979862,15z/data=!4m18!1m12!4m11!1m6!1m2!1s0x357ca5a6567d2211:0x3f2975ea3640d4f4!2z66Gv642w7JuU65Oc7Ie87ZWR66qwIFNlb3VsLCBTb25ncGEtZ3UsIEphbXNpbCAzKHNhbSktZG9uZywgT2x5bXBpYy1ybywg77yS77yU77yQ!2m2!1d127.0962096!2d37.5114881!1m3!2m2!1d127.1039425!2d37.5108925!3m4!1s0x357ca56f596fa0f1:0xc122a6356e605b58!8m2!3d37.5132571!4d127.1009102";
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }
    }
}
