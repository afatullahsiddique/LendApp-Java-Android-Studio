package com.example.lendapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Community extends AppCompatActivity {

    private LinearLayout HomeButton, MeButton;
    private EditText searchBar;
    private ImageView searchButton;
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.community);

        HomeButton = findViewById(R.id.home_btn);
        MeButton = findViewById(R.id.me_btn);

        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        loadUsers("");

        HomeButton.setOnClickListener(v -> navigateTo(Home.class));
        MeButton.setOnClickListener(v -> navigateTo(MyAccount.class));

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    private void loadUsers(String query) {
        Query firebaseSearchQuery = databaseReference.orderByChild("email").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(firebaseSearchQuery, User.class)
                        .build();

        adapter = new UserAdapter(options, this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public static class User {
        private String fullName;
        private String phoneNumber;
        private String email;
        private String profilePicture;

        public User() {}

        public User(String fullName, String phoneNumber, String email, String profilePicture) {
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.profilePicture = profilePicture;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getProfilePicture() {
            return profilePicture;
        }

        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }
    }

    public static class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.UserViewHolder> {

        private Community context;

        public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options, Community community) {
            super(options);
            this.context = community;
        }

        @Override
        protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
            holder.fullName.setText(model.getFullName());
            holder.phoneNumber.setText(model.getPhoneNumber());
            holder.email.setText(model.getEmail());
            Glide.with(holder.profileImage.getContext())
                    .load(model.getProfilePicture())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.profileImage);

            holder.itemView.setOnClickListener(v -> context.navigateToUserDetails(model));
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.community_items, parent, false);
            return new UserViewHolder(view);
        }

        public static class UserViewHolder extends RecyclerView.ViewHolder {
            TextView fullName, phoneNumber, email;
            ImageView profileImage;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                fullName = itemView.findViewById(R.id.fullname);
                phoneNumber = itemView.findViewById(R.id.phoneNumber);
                email = itemView.findViewById(R.id.email);
                profileImage = itemView.findViewById(R.id.img);
            }
        }
    }

    private void navigateToUserDetails(User user) {
        Intent intent = new Intent(this, UserDetails.class);
        intent.putExtra("fullName", user.getFullName());
        intent.putExtra("phoneNumber", user.getPhoneNumber());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("profilePicture", user.getProfilePicture());
        startActivity(intent);
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        startActivity(intent);
        finish();
    }
}
