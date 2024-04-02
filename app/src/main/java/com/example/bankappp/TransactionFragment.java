package com.example.bankappp;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import android.widget.TextView;

import com.example.bankappp.Adapters.TransactionAdapter;

import com.example.bankappp.Model.Profile;
import com.example.bankappp.Model.Transaction;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class TransactionFragment extends Fragment {
    private ListView lstTransactions;

    private Profile userProfile;

    private int selectedAccountIndex;









    private TextView txtAccountName;
    private TextView txtAccountBalance;

    private TextView txtTransactionMsg;
    private TextView txtTransfersMsg;
    private TextView txtPaymentsMsg;
    private TextView txtDepositMsg;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();

        getActivity().setTitle("Transactions");
        selectedAccountIndex = bundle.getInt("SelectedAccount", 0);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);
        txtAccountName = rootView.findViewById(R.id.txt_account_name);
        txtAccountBalance = rootView.findViewById(R.id.txt_account_balance);

        txtTransactionMsg = rootView.findViewById(R.id.txt_no_transactions);
        txtPaymentsMsg = rootView.findViewById(R.id.txt_no_payments);
        txtTransfersMsg = rootView.findViewById(R.id.txt_no_transfers);
        txtDepositMsg = rootView.findViewById(R.id.txt_no_deposits);



        lstTransactions = rootView.findViewById(R.id.lst_transactions);

        ((DrawerActivity) getActivity()).showUpButton();

        setValues();
        return rootView;
    }

    /**
     * method used to setup the values for the views and fields
     */
    private void setValues() {

        SharedPreferences userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);



        setupTransactionAdapter(selectedAccountIndex);




        txtAccountName.setText("Account: " + userProfile.getAccounts().get(selectedAccountIndex).toTransactionString());
        txtAccountBalance.setText("Balance: RS." + String.format(Locale.getDefault(), "%.2f",userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance()));
    }



    /**
     * method used to setup the adapters
     */
    private void setupTransactionAdapter(int selectedAccountIndex) {
        ArrayList<Transaction> transactions = userProfile.getAccounts().get(selectedAccountIndex).getTransactions();

        txtDepositMsg.setVisibility(GONE);
        txtTransfersMsg.setVisibility(GONE);
        txtPaymentsMsg.setVisibility(GONE);

        if (transactions.size() > 0) {

            txtTransactionMsg.setVisibility(GONE);
            lstTransactions.setVisibility(VISIBLE);




                TransactionAdapter transactionAdapter = new TransactionAdapter(getActivity(), R.layout.lst_transactions, transactions);
                lstTransactions.setAdapter(transactionAdapter);



        } else {
            txtTransactionMsg.setVisibility(VISIBLE);
            lstTransactions.setVisibility(GONE);
        }

    }



}
