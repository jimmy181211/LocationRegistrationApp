package controller;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.signinapp.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description: this class fetches user portrait (only used in SettingActivity class), and shows it
 * on the ImageView Component given. It can also save the Image so the app remembers user's choice
 * @version 1.0
 */
public class PortraitGetter {
    private static final String TAG="CourseWork:Portrait";
    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGE=2;
    private Context ctx;
    private Activity activity;
    private ImageView portrait;
    public String imgPath=null;

    public PortraitGetter(Context ctx, ImageView portrait){
        this.ctx=ctx;
        this.activity=(Activity)ctx;
        this.portrait=portrait;
    }

    /**
     * Description: this method has a permission request process and encapsulates the selectImageRaw
     * private method
     */
    public void selectImage(){
        //if the app doesn't have the permission to read external storage then ask for it
        if(ContextCompat.checkSelfPermission(
                ctx.getApplicationContext(),"android.permission.READ_MEDIA_IMAGES")
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{
                            "android.permission.READ_MEDIA_IMAGES"},
                    REQUEST_CODE_STORAGE_PERMISSION);
            Log.d(TAG,"don't have permission");
        }
        else{
            Log.d(TAG,"have permission");
            selectImageRaw();
        }
    }

    /**
     * Description: this method starts an intent to jump to the photo selection page. The result of
     * the page (activity) will be returned
     */
    private void selectImageRaw(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
    }

    /**
     * Description: this method uses an uri and set the image onto the given ImageView
     * @param uri uniform resource identifier of the image
     * @throws FileNotFoundException
     */
    private Bitmap getImage(Uri uri) throws FileNotFoundException {
        InputStream input=activity.getContentResolver().openInputStream(uri);
        return BitmapFactory.decodeStream(input);
    }

    /**
     * Description: this method groups the parameter 'path' into two types: the one relying on
     * drawable (default portrait name), and those that has been set
     * @param path the string path of the image
     */
    public void setPortrait(String path){
        try{
            Bitmap bitmap;
            if(path.equals(FileHandler.DEFAULT_PORTRAIT_PATH)){
//                bitmap=(ResourcesCompat.getDrawable(ctx.getResources(),R.drawable.baseline_portrait_24, null));
                bitmap=BitmapFactory.decodeResource(ctx.getResources(),R.drawable.baseline_portrait_24);
            }else{
                try {
                    bitmap=getBitmapFromPath(path);
                } catch (FileNotFoundException e) {
                    Log.d(TAG,"rune time exception takes place");
                    throw new RuntimeException(e);
                }
            }
            portrait.setImageBitmap(bitmap);
        }
        catch(Exception e){
            Log.d(TAG,"error here");
            System.out.println(e.getMessage());
        }
    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    public Bitmap getBitmapFromDrawable(){
//        return ctx.getResources().getDrawable(R.drawable.portrait_forground));
//    }

    public Bitmap getBitmapFromPath(String path) throws FileNotFoundException {
//        return getImage(getUriFromPath(path));
        return BitmapFactory.decodeFile(path);
    }

    public void onActivityResultComponent(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode!=REQUEST_CODE_SELECT_IMAGE || resultCode!=RESULT_OK || data==null) {
            return;
        }
        Uri selectedImageUri=data.getData();
        if(selectedImageUri==null){
            return;
        }
        try{
            Bitmap bitmap=getImage(selectedImageUri);
            portrait.setImageBitmap(bitmap);
            imgPath=getPathFromUri(selectedImageUri);
            //update the local file about the image path
            UserInfo uInfo=FileHandler.readFile(ctx);
            uInfo.setPortrait_path(imgPath);
            FileHandler.writeFile(ctx,uInfo);

//            storeImage(bitmap,"user_portrait");

        }
        catch(Exception e){
            Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Description: this method checks whether the app has permission to selectImage. If it has
     * (permission granted), then execute selectImageRaw() method. The decision is made
     * based on the result of selfPermissionCheck() method which is the user's choice of whether
     * allow, not allow or not asking again.
     * @param requestCode an integer. It tells the method whether request is successful
     * @param grantResults none
     */
    public void onRequestPermissionResultComponent(int requestCode, @NonNull int[] grantResults){
        if(requestCode== REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImageRaw();
            }
            else{
                Toast.makeText(ctx,"permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Description: this method enables the user's choice of image to be saved
     * @param contentUri Uniform Resource Identifier of the image
     * @return the file path of the image stored
     */
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor=activity.getContentResolver()
                .query(contentUri,null,null,null,null);
        if(cursor==null){
            filePath=contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int idx=cursor.getColumnIndex("_data");
            filePath=cursor.getString(idx);
            cursor.close();
        }
        return filePath;
    }

    private Uri getUriFromPath(String path){
        return Uri.fromFile(new File(path));
    }

    private void storeImage(Bitmap bitmap, String fileName) throws IOException {
        FileOutputStream fos=new FileOutputStream(new File("C:/Users/JimmyOuyangJinhong/AndroidStudioProjects/70004CW2SP24Release/app/src/main/java/uk/ac/imperial/apss/android/comp70004/cw2/a70004cw2sp24",fileName));
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
        fos.flush();
        fos.close();
    }

    private void storeImage2(String path, String imgName) throws IOException {
        //split the image name from the absolute path
        FileInputStream fis=new FileInputStream(path);
        FileOutputStream fos=new FileOutputStream("C:/Users/jimmy/Downloads/Release/Release/Part1/70004CW2SP24Release/app/src/main/java/uk/ac/imperial/apss/android/comp70004/cw2/a70004cw2sp24/"+imgName);
        BufferedInputStream buff_fis=new BufferedInputStream(fis);
        BufferedOutputStream buff_fos=new BufferedOutputStream(fos);
        byte[] bs=new byte[1024];
        while(buff_fis.read(bs)!=-1){
            buff_fos.write(bs);
        }
        buff_fos.flush();
        buff_fis.close();
        buff_fos.close();
    }
}
