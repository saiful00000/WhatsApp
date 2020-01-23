package com.example.whatsapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsapp.R;
import com.example.whatsapp.utilitys.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessageList;

    private FirebaseAuth myAuth;
    private DatabaseReference usersReference;



    public MessageAdapter(List<Messages> userMessageList) {
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_msg_layout, viewGroup, false);

        myAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        String msgSenderId = myAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(i);

        String fromUserId = messages.getFrom();
        String fromMsgType = messages.getType();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("profile_image")) {
                    String receiverImage = dataSnapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(receiverImage).into(messageViewHolder.receiverProfileImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMsgType.equals("text")) {
            messageViewHolder.receiverMsgTv.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMsgTv.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(msgSenderId)) {
                messageViewHolder.senderMsgTv.setVisibility(View.VISIBLE);

                messageViewHolder.senderMsgTv.setBackgroundResource(R.drawable.sender_msg_layout);
                messageViewHolder.senderMsgTv.setText(messages.getMessage());
            } else {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMsgTv.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMsgTv.setBackgroundResource(R.drawable.receiver_msg_layout);
                messageViewHolder.receiverMsgTv.setText(messages.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView receiverMsgTv;
        public TextView senderMsgTv;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMsgTv = itemView.findViewById(R.id.sender_msg_text_id);
            receiverMsgTv = itemView.findViewById(R.id.receiver_msg_text_id);
            receiverProfileImage = itemView.findViewById(R.id.msg_receiver_profile_image);
        }
    }
}
















