package com.yehiahd.chatroom;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity {

    private static final int CAMERA_REQUEST = 2222;
    @BindView(R.id.first_name_ET)
    EditText firstNameET;
    @BindView(R.id.last_name_ET)
    EditText lastNameET;
    @BindView(R.id.sign_up_email_ET)
    EditText signUpEmailET;
    @BindView(R.id.sign_up_password_ET)
    EditText signUpPasswordET;
    @BindView(R.id.sign_up_create_btn)
    Button signUpCreateBtn;
    @BindView(R.id.sign_up_progress_bar)
    ProgressBar signUpProgressBar;
    @BindView(R.id.profile_pic)
    ImageView profilePic;


    private Uri uri;
    private String url = "https://firebasestorage.googleapis.com/v0/b/ai-calendar-3ff7b.appspot.com/o/users%2FrhtUJsKz5rgMxVr7yA9U67xUeaN2%2F83403?alt=media&token=a08738ff-1ad6-457e-ab39-d1aabdd9de2b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.sign_up_create_btn)
    public void signUp() {
        if (isValidData(firstNameET, lastNameET, signUpEmailET, signUpPasswordET)) {
            signUpProgressBar.setVisibility(View.VISIBLE);

            final String fName = firstNameET.getText().toString().trim();
            final String lName = lastNameET.getText().toString().trim();
            final String email = signUpEmailET.getText().toString().trim();
            String password = signUpPasswordET.getText().toString();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                User user = new User();
                                user.setEmail(email);
                                user.setfName(fName);
                                user.setlName(lName);
                                user.setUrl(url);
                                uploadUserInfo(user);
                            } else {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void uploadUserInfo(final User user) {

        FirebaseDatabase.getInstance()
                .getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signUpProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("url", user.getUrl());
                            editor.putString("name", user.getfName() + " " + user.getlName());
                            editor.apply();

                            startActivity(new Intent(SignUpActivity.this, ChatActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @OnClick(R.id.profile_pic)
    public void picImage() {
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            try {
                Bitmap capturedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                Bitmap bitmap = Bitmap.createScaledBitmap(capturedImage, 1024, (1024 * capturedImage.getHeight()) / capturedImage.getWidth(), true);
                SignUpActivity.this.uri = getNewURI(this, bitmap);

                Picasso.with(this)
                        .load(uri)
                        .placeholder(R.drawable.progress_placeholder)
                        .transform(new CircleTransform())
                        .into(profilePic);

                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage() {
        FirebaseStorage.getInstance().getReference()
                .child("photos")
                .child(uri.getLastPathSegment())
                .putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            url = task.getResult().getDownloadUrl().toString();
                            Toast.makeText(SignUpActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private Uri getNewURI(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
