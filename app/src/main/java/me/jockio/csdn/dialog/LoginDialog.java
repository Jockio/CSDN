package me.jockio.csdn.dialog;

import android.app.Dialog;
import android.content.Context;

/**
 * Created by jockio on 16/6/12.
 */

public class LoginDialog extends Dialog {
    public LoginDialog(Context context) {
        super(context);
    }

    public LoginDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected LoginDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


}
