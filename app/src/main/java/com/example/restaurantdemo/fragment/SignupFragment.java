package com.example.restaurantdemo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.server.SignupAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class SignupFragment extends Fragment implements View.OnClickListener {
    EditText edName, edEmail, edContactNo, edPwd, edRePwd;
    Button btnJoin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitView();
    }

    private void InitView() {
        edName = (EditText) getView().findViewById(R.id.signup_edname);
        edEmail = (EditText) getView().findViewById(R.id.signup_edemail);
        edContactNo = (EditText) getView().findViewById(R.id.signup_edcontact_no);
        edPwd = (EditText) getView().findViewById(R.id.signup_edpwd);
        edRePwd = (EditText) getView().findViewById(R.id.signup_edCPwd);
        btnJoin = (Button) getView().findViewById(R.id.join_btnJoin);

        btnJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.join_btnJoin) {
            if (edName.getText() == null || edName.getText().toString().isEmpty()) {
                Toast.makeText(this.getActivity(), "Please enter your name.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if (edEmail.getText() == null || edEmail.getText().toString().isEmpty()) {
                Toast.makeText(this.getActivity(), "Please enter your email.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            } else {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(edEmail.getText()).matches())
                {
                    Toast.makeText(this.getActivity(), "Please check your email format.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (edContactNo.getText() == null || edContactNo.getText().toString().isEmpty()) {
                Toast.makeText(this.getActivity(), "Please enter your contact no.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if ((edPwd.getText() == null || edPwd.getText().toString().isEmpty()) ||
                    (edRePwd.getText() == null || edRePwd.getText().toString().isEmpty())) {
                Toast.makeText(this.getActivity(), "Please enter your password.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if (!edPwd.getText().toString().equals(edRePwd.getText().toString())) {
                Toast.makeText(this.getActivity(), "Please check your password.", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Pattern.matches("^[a-zA-Z0-9]*$", edPwd.getText()))
            {
                Toast.makeText(this.getActivity(), "Please check you password format.", Toast.LENGTH_SHORT).show();
                return;
            }

            MySignupTask loginTask = new MySignupTask(this.getActivity());
            loginTask.execute(edEmail.getText().toString(), edPwd.getText().toString(),
                    edName.getText().toString(), edContactNo.getText().toString());
        }
    }

    class MySignupTask extends SignupAsyncTask {
        Context context;

        public MySignupTask(Context context) {
            super(context);
        }

        ProgressDialog loading = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("회원 가입 처리중입니다...");
            loading.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(getContext(), "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    loading.dismiss();;

                    if (json.getString("result").equals("EXISTS_EMAIL")) {
                        Toast.makeText(getContext(), "이미 등록된 이메일입니다.", android.widget.Toast.LENGTH_LONG).show();

                        edEmail.requestFocus();
                    } else if (json.getString("result").equals("OK")) {
                        Toast.makeText(getContext(), "회원 가입이 완료 되었습니다.", android.widget.Toast.LENGTH_LONG).show();

                        edEmail.setText("");
                        edName.setText("");
                        edContactNo.setText("");
                        edPwd.setText("");
                        edRePwd.setText("");

                        edName.requestFocus();
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
