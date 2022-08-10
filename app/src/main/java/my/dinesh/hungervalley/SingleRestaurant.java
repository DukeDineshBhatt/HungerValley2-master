package my.dinesh.hungervalley;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;

public class SingleRestaurant extends AppCompatActivity {

    private Toolbar toolbar;
    String restauratId;
    ImageView header_image;
    DatabaseReference mRestaurantDatabase;
    DatabaseReference mMenuDatabase;
    DatabaseReference mCartDatabase, mAdminDatabase;
    TextView txt_title, txt_type, txt_res_add;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;
    LinearLayout cartLayout, layout_rating, rating_layout;
    TextView price, rating, status;

    boolean isRestaurantOff = false;
    String uId;
    int count;
    EditText searchview;
    int totalPrice = 0;
    String bookskey, reasonString;
    boolean isOn;
    boolean isFoodAvailabe = true;
    String pricee;
    mainAdapter adapter;

    public static final String MY_PREFS_NAME = "HungerValleyCart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        header_image = (ImageView) findViewById(R.id.headerimage);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_type = (TextView) findViewById(R.id.txt_type);
        txt_res_add = (TextView) findViewById(R.id.restaurant_add);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        cartLayout = (LinearLayout) findViewById(R.id.cart_layout);
        layout_rating = (LinearLayout) findViewById(R.id.layout_rating);
        //item_count = (TextView) findViewById(R.id.item_count);
        price = (TextView) findViewById(R.id.price);
        rating = (TextView) findViewById(R.id.rating);
        status = (TextView) findViewById(R.id.status);
        searchview = findViewById(R.id.searchView);
        rating_layout = findViewById(R.id.rating_layout);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        restauratId = intent.getStringExtra("restauranr_id");
        getSupportActionBar().setTitle(restauratId);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        uId = (shared.getString("user_id", ""));

        mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

        mAdminDatabase = FirebaseDatabase.getInstance().getReference().child("Admin").child("Logout");

        /*final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(restauratId);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });*/


        mAdminDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("status").getValue().toString().equals("on")) {

                    isOn = true;
                } else {

                    isOn = false;

                    if (dataSnapshot.child("Reason").exists()) {

                        reasonString = dataSnapshot.child("Reason").getValue().toString();

                    } else {
                        reasonString = " ";

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRestaurantDatabase = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId);
        mRestaurantDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String banner_url = dataSnapshot.child("Banner").getValue().toString();
                String type = dataSnapshot.child("Restaurant_type").getValue().toString();
                String res_address = dataSnapshot.child("Address").getValue().toString();

                DecimalFormat df = new DecimalFormat("0.0");

                String Srating = dataSnapshot.child("Rating").getValue().toString();

                if (dataSnapshot.child("Status").exists()) {

                    isRestaurantOff = true;
                    status.setVisibility(View.VISIBLE);

                }

                if (dataSnapshot.child("Reason").exists()) {

                    final String reason = dataSnapshot.child("Reason").getValue().toString();
                    status.setText(reason);


                }

                txt_title.setText(restauratId);
                txt_type.setText(type);
                txt_res_add.setText(res_address);
                rating.setText(df.format(dataSnapshot.child("Rating").getValue()));

                float a = Float.parseFloat(Srating);

                if (a > 4.0) {

                    layout_rating.setBackgroundResource(R.drawable.star_bg);
                } else if (a > 3.0) {

                    layout_rating.setBackgroundResource(R.drawable.star_bg_two);
                } else {

                    layout_rating.setBackgroundResource(R.drawable.star_bg_three);
                }

                Glide.with(getApplicationContext()).
                        load(banner_url)
                        .into(header_image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rating_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SingleRestaurant.this, ReviewActivity.class);
                intent.putExtra("res_id", restauratId);
                startActivity(intent);

            }
        });

        mMenuDatabase = FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Menu");

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);


        FirebaseRecyclerOptions<MenuModel> options =
                new FirebaseRecyclerOptions.Builder<MenuModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Menu"), MenuModel.class)
                        .build();

        adapter = new mainAdapter(options);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        searchview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                processsearch(editable.toString());
            }
        });


    }

    private void processsearch(String s) {
        FirebaseRecyclerOptions<MenuModel> options1 =
                new FirebaseRecyclerOptions.Builder<MenuModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Restaurants").child(restauratId).child("Menu").orderByChild("LowerCase_FoodName").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "\uf8ff"), MenuModel.class)
                        .build();

        adapter = new mainAdapter(options1);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.startListening();
        recyclerView.setAdapter(adapter);


    }


    @Override
    protected void onRestart() {

        super.onRestart();
        adapter.startListening();

    }



    public class mainAdapter extends FirebaseRecyclerAdapter<MenuModel, mainAdapter.myviewholder> {

        public mainAdapter(@NonNull FirebaseRecyclerOptions<MenuModel> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull MenuModel model) {

            holder.setIsRecyclable(false);
            holder.name.setText(model.getFoodName());

            pricee = Integer.toString(model.getPrice());
            String type = model.getType();

            holder.price.setText(pricee);

            if (isRestaurantOff == true) {

                holder.main_view.setAlpha(0.6f);
            } else {

                holder.main_view.setBackgroundColor(Color.TRANSPARENT);
            }

            if (model.getStatus() != null) {
                isFoodAvailabe = false;
                holder.add.setText(model.getStatus());
                holder.add.setEnabled(false);
                holder.main_view.setAlpha(0.6f);

            }

            mCartDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(restauratId).child(model.getFoodName()).exists()) {

                        holder.layout_button.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);

                        holder.textCount.setText(dataSnapshot.child(restauratId).child(model.getFoodName()).child("quantity").getValue().toString());
                        //int count = Integer.parseInt(String.valueOf(viewHolder.textCount.getText()));

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (!type.equals("Non-Veg")) {

                Glide.with(holder.type_image.getContext()).load(R.drawable.veg).into(holder.type_image);


            } else {

                Glide.with(holder.type_image.getContext()).load(R.drawable.non_veg).into(holder.type_image);

            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            mCartDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {

                        cartLayout.setVisibility(View.VISIBLE);

                        for (DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()) {

                            if (!uniqueKeySnapshot.getKey().equals("Total price")) {

                                bookskey = uniqueKeySnapshot.getKey();

                            }

                        }

                        Log.d("DINESH KEY", bookskey);
                        pricee = Integer.toString(model.getPrice());

                        holder.add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (isOn) {


                                    if (isRestaurantOff == false) {


                                        if (restauratId.equals(bookskey)) {

                                            cartLayout.setClickable(false);

                                            progressBar.setVisibility(View.VISIBLE);

                                            holder.layout_button.setVisibility(View.VISIBLE);
                                            holder.add.setVisibility(View.GONE);

                                            int count = Integer.parseInt(String.valueOf(holder.textCount.getText()));

                                            mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                            HashMap<String, Object> cartMap = new HashMap<>();
                                            cartMap.put("pName", model.getFoodName());
                                            cartMap.put("price", model.getPrice());
                                            cartMap.put("quantity", count);
                                            cartMap.put("Type", model.getType());


                                            mCartDatabase.child("User View").child(uId).child(restauratId).child(model.getFoodName())
                                                    .updateChildren(cartMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {

                                                                mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                                                mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                        totalPrice = model.getPrice() + Integer.parseInt(dataSnapshot.child("Total price").getValue().toString());

                                                                        mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));

                                                                        cartLayout.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {

                                                                                Intent intent = new Intent(SingleRestaurant.this, CartActivity.class);
                                                                                startActivity(intent);

                                                                            }
                                                                        });

                                                                        progressBar.setVisibility(View.GONE);

                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        progressBar.setVisibility(View.GONE);
                                                                    }
                                                                });


                                                            }

                                                        }
                                                    });


                                        }


                                    } else {

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SingleRestaurant.this);
                                        builder1.setTitle("This Restaurant is Currently not accepting orders.");
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "Ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });


                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }


                                } else {

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SingleRestaurant.this);
                                    builder1.setTitle("Currently not accepting orders");
                                    builder1.setMessage(reasonString);
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });


                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                }

                            }
                        });


                        holder.buttonInc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                progressBar.setVisibility(View.VISIBLE);
                                cartLayout.setClickable(false);

                                count = Integer.parseInt(String.valueOf(holder.textCount.getText()));
                                count++;


                                mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                HashMap<String, Object> cartMap = new HashMap<>();
                                cartMap.put("pName", model.getFoodName());
                                cartMap.put("price", model.getPrice() * count);
                                cartMap.put("quantity", count);
                                cartMap.put("Type", model.getType());

                                mCartDatabase.child("User View").child(uId).child(restauratId).child(model.getFoodName())
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    pricee = Integer.toString(model.getPrice());

                                                    mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                                    mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            totalPrice = model.getPrice() + Integer.parseInt(dataSnapshot.child("Total price").getValue().toString());

                                                            mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));
                                                            holder.textCount.setText(String.valueOf(count));
                                                            progressBar.setVisibility(View.GONE);
                                                            cartLayout.setClickable(true);


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                }

                                            }
                                        });

                            }
                        });

                        holder.buttonDec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                progressBar.setVisibility(View.VISIBLE);
                                cartLayout.setClickable(false);

                                count = Integer.parseInt(String.valueOf(holder.textCount.getText()));

                                if (count == 1) {

                                    holder.layout_button.setVisibility(View.GONE);
                                    holder.add.setVisibility(View.VISIBLE);

                                    mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                    mCartDatabase.child(restauratId).child(model.getFoodName()).removeValue();

                                    mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            totalPrice = -(model.getPrice() - Integer.parseInt(dataSnapshot.child("Total price").getValue().toString()));

                                            mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));

                                            progressBar.setVisibility(View.GONE);
                                            cartLayout.setClickable(true);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                } else if (count > 0) {

                                    count--;


                                    mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List");


                                    HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("pName", model.getFoodName());
                                    cartMap.put("price", model.getPrice() * count);
                                    cartMap.put("quantity", count);
                                    cartMap.put("Type", model.getType());

                                    mCartDatabase.child("User View").child(uId).child(restauratId).child(model.getFoodName())
                                            .updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {


                                                        mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                                        mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                totalPrice = -(model.getPrice() - Integer.parseInt(dataSnapshot.child("Total price").getValue().toString()));

                                                                mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));
                                                                holder.textCount.setText(String.valueOf(count));

                                                                progressBar.setVisibility(View.GONE);
                                                                cartLayout.setClickable(true);

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                }
                                            });

                                }

                            }
                        });


                    } else {

                        SharedPreferences mPrefs = getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putString("restaurant", restauratId);
                        editor.commit();

                        cartLayout.setVisibility(View.GONE);

                        holder.add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (isRestaurantOff == false) {

                                    if (isOn) {

                                        progressBar.setVisibility(View.VISIBLE);
                                        cartLayout.setClickable(false);

                                        holder.layout_button.setVisibility(View.VISIBLE);
                                        holder.add.setVisibility(View.GONE);

                                        int count = Integer.parseInt(String.valueOf(holder.textCount.getText()));

                                        mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                        pricee = Integer.toString(model.getPrice());
                                        HashMap<String, Object> cartMap = new HashMap<>();
                                        cartMap.put("pName", model.getFoodName());
                                        cartMap.put("price", model.getPrice());
                                        cartMap.put("quantity", count);
                                        cartMap.put("Type", model.getType());

                                        mCartDatabase.child("User View").child(uId).child(restauratId).child(model.getFoodName())
                                                .updateChildren(cartMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                                            mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                                                    mCartDatabase.child("Total price").setValue(model.getPrice());

                                                                    cartLayout.setVisibility(View.VISIBLE);
                                                                    progressBar.setVisibility(View.GONE);
                                                                    cartLayout.setClickable(true);

                                                                    cartLayout.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {

                                                                            Intent intent = new Intent(SingleRestaurant.this, CartActivity.class);
                                                                            startActivity(intent);

                                                                        }
                                                                    });


                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                }
                                                            });


                                                        }

                                                    }
                                                });


                                    } else {

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SingleRestaurant.this);
                                        builder1.setTitle("Currently not accepting orders");
                                        builder1.setMessage(reasonString);
                                        builder1.setCancelable(true);

                                        builder1.setPositiveButton(
                                                "Ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });


                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();

                                    }

                                } else {

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SingleRestaurant.this);
                                    builder1.setTitle("This Restaurant is Currently not accepting orders");
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });


                                    AlertDialog alert11 = builder1.create();
                                    alert11.show();

                                }


                            }
                        });

                        holder.buttonInc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                progressBar.setVisibility(View.VISIBLE);

                                count = Integer.parseInt(String.valueOf(holder.textCount.getText()));
                                count++;


                                mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                HashMap<String, Object> cartMap = new HashMap<>();
                                cartMap.put("pName", model.getFoodName());
                                cartMap.put("price", Integer.parseInt(pricee) * count);
                                cartMap.put("quantity", count);
                                cartMap.put("Type", model.getType());

                                mCartDatabase.child("User View").child(uId).child(restauratId).child(model.getFoodName())
                                        .updateChildren(cartMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                                    mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            totalPrice = Integer.parseInt(pricee) + Integer.parseInt(dataSnapshot.child("Total price").getValue().toString());

                                                            mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));
                                                            holder.textCount.setText(String.valueOf(count));

                                                            progressBar.setVisibility(View.GONE);

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    });

                                                }

                                            }
                                        });

                            }
                        });

                        holder.buttonDec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                progressBar.setVisibility(View.VISIBLE);

                                count = Integer.parseInt(String.valueOf(holder.textCount.getText()));

                                if (count == 1) {

                                    holder.layout_button.setVisibility(View.GONE);
                                    holder.add.setVisibility(View.VISIBLE);

                                    mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                    mCartDatabase.child(restauratId).child(model.getFoodName()).removeValue();

                                    mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            totalPrice = -(Integer.parseInt(pricee) - Integer.parseInt(dataSnapshot.child("Total price").getValue().toString()));

                                            mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));
                                            progressBar.setVisibility(View.GONE);


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });

                                } else if (count > 0) {

                                    count--;

                                    mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List");


                                    HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("pName", model.getFoodName());
                                    cartMap.put("price", Integer.parseInt(pricee) * count);
                                    cartMap.put("quantity", count);
                                    cartMap.put("Type", model.getType());

                                    mCartDatabase.child("User View").child(uId).child(restauratId).child(model.getFoodName())
                                            .updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {


                                                        mCartDatabase = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uId);

                                                        mCartDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                totalPrice = -(Integer.parseInt(pricee) - Integer.parseInt(dataSnapshot.child("Total price").getValue().toString()));

                                                                mCartDatabase.child("Total price").setValue(String.valueOf(totalPrice));
                                                                holder.textCount.setText(String.valueOf(count));
                                                                progressBar.setVisibility(View.GONE);

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }

                                                }
                                            });

                                }

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
        public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_menu_item, parent, false);
            return new myviewholder(view);
        }

        class myviewholder extends RecyclerView.ViewHolder {
            View mView;

            Button buttonInc, buttonDec, add;
            TextView textCount, price, name;
            ImageView type_image;
            LinearLayout layout_button;
            LinearLayout main_view;

            public myviewholder(@NonNull View itemView) {
                super(itemView);

                buttonInc = (Button) itemView.findViewById(R.id.btn_add);
                buttonDec = (Button) itemView.findViewById(R.id.btn_minus);
                add = (Button) itemView.findViewById(R.id.add);
                textCount = (TextView) itemView.findViewById(R.id.text);
                price = (TextView) itemView.findViewById(R.id.price);
                type_image = (ImageView) itemView.findViewById(R.id.type_image);
                layout_button = (LinearLayout) itemView.findViewById(R.id.layout_button);
                main_view = itemView.findViewById(R.id.main_view);
                name = itemView.findViewById(R.id.name);


                mView = itemView;

            }


        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(SingleRestaurant.this, MainActivity.class);
        startActivity(intent);
        finish();


    }
}
