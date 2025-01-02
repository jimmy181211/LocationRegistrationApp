package controller;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import com.example.signinapp.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Description: this class provides a file handler that reads and writes data from the to the file
 */
public class FileHandler {
    private final static String TAG="CourseWork:fileHandler";
    public static final String fileName="userData.txt";
    public static final String DEFAULT_UNAME="none";
    public static final String DEFAULT_PORTRAIT_PATH="none";

    /**
     * Description:
     * @param ctx the context that the file system is relied on
     * @return a class that encapsulates the data fetched
     */
    public static UserInfo readFile(Context ctx){
        ObjectInputStream ois;
        FileInputStream fis;
        //create objects for reading
        try {
            fis=ctx.openFileInput(fileName);
            ois=new ObjectInputStream(fis);
        }
        //if the file doesn't exists then create the file
        catch(FileNotFoundException e){
            Log.d(TAG,"create the userinfo file for the first time!");
            unLoginFileSetting(ctx);
            //after creating the file, creates objects for reading again
            try{
                fis=ctx.openFileInput(fileName);
                ois=new ObjectInputStream(fis);
            }
            catch(IOException e2){
                throw new RuntimeException("the second time the file being read still fails");
            }
        }
        catch (IOException e) {
            Log.d(TAG,"file "+fileName+" fails to read!");
            throw new RuntimeException(e);
        }
        try {
            //read the user info from the file
            UserInfo info=(UserInfo)ois.readObject();
            //close and release the resources
            ois.close();
            fis.close();
            return info;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: if the ser is not logged in, then he will be given a default file setting
     * @param ctx the context that the file system relies on
     */
    public static void unLoginFileSetting(Context ctx){
        UserInfo uInfo=new UserInfo();
        uInfo.setIs_login(false);
        uInfo.setWarning_lv("0");
        uInfo.setUsername(DEFAULT_UNAME);
        uInfo.setPortrait_path(DEFAULT_PORTRAIT_PATH);
        FileHandler.writeFile(ctx, uInfo);
    }

    /**
     * Description:
     * @param ctx the context that the file system relies on
     * @param uInfo the class that encapsulates all the user information
     */
    public static void writeFile(Context ctx, UserInfo uInfo){
        //create local file to preserve the user information
        try {
            FileOutputStream fos=ctx.openFileOutput(fileName,MODE_PRIVATE);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(uInfo);
            fos.close();
            oos.close();
        }
        catch (IOException e) {
            Log.d(TAG,"file "+fileName+" fails to write!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
