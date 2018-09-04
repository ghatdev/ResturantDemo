package com.example.restaurantdemo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.server.ContactAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;


public class ContactUSFragment extends BaseFragment implements View.OnClickListener {
    EditText edName, edEmail, edContactNo, edMsg;
    Button btnContact;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitView();
    }

    private void InitView() {
        edName = (EditText) getView().findViewById(R.id.contact_us_edName);
        edEmail = (EditText) getView().findViewById(R.id.contact_us_edEmail);
        edContactNo = (EditText) getView().findViewById(R.id.contact_us_edPhoneNumber);
        edMsg = (EditText) getView().findViewById(R.id.contact_us_edMessage);
        btnContact = (Button) getView().findViewById(R.id.btn_placeOrder);

        btnContact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (edEmail.getText() == null || edEmail.getText().toString().isEmpty()) {
            Toast.makeText(this.getActivity(), "Please enter your name.", android.widget.Toast.LENGTH_LONG).show();
            return;
        }
        if (edName.getText() == null || edName.getText().toString().isEmpty()) {
            Toast.makeText(this.getActivity(), "Please enter your email.", android.widget.Toast.LENGTH_LONG).show();
            return;
        }
        if (edContactNo.getText() == null || edContactNo.getText().toString().isEmpty()) {
            Toast.makeText(this.getActivity(), "Please enter your contact no.", android.widget.Toast.LENGTH_LONG).show();
            return;
        }
        if ((edMsg.getText() == null || edMsg.getText().toString().isEmpty()) ||
                (edMsg.getText() == null || edMsg.getText().toString().isEmpty())) {
            Toast.makeText(this.getActivity(), "Please enter a message", android.widget.Toast.LENGTH_LONG).show();
            return;
        }

        MyContactTask contactTask = new MyContactTask(this.getActivity());
        contactTask.execute(edName.getText().toString(), edEmail.getText().toString(),
                edContactNo.getText().toString(), edMsg.getText().toString());
    }

    class MyContactTask extends ContactAsyncTask {
        Context context;

        public MyContactTask(Context context) {
            super(context);
            this.context = context;
        }

        ProgressDialog loading = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("처리중입니다...");
            loading.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(context, "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    if (json.getString("result").equals("OK")) {
                        loading.dismiss();;
                        Toast.makeText(context, "글이 등록 되었습니다. 감사합니다.", android.widget.Toast.LENGTH_LONG).show();

                        edEmail.setText("");
                        edName.setText("");
                        edContactNo.setText("");
                        edMsg.setText("");
                        edName.requestFocus();
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
