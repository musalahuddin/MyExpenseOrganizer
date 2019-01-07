package org.musalahuddin.myexpenseorganizer.camera;

import org.musalahuddin.myexpenseorganizer.MyApplication;
import org.musalahuddin.myexpenseorganizer.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class CameraModule extends FragmentActivity{

    public static final int REQUEST_CODE_CAMERA = 100;
    public static final int REQUEST_CODE_PICTURE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 1;

    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "My Expense Organizer";

    private static Uri fileUri; // file url to store image/video

    public interface ClearImageCallback {
        public void clearImage();

        public void viewImage();
    }

    public interface CameraResultCallback {
        public void handleCameraResult(Bitmap bitmap, String imagePath, Uri uri);
    }

    static class MyAsyncTask extends AsyncTask<Uri, Void, Void> {

        Bitmap bitmap;
        Uri saveImageUri;
        String saveImagePath;
        private Activity activity;
        private CameraResultCallback camResult;
        private int requestCode;

        /**
         * @param context
         */
        public MyAsyncTask(Activity activity, int requestCode, CameraResultCallback result) {
            attach(activity,requestCode,result);
        }

        public void attach(Activity activity, int requestCode, CameraResultCallback result) {
            this.activity=activity;
            this.camResult=result;
            this.requestCode=requestCode;

        }

        @Override
        protected Void doInBackground(Uri... params) {
            String imagePath="";
            if(requestCode == CameraModule.REQUEST_CODE_PICTURE){
                imagePath = getRealPathFromURI(params[0],activity);
                //imagePath = params[0].getPath();
                Log.i("requestCode:"," picture");
            }
            else if (requestCode == CameraModule.REQUEST_CODE_CAMERA){
                imagePath = fileUri.getPath();
                Log.i("imagePath",imagePath);
                Log.i("requestCode:"," camera");
            }
            File check = new File(imagePath).getParentFile();
            String dirName = check.getName();
            Log.i("dirName",dirName);
            if(!dirName.equals(IMAGE_DIRECTORY_NAME)){
                File file2 = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                String imagePath2 = file2.getAbsolutePath();
                copyFile(imagePath,imagePath2);
                saveImagePath= new File(imagePath2).getName();
            }
            else{
                saveImagePath=new File(imagePath).getName();
            }

            bitmap = readScaledBitmap(imagePath);
            saveImageUri = params[0];

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(bitmap != null) {
                //activity.setResult(Activity.RESULT_OK);
                camResult.handleCameraResult(bitmap,saveImagePath,saveImageUri);
            }
        }

    }

    public static void showPictureLauncher(final Activity activity, final ClearImageCallback clearImageOption) {

        ArrayList<String> options = new ArrayList<String>();

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager pm = activity.getPackageManager();

        final boolean cameraAvailable = pm.queryIntentActivities(cameraIntent, 0).size() > 0;


        if(cameraAvailable)
            options.add(activity.getString(R.string.picture_camera));
        options.add(activity.getString(R.string.picture_gallery));

        if (clearImageOption != null){
            options.add(activity.getString(R.string.picture_view));
            options.add(activity.getString(R.string.picture_clear));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_dropdown_item, options.toArray(new String[options.size()]));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @SuppressWarnings("nls")
            @Override
            public void onClick(DialogInterface d, int which) {
                if(which == 0 && cameraAvailable) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    activity.startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
                else if ((which == 1 && cameraAvailable) || (which == 0 && !cameraAvailable)) {


                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    //intent.setData(Uri.fromFile(mediaStorageDir));
                    activity.startActivityForResult(intent, REQUEST_CODE_PICTURE);

                }
                else if (which == 2){

                    clearImageOption.viewImage();
                }
                else{
                    clearImageOption.clearImage();
                }

            }
        };


        // show a menu of available options
        new AlertDialog.Builder(activity)
                .setAdapter(adapter, listener)
                .show().setOwnerActivity(activity);

    }


    /*
	 * Creating file uri to store image/video
	 */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
				/*
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

                Environment
                        .getExternalStorageDirectory(),
                IMAGE_DIRECTORY_NAME);
        */

        MyApplication.getInstance().getExternalCacheDir(),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private static File getOutputMediaFile(String fileName) {
        // External sdcard location
        File mediaStorageDir = new File(
				/*
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

                Environment
                        .getExternalStorageDirectory(),
                IMAGE_DIRECTORY_NAME);
                */

        MyApplication.getInstance().getExternalCacheDir(),IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + fileName);

        return mediaFile;
    }

    public static boolean activityResult(Activity activity, int requestCode, int resultCode, Intent data,
                                         CameraResultCallback cameraResult) {
        if(requestCode == CameraModule.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {

            new MyAsyncTask(activity,requestCode,cameraResult).execute(fileUri);

            return true;
        }
        else if(requestCode == CameraModule.REQUEST_CODE_PICTURE && resultCode == Activity.RESULT_OK) {

            new MyAsyncTask(activity,requestCode,cameraResult).execute(data.getData());

            return true;
        }

        return false;
    }

    public static void getBitmap(String fileName, final Activity activity, int requestCode, CameraResultCallback cameraResult){
        fileUri = Uri.fromFile(getOutputMediaFile(fileName));

        new MyAsyncTask(activity,requestCode,cameraResult).execute(fileUri);
    }


    public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        String [] proj      = {MediaStore.Images.Media.DATA};
        //Cursor cursor       = activity.managedQuery( contentUri, proj, null, null,null);
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;

        int column_index    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    /** Read a bitmap from the specified file, scaling if necessary
     *  Returns null if scaling failed after several tries */
    private static final int[] SAMPLE_SIZES = { 1, 2, 4, 6, 8, 10 };
    private static final int MAX_DIM = 1024;
    public static Bitmap readScaledBitmap(String file) {
        Bitmap bitmap = null;

        int tries = 1;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inSampleSize = SAMPLE_SIZES[tries];
        BitmapFactory.decodeFile(file, opts);
        while((opts.outWidth > MAX_DIM || opts.outHeight > MAX_DIM) && tries < SAMPLE_SIZES.length){
            opts.inSampleSize = SAMPLE_SIZES[tries];
            Log.i("camera size", String.valueOf(opts.outWidth) + " " + String.valueOf(opts.outHeight));
            BitmapFactory.decodeFile(file, opts);
            tries++;
        }

        Log.i("camera final size", String.valueOf(opts.outWidth) + " " + String.valueOf(opts.outHeight));

        try {
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(file, opts);
        } catch (OutOfMemoryError e) {
            // Too big
            Log.e("decode-bitmap", "Out of memory with sample size " + opts.inSampleSize, e);  //$NON-NLS-1$//$NON-NLS-2$
        }

        /*
        int tries = 0;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        while((bitmap == null || (bitmap.getWidth() > MAX_DIM || bitmap.getHeight() > MAX_DIM)) && tries < SAMPLE_SIZES.length) {
            opts.inSampleSize = SAMPLE_SIZES[tries];
            try {
                bitmap = BitmapFactory.decodeFile(file, opts);
            } catch (OutOfMemoryError e) {
                // Too big
                Log.e("decode-bitmap", "Out of memory with sample size " + opts.inSampleSize, e);  //$NON-NLS-1$//$NON-NLS-2$
            }
            tries++;
        }
        */


        // here it checks for the correct orientation
        try {
            ExifInterface exif = new ExifInterface(file);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.i("orientation",String.valueOf(orientation));

            Matrix matrix = new Matrix();
            boolean changeOrientation = false;
            if (orientation == 6) {
                changeOrientation = true;
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                changeOrientation = true;
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                changeOrientation = true;
                matrix.postRotate(270);
            }

            if(changeOrientation == true){
                Log.i("orientation","changed");
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return bitmap;
    }

    public static boolean copyFile(String from, String to) {
        try {
            //File dir = Environment.getExternalStorageDirectory();
            File dir = new File (MyApplication.getInstance().getExternalCacheDir(), IMAGE_DIRECTORY_NAME);
            if (dir.canWrite()) {
                File source = new File(from);
                File destination= new File(to);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
