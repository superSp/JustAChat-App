package com.grapro.chatapplication.adapter;

import android.nfc.Tag;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.bean.ChatMsgBean;
import com.grapro.chatapplication.content.ItemTypes;
import com.grapro.chatapplication.content.MsgType;
import com.grapro.chatapplication.db.DBhelperUtil;

public class ReceiveItemTypeProvider extends BaseItemProvider<ChatMsgBean> {
    onFirerImageTouchListener onFirerImageTouchListener;
    private static final String TAG = "ReceiveItemTypeProvider";

    public ReceiveItemTypeProvider(ReceiveItemTypeProvider.onFirerImageTouchListener onFirerImageTouchListener) {
        this.onFirerImageTouchListener = onFirerImageTouchListener;
    }

    @Override
    public int getItemViewType() {
        return ItemTypes.RECEIVE;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_receive;
    }

    public interface onFirerImageTouchListener {
        void onTouchDown(String picPath);

        void onTouchUp(String picPath);
    }



    @Override
    public void convert(@NonNull BaseViewHolder holder, ChatMsgBean data) {
        holder.setText(R.id.chat_item_date, data.getTime());
        holder.setText(R.id.chat_item_header, data.getName());
        holder.getView(R.id.txt_fire).setVisibility(data.getMsgType() == MsgType.FIRE_IMAGE ? View.VISIBLE : View.GONE);
        switch (data.getMsgType()) {
            case MsgType.TEXT:
                holder.setText(R.id.chat_item_content_text, data.getMsg());
                holder.setVisible(R.id.chat_item_layout_content, true);
                holder.setVisible(R.id.chat_item_content_text, true);
                holder.getView(R.id.chat_item_content_image).setVisibility(View.GONE);
                holder.getView(R.id.chat_item_content_image).setFocusable(false);
                holder.getView(R.id.chat_item_content_image).setClickable(false);
                break;
            case MsgType.IMAGE:
            case MsgType.FIRE_IMAGE:
                holder.getView(R.id.chat_item_content_image).setFocusable(true);
                holder.getView(R.id.chat_item_content_image).setClickable(true);
                holder.setVisible(R.id.chat_item_layout_content, false);
                holder.setVisible(R.id.chat_item_content_text, false);
                holder.getView(R.id.chat_item_content_image).setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(data.getMsgType() == MsgType.FIRE_IMAGE ? R.mipmap.pic :
                    data.getPicPath()).into((ImageView) holder.getView(R.id.chat_item_content_image));
                holder.getView(R.id.chat_item_content_image).setOnTouchListener(data.getMsgType() == MsgType.IMAGE ? null : new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.e(TAG, event.getAction() + "");
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        if (data.getMsgType() == MsgType.IMAGE) {
                            return true;
                        }
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (data.isDestoryd()) {
                                    Toast.makeText(getContext(), "The image has since been destroyed", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                onFirerImageTouchListener.onTouchDown(data.getPicPath());
                                data.setPicPath("");
                                data.setDestoryd(true);
                                DBhelperUtil.getInstance().updateFirImgDestroy(data.getId(),data.getTime(),context);
                                return true;
                            case MotionEvent.ACTION_UP:
                                onFirerImageTouchListener.onTouchUp(data.getPicPath());
                                return true;
                        }
                        return false;
                    }
                });
                break;
        }
    }
}
