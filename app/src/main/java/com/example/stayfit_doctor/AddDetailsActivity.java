package com.example.stayfit_doctor;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stayfit_doctor.databinding.ActivityAddDetailsBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class AddDetailsActivity extends AppCompatActivity {

    ActivityAddDetailsBinding binding;
    ImageView profileImage;
    DatabaseReference userRef;
    FirebaseUser mUser;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        profileImage = binding.profileImage;
        username = getIntent().getStringExtra("username");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference().child("doctors").child(mUser.getUid());
        userRef.child("username").setValue(username);

        userRef = FirebaseDatabase.getInstance().getReference().child("doctors").child(username);

        binding.buttonSave.setOnClickListener(view1 -> { verifyDataAndUpload(); });

        profileImage.setOnClickListener(view12 -> selectImageIntent());

    }
    private void selectImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri imageUri = data.getData();
                        binding.profileImage.setImageURI(imageUri);
                    }
                }
            });

    private void verifyDataAndUpload() {
        if(verifyData()){
            uploadData();
        }else {
            Toast.makeText(this, "Enter the details Correctly", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadData() {
        String name = binding.edtName.getText().toString();
        String edu = binding.edtEducation.getText().toString();
        String experience = binding.edtExperience.getText().toString();
        String speciality = binding.edtSpeciality.getText().toString();
        String timeSlots = binding.edtTimeSlots.getText().toString();
        String languages = binding.edtLanguages.getText().toString();

        StringBuilder availableDays= new StringBuilder(" ");
        ArrayList<String> selectedDays = new ArrayList<>();
        if (binding.checkBoxMonday.isChecked()) {
            selectedDays.add(binding.checkBoxMonday.getText().toString());
        }
        if (binding.checkBoxTuesday.isChecked()) {
            selectedDays.add(binding.checkBoxTuesday.getText().toString());
        }
        if (binding.checkBoxWednesday.isChecked()) {
            selectedDays.add(binding.checkBoxWednesday.getText().toString());
        }
        if (binding.checkBoxThursday.isChecked()) {
            selectedDays.add(binding.checkBoxThursday.getText().toString());
        }
        if (binding.checkBoxFriday.isChecked()) {
            selectedDays.add(binding.checkBoxFriday.getText().toString());
        }
        if (binding.checkBoxSaturday.isChecked()) {
            selectedDays.add(binding.checkBoxSaturday.getText().toString());
        }
        for(int i = 0; i < selectedDays.size()-1; i++){
            availableDays.append(" | ").append(selectedDays.get(i));
        }
        availableDays.append(" | ").append(selectedDays.get(selectedDays.size() - 1));

        if (profileImage.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();

            // Get a reference to Firebase Storage and create a unique file name for the image
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String fileName = UUID.randomUUID().toString();
            StorageReference imageRef = storageRef.child("images/" + fileName + ".jpg");

            // Convert the bitmap image to a byte array and upload it to Firebase Storage
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageRef.putBytes(data);

            // After the upload is complete, get the download URL of the image and store it in Firebase Realtime Database
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            // Store the image URL in Firebase Realtime Database
                            userRef.child("imageUrl")
                                    .setValue(imageUrl);

                            Toast.makeText(AddDetailsActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(AddDetailsActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        }
                    });
                }
            });
        }
        userRef.child("name").setValue(name);
        userRef.child("education").setValue(edu);
        userRef.child("experience").setValue(experience);
        userRef.child("speciality").setValue(speciality);
        userRef.child("rating").setValue(0);
        userRef.child("availableDays").setValue(availableDays);
        userRef.child("availableTime").setValue(timeSlots);
        userRef.child("languagesSpoken").setValue(languages);
    }

    private boolean verifyData() {
        String edu = binding.edtEducation.getText().toString();
        String experience = binding.edtExperience.getText().toString();
        String name = binding.edtName.getText().toString();
        String speciality = binding.edtSpeciality.getText().toString();
        String timeSlots = binding.edtTimeSlots.getText().toString();
        String languages = binding.edtLanguages.getText().toString();
        boolean result = true;
        if(edu.length() == 0 ){
            result = false;
            binding.edtEducation.setError("Please enter correct date of birth");
        } else if (experience.length() == 0) {
            result = false;
            binding.edtExperience.setError("Please enter correct weight");
        }else if (speciality.length() == 0) {
            result = false;
            binding.edtSpeciality.setError("Please enter correct height");
        }else if (name.length()==0) {
            result = false;
            binding.edtName.setError("Please enter correct name");
        }else if (timeSlots.length()==0) {
            result = false;
            binding.edtTimeSlots.setError("Please enter correct name");
        }else if (languages.length()==0) {
            result = false;
            binding.edtTimeSlots.setError("Please enter correct name");
        }
        return result;
    }

}