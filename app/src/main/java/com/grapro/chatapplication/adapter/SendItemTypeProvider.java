package com.grapro.chatapplication.adapter;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.grapro.chatapplication.R;
import com.grapro.chatapplication.bean.ChatMsgBean;
import com.grapro.chatapplication.content.ItemTypes;
import com.grapro.chatapplication.content.MsgStatus;
import com.grapro.chatapplication.content.MsgType;
import com.grapro.chatapplication.content.SpUsersdNames;
import com.grapro.chatapplication.util.SpUtils;

public class SendItemTypeProvider extends BaseItemProvider<ChatMsgBean> {
    private String myName;
    private static final String TAG = "SendItemTypeProvider";

    public SendItemTypeProvider(String friendName) {
        myName = friendName;
    }

    @Override
    public int getItemViewType() {
        return ItemTypes.SEND;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_send;
    }
    @Override
    public void convert(@NonNull BaseViewHolder holder, ChatMsgBean data) {
        holder.setText(R.id.chat_item_date, data.getTime());
        holder.setText(R.id.chat_item_header, myName);
        switch (data.getMsgType()) {
            case MsgType.TEXT:
                holder.setText(R.id.chat_item_date, data.getTime());
                holder.setText(R.id.chat_item_content_text, data.getMsg());
                holder.setVisible(R.id.chat_item_layout_content, true);
                holder.setVisible(R.id.chat_item_content_text, true);
                holder.getView(R.id.chat_item_content_image).setVisibility(View.GONE);
                holder.getView(R.id.fire_tag).setVisibility(View.GONE);
                break;
            case MsgType.IMAGE:
            case MsgType.FIRE_IMAGE:
                holder.getView(R.id.chat_item_content_image).setFocusable(true);
                holder.getView(R.id.chat_item_content_image).setClickable(true);
                holder.setText(R.id.chat_item_date, data.getTime());
                holder.setVisible(R.id.chat_item_layout_content, false);
                holder.setVisible(R.id.chat_item_content_text, false);
                holder.getView(R.id.chat_item_content_image).setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(data.getPicPath()).into((ImageView) holder.getView(R.id.chat_item_content_image));
                holder.getView(R.id.fire_tag).setVisibility(data.getMsgType() == MsgType.FIRE_IMAGE ? View.VISIBLE : View.GONE);
                break;
        }
        switch (data.getMsgStatus()) {
            case MsgStatus.Sending:
                holder.getView(R.id.chat_item_fail).setVisibility(View.GONE);
                holder.getView(R.id.chat_item_progress).setVisibility(View.VISIBLE);
                break;
            case MsgStatus.Error:
                holder.getView(R.id.chat_item_fail).setVisibility(View.VISIBLE);
                holder.getView(R.id.chat_item_progress).setVisibility(View.GONE);
                break;
            case MsgStatus.Successed:
                holder.getView(R.id.chat_item_fail).setVisibility(View.GONE);
                holder.getView(R.id.chat_item_progress).setVisibility(View.GONE);
                break;
        }
    }
}
