package my.dinesh.hungervalley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddReviewActivity extends AppCompatActivity {

    int flags;
    private Toolbar toolbar;
    String restauratId, uId, text;
    RatingBar ratingbar;
    Button btn_submit;
    EditText et_review;
    DatabaseReference mReviewDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        flags = getWindow().getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().setStatusBarColor(Color.WHITE);

        toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();

        restauratId = intent.getStringExtra("restauranr_id");

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        toolbar.setTitleTextColor(getColor(R.color.black));
        getSupportActionBar().setTitle("Add a Review");

        ratingbar = (RatingBar) findViewById(R.id.ratingBar);
        btn_submit = findViewById(R.id.btn_submit);
        et_review = findViewById(R.id.et_review);

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        uId = (shared.getString("user_id", ""));

        FirebaseApp.initializeApp(this);

        mReviewDatabase = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Reviews");
        mReviewDatabase.keepSynced(true);

        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (ratingbar.getRating() == 0.0) {

                    Toast.makeText(getApplicationContext(), "give a rating", Toast.LENGTH_SHORT).show();

                } else {

                    text = et_review.getText().toString();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("rating", ratingbar.getRating());
                    map.put("review", text);

                    mReviewDatabase.child(uId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(AddReviewActivity.this, "Review Submitted Successfully", Toast.LENGTH_SHORT).show();

                                finish();

                            } else {
                                Toast.makeText(AddReviewActivity.this, "Something Went Wrong, Try Again.", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }
            }

        });


    }
}