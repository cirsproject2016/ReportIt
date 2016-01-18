package com.cirs.reportit.ui.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cirs.reportit.utils.Constants;
import com.example.kshitij.reportit.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity implements Validator.ValidationListener {

    private LinearLayout linearLayout;

    private EditText edtUsername;

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

    private EditText edtGender;

    private List<EditText> views;

    private Boolean inEditMode = false;

    private ActionBar actionBar;

    private Context context = this;

    private Validator validator;

    private MenuItem menuItem;

    private CircleImageView imgProfile;

    private TextView txtRemove;

    private FloatingActionMenu floatingActionMenu;

    private FloatingActionButton fabCamera;

    private FloatingActionButton fabGallery;

    private DatePickerDialog datePickerDialog;

    private SharedPreferences pref;

    private SharedPreferences.Editor editor;

    private boolean isImageSet;

    private AlertDialog.Builder gendersDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        initializeViews();
        validator = new Validator(context);
        pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_USER_DETAILS, 0);
        linearLayout.requestFocus();
        views = new ArrayList<>(Arrays.asList(edtFirstname, edtLastname, edtGender, edtDOB, edtEmail, edtPhone));
        toggleViews(views, false);
        initializeFields();
        setDatePicker();
        setListeners();
        createGendersDialog();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_edit_or_confirm:
                menuItem = item;
                if (!inEditMode) {
                    inEditMode = true;
                    if (isImageSet) {
                        txtRemove.setVisibility(View.VISIBLE);
                        floatingActionMenu.setVisibility(View.GONE);
                    } else {
                        txtRemove.setVisibility(View.GONE);
                        floatingActionMenu.setVisibility(View.VISIBLE);
                    }
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    actionBar.setTitle(getResources().getString(R.string.view_profile_alt_title_edit_profile));
                    toggleViews(views, true);
                    item.setIcon(R.drawable.ic_white_tick);
                    item.setTitle(getResources().getString(R.string.view_profile_menu_item_title_confirm));
                } else {
                    validator.validate();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void toggleViews(List<EditText> views, boolean visibility) {
        for (View v : views) {
            v.setEnabled(visibility);
        }
    }

    @Override
    public void onBackPressed() {
        if (!inEditMode) {
            startActivity(new Intent(ViewProfileActivity.this, HomeActivity.class));
            finish();
        } else
            Toast.makeText(context, "Please save the changes first!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        saveToSharedPref();
        inEditMode = false;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.title_activity_view_profile));
        toggleViews(views, false);
        txtRemove.setVisibility(View.GONE);
        floatingActionMenu.setVisibility(View.GONE);
        menuItem.setIcon(R.drawable.ic_edit);
        menuItem.setTitle(getResources().getString(R.string.view_profile_menu_item_title_edit));
        linearLayout.requestFocus();
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
            isImageSet = true;
            imgProfile.setVisibility(View.VISIBLE);
            txtRemove.setVisibility(View.VISIBLE);
            floatingActionMenu.setVisibility(View.GONE);
            if (requestCode == 100) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                saveImage(bitmap);
            } else if (requestCode == 200) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    saveImage(bitmap);
                } catch (Exception e) {
                }
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        isImageSet = true;
        try {
            FileOutputStream out;
            out = context.openFileOutput(Constants.FILE_PATH_PROFILE_PIC, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
        }
        setProfilePic();
    }

    private void setProfilePic() {
        imgProfile.setVisibility(View.VISIBLE);
        if (inEditMode) {
            txtRemove.setVisibility(View.VISIBLE);
        } else {
            txtRemove.setVisibility(View.GONE);
        }
        floatingActionMenu.setVisibility(View.GONE);
        try {
            FileInputStream fis = context.openFileInput(Constants.FILE_PATH_PROFILE_PIC);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            imgProfile.setImageBitmap(b);
        } catch (Exception e) {
        }
    }

    private void initializeViews() {
        linearLayout = (LinearLayout) findViewById(R.id.lnr_layout);
        edtUsername = (EditText) findViewById(R.id.edt_username);
        edtFirstname = (EditText) findViewById(R.id.edt_firstname);
        edtLastname = (EditText) findViewById(R.id.edt_lastname);
        edtDOB = (EditText) findViewById(R.id.edt_dob);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPhone = (EditText) findViewById(R.id.edt_mobile);
        edtGender = (EditText) findViewById(R.id.edt_gender);
        imgProfile = (CircleImageView) findViewById(R.id.img_profile_pic);
        txtRemove = (TextView) findViewById(R.id.txt_remove);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam_add_pic);
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_from_camera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fab_from_gallery);
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
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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

    private void setDatePicker() {
        Date date = null;
        final DateFormat dateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        try {
            date = dateFormatter.parse(edtDOB.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDOB.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                linearLayout.requestFocus();
            }
        });
    }

    private void initializeFields() {
        edtUsername.setText(pref.getString(Constants.SPUD_USERNAME, null));
        edtFirstname.setText(pref.getString(Constants.SPUD_FIRSTNAME, null));
        edtLastname.setText(pref.getString(Constants.SPUD_LASTNAME, null));
        edtDOB.setText(pref.getString(Constants.SPUD_DOB, null));
        edtEmail.setText(pref.getString(Constants.SPUD_EMAIL, null));
        edtPhone.setText(pref.getString(Constants.SPUD_PHONE, null));
        edtGender.setText(pref.getString(Constants.SPUD_GENDER, null));
        isImageSet = pref.getBoolean(Constants.SPUD_IS_IMAGE_SET, false);
        if (isImageSet) {
            setProfilePic();
        } else {
            imgProfile.setVisibility(View.GONE);
        }
    }

    private void createGendersDialog() {
        gendersDialog = new AlertDialog.Builder(this);
        final ArrayList<String> gendersList = new ArrayList<String>(Arrays.asList("Male", "Female"));
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
                linearLayout.requestFocus();
            }
        }).create();
    }

    private void saveToSharedPref() {
        editor = pref.edit();
        editor.putString(Constants.SPUD_FIRSTNAME, edtFirstname.getText().toString().trim());
        editor.putString(Constants.SPUD_LASTNAME, edtLastname.getText().toString().trim());
        editor.putString(Constants.SPUD_GENDER, edtGender.getText().toString().trim());
        editor.putString(Constants.SPUD_DOB, edtDOB.getText().toString().trim());
        editor.putString(Constants.SPUD_EMAIL, edtEmail.getText().toString().trim());
        editor.putString(Constants.SPUD_PHONE, edtPhone.getText().toString().trim());
        editor.putBoolean(Constants.SPUD_IS_IMAGE_SET, isImageSet);
        editor.commit();
    }

}
