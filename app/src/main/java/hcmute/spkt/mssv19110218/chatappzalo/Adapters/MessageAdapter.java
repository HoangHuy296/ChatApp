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

    Context context; //Context chứa adapter
    ArrayList<Message> messages; //Khởi tạo danh sách của messages

    //gán 2 biến toàn cục là ITEM_SEND và ITEM_RECEIVE
    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Nếu viewType là ITEM_SEND thì set RecyclerView là layout của msg_send
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.msg_send, parent, false);
            return new SendViewHolder(view);
        }
        //Ngược lại với ở trên
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.msg_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    //hàm lấy itemViewType
    @Override
    public int getItemViewType(int position) {
        //hàm lấy vị trí của position
        Message message = messages.get(position);
        //kiểm tra nếu Uid trùng với Id của người gửi thì trả bề Item_Send
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderID())){
            return ITEM_SEND;
        }
        //Ngược lại trả về ITEM_RECEIVE
        else {
            return ITEM_RECEIVE;
        }
    }

    //hàm để holder các View
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Khởi tạo vị trí của message
        Message message = messages.get(position);

        //set holder bằng sendViewHolder
        if (holder.getClass() == SendViewHolder.class){
            SendViewHolder viewHolder = (SendViewHolder)holder;
            //Nếu message là [photo]
            if(message.getMessage().equals("[photo]")) {
                //hiện btn gửi Image
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                //ẩn khung nhắn tin
                viewHolder.binding.msgSend.setVisibility(View.GONE);
                //ẩn gửi voice Record
                viewHolder.binding.voicePlayerView.setVisibility(View.GONE);
                //tạo 1 singleton để trình bày một giao diện tĩnh đơn giản để xây dựng các yêu cầu với RequestBuilder
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            }
            //Nếu message bằng với [voice*]
            else if (message.getMessage().equals("[voice*]")) {
                //ẩn btn send Image
                viewHolder.binding.image.setVisibility(View.GONE);
                //ẩn khung chat
                viewHolder.binding.msgSend.setVisibility(View.GONE);
                //hiện nút để record
                viewHolder.binding.voicePlayerView.setVisibility(View.VISIBLE);
                //set Audio để lấy VoideUrl trong Message Adapter
                viewHolder.binding.voicePlayerView.setAudio(message.getVoiceUrl());
            }
            //lưu lại tin nhắn để xử lí hàm dưới
            viewHolder.binding.msgSend.setText(message.getMessage());
        }
        //Hàm xử lí bên nhận tương tự
        else {
            ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
            //Nếu message là [photo]
            if(message.getMessage().equals("[photo]")){
                //hiện btn gửi Image
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                //ẩn khung nhắn tin
                viewHolder.binding.msgReceive.setVisibility(View.GONE);
                //ẩn gửi voice Record
                viewHolder.binding.voicePlayerView.setVisibility(View.GONE);
                //tạo 1 singleton để trình bày một giao diện tĩnh đơn giản để xây dựng các yêu cầu với RequestBuilder
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(viewHolder.binding.image);
            }
            //nếu message bằng [voice*]
            else if (message.getMessage().equals("[voice*]")) {
                //ẩn btn send Image
                viewHolder.binding.image.setVisibility(View.GONE);
                //ẩn khung chat
                viewHolder.binding.msgReceive.setVisibility(View.GONE);
                //Hiện nút audio Record
                viewHolder.binding.voicePlayerView.setVisibility(View.VISIBLE);
                viewHolder.binding.voicePlayerView.setAudio(message.getVoiceUrl());
            }
            //get message
            viewHolder.binding.msgReceive.setText(message.getMessage());
        }
    }

    //trả về chiếu dài của list message
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder {

        MsgSendBinding binding; //tạo các view trong MsgSendBinding

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MsgSendBinding.bind(itemView);
        }
    }

    public class ReceiveViewHolder extends RecyclerView.ViewHolder {

        MsgReceiveBinding binding; //tạo các view trong MsgSendBinding

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = MsgReceiveBinding.bind(itemView);
        }
    }
}
