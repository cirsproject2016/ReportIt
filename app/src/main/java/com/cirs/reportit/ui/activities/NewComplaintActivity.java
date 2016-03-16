package com.cirs.reportit.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.NoConnectionError;
import com.cirs.R;
import com.cirs.entities.CIRSUser;
import com.cirs.entities.Category;
import com.cirs.entities.Complaint;
import com.cirs.reportit.ReportItApplication;
import com.cirs.reportit.offline.OfflineManager;
import com.cirs.reportit.utils.Constants;
import com.cirs.reportit.utils.Generator;
import com.cirs.reportit.utils.VolleyRequest;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewComplaintActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static final String TAG = NewComplaintActivity.class.getSimpleName();
    private LinearLayout linearLayout;

    @NotEmpty
    private EditText edtCategory;

    @NotEmpty
    private EditText edtTitle;

    private EditText edtDescription;

    @NotEmpty
    private EditText edtLocation;

    private EditText edtLandmark;

    private AlertDialog.Builder categoriesDialog;

    private Context mActivityContext = this;

    private FloatingActionMenu floatingActionMenu;

    private FloatingActionButton fabCamera;

    private FloatingActionButton fabGallery;

    private boolean isImageSet = false;

    private ImageView imgComplaint;

    private TextView txtRemove;

    private Validator validator;

    private boolean isComplaintComplete = false;

    private Category selectedcategory;

    private Bitmap bmpComplaintPic;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        validator = new Validator(mActivityContext);
        initializeViews();
        linearLayout.requestFocus();
        createCategoriesDialog();
        setListeners();
        ReportItApplication.fetchCategories();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_complaint, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_submit:
                if (isImageSet()) {
                    validator.validate();
                } else {
                    Toast.makeText(mActivityContext, "Please select an image for your complaint!",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isComplaintComplete || areAllViewsEmpty()) {
            startActivity(new Intent(NewComplaintActivity.this, HomeActivity.class));
            finish();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivityContext);
            alertDialog.setTitle("Alert!")
                    .setMessage("Any details entered will not be saved. " +
                            "Are you sure you want to go back?")
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isComplaintComplete = true;
                            onBackPressed();
                        }
                    }).create().show();
        }
    }

    private void initializeViews() {
        edtCategory = (EditText) findViewById(R.id.edt_category);
        edtTitle = (EditText) findViewById(R.id.edt_title);
        edtDescription = (EditText) findViewById(R.id.edt_description);
        edtLocation = (EditText) findViewById(R.id.edt_location);
        edtLandmark = (EditText) findViewById(R.id.edt_landmark);
        linearLayout = (LinearLayout) findViewById(R.id.lnr_dummy);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fam_add_pic);
        fabCamera = (FloatingActionButton) findViewById(R.id.fab_from_camera);
        fabGallery = (FloatingActionButton) findViewById(R.id.fab_from_gallery);
        imgComplaint = (ImageView) findViewById(R.id.img_complaint);
        txtRemove = (TextView) findViewById(R.id.txt_remove);
        progressDialog = new ProgressDialog(mActivityContext);
        progressDialog.setMessage("Submitting complaint");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void createCategoriesDialog() {
        categoriesDialog = new AlertDialog.Builder(this);
        final ArrayList<Category> categoriesList = new ArrayList<>(ReportItApplication.getCategories());
        ArrayAdapter<Category> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesList);
        categoriesDialog.setTitle("Select Category").setSingleChoiceItems(categoriesAdapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edtCategory.setText(categoriesList.get(i).toString());
                selectedcategory = categoriesList.get(i);
                dialogInterface.dismiss();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                edtTitle.requestFocus();
            }
        }).create();
    }

    private void setListeners() {
        edtCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoriesDialog.show();
            }
        });

        edtCategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) categoriesDialog.show();
            }
        });

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory() + File.separator
                        + Constants.FILE_PATH_COMPLAINT_PIC);
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
                setIsImageSet(false);
            }
        });

        validator.setValidationListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            setIsImageSet(true);
            if (requestCode == 100) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator
                        + Constants.FILE_PATH_COMPLAINT_PIC);
                cropCapturedImage(Uri.fromFile(file));
            } else if (requestCode == 200) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator
                            + Constants.FILE_PATH_COMPLAINT_PIC);
                    OutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    cropCapturedImage(Uri.fromFile(file));
                } catch (Exception e) {
                }
            } else if (requestCode == 300) {
                Bundle extras = data.getExtras();
                bmpComplaintPic = extras.getParcelable("data");
                imgComplaint.setImageBitmap(bmpComplaintPic);
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        progressDialog.show();
        final Complaint complaint = new Complaint();
        complaint.setCategory(selectedcategory);
        complaint.setTitle(edtTitle.getText().toString());
        complaint.setDescription(edtDescription.getText().toString());
        complaint.setLocation(edtLocation.getText().toString());
        complaint.setLandmark(edtLandmark.getText().toString());
        CIRSUser user = new CIRSUser();
        user.setId(ReportItApplication.getCirsUser().getId());
        complaint.setUser(user);
        complaint.setTimestamp(new Timestamp(System.currentTimeMillis()));

        new VolleyRequest<Complaint>(mActivityContext).makeGsonRequest(
                Request.Method.PUT,
                Generator.getURLtoSendComplaint(),
                complaint,
                new Response.Listener<Complaint>() {
                    @Override
                    public void onResponse(Complaint response) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmpComplaintPic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        new VolleyRequest<byte[]>(mActivityContext).makeImageRequest(
                                Generator.getUrltoUploadComplaintPic(response),
                                "put",
                                VolleyRequest.FileType.PNG,
                                byteArray,
                                new Response.Listener<Integer>() {
                                    @Override
                                    public void onResponse(Integer response) {
                                        progressDialog.dismiss();
                                        isComplaintComplete = true;
                                        Toast.makeText(mActivityContext, "Your complaint has been submitted!", Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        progressDialog.dismiss();
                                        error.printStackTrace();
                                        Toast.makeText(mActivityContext, "There was an error uploading image", Toast.LENGTH_SHORT).show();
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
                        if (error instanceof NoConnectionError || error instanceof TimeoutError) {
                            Log.i(TAG, "enqueuing complaint " + complaint);
                            //Add complaint to offline requests
                            OfflineManager.getInstance(NewComplaintActivity.this).enqueueComplaintRequest(complaint);
                            return;
                        }
                        Toast.makeText(mActivityContext, "There was an error. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                Complaint.class);
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

    private boolean areAllViewsEmpty() {
        List<EditText> views = new ArrayList<>(Arrays.asList(edtCategory, edtTitle, edtDescription,
                edtLocation, edtLandmark));
        for (EditText e : views)
            if (!e.getText().toString().trim().isEmpty()) return false;
        if (isImageSet()) return false;
        return true;
    }

    private boolean isImageSet() {
        return isImageSet;
    }

    private void setIsImageSet(boolean value) {
        isImageSet = value;
        if (isImageSet()) {
            imgComplaint.setVisibility(View.VISIBLE);
            txtRemove.setVisibility(View.VISIBLE);
            floatingActionMenu.setVisibility(View.GONE);
        } else {
            floatingActionMenu.close(false);
            imgComplaint.setVisibility(View.GONE);
            txtRemove.setVisibility(View.GONE);
            floatingActionMenu.setVisibility(View.VISIBLE);
        }
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
