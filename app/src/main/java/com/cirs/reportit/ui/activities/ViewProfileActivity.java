package com.cirs.reportit.ui.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.db.dbhelpers.QueryHelper;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.ErrorUtils;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
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

    private Context mActivityContext = this;

    private Validator validator;

    private MenuItem menuItem;

    private CircleImageView imgProfile;

    private TextView txtRemove;

    private FloatingActionMenu floatingActionMenu;

    private FloatingActionButton fabCamera;

    private FloatingActionButton fabGallery;

    private DatePickerDialog datePickerDialog;

    private boolean isImageSet;

    private AlertDialog.Builder gendersDialog;

    private boolean isImageChanged = false;

    private Bitmap bitmapProfilePic;

    private TextInputLayout tilPhone;

    private static final String TAG = ViewProfileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initializeViews();
        validator = new Validator(mActivityContext);
        linearLayout.requestFocus();
        views = new ArrayList<>(Arrays.asList(edtFirstname, edtLastname, edtGender, edtDOB, edtEmail, edtPhone));

        initializeFields();
        setDatePicker();
        setListeners();
        createGendersDialog();
        toggleViews(views, false);
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
                    //Don't allow user to edit if offline.
                    if (!ErrorUtils.isConnected(ViewProfileActivity.this)) {
                        new AlertDialog.Builder(ViewProfileActivity.this).setMessage("Cannot edit offline").setPositiveButton("OK", null).create().show();
                        return true;
                    }
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
            tilPhone.setCounterEnabled(visibility);
            v.setClickable(visibility);
            v.setFocusable(visibility);
            v.setFocusableInTouchMode(visibility);
            v.setLongClickable(false);
            if (!visibility) {
                v.setOnClickListener(null);
            } else {
                setListeners();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!inEditMode) {
            startActivity(new Intent(ViewProfileActivity.this, HomeActivity.class));
            finish();
        } else
            Toast.makeText(mActivityContext, "Please save the changes first!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        if (isImageChanged) {
            CIRSUser user = ReportItApplication.getCirsUser();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapProfilePic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            new VolleyRequest<byte[]>(mActivityContext).makeImageRequest(
                    Generator.getURLtoUploadProfilePic(user),
                    "put",
                    VolleyRequest.FileType.PNG,
                    byteArray,
                    new Response.Listener<Integer>() {
                        @Override
                        public void onResponse(Integer response) {
                            System.out.println("Response: " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Error: " + error);
                        }
                    }
            );
        }
        if (hasAnyValueChanged()) {
            //retrieve value from fields
            final CIRSUser user = createUserFromFields();
            final ProgressDialog dialog = ProgressDialog.show(ViewProfileActivity.this, null, "Changing Details", true, false);
            //make request
            new VolleyRequest<CIRSUser>(mActivityContext).makeGsonRequest(
                    Request.Method.PUT,
                    Generator.getURLtoEditUser(user),
                    user,
                    new Response.Listener<CIRSUser>() {
                        @Override
                        public void onResponse(CIRSUser response) {
                            //if OK, save, and end editing
                            saveToSharedPref();
                            new QueryHelper(mActivityContext).insertOrUpdateCirsUser(user);
                            inEditMode = false;
                            actionBar.setDisplayHomeAsUpEnabled(true);
                            actionBar.setTitle(getResources().getString(R.string.title_activity_view_profile));
                            toggleViews(views, false);
                            txtRemove.setVisibility(View.GONE);
                            floatingActionMenu.setVisibility(View.GONE);
                            menuItem.setIcon(R.drawable.ic_edit);
                            menuItem.setTitle(getResources().getString(R.string.view_profile_menu_item_title_edit));
                            hideKeyboard();
                            dialog.dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //if not, reset value of email text, show error and remain in edit mode.
                            error.printStackTrace();
                            Log.d(TAG, "in on error response");
                            String message = ErrorUtils.parseVolleyError(error);
                            new AlertDialog.Builder(ViewProfileActivity.this).setMessage(message).setPositiveButton("OK", null).create().show();
                            dialog.dismiss();
                            Log.i(TAG, "reverting to previous email" + ReportItApplication.getCirsUser().getEmail());
                            edtEmail.setText(ReportItApplication.getCirsUser().getEmail());
                        }
                    },
                    CIRSUser.class
            );
        }

    }


    private CIRSUser createUserFromFields() {
        CIRSUser user = new CIRSUser();
        user.setId(ReportItApplication.getCirsUser().getId());
        user.setFirstName(edtFirstname.getText().toString());
        user.setLastName(edtLastname.getText().toString());
        user.setGender(edtGender.getText().toString());
        user.setDob(edtDOB.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        user.setPhone(edtPhone.getText().toString());
        return user;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
                view.requestFocus();
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
                    bitmapProfilePic = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator
                            + Constants.FILE_PATH_PROFILE_PIC);
                    OutputStream outputStream = new FileOutputStream(file);
                    bitmapProfilePic.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    cropCapturedImage(Uri.fromFile(file));
                } catch (Exception e) {
                }
            } else if (requestCode == 300) {
                Bundle extras = data.getExtras();
                bitmapProfilePic = extras.getParcelable("data");
                isImageSet = true;
                saveImage(bitmapProfilePic);
            }
        }
    }

    private void saveImage(Bitmap bitmap) {
        isImageSet = true;
        try {
            FileOutputStream out;
            out = mActivityContext.openFileOutput(Constants.FILE_PATH_PROFILE_PIC, Context.MODE_PRIVATE);
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
            FileInputStream fis = mActivityContext.openFileInput(Constants.FILE_PATH_PROFILE_PIC);
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
        tilPhone = (TextInputLayout) findViewById(R.id.til_mobile);
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
                if (!view.isClickable())
                    return;
                datePickerDialog.show();
            }
        });

        edtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!view.isClickable())
                    return;
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
                isImageChanged = true;
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
                hideKeyboard();
            }
        });
    }

    private void initializeFields() {
        edtUsername.setText(Prefs.getString(Constants.SPUD_USERNAME, null));
        edtFirstname.setText(Prefs.getString(Constants.SPUD_FIRSTNAME, null));
        edtLastname.setText(Prefs.getString(Constants.SPUD_LASTNAME, null));
        edtDOB.setText(Prefs.getString(Constants.SPUD_DOB, null));
        edtEmail.setText(Prefs.getString(Constants.SPUD_EMAIL, null));
        edtPhone.setText(Prefs.getString(Constants.SPUD_PHONE, null));
        edtGender.setText(Prefs.getString(Constants.SPUD_GENDER, null));
        isImageSet = Prefs.getBoolean(Constants.SPUD_IS_IMAGE_SET, false);
        if (isImageSet) {
            setProfilePic();
        } else {
            imgProfile.setVisibility(View.GONE);
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
                hideKeyboard();
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

    private void hideKeyboard() {
        linearLayout.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
    }

    private boolean hasAnyValueChanged() {
        List<String> storedDetails = new ArrayList<>(ReportItApplication.getCirsUser().getAllFields());
        for (int i = 0; i < storedDetails.size(); i++) {
            if (!storedDetails.get(i).equals(views.get(i).getText().toString())) {
                return true;
            }
        }
        return false;
    }
}
