package com.cirs.reportit.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kshitij.reportit.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewComplaintActivity extends AppCompatActivity implements Validator.ValidationListener {

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

    private Context context = this;

    private FloatingActionMenu floatingActionMenu;

    private FloatingActionButton fabCamera;

    private FloatingActionButton fabGallery;

    private boolean isImageSet = false;

    private ImageView imgComplaint;

    private TextView txtRemove;

    private Validator validator;

    private boolean isComplaintComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        validator = new Validator(context);
        initializeViews();
        linearLayout.requestFocus();
        createCategoriesDialog();
        setListeners();
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
                validator.validate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isComplaintComplete || areAllViewsEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
    }

    private void createCategoriesDialog() {
        categoriesDialog = new AlertDialog.Builder(this);
        final ArrayList<String> categoriesList = new ArrayList<String>(Arrays.asList("Cat1", "Cat2", "Cat3"));
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoriesList);
        categoriesDialog.setTitle("Select Category").setSingleChoiceItems(categoriesAdapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edtCategory.setText(categoriesList.get(i));
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
                imgComplaint.setVisibility(View.GONE);
                txtRemove.setVisibility(View.GONE);
                floatingActionMenu.setVisibility(View.VISIBLE);
            }
        });

        validator.setValidationListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            isImageSet = true;
            imgComplaint.setVisibility(View.VISIBLE);
            txtRemove.setVisibility(View.VISIBLE);
            floatingActionMenu.setVisibility(View.GONE);
            if (requestCode == 100) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgComplaint.setImageBitmap(bitmap);
            } else if (requestCode == 200) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imgComplaint.setImageBitmap(bitmap);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onValidationSucceeded() {
        isComplaintComplete = true;
        Toast.makeText(context, "Your complaint has been successfully submitted!", Toast.LENGTH_LONG).show();
        onBackPressed();
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
        if (isImageSet) return false;
        return true;
    }
}
