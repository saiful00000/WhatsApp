package com.example.whatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.whatsapp.utilitys.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View chatFragmentView;
    private RecyclerView chatListRv;

    private DatabaseReference chatReference, usersReference;
    private FirebaseAuth myAuth;


    private String currentUserId;
    private String image = "default_image";

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatFragmentView = inflater.inflate(R.layout.fragment_chats, container, false);

        chatListRv = chatFragmentView.findViewById(R.id.chat_list_rv_id);

        chatListRv.setLayoutManager(new LinearLayoutManager(getContext()));


        myAuth = FirebaseAuth.getInstance();

        currentUserId = myAuth.getCurrentUser().getUid().toString();

        chatReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        return chatFragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options
                = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatReference, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, ChatViewHolde> adapter
                = new FirebaseRecyclerAdapter<Contacts, ChatViewHolde>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolde holder, int position, @NonNull Contacts model) {

                final String usersId = getRef(position).getKey();

                usersReference.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profile_image")) {
                                image = dataSnapshot.child("profile_image").getValue().toString();
                                Picasso.get().load(image).into(holder.userImageView);
                            }

                            final String name = dataSnapshot.child("name").getValue().toString();
                            final String status = dataSnapshot.child("status").getValue().toString();

                            holder.usernameTv.setText(name);
                            holder.userStatusTv.setText("");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("visitUserId", usersId);
                                    intent.putExtra("visitUserName", name);
                                    intent.putExtra("visitUserImage", image);
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public ChatViewHolde onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sample, viewGroup, false);

                return new ChatViewHolde(view);

            }
        };

        chatListRv.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatViewHolde extends RecyclerView.ViewHolder {
        CircleImageView userImageView;
        TextView usernameTv, userStatusTv;


        public ChatViewHolde(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.users_profile_image);
            usernameTv = itemView.findViewById(R.id.user_profile_name);
            userStatusTv = itemView.findViewById(R.id.user_status);
        }
    }
}
