package org.jaram.ds.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import org.jaram.ds.R;
import org.jaram.ds.networks.ApiConstants;
import org.jaram.ds.util.EasySharedPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jdekim43 on 2016. 5. 23..
 */
public class SettingDialog extends AppCompatDialogFragment {

    public static SettingDialog newInstance() {
        return new SettingDialog();
    }

    @BindView(R.id.serverUrl) EditText serverUrlView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_setting, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(R.string.label_setting_server_url);
        return dialog;
    }

    @OnClick(R.id.cancel)
    @Override
    public void dismiss() {
        super.dismiss();
    }

    @OnClick(R.id.confirm)
    protected void save() {
        EasySharedPreferences.with(getContext()).putString(ApiConstants.PREF_URL,
                serverUrlView.getText().toString());
        dismiss();
    }

    @Override
    public Context getContext() {
        return super.getActivity() == null ? super.getContext() : super.getActivity();
    }
}
