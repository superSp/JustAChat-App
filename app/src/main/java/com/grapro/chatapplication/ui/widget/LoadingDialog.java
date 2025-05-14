package com.grapro.chatapplication.ui.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.grapro.chatapplication.R;

import java.lang.ref.WeakReference;

/**
 * Created by lsp on 2015/10/26.
 * 加载对话框封装
 */
public class LoadingDialog {
    private static final String TAG = "ToastUtils";
    private WeakReference<ProgressDialog> mProgress;
    private Context context;
    private Dialog dialog;

    private int type = 0;     //0普通加载中，1 附带进度条
    private ProgressBar progressBar;
    private TextView textView;
    private TextView titleTv;
    private String titleStr;

    private long tempMax = 0;

    private String noticMsg = "";

    public LoadingDialog(Context context, String str) {
        this.noticMsg = str;
        this.context = context;
    }

    public LoadingDialog(Context context) {
        this.context = context;
    }

    public LoadingDialog(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public LoadingDialog(Context context, int type, String title) {
        this.context = context;
        this.type = type;
        this.titleStr = title;
    }

    public boolean isRunning() {
        final Dialog progressDialog = dialog;
        if (progressDialog == null) return false;
        return progressDialog.isShowing();
    }

    public void cancel() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void changeProgressBar(long max, long progress, String percent) {
        //因为每次上传完图片后还有一些文字要上传 去除文字的size
        if (tempMax != max && tempMax != 0) {
            return;
        }
        tempMax = max;

        progressBar.setMax((int) max);
        progressBar.setProgress((int) progress);
        textView.setText(percent);
    }

    public void showToastAlong() {
        if (context == null) {
            return;
        }
        switch (type) {
            case 0: {
                final View view = LayoutInflater.from(context).inflate(R.layout.ep_alert_dialog4, null);
                dialog = new Dialog(context, R.style.alert_dialog);
                dialog.setContentView(view);
                dialog.show();

                if(!"".equals(noticMsg)){
                    ((TextView)view.findViewById(R.id.id_pb_tv)).setText(noticMsg);
                }
                break;
            }
            case 1: {
                final View view = LayoutInflater.from(context).inflate(R.layout.ep_alert_dialog5, null);
                dialog = new Dialog(context, R.style.alert_dialog);
                dialog.setContentView(view);

                dialog.setCancelable(false);

                progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
                textView = (TextView) view.findViewById(R.id.textView2);
                titleTv = (TextView) view.findViewById(R.id.id_update_msg);
                titleTv.setText(titleStr);

                dialog.show();
                break;
            }
            default: {
                break;
            }
        }
    }

    public void showToastAlong(boolean canCancle) {
        if (context == null) {
            return;
        }
        switch (type) {
            case 0: {
                final View view = LayoutInflater.from(context).inflate(R.layout.ep_alert_dialog4, null);
                dialog = new Dialog(context, R.style.alert_dialog);
                dialog.setContentView(view);

                if (!canCancle) {
                    dialog.setCancelable(false);
//                    dialog.setCanceledOnTouchOutside(false);
                }


                dialog.show();
                break;
            }
            case 1: {
                final View view = LayoutInflater.from(context).inflate(R.layout.ep_alert_dialog5, null);
                dialog = new Dialog(context, R.style.alert_dialog);
                dialog.setContentView(view);

                progressBar = (ProgressBar) view.findViewById(R.id.progressBar2);
                textView = (TextView) view.findViewById(R.id.textView2);
                titleTv = (TextView) view.findViewById(R.id.id_update_msg);
                titleTv.setText(titleStr);

                if (!canCancle) {
                    dialog.setCancelable(false);
                }

                dialog.show();
                break;
            }
            default: {
                break;
            }
        }
    }
}
