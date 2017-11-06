package com.example.sonminhee.checkheart.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.sonminhee.checkheart.R;
import com.example.sonminhee.checkheart.activity.HeartCheckActivity;

/**
 * Created by sonminhee on 2017. 10. 11..
 */

public class EcallDialog extends Dialog implements View.OnClickListener{

    private TextView mTitleView;
    private TextView mContentView;
    private TextView timerView;
    private Button mLeftButton;
    private Button mRightButton;
    private String mTitle;
    private String mContent;

    int timerValue = 15;

    /**
     * 타이머
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            timerValue--;
            setTimer(timerValue);

            // 메세지를 처리하고 또다시 핸들러에 메세지 전달 (1000ms 지연)
            if (timerValue == 0) {
                mHandler.removeMessages(0);
                openDial();
                dismiss();
            } else {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    public EcallDialog(Context context) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        mHandler.sendEmptyMessage(0);
    }

    public EcallDialog(Context context, String title) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        mHandler.sendEmptyMessage(0);
    }

    public EcallDialog(Context context, String title, String content) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mTitle = title;
        this.mContent = content;
        mHandler.sendEmptyMessage(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.popup_ecall);

        init();
        setTitle(mTitle);
        setContent(mContent);

    }

    private void init() {
        mTitleView = (TextView) findViewById(R.id.tv_title);
        mContentView = (TextView) findViewById(R.id.tv_content);
        timerView = (TextView) findViewById(R.id.tv_timer);
        mLeftButton = (Button) findViewById(R.id.bt_left);
        mLeftButton.setOnClickListener(this);
        mRightButton = (Button) findViewById(R.id.bt_right);
        mRightButton.setOnClickListener(this);
    }

    private void setTitle(String title) {
        mTitleView.setText(title);
    }

    private void setContent(String content) {
        mContentView.setText(content);
    }

    public void setTimer(int time) {
        timerView.setText(time + "초 후 서비스 자동 실행");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_left:
                openDial();
                dismiss();
                mHandler.removeMessages(0);
                break;
            case R.id.bt_right:
                dismiss();
                mHandler.removeMessages(0);
                break;
        }
    }

    public void openDial() {
        ((HeartCheckActivity)HeartCheckActivity.mContext).openDial();
    }

}
