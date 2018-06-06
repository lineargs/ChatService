package com.lineargs.chatservice.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lineargs.chatservice.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileEditActivity extends AppCompatActivity {

    private static final int RC_PICK_IMAGE = 333;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.editProfileName)
    EditText editProfileName;
    @BindView(R.id.scrim)
    View scrim;
    @BindView(R.id.profileImage)
    ImageView profileImage;
    @BindView(R.id.editProfileImage)
    ImageView editProfileImage;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);
        setupActionBar();
        photoUri = Uri.parse("");
        //Firebase Auth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //Firebase Cloud Storage
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("profile_pictures");
        editProfileName.setText(firebaseUser.getDisplayName());
    }

    @OnClick(R.id.editProfileImage)
    public void attachImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                showLoading();
                Uri uri = data.getData();
                final StorageReference photoReference = storageReference.child(uri.getLastPathSegment());
                photoReference.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                photoUri = uri;
                                showEditImage();
                                Toast.makeText(ProfileEditActivity.this, R.string.picture_uploaded_toast, Toast.LENGTH_SHORT).show();
                                Glide.with(profileImage.getContext())
                                        .load(uri)
                                        .into(profileImage);
                                scrim.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        }
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //TODO Update profile (Display name and Image)
                if (!Uri.EMPTY.equals(photoUri) && !TextUtils.isEmpty(editProfileName.getText().toString())) {
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(photoUri)
                            .setDisplayName(editProfileName.getText().toString())
                            .build();
                    if (firebaseUser != null) {
                        firebaseUser.updateProfile(userProfileChangeRequest)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileEditActivity.this, R.string.profile_updated_toast, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        editProfileImage.setVisibility(View.GONE);
    }

    private void showEditImage() {
        progressBar.setVisibility(View.GONE);
        editProfileImage.setVisibility(View.VISIBLE);
    }
}
