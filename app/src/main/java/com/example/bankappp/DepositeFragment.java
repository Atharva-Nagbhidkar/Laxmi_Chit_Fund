package com.example.bankappp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.bankappp.Model.Account;
import com.example.bankappp.Model.Profile;
import com.example.bankappp.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class DepositeFragment extends Fragment {
    private Spinner spnAccounts;
    private ArrayAdapter<Account> accountAdapter;
    private EditText edtDepositAmount;
    private Button btnCancel;
    private Button btnDeposit;

    private Profile userProfile;

    private SharedPreferences userPreferences;
    private Gson gson;
    private String json;

    private static final String CHANNEL_ID = "DepositChannel";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deposite, container, false);

        createNotificationChannel();

        userPreferences = getContext().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        spnAccounts = v.findViewById(R.id.dep_spn_accounts1);
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, userProfile.getAccounts());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAccounts.setAdapter(accountAdapter);
        spnAccounts.setSelection(0);

        edtDepositAmount = v.findViewById(R.id.edt_deposit_amount1);

        btnCancel = v.findViewById(R.id.btn_cancel_deposit1);
        btnDeposit = v.findViewById(R.id.btn_deposit1);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Deposit Cancelled", Toast.LENGTH_SHORT).show();
                navigateToDashboard();
            }
        });

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDeposit();
            }
        });

        // Back to Dashboard
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navigateToDashboard();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return v;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Deposit Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void makeDeposit() {
        int selectedAccountIndex = spnAccounts.getSelectedItemPosition();

        double depositAmount = 0;
        boolean isNum = false;

        try {
            depositAmount = Double.parseDouble(edtDepositAmount.getText().toString());
            isNum = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (depositAmount < 0.01 && !isNum) {
            Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        } else {
            Account account = userProfile.getAccounts().get(selectedAccountIndex);
            account.addDepositTransaction(depositAmount);

            SharedPreferences.Editor prefsEditor = userPreferences.edit();
            gson = new Gson();
            json = gson.toJson(userProfile);
            prefsEditor.putString("LastProfileUsed", json).apply();

            ApplicationDB applicationDb = new ApplicationDB(getContext());
            applicationDb.overwriteAccount(userProfile, account);
            applicationDb.saveNewTransaction(userProfile, account.getAccountNo(),
                    account.getTransactions().get(account.getTransactions().size() - 1));

            sendNotification("Deposit Successful", "Deposit of RS." + String.format(Locale.getDefault(), "%.2f", depositAmount) + " made successfully");

            Toast.makeText(getContext(), "Deposit of RS." + String.format(Locale.getDefault(), "%.2f", depositAmount) + " made successfully", Toast.LENGTH_SHORT).show();

            accountAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userProfile.getAccounts());
            accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAccounts.setAdapter(accountAdapter);
        }
    }

    private void sendNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.laxmichitfund)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(getContext(), DashboardFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat mManager = NotificationManagerCompat.from(getContext());
        mManager.notify(0, builder.build());
    }

    private void navigateToDashboard() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, new DashboardFragment())
                .commit();
        getActivity().setTitle("Dashboard");
    }
}
