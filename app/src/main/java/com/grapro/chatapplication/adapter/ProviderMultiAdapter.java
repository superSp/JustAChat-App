package com.grapro.chatapplication.adapter;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.grapro.chatapplication.bean.ChatMsgBean;

import java.util.List;

public class ProviderMultiAdapter extends BaseProviderMultiAdapter<ChatMsgBean> {
    public ReceiveItemTypeProvider.onFirerImageTouchListener onFirerImageTouchListener;
    @Override
    protected int getItemType(@NonNull List<? extends ChatMsgBean> list, int i) {
        return list.get(i).getItemType();
    }

    public ProviderMultiAdapter(String friendName, ReceiveItemTypeProvider.onFirerImageTouchListener onFirerImageTouchListener) {
        super();
        this.onFirerImageTouchListener=onFirerImageTouchListener;
        // 注册 Provider
        addItemProvider(new ReceiveItemTypeProvider(onFirerImageTouchListener));
        addItemProvider(new SendItemTypeProvider(friendName));
    }
}
