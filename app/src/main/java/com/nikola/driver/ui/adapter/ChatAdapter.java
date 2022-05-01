package com.nikola.driver.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.ChatObject;
import com.nikola.driver.ui.activity.ChatActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private static final int MY_TEXT = 0;
    private static final int NOT_MY_TEXT = 1;
    private ChatScreenInterface chatScreen;
    Context context;
    LayoutInflater inflate;
    private ArrayList<ChatObject> chatMessages;


    public ChatAdapter(ChatActivity chatScreen, ArrayList<ChatObject> chatMessages) {
        this.chatMessages = chatMessages;
        this.chatScreen = chatScreen;
        this.context = chatScreen;
        inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == MY_TEXT) {
            view = inflate.inflate(R.layout.item_my_chat, viewGroup, false);
            return new MyMessageViewHolder(view);
        } else {
            view = inflate.inflate(R.layout.item_not_my_chat, viewGroup, false);
            return new NotMyMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ChatObject chatItem = chatMessages.get(position);
        if (chatItem.isMyText()) {
            MyMessageViewHolder holder = (MyMessageViewHolder) viewHolder;
            if(chatItem.isMyText()) {
                if (position == chatMessages.size() - 1)
                    holder.personImage.setVisibility(View.GONE);
                else if (chatMessages.get(position + 1).isMyText()) {
                    holder.personImage.setVisibility(View.INVISIBLE);
                } else
                    holder.personImage.setVisibility(View.GONE);

                Glide.with(context).load(chatItem.getPersonImage())
                        .into(holder.personImage);
                holder.messageTime.setText(chatMessages.get(position).getMessageTime());
                holder.message.setText(chatMessages.get(position).getMessageText());
            }
        } else {
            if(!chatItem.isMyText()) {
                NotMyMessageViewHolder notMyHolder = (NotMyMessageViewHolder) viewHolder;

                if (position == chatMessages.size() - 1)
                    notMyHolder.personImage.setVisibility(View.VISIBLE);
                else if (!chatMessages.get(position + 1).isMyText()) {
                    notMyHolder.personImage.setVisibility(View.INVISIBLE);
                } else
                    notMyHolder.personImage.setVisibility(View.VISIBLE);

                Glide.with(context).load(chatItem.getPersonImage())
                        .into(notMyHolder.personImage);
                notMyHolder.messageTime.setText(chatMessages.get(position).getMessageTime());
                notMyHolder.message.setText(chatMessages.get(position).getMessageText());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).isMyText() ? MY_TEXT : NOT_MY_TEXT;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    public void showLoading() {
        if(chatScreen!=null)
            chatScreen.onLoadMoreChatsAfter(chatMessages.size());
    }

    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.persionImage)
        ImageView personImage;
        @BindView(R.id.messageTime)
        TextView messageTime;
        @BindView(R.id.message)
        TextView message;

        MyMessageViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class NotMyMessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.persionImage)
        ImageView personImage;
        @BindView(R.id.messageTime)
        TextView messageTime;
        @BindView(R.id.message)
        TextView message;

        NotMyMessageViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ChatScreenInterface{
        void onLoadMoreChatsAfter(int skip);

    }
}
