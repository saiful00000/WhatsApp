package com.example.whatsapp;


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


public class ContactsFragment extends Fragment {

    private View contactsView;
    private RecyclerView contactListRv;

    private DatabaseReference contactsReference, usersReference;
    private FirebaseAuth myAuth;

    private String currentUserId;

    public ContactsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactListRv = contactsView.findViewById(R.id.contactlist_recycler_view_id);
        contactListRv.setLayoutManager(new LinearLayoutManager(getContext()));

        myAuth = FirebaseAuth.getInstance();
        currentUserId = myAuth.getCurrentUser().getUid().toString();

        contactsReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsReference, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                String userIds = getRef(position).getKey();

                usersReference.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("profile_image")) {
                            String image = dataSnapshot.child("profile_image").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                            Picasso.get().load(image).into(holder.userImageView);
                            holder.usernameTv.setText(name);
                            holder.userStatusTv.setText(status);
                        } else {
                            String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                            holder.usernameTv.setText(name);
                            holder.userStatusTv.setText(status);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_layout, viewGroup, false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        contactListRv.setAdapter(adapter);
        adapter.startListening();

    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTv, userStatusTv;
        CircleImageView userImageView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.user_profile_imageview_id);
            usernameTv = itemView.findViewById(R.id.user_name_tv_id);
            userStatusTv = itemView.findViewById(R.id.user_status_tv_id);
        }
    }
}









