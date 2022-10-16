package com.example.plantary.bot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.plantary.R;

import java.util.List;

public class BotAdapter extends ArrayAdapter<Message> {

    private final Context context;
    private final LayoutInflater inflater;
    private final int layout;
    private final List<Message> messages;

    public BotAdapter(@NonNull Context context, int resource, List<Message> messages) {
        super(context, resource);
        this.context = context;
        this.messages = messages;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }

        TextView textMessage = convertView.findViewById(R.id.chat_text);

        Message message = messages.get(position);

        textMessage.setText(message.getTextMessage());
        final float scale = context.getResources().getDisplayMetrics().density;
        int padding_26dp = (int) (26 * scale + 0.5f);
        int padding_10dp = (int) (10 * scale + 0.5f);
        int padding_50dp = (int) (50 * scale + 0.5f);

        if (!message.isBot()) {
            textMessage.setBackground(ContextCompat.getDrawable(context, R.drawable.user_mes));

            textMessage.setPadding(padding_10dp, padding_10dp, padding_26dp, padding_10dp);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textMessage.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.setMargins(padding_50dp, padding_10dp, padding_10dp, padding_10dp);
            textMessage.setLayoutParams(params);
        } else {
            textMessage.setBackground(ContextCompat.getDrawable(context, R.drawable.bot_mes));
            textMessage.setPadding(padding_26dp, padding_10dp, padding_10dp, padding_10dp);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textMessage.getLayoutParams();
            params.setMargins(padding_10dp, padding_10dp, padding_50dp, padding_10dp);
        }

        return convertView;
    }
}
