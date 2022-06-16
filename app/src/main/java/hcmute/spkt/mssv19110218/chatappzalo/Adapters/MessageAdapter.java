package hcmute.spkt.mssv19110218.chatappzalo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hcmute.spkt.mssv19110218.chatappzalo.Models.Message;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ChatboxBinding;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.MsgReceiveBinding;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.MsgSendBinding;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.msg_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.msg_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderID())){
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getClass() == SendViewHolder.class){
            SendViewHolder viewHolder = (SendViewHolder)holder;

            if(message.getMessage().equals("[photo]")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.msgSend.setVisibility(View.GONE);
                viewHolder.binding.voicePlayerView.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            } else if (message.getMessage().equals("[voice*]")) {
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.msgSend.setVisibility(View.GONE);
                viewHolder.binding.voicePlayerView.setVisibility(View.VISIBLE);
                viewHolder.binding.voicePlayerView.setAudio(message.getVoiceUrl());
            }
            viewHolder.binding.msgSend.setText(message.getMessage());
        } else {
            ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;

            if(message.getMessage().equals("[photo]")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.msgReceive.setVisibility(View.GONE);
                viewHolder.binding.voicePlayerView.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            } else if (message.getMessage().equals("[voice*]")) {
                viewHolder.binding.image.setVisibility(View.GONE);
                viewHolder.binding.msgReceive.setVisibility(View.GONE);
                viewHolder.binding.voicePlayerView.setVisibility(View.VISIBLE);
                viewHolder.binding.voicePlayerView.setAudio(message.getVoiceUrl());
            }
            viewHolder.binding.msgReceive.setText(message.getMessage());
        }
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder {

        MsgSendBinding binding;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MsgSendBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {

        MsgReceiveBinding binding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MsgReceiveBinding.bind(itemView);
        }
    }
}
