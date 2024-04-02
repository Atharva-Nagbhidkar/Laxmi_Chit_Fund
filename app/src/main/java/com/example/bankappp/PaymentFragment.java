package com.example.bankappp;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.bankappp.Model.Account;
import com.example.bankappp.Model.Payee;
import com.example.bankappp.Model.Profile;
import com.example.bankappp.Model.db.ApplicationDB;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PaymentFragment extends Fragment {
    private Spinner spnSelectAccount;
    private TextView txtNoPayeesMsg;
    private Spinner spnSelectPayee;
    private EditText edtPaymentAmount;
    private Button btnMakePayment;
    private Button btnAddPayee;

    private Dialog payeeDialog;
    private EditText edtPayeeName;
    private Button btnCancel;
    private Button btnConfirmAddPayee;

    private static final String CHANNEL_ID = "PaymentChannel";

    private ArrayList<Account> accounts;
    private ArrayAdapter<Account> accountAdapter;

    private ArrayList<Payee> payees;
    private ArrayAdapter<Payee> payeeAdapter;

    private Gson gson;
    private String json;
    private Profile userProfile;
    private SharedPreferences userPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        createNotificationChannel();

        spnSelectAccount = rootView.findViewById(R.id.spn_select_acc);
        txtNoPayeesMsg = rootView.findViewById(R.id.txt_no_payees);
        spnSelectPayee = rootView.findViewById(R.id.spn_select_payee);
        edtPaymentAmount = rootView.findViewById(R.id.edt_payment_amount);
        btnMakePayment = rootView.findViewById(R.id.btn_make_payment);
        btnAddPayee = rootView.findViewById(R.id.floating_action_btn);

        setValues();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                navigateToDashboard();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return rootView;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Payment Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setValues() {
        userPreferences = getActivity().getSharedPreferences("LastProfileUsed", MODE_PRIVATE);
        gson = new Gson();
        json = userPreferences.getString("LastProfileUsed", "");
        userProfile = gson.fromJson(json, Profile.class);

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });

        btnAddPayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayPayeeDialog();
            }
        });

        accounts = userProfile.getAccounts();
        accountAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, accounts);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectAccount.setAdapter(accountAdapter);

        payees = userProfile.getPayees();
        payeeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, payees);
        payeeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectPayee.setAdapter(payeeAdapter);

        checkPayeeInformation();
    }

    private void displayPayeeDialog() {
        payeeDialog = new Dialog(getActivity());
        payeeDialog.setContentView(R.layout.payee_dialog);
        payeeDialog.setCanceledOnTouchOutside(true);
        payeeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getActivity(), "Payee Addition Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        edtPayeeName = payeeDialog.findViewById(R.id.edt_payee_name);
        btnCancel = payeeDialog.findViewById(R.id.btn_cancel_dialog);
        btnConfirmAddPayee = payeeDialog.findViewById(R.id.btn_add_payee);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payeeDialog.dismiss();
                Toast.makeText(getActivity(), "Payee Creation Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        btnConfirmAddPayee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPayee();
            }
        });

        payeeDialog.show();
    }

    private void checkPayeeInformation() {
        if (userProfile.getPayees().size() == 0) {
            txtNoPayeesMsg.setVisibility(VISIBLE);
            spnSelectPayee.setVisibility(GONE);
            edtPaymentAmount.setVisibility(GONE);
            btnMakePayment.setVisibility(GONE);
        } else {
            txtNoPayeesMsg.setVisibility(GONE);
            spnSelectPayee.setVisibility(VISIBLE);
            edtPaymentAmount.setVisibility(VISIBLE);
            btnMakePayment.setVisibility(VISIBLE);
        }
    }

    private void makePayment() {
        boolean isNum = false;
        double paymentAmount = 0;

        try {
            paymentAmount = Double.parseDouble(edtPaymentAmount.getText().toString());
            if (Double.parseDouble(edtPaymentAmount.getText().toString()) >= 0.01) {
                isNum = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isNum) {
            int selectedAccountIndex = spnSelectAccount.getSelectedItemPosition();

            if (paymentAmount > userProfile.getAccounts().get(selectedAccountIndex).getAccountBalance()) {
                Toast.makeText(getActivity(), "You do not have sufficient funds to make this payment", Toast.LENGTH_SHORT).show();
            } else {
                int selectedPayeeIndex = spnSelectPayee.getSelectedItemPosition();
                String selectedPayee = userProfile.getPayees().get(selectedPayeeIndex).toString();

                userProfile.getAccounts().get(selectedAccountIndex).addPaymentTransaction(selectedPayee, paymentAmount);

                accounts = userProfile.getAccounts();
                spnSelectAccount.setAdapter(accountAdapter);
                spnSelectAccount.setSelection(selectedAccountIndex);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());
                applicationDb.saveNewTransaction(userProfile, userProfile.getAccounts().get(selectedAccountIndex).getAccountNo(), userProfile.getAccounts().get(selectedAccountIndex).getTransactions().get(userProfile.getAccounts().get(selectedAccountIndex).getTransactions().size() - 1));
                applicationDb.overwriteAccount(userProfile, userProfile.getAccounts().get(selectedAccountIndex));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Payment of RS." + String.format(Locale.getDefault(), "%.2f", paymentAmount) + " successfully made", Toast.LENGTH_SHORT).show();
                sendNotification("Payment Successful", "Payment of RS." + String.format(Locale.getDefault(), "%.2f", paymentAmount) + " successfully made");

                navigateToDashboard();
            }
        } else {
            Toast.makeText(getActivity(), "Please enter a valid number, greater than $0.01", Toast.LENGTH_SHORT).show();
            edtPaymentAmount.getText().clear();
        }
    }

    private void addPayee() {
        if (!(edtPayeeName.getText().toString().equals(""))) {
            boolean match = false;
            for (int i = 0; i < userProfile.getPayees().size(); i++) {
                if (edtPayeeName.getText().toString().equalsIgnoreCase(userProfile.getPayees().get(i).getPayeeName())) {
                    match = true;
                }
            }

            if (!match) {
                userProfile.addPayee(edtPayeeName.getText().toString());

                edtPayeeName.setText("");

                txtNoPayeesMsg.setVisibility(GONE);
                spnSelectPayee.setVisibility(VISIBLE);
                edtPaymentAmount.setVisibility(VISIBLE);
                btnMakePayment.setVisibility(VISIBLE);

                payees = userProfile.getPayees();
                spnSelectPayee.setAdapter(payeeAdapter);
                spnSelectPayee.setSelection(userProfile.getPayees().size() - 1);

                ApplicationDB applicationDb = new ApplicationDB(getActivity().getApplicationContext());
                applicationDb.saveNewPayee(userProfile, userProfile.getPayees().get(userProfile.getPayees().size() - 1));

                SharedPreferences.Editor prefsEditor = userPreferences.edit();
                gson = new Gson();
                json = gson.toJson(userProfile);
                prefsEditor.putString("LastProfileUsed", json).apply();

                Toast.makeText(getActivity(), "Payee Added Successfully", Toast.LENGTH_SHORT).show();

                payeeDialog.dismiss();
            } else {
                Toast.makeText(getActivity(), "A Payee with that name already exists", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.laxmichitfund)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

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
