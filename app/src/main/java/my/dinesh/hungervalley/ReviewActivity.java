package my.dinesh.hungervalley;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    int flags;
    RelativeLayout reviewLayout;
    String restauratId;
    RecyclerView recyclerView;
    DatabaseReference usersRef;
    FirebaseDatabase database;
    myadapter adapter1;
    private LinearLayoutManager linearLayoutManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        flags = getWindow().getDecorView().getSystemUiVisibility(); // get current flag
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;   // add LIGHT_STATUS_BAR to flag
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().setStatusBarColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();

        restauratId = intent.getStringExtra("res_id");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        toolbar.setTitleTextColor(getColor(R.color.black));
        getSupportActionBar().setTitle(restauratId);

        recyclerView = (RecyclerView) findViewById(R.id.upload_list);
        reviewLayout = findViewById(R.id.reviewLayout);

        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance();

        usersRef = database.getReference("Users");


        linearLayoutManager = new

                LinearLayoutManager(ReviewActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<ReviewDataSetGet> options1 =
                new FirebaseRecyclerOptions.Builder<ReviewDataSetGet>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Reviews"), ReviewDataSetGet.class)
                        .build();


        adapter1 = new myadapter(options1);
        recyclerView.setNestedScrollingEnabled(false);
        adapter1.startListening();
        recyclerView.setAdapter(adapter1);

        reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chatIntent = new Intent(ReviewActivity.this, AddReviewActivity.class);
                chatIntent.putExtra("restauranr_id", restauratId);
                startActivity(chatIntent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    public class myadapter extends FirebaseRecyclerAdapter<ReviewDataSetGet, myadapter.myviewholder> {
        public myadapter(@NonNull FirebaseRecyclerOptions<ReviewDataSetGet> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull myadapter.myviewholder holder, int position, @NonNull ReviewDataSetGet model) {

            holder.setIsRecyclable(false);
            holder.rating.setText(String.valueOf(model.getRating()));
            holder.review.setText(model.getReview());

            float a = Float.parseFloat(String.valueOf(model.getRating()));

            if (a > 4.0) {

                holder.layoutRating.setBackgroundResource(R.drawable.star_bg);
            } else if (a > 3.0) {

                holder.layoutRating.setBackgroundResource(R.drawable.star_bg_two);
            } else {

                holder.layoutRating.setBackgroundResource(R.drawable.star_bg_three);
            }


            usersRef.child(getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    holder.name.setText(dataSnapshot.child("username").getValue().toString());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public myadapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rating, parent, false);
            return new myadapter.myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {

            TextView name, review, rating;
            LinearLayout layoutRating;

            public myviewholder(@NonNull View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.name);
                review = (TextView) itemView.findViewById(R.id.review);
                rating = (TextView) itemView.findViewById(R.id.rating);
                layoutRating = (LinearLayout) itemView.findViewById(R.id.layout_rating);

            }
        }
    }

}