package com.example.restaurantdemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantdemo.R;
import com.example.restaurantdemo.activity.ForgotPasswordActivity;
import com.example.restaurantdemo.activity.MainActivity;
import com.example.restaurantdemo.common.ApplicationShare;
import com.example.restaurantdemo.server.SigninAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInFragment extends Fragment implements View.OnClickListener {

    private TextView tvEdemail, tvEdPwd, tvForgotpwd;
    private Button btnLogin;
    private View view;
    private ProgressBar loading;
    private CheckBox cbAutosave;

    private String server_url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        InitView();
    }

    private void InitView() {
        tvEdemail = (TextView) getView().findViewById(R.id.login_edemail);
        tvEdPwd = (TextView) getView().findViewById(R.id.login_edpwd);
        tvForgotpwd = (TextView) getView().findViewById(R.id.login_tvforgot_pwd);
        btnLogin = (Button) getView().findViewById(R.id.login_btnLogin);
        loading = (ProgressBar) getView().findViewById(R.id.loading);

        cbAutosave = (CheckBox) getView().findViewById(R.id.cb_autosave);

        tvForgotpwd.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

        try {
            ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            server_url = (String)ai.metaData.get("server_url");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SharedPreferences prefs = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE );
        if (prefs != null) {
            String savedEmail = prefs.getString("email", "");
            String savedPwd = prefs.getString("pwd", "");
            if (!savedEmail.isEmpty()) {
                tvEdemail.setText(savedEmail);
                tvEdPwd.setText(savedPwd);
                cbAutosave.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_btnLogin) {
            if ((tvEdemail.getText() == null || tvEdemail.getText().toString().isEmpty()) ||
                    tvEdPwd.getText() == null || tvEdPwd.getText().toString().isEmpty()) {
                Toast.makeText(this.getActivity(), "Please enter your email and password.", android.widget.Toast.LENGTH_LONG).show();
                return;
            }

            MySigninTask loginTask = new MySigninTask(this.getActivity());
            loginTask.execute(server_url, tvEdemail.getText().toString(), tvEdPwd.getText().toString());
            loading.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.login_tvforgot_pwd) {
            Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
            getActivity().startActivity(intent);
        }
    }

    class MySigninTask extends SigninAsyncTask {
        Context context;

        public MySigninTask(Context context) {
            super(context);
        }

        @Override
        protected void onPreExecute() {
            // DB initialize
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject json =  new JSONObject(result);

                if (json == null) {
                    Toast.makeText(getContext(), "Server message=" + result, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    loading.setVisibility(View.GONE);
                    if (json.getString("result").equals("OK")) {
                        ApplicationShare app = (ApplicationShare)getActivity().getApplication();
                        app.getCart(tvEdemail.getText().toString());
                        app.setLoginEmail(tvEdemail.getText().toString());
                        app.setLoginName(json.getString("name"));
                        app.setLoginTel(json.getString("tel"));

                        // Check auto save status of Login info
                        SharedPreferences prefs = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE );
                        SharedPreferences.Editor editor = prefs.edit();
                        if (cbAutosave.isChecked()) {
                            editor.putString("email", tvEdemail.getText().toString());
                            editor.putString("pwd", tvEdPwd.getText().toString());
                        } else {
                            editor.remove("email");
                            editor.remove("pwd");
                        }
                        editor.commit();

                        Toast.makeText(getContext(), "로그인 되었습니다.", android.widget.Toast.LENGTH_LONG).show();
                        getActivity().finish();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else if(json.getString("result").equals("NG") || json.getString("result").equals("ERROR")) {
                        Toast.makeText(getContext(), "Please check your Email and Password.", android.widget.Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Sorry, please try again after a while.", android.widget.Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
