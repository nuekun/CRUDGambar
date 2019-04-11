package com.nue.crudgambar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private Button btnPencarian ;
    private EditText txtPencarian ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnPencarian = findViewById(R.id.btnMainPencarian);
        txtPencarian = findViewById(R.id.txtMainPencarian);
        recyclerView = findViewById(R.id.RecMain);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        ambilData("");


        btnPencarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String Pencarian = txtPencarian.getText().toString();
                ambilData(Pencarian);


            }
        });
        FloatingActionButton cap = findViewById(R.id.cap);
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kamera = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(kamera);

            }
        });



    }

    private void ambilData(String Pencarian) {


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("gambar")
                .orderByChild("nama")
                .startAt(Pencarian).endAt(Pencarian + "\uf8ff");

        FirebaseRecyclerOptions<GambarModel> options =
                new FirebaseRecyclerOptions.Builder<GambarModel>()
                        .setQuery(query, new SnapshotParser<GambarModel>() {
                            @NonNull
                            @Override
                            public GambarModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new GambarModel(snapshot.child("nama").getValue().toString(),
                                        snapshot.child("url").getValue().toString());
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<GambarModel, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gambar, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ViewHolder holder, final int position, GambarModel model) {




                final String IDgambar = getRef(position).getKey();
                holder.setTxtnama(model.getNama());
                holder.setGbr(model.getUrl());

                holder.gbrRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    Intent edit = new Intent(MainActivity.this,EditActivity.class);
                    edit.putExtra("IDgambar",IDgambar);
                    startActivity(edit);

                    }
                });
            }






        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

//
//    private void fetch() {
//
//        Query query = FirebaseDatabase.getInstance()
//                .getReference()
//                .child("gambar");
//
//        FirebaseRecyclerOptions<GambarModel> options =
//                new FirebaseRecyclerOptions.Builder<GambarModel>()
//                        .setQuery(query, new SnapshotParser<GambarModel>() {
//                            @NonNull
//                            @Override
//                            public GambarModel parseSnapshot(@NonNull DataSnapshot snapshot) {
//                                return new GambarModel(snapshot.child("nama").getValue().toString(),
//                                        snapshot.child("url").getValue().toString());
//                            }
//                        })
//                        .build();
//
//        adapter = new FirebaseRecyclerAdapter<GambarModel, ViewHolder>(options) {
//            @Override
//            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.gambar, parent, false);
//
//                return new ViewHolder(view);
//            }
//
//
//            @Override
//            protected void onBindViewHolder(ViewHolder holder, final int position, GambarModel model) {
//
//
//
//
//                final String IDgambar = getRef(position).getKey();
//                holder.setTxtnama(model.getNama());
//                holder.setGbr(model.getUrl());
//
//                holder.gbrRoot.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//
//
//
//
//                    }
//                });
//            }
//
//
//
//
//
//
//        };
//        recyclerView.setAdapter(adapter);
//
//    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtNama;
        public String uri ;
        public RelativeLayout gbrRoot;
        public ImageView gbrPrivew;

        public ViewHolder(View itemView) {
            super(itemView);
            gbrRoot =itemView.findViewById(R.id.rootGambar);
            txtNama = itemView.findViewById(R.id.txtGambarNama);
            gbrPrivew = itemView.findViewById(R.id.gbrGambarPriview);
        }
        public void setTxtnama(String string) {
            txtNama.setText(string);
        }


        public void setGbr(String string) {
            uri = string;
            Picasso.with(MainActivity.this).load(uri).into(gbrPrivew);
        }

    }
}
