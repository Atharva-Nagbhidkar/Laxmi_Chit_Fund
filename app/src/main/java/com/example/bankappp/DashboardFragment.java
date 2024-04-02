package com.example.bankappp;


import android.os.Bundle;


import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;


public class DashboardFragment extends Fragment {



    private ImageView imgaccount,imagdeposit,imgpayment,imgfundtansf;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =   inflater.inflate(R.layout.fragment_dashboard, container, false);


        imgaccount =rootView.findViewById(R.id.img_account_dash);
        imagdeposit=rootView.findViewById(R.id.img_deposit);
        imgpayment=rootView.findViewById(R.id.img_payment);
        imgfundtansf=rootView.findViewById(R.id.img_fundtransfer);

        /////GO to Accounts
        imgaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, new AccountOverviewFragment())
                        .commit();
                getActivity().setTitle("Accounts");

            }
        });

        /////////Go to Deposit
        imagdeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, new DepositeFragment())
                        .commit();
                getActivity().setTitle("Deposit");
            }
        });

        //////////Go to Payment
        imgpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, new PaymentFragment())
                        .commit();
                getActivity().setTitle("Payment");
            }
        });

        ///////////Go to fund transfer
        imgfundtansf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, new TransferFragment())
                        .commit();
                getActivity().setTitle("Transfer");
            }
        });
        return rootView;

    }




    /**
     * method used to setup the values for the views and fields
     */


}
