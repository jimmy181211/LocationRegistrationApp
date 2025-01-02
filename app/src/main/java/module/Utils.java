package module;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: this class contains some functions and procedures that are repeatedly called from
 * other classes. So they are aggregated into this class
 */
public class Utils {
    private Handler handler;

    public Utils(Handler handler){
        this.handler=handler;
    }

    public Handler getHandler(){
        return this.handler;
    }

    /**
     * Description: this method provides a dialog UI
     * @param title title of the dialog
     * @param text content of the dialog
     * @param ctx context that the dialog is shown onto
     * @param msgType the type of message so that when the confirm button is clicked, the handler received an empty message and branches to the action according to the message type
     */
    public void dialog(String title, String text, Context ctx, int msgType){
        AlertDialog alert=new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //send a message to tell the main thread to do log-out operation
                        handler.sendEmptyMessage(msgType);
                    }
                }).create();
        alert.show();
    }

    /**
     * Description: this method converts dp to px. This is used when the programmer modifies layout dynamically in the activities, instead of the xml file
     * @param ctx context the operation relies on
     * @param dipVal the value of the dp
     * @return the px value
     */
    public static int dp2px(@NonNull Context ctx, float dipVal){
        final float scale=ctx.getResources().getDisplayMetrics().density;
        return (int)(dipVal*scale+0.5f);
    }


    /**
     * Description: this method enables activity jumping when a click event on 'vComp' is triggered
     * @param vComp the component responsible for click-event-listening
     * @param currCtx the context that the 'back' icon is bind with.
     * @param targetCtxCls the Class class of name 'targetCtx'. This refers to the activity to jump to.
     * @param <T extends View> this generic type refers to all components that possess setOnClickListener() method, which inherit from View class
     * @param <E extends AppCompatActivity> a generic type that refers to all Activities in the project
     */
    public static <T extends View,E extends AppCompatActivity> void backLogic(T vComp, Context currCtx, Class<E> targetCtxCls){

        vComp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                jump2Activity(currCtx,targetCtxCls);
            }
        });
    }

    /**
     * Description: this method uses intent to enable jumping between activities
     */
    public static <E extends AppCompatActivity> void jump2Activity(Context currCtx, Class<E> targetCtxCls){
        Intent intent=new Intent(currCtx, targetCtxCls);
        currCtx.startActivity(intent);
    }

    /**
     *Description: this method splits a string from right to left using a separator (a character).
     * the procedure stops after the 'num' number of separators are found
     * @param str input string that is to be split
     * @param sep a character that marks the position where the string should be split
     * @return the strings that are split by the separator
     */
    public static List<String> rsplit(String str, char sep, int num){
        List<String> strs=new ArrayList<>();
        int len=str.length();
        int prevI=len;
        for(int i=len-1;i>=0;i--){
            if(str.charAt(i)==sep){
                num--;
                strs.add(str.substring(i+1,prevI));
                prevI=i;
                if(num<=0){
                    break;
                }
            }
        }
        strs.add(str.substring(0,prevI));
        return strs;
    }
}
