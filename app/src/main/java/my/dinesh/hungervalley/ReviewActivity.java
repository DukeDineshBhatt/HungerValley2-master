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
    String restauratId;
    RecyclerView recyclerView;
    DatabaseReference mReviewDatabase;
    LinearLayoutManager linearLayoutManager;
    mainAdapter adapter1;

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

        FirebaseApp.initializeApp(this);

        mReviewDatabase = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Reviews");
        mReviewDatabase.keepSynced(true);

        linearLayoutManager = new

                LinearLayoutManager(ReviewActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<MyDataSetGet> options1 =
                new FirebaseRecyclerOptions.Builder<MyDataSetGet>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Reviews"), MyDataSetGet.class)
                        .build();

        adapter1 = new mainAdapter(options1);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter1);

    }


    public class mainAdapter extends FirebaseRecyclerAdapter<MyDataSetGet, mainAdapter.myviewholder> {

        public mainAdapter(@NonNull FirebaseRecyclerOptions<MyDataSetGet> options1) {
            super(options1);
        }

        @Override
        protected void onBindViewHolder(@NonNull mainAdapter.myviewholder holder, int position, @NonNull MyDataSetGet model) {

            //holder.name.setText(model.getRestaurant_name());
            //holder.type.setText(model.getRestaurant_type());

            // holder.rating.setText(rating);


            /*holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mCartDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()) {

                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Replace cart items?")
                                        .setMessage("Your cart have some items. Do you want to discard and change the restaurant?")
                                        .setNegativeButton(android.R.string.no, null)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface arg0, int arg1) {
                                                MainActivity.super.onBackPressed();

                                                mCartDatabase.child(uId).removeValue();

                                                Intent chatIntent = new Intent(MainActivity.this, SingleRestaurant.class);
                                                chatIntent.putExtra("restauranr_id", model.Restaurant_name);
                                                startActivity(chatIntent);
                                            }
                                        }).create().show();


                            } else {

                                Intent chatIntent = new Intent(MainActivity.this, SingleRestaurant.class);
                                chatIntent.putExtra("restauranr_id", model.Restaurant_name);
                                startActivity(chatIntent);


                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            });
*/

        }

        @NonNull
        @Override
        public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_rating, parent, false);
            return new myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {
            View mView;
            LinearLayout layout_discount, layout_rating;
            TextView rating, status, name, type;
            LinearLayout main_view;
            ImageView image;

            public myviewholder(@NonNull View itemView) {
                super(itemView);

                rating = (TextView) itemView.findViewById(R.id.rating);
                name = (TextView) itemView.findViewById(R.id.name);

                mView = itemView;

            }

        }
    }
}