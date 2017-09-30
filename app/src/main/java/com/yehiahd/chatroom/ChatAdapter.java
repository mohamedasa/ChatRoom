package com.yehiahd.chatroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yehia on 23/09/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {


    private Context mContext;
    private List<Message> list;

    public ChatAdapter(Context mContext, List<Message> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatHolder(view);
    }

    public void updateList(List<Message> messages) {
        list.clear();
        list.addAll(messages);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        Message message = list.get(position);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String url = preferences.getString("url", "");
        if (message.getImgUrl().equals(url)) {
            holder.chatUserName.setTextColor(Color.parseColor("#C51162"));
        } else {
            holder.chatUserName.setTextColor(Color.WHITE);
        }

        holder.chatUserName.setText(message.getName());
        holder.chatMessage.setText(message.getMsg());
        holder.chatDate.setText(message.getDate());

        Picasso.with(mContext)
                .load(message.getImgUrl())
                .placeholder(R.drawable.progress_placeholder)
                .transform(new CircleTransform())
                .into(holder.chatImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chat_img)
        ImageView chatImg;
        @BindView(R.id.chat_user_name)
        TextView chatUserName;
        @BindView(R.id.chat_message)
        TextView chatMessage;
        @BindView(R.id.chat_date)
        TextView chatDate;


        public ChatHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
