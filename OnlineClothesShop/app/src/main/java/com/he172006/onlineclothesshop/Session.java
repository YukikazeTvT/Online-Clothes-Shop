package com.he172006.onlineclothesshop;
import android.content.Context;
import android.content.SharedPreferences;
import com.he172006.onlineclothesshop.DAO.AccountDAO;
import com.he172006.onlineclothesshop.entity.Account;

public class Session {
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_ACCOUNT_ID = "accountId";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public Session(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(Account account) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ACCOUNT_ID, account.getAccountId());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public Account getLoggedInAccount() {
        if (!isLoggedIn()) {
            return null;
        }
        int accountId = pref.getInt(KEY_ACCOUNT_ID, -1);
        if (accountId == -1) {
            return null;
        }
        AccountDAO accountDAO = new AccountDAO(context);
        Account account = accountDAO.getAccountById(accountId);
        accountDAO.close();
        return account;
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}