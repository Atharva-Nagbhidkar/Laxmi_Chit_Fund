package com.example.bankappp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bankappp.Model.Profile;
import com.example.bankappp.Model.db.ApplicationDB;


import java.util.ArrayList;



public class LoginFragment extends Fragment {
    private Bundle bundle;
    private String username;
    private String password;

    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView btnCreateAccount;




    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == btnLogin.getId()) {
                validateAccount();
            } else if (view.getId() == btnCreateAccount.getId()) {
                createAccount();
            }
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = this.getArguments();
        if (bundle != null) {
            username = bundle.getString("Username", "");
            password = bundle.getString("Password", "");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        edtUsername = rootView.findViewById(R.id.edt_username);
        edtPassword = rootView.findViewById(R.id.edt_password);
        btnLogin = rootView.findViewById(R.id.btn_login);
        btnCreateAccount = rootView.findViewById(R.id.btn_create_account);

        getActivity().setTitle("Laxmi Chit Fund");
        ((LoginAcitviy) getActivity()).removeUpButton();

        setupViews();

        if (bundle != null) {
            edtUsername.setText(username);
            edtPassword.setText(password);
        }

        return rootView;
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setupViews() {

        btnLogin.setOnClickListener(clickListener);
        btnCreateAccount.setOnClickListener(clickListener);




    }


    private void validateAccount() {
        ApplicationDB applicationDB = new ApplicationDB(getActivity().getApplicationContext());
        ArrayList<Profile> profiles = applicationDB.getAllProfiles();

        boolean match = false;

        if (profiles.size() > 0) {
            for (int i = 0; i < profiles.size(); i++) {
                if (edtUsername.getText().toString().equals(profiles.get(i).getUsername()) && edtPassword.getText().toString().equals(profiles.get(i).getPassword())) {

                    match = true;


                    ((LoginAcitviy) getActivity()).login();
                }
            }
            if (!match) {
                Toast.makeText(getActivity(), R.string.incorrect_login, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.incorrect_login, Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * method that creates an account
     */
    private void createAccount() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_frm_content, new CreateProfileFragment())
                .addToBackStack(null)
                .commit();
    }

}
