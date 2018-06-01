package com.vijayjaidewan01vivekrai.firestore_github;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private Button save, retrieve, deleteAll, deleteTitle, updateTitle;
    private TextView textView, textViewDynamic;
    private EditText editTextTitle, editTextDes;
    public static final String KEY_TITLE = "title";
    public static final String DESCRIPTION = "description";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference reference = db.collection("NoteBook").document("First Note");
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDes = findViewById(R.id.edit_text_des);
        editTextTitle = findViewById(R.id.edit_text_title);
        textView = findViewById(R.id.text_view);
        textViewDynamic = findViewById(R.id.text_view_dynamic);
        save = findViewById(R.id.save);
        retrieve = findViewById(R.id.retrieve);
        deleteAll = findViewById(R.id.delete_all);
        deleteTitle = findViewById(R.id.delete_title);
        updateTitle = findViewById(R.id.update_title);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new ProgressDialog(MainActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Loading...");
                dialog.show();

                Map<String, Object> note = new HashMap<>();
                note.put(KEY_TITLE, editTextTitle.getText().toString());
                note.put(DESCRIPTION, editTextDes.getText().toString());

                db.collection("NoteBook").document("First Note").set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Saved Successfully!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.hide();
                        Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Retrieving...");
                dialog.setMessage("Please wait while we retrieve data");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Data Retrieved succeefully!", Toast.LENGTH_SHORT).show();
                            textView.setText("Title: "+ documentSnapshot.getString(KEY_TITLE)+"\nDescription: "+ documentSnapshot.getString(DESCRIPTION));

                        }else {
                            dialog.hide();
                            Toast.makeText(MainActivity.this, "Document doesn't exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.hide();
                        Toast.makeText(MainActivity.this, "Unable to retrieve, some error occured!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        updateTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> noteTitle = new HashMap<>();
                noteTitle.put(KEY_TITLE, editTextTitle.getText().toString());
                reference.update(noteTitle);
            }
        });

        deleteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.update(KEY_TITLE, FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Deleted Successfull!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    textViewDynamic.setText("Title: "+ documentSnapshot.getString(KEY_TITLE)+"\nDescription: "+documentSnapshot.getString(DESCRIPTION));

                }else {
                    textViewDynamic.setText("");
                    textView.setText("");

                }

                if (e != null){
                    Toast.makeText(MainActivity.this, "Error while loading.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
