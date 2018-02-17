package com.ribhav.getcontact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/*
 * Created by ribhav on 14/2/18.
 */

public class IncomingSMS extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    String password;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"The message was received.",Toast.LENGTH_SHORT).show();
        Log.d("onReceive", "onReceive: Received a message.w");
        final Bundle bundle = intent.getExtras();
        SharedPreferences sharedPreferences = context.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        password = sharedPreferences.getString("Password", null);
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.d("Onreceive",password);
                    Log.d("Onreceive",message);
                    if(message.contains(password)){
//                        Toast.makeText(context,"Your message seems to be compatible with the app.",Toast.LENGTH_SHORT).show();
                        //Send back a text message containing the information of the contact.
                        Log.d("OnReceive", "onReceive: Compatible");
                    }else{
                        Log.d("OnReceive", "onReceive: Incompatible");
                    }
//                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
//                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context,
//                            "senderNum: "+ senderNum + ", message: " + message, duration);
//                    toast.show();
                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}
