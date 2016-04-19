package com.cirs.reportit.ui.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.utils.CircularNetworkImageView;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.ErrorUtils;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyImageRequest;
import com.cirs.reportit.utils.VolleyRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.*;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    @NotEmpty
    private EditText edtFirstname;

    @NotEmpty
    private EditText edtLastname;

    @NotEmpty
    private EditText edtDOB;

    @NotEmpty
    @Email
    private EditText edtEmail;

    @NotEmpty
    @Pattern(regex = Constants.REGEX_PHONE, message = "Invalid mobile number")
    private EditText edtPhone;

    @NotEmpty
    private EditText edtGender;

    private Validator validator;

    private Context mActivityContext = this;

    private FloatingActionMenu floatingActionMenu;

    private FloatingActionButton fabCamera;

    private FloatingActionButton fabGallery;

    private CircularNetworkImageView imgProfile;

    private TextView txtRemove;

    private DatePickerDialog datePickerDialog;

    private boolean isImageSet = false;

    private AlertDialog.Builder gendersDialog;

    private Bitmap bmpProfilePic;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        validator = new Validator(mActivityContext);

        initializeViews();
        setDatePicker();
        setListeners();
        createGendersDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_submit:
                validator.validate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onValidationSucceeded() {
        if (!isImageSet) {
            Toast.makeText(mActivityContext, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        final CIRSUser user;
        user = ReportItApplication.getCirsUser();
        user.setFirstName(edtFirstname.getText().toString().trim());
        user.setLastName(edtLastname.getText().toString().trim());
        user.setGender(edtGender.getText().toString().trim());
        user.setDob(edtDOB.getText().toString().trim());
        user.setEmail(edtEmail.getText().toString().trim());
        user.setPhone(edtPhone.getText().toString().trim());
        new VolleyRequest<CIRSUser>(mActivityContext).makeGsonRequest(
                Request.Method.PUT,
                Generator.getURLtoEditUser(user),
                user,
                new Response.Listener<CIRSUser>() {
                    @Override
                    public void onResponse(CIRSUser response) {
                        bmpProfilePic = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
                        setAndSaveImage(bmpProfilePic);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmpProfilePic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        new VolleyRequest<byte[]>(mActivityContext).makeImageRequest(
                                Generator.getURLtoUploadProfilePic(user),
                                "put",
                                VolleyRequest.FileType.PNG,
                                byteArray,
                                new Response.Listener<Integer>() {
                                    @Override
                                    public void onResponse(Integer response) {
                                        new QueryHelper(mActivityContext).insertOrUpdateCirsUser(user);
                                        saveToSharedPref();
                                        progressDialog.dismiss();
                                        Toast.makeText(mActivityContext, "Profile created", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(CreateProfileActivity.this, HomeActivity.class));
                                        finish();
                                        System.out.println("Response: " + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        error.printStackTrace();
                                        Toast.makeText(mActivityContext, "There was some error uploading image", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        String message = ErrorUtils.parseVolleyError(error);  //In case of existing email
                        Toast.makeText(mActivityContext, message, Toast.LENGTH_LONG).show();
                    }
                },
                CIRSUser.class
        );
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator
                        + Constants.FILE_PATH_PROFILE_PIC);
                cropCapturedImage(Uri.fromFile(file));
            } else if (requestCode == 200) {
                Uri uri = data.getData();
                try {
                    bmpProfilePic = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator
                            + Constants.FILE_PATH_PROFILE_PIC);
                    OutputStream outputStream = new FileOutputStream(file);
                    bmpProfilePic.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    cropCapturedImage(Uri.fromFile(file));
                } catch (Exception e) {
                }
            } else if (requestCode == 300) {
                Bundle extras = data.getExtras();
                bmpProfilePic = extras.getParcelable("data");
                isImageSet = true;
                setAndSaveImage(bmpProfilePic);
            }
        }
    }

    private void initializeViews() {
        progressDialog = new ProgressDialog(mActivityContext);
        progressDialog.setMessage("Creating profile");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        edtFirstname = (EditText) findViewById(R.id.edt_firstname);
        edtLastname = (EditText) findViewById(R.id.edt_lastname);
        edtDOB = (EditText) findViewById(R.id.edt_dob);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPhone = (EditText) findViewById(R.id.edt_mobile);
        edtGender = (EditText) findViewById(R.id.edt_gender);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam_add_pic);
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_from_camera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fab_from_gallery);
        imgProfile = (CircularNetworkImageView) findViewById(R.id.img_profile_pic);
        txtRemove = (TextView) findViewById(R.id.txt_remove);

        CIRSUser user = ReportItApplication.getCirsUser();
        edtFirstname.setText(user.getFirstName());
        edtLastname.setText(user.getLastName());
        edtGender.setText(user.getGender());
        edtDOB.setText(user.getDob());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(user.getPhone());

        isImageSet = true;
        imgProfile.setVisibility(View.VISIBLE);
        txtRemove.setVisibility(View.VISIBLE);
        floatingActionMenu.setVisibility(View.GONE);

        getProfilePicFromServer(user.getId());

    }

    private void getProfilePicFromServer(Long id) {
        String URL = Generator.getURLtoGetUserImage(id);
        ImageLoader imageLoader = VolleyImageRequest.getInstance(mActivityContext).getImageLoader();
        imageLoader.get(URL, ImageLoader.getImageListener(
                imgProfile, R.drawable.ic_my_profile, R.mipmap.ic_no_connection));
        imgProfile.setImageUrl(URL, imageLoader);
    }

    private void setDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final DateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDOB.setText(dateFormatter.format(newDate.getTime()));

            }
        }, calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                edtEmail.requestFocus();
            }
        });
        datePickerDialog.setTitle("Select Date of Birth");
    }

    private void setListeners() {
        edtDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) datePickerDialog.show();
            }
        });
        edtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        edtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gendersDialog.show();
            }
        });

        edtGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) gendersDialog.show();
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory() + File.separator
                        + Constants.FILE_PATH_PROFILE_PIC);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent, 100);
            }
        });

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);
            }
        });

        txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isImageSet = false;
                floatingActionMenu.close(false);
                imgProfile.setVisibility(View.GONE);
                txtRemove.setVisibility(View.GONE);
                floatingActionMenu.setVisibility(View.VISIBLE);
            }
        });

        validator.setValidationListener(this);
    }

    private void setAndSaveImage(Bitmap bitmap) {
        try {
            if (isImageSet) {
                imgProfile.setVisibility(View.VISIBLE);
                txtRemove.setVisibility(View.VISIBLE);
                floatingActionMenu.setVisibility(View.GONE);
            }
            imgProfile.setImageBitmap(bitmap);
            FileOutputStream out;
            out = mActivityContext.openFileOutput(Constants.FILE_PATH_PROFILE_PIC, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
        }
    }

    private void createGendersDialog() {
        gendersDialog = new AlertDialog.Builder(this);
        final ArrayList<String> gendersList = new ArrayList<String>(Arrays.asList("MALE", "FEMALE"));
        ArrayAdapter<String> gendersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gendersList);
        gendersDialog.setTitle("Select Gender").setSingleChoiceItems(gendersAdapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edtGender.setText(gendersList.get(i));
                dialogInterface.dismiss();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                edtDOB.requestFocus();
            }
        }).create();
    }

    private void saveToSharedPref() {
        Prefs.putString(Constants.SPUD_FIRSTNAME, edtFirstname.getText().toString().trim());
        Prefs.putString(Constants.SPUD_LASTNAME, edtLastname.getText().toString().trim());
        Prefs.putString(Constants.SPUD_GENDER, edtGender.getText().toString().trim());
        Prefs.putString(Constants.SPUD_DOB, edtDOB.getText().toString().trim());
        Prefs.putString(Constants.SPUD_EMAIL, edtEmail.getText().toString().trim());
        Prefs.putString(Constants.SPUD_PHONE, edtPhone.getText().toString().trim());
        Prefs.putBoolean(Constants.SPUD_IS_IMAGE_SET, isImageSet);
        Prefs.putBoolean(Constants.SPUD_IS_PROFILE_CREATED, true);
    }

    private void cropCapturedImage(Uri picUri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(picUri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 500);
        cropIntent.putExtra("outputY", 500);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, 300);
    }
}