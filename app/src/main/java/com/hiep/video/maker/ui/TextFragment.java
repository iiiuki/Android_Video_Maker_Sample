package com.hiep.video.maker.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;


import com.hiep.video.maker.R;
import com.hiep.video.maker.system.VideoMaker;
import com.hiep.video.maker.ui.adapter.ColorPickerAdapter;
import com.hiep.video.maker.ui.adapter.FontPickerAdapter;
import com.hiep.video.maker.ui.adapter.MyAdapter;

import java.io.IOException;

/**
 * Created by hiep on 6/11/2016.
 */
public class TextFragment extends Fragment implements View.OnClickListener {
    private Context mContext;
    private RecyclerView mRecyclerView_Color,mRecyclerView_font;
    private ImageView mIvCancelText,mIvDoneText;
    private EditText mEdAddText;
    private EditorActivity editorActivity;
    private boolean isShow;
    private String[] listFonts;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
        editorActivity=(EditorActivity)this.getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_text, container, false);
        if (root != null) {
            initView(root);
            return root;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    private void initView(View view) {
        mRecyclerView_Color=(RecyclerView)view.findViewById(R.id.recyclerView_color);
        mRecyclerView_font=(RecyclerView)view.findViewById(R.id.recyclerView_font);

        mIvCancelText=(ImageView)view.findViewById(R.id.iv_cancel_text);
        mIvDoneText=(ImageView)view.findViewById(R.id.iv_done_text);
        mEdAddText=(EditText)view.findViewById(R.id.ed_add_text);

        mIvCancelText.setOnClickListener(this);
        mIvDoneText.setOnClickListener(this);

        iniColor();
        iniFont();
    }

    private void iniColor(){
        int colorFlipper = this.getResources().getColor(R.color.trgb_262626);
        int colorBtnFooter = this.getResources().getColor(R.color.collage_purple);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);

        ColorPickerAdapter colorPickerAdapter=new ColorPickerAdapter(new MyAdapter.CurrentCollageIndexChangedListener() {
            public void onIndexChanged(int index) {
                mEdAddText.setTextColor(index);
                mEdAddText.invalidate();
            }
        }, colorFlipper, colorBtnFooter);
        mRecyclerView_Color.setLayoutManager(linearLayoutManager);
        mRecyclerView_Color.setAdapter(colorPickerAdapter);
        mRecyclerView_Color.setItemAnimator(new DefaultItemAnimator());
    }

    private String[] getListFont() {
        String[] mList = null;
        try {
            mList = mContext.getAssets().list("font");
            return mList;
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return mList;
    }

    private void iniFont(){
        listFonts=getListFont();

        int colorFlipper = this.getResources().getColor(R.color.trgb_262626);
        int colorBtnFooter = this.getResources().getColor(R.color.collage_purple);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);

        FontPickerAdapter colorPickerAdapter=new FontPickerAdapter(listFonts,new MyAdapter.CurrentCollageIndexChangedListener() {
            public void onIndexChanged(int index) {
                String name=listFonts[index];
                Typeface mTypeface = Typeface.createFromAsset(VideoMaker.context.getAssets(), (new StringBuilder("font/")).append(name).toString());
                mEdAddText.setTypeface(mTypeface);
            }
        }, colorFlipper, colorBtnFooter);

        mRecyclerView_font.setLayoutManager(linearLayoutManager);
        mRecyclerView_font.setAdapter(colorPickerAdapter);
        mRecyclerView_font.setItemAnimator(new DefaultItemAnimator());
    }

    private long mLastClickTime = 0;
    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (v.getId()){
            case R.id.iv_cancel_text:
                editorActivity.clearViewFlipper();
                break;
            case R.id.iv_done_text:
                editorActivity.addStickerText(mEdAddText);
                editorActivity.clearViewFlipper();
                break;
        }
    }

    public void show() {
        isShow=true;
        FragmentTransaction transaction = ((FragmentActivity) this.mContext).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.show(this);
        transaction.commit();
    }
    public void hide() {
        isShow=false;
        FragmentTransaction transaction = ((FragmentActivity) this.mContext).getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.hide(this);
        transaction.commitAllowingStateLoss();
    }

    public boolean getShowFragment() {
        return isShow;
    }
}
