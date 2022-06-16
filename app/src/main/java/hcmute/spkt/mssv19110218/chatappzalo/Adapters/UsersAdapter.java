package hcmute.spkt.mssv19110218.chatappzalo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hcmute.spkt.mssv19110218.chatappzalo.Activity.ChatActivity;
import hcmute.spkt.mssv19110218.chatappzalo.R;
import hcmute.spkt.mssv19110218.chatappzalo.Models.User;
import hcmute.spkt.mssv19110218.chatappzalo.databinding.ChatboxBinding;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    Context context; //Context chứa adapter
    ArrayList<User> users; //Khởi tạo danh sách user

    public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //set layout của UserViewHolder là chatbox
        View view = LayoutInflater.from(context).inflate(R.layout.chatbox, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        //lấy vị trí của user
        User user = users.get(position);
        //khởi tạo senderID bằng hàm getUid của FirebaseAuth
        String senderID = FirebaseAuth.getInstance().getUid();
        //khởi tạo và quy định phòng của người gửi được gán là senderID + user.getUserid()
        String senderRoom = senderID + user.getUserid();
        //đưa đường dẫn "chats" để theo dõi dữ liệu của database
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Nếu data của phòng chat có thay đổi
                        //có nghĩa là có người nhắn tin thì sẽ lưu lại lastMsg và lastMsgTime
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            Long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a" );
                            //gán textView time bằng time đã được chuyển sang dạng hh:mm a
                            holder.binding.time.setText(dateFormat.format(new Date(time)));
                            holder.binding.lastMsg.setText(lastMsg);
                        }
                        //còn nếu không có sự thay đổi
                        //Chưa ai nhắn cho nhau thì sẽ set lastMsg là Tap to chat
                        else {
                            holder.binding.lastMsg.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //set text cho userName bằng user.getName() trong user Model
        holder.binding.userName.setText(user.getName());
        //tạo 1 singleton để trình bày một giao diện tĩnh đơn giản để xây dựng các yêu cầu với RequestBuilder
        Glide.with(context).load(user.getAvatar())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.avatar);

        //set sự kiện onclick của ItemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //Lưu hết biến để xài cho intent sau
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName()); //lưu biến name bằng user.getName()
                intent.putExtra("image", user.getAvatar()); //lưu biến image bằng user.getAvatar()
                intent.putExtra("uid", user.getUserid()); //lưu biến uid bằng user.getUserid()
                intent.putExtra("token", user.getToken()); //lưu biến token bằng user.getToken()
                context.startActivity(intent);
            }
        });
    }

    //hàm trả về chiều dài của users
    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        ChatboxBinding binding; //khởi tạo các view bằng ChatboxBinding
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChatboxBinding.bind(itemView);
        }

    }
}
