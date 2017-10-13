package fpuna.py.com.appolympos.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.adapters.ClientsAdapter;
import fpuna.py.com.appolympos.dialogs.ProgressDialogFragment;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.network.MyRequest;
import fpuna.py.com.appolympos.network.NetworkQueue;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.utiles.Constants;
import fpuna.py.com.appolympos.utiles.Utils;
import id.zelory.compressor.Compressor;

public class PictureActivity extends AppCompatActivity {
    private static final String TAG_CLASS = PictureActivity.class.getName();
    private CoordinatorLayout mCoordinator;
    private ImageView mImageViewCommerce;
    private TextInputEditText mTextInputClienteDescripcion;
    private TextInputEditText mTextInputClienteRuc;
    private Clientes mCliente;
    private Uri mUriFile;
    private String mCompressFilePath;
    private Bitmap mBitmapPicture;
    private static final int CAMERA_RESULT = 100;
    private boolean takePicture;
    private PictureTask mPictureTask;
    private NetworkQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.picture_coordinator_view);
        mImageViewCommerce = (ImageView) findViewById(R.id.picture_client_image);
        mTextInputClienteDescripcion = (TextInputEditText) findViewById(R.id.picture_client_name);
        mTextInputClienteDescripcion.setFocusable(false);
        mTextInputClienteRuc = (TextInputEditText) findViewById(R.id.picture_client_ruc);
        mTextInputClienteRuc.setFocusable(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_button_capture);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptShot();
            }
        });
        Button buttonSend = (Button) findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        Button buttonCancel = (Button) findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setData();
        mQueue = new NetworkQueue(getApplicationContext());
    }

    private void setData() {
        if (!getIntent().hasExtra(ClientsAdapter.CLIENT_ID)) {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }

        if (getIntent().getExtras().containsKey(ClientsAdapter.CLIENT_ID)) {
            long clientId = getIntent().getExtras().getLong(ClientsAdapter.CLIENT_ID);
            mCliente = ClienteRepository.getById(clientId);
            if (mCliente == null) {
                Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
                finish();
                return;
            }
            mTextInputClienteDescripcion.setText(mCliente.getNombreNegocio());
            mTextInputClienteRuc.setText(mCliente.getRuc());
        } else {
            Utils.getToast(getApplicationContext(), getString(R.string.error_client_id_not_found));
            finish();
            return;
        }
    }

    private void attemptShot() {
        PackageManager pm = getApplicationContext().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Utils.getSnackBar(mCoordinator, getString(R.string.error_camera_not_available));
            return;
        }

        setTakePicture(false);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                setmUriFile(Uri.fromFile(photoFile));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_RESULT);
            } else {
                Utils.getSnackBar(mCoordinator, getString(R.string.error_create_file));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            setImage();
        } else {
            Utils.getSnackBar(mCoordinator, getString(R.string.error_capture_picture));
        }
    }

    private void setImage() {
        try {
            mImageViewCommerce.setImageBitmap(BitmapFactory.decodeFile(compressImage()));
            setTakePicture(true);
            setmBitmapPicture(BitmapFactory.decodeFile(getmCompressFilePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //create a compress image in the directory compress
    private String compressImage() {
        File mImage = new File(getmUriFile().getPath());
        setmCompressFilePath(new Compressor.Builder(this)
                .setQuality(75)
                .setMaxWidth(640)
                .setMaxHeight(480)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(new File(getApplicationContext().getExternalFilesDir(null), "compress").getPath())
                .build()
                .compressToFile(mImage).getPath());
        return getmCompressFilePath();
    }

    //create a original image in the directory photo
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(getApplicationContext().getExternalFilesDir(null), "photo");

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG_CLASS, "failed to create directory");
                return null;
            }
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void validateFields() {
        String imageBase64 = "";

        if (!isTakePicture()) {
            Utils.getSnackBar(mCoordinator, getString(R.string.error_take_picture));
            return;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            getmBitmapPicture().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            imageBase64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.getSnackBar(mCoordinator, getString(R.string.error_image_conversion));
            return;
        }

        mPictureTask = new PictureTask(imageBase64);
        mPictureTask.confirm();
    }

    private class PictureTask extends MyRequest {
        public static final String REQUEST_TAG = "PictureTask";
        private String mPicture;

        private PictureTask(String picture) {
            mPicture = picture;
        }

        @Override
        protected void confirm() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
            builder.setIcon(R.mipmap.ic_info_black_24dp);
            builder.setTitle(R.string.dialog_confirmation_title);
            builder.setMessage(R.string.dialog_confirmation_message);
            builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    execute();
                }
            });

            builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPictureTask = null;
                }
            });

            AlertDialog confirmDialog = builder.create();
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.show();
        }

        @Override
        protected void execute() {
            mProgressDialog = ProgressDialogFragment.newInstance(getApplicationContext());
            mProgressDialog.show(getSupportFragmentManager(), ProgressDialogFragment.TAG_CLASS);

            // Cancel any pending request before starting a new one
            if (mJsonObjectRequest != null)
                mQueue.getRequestQueue(getApplicationContext()).cancelAll(PictureActivity.PictureTask.REQUEST_TAG);

            mJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.UPDATE_PICTURE_URL, getParams(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mProgressDialog.dismiss();
                    handleResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressDialog.dismiss();
                    String message = NetworkQueue.handleError(error, getApplicationContext());
                    mJsonObjectRequest.cancel();
                    Utils.getSnackBar(mCoordinator, message);
                }
            });
            mQueue.addToRequestQueue(mJsonObjectRequest, getApplicationContext());
        }

        @Override
        protected JSONObject getParams() {
            mJsonParams = new JSONObject();
            try {
                mJsonParams.put("clientId", mCliente.getId());
                mJsonParams.put("picture", mPicture);
            } catch (JSONException e) {
                System.err.println(e);
            }
            return mJsonParams;
        }

        @Override
        protected void handleResponse(JSONObject response) {
            String message = null;
            int status = -1;
            Log.i(TAG_CLASS, REQUEST_TAG + " | Response: " + response.toString());

            try {
                if (response.has("status")) status = response.getInt("status");
                if (response.has("message")) message = response.getString("message");

                if (status != Constants.RESPONSE_OK) {
                    Utils.getSnackBar(mCoordinator, (message == null) ? getString(R.string.volley_default_error) : message);
                } else {
                    mCliente.setTieneFoto(true);
                    mCliente.setFoto(mPicture.getBytes());
                    ClienteRepository.store(mCliente);
                    successDialog(message);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Utils.getSnackBar(mCoordinator, getString(R.string.volley_default_error));
            }
        }
    }

    private void successDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PictureActivity.this);
        builder.setIcon(R.mipmap.ic_done_black_24dp);
        builder.setTitle(R.string.dialog_info_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    public Uri getmUriFile() {
        return mUriFile;
    }

    public void setmUriFile(Uri mUriFile) {
        this.mUriFile = mUriFile;
    }

    public String getmCompressFilePath() {
        return mCompressFilePath;
    }

    public void setmCompressFilePath(String mCompressFilePath) {
        this.mCompressFilePath = mCompressFilePath;
    }

    public Bitmap getmBitmapPicture() {
        return mBitmapPicture;
    }

    public void setmBitmapPicture(Bitmap mBitmapPicture) {
        this.mBitmapPicture = mBitmapPicture;
    }

    public boolean isTakePicture() {
        return takePicture;
    }

    public void setTakePicture(boolean takePicture) {
        this.takePicture = takePicture;
    }
}
