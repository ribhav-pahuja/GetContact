package com.ribhav.getcontact;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
/*
 * Created by ribhav on 14/2/18.
 */

public class IncomingSMS extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();
    String password;
    HashMap<String,String> map;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"The message was received.",Toast.LENGTH_SHORT).show();
        Log.d("onReceive", "Received a message.");
        final Bundle bundle = intent.getExtras();
        map = new HashMap<>();
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
                        String contactToBeExtracted = message.substring(password.length()).trim();
                        Log.d("onReceive: ", "" + contactToBeExtracted);
                        map = getContactList(context,map);
                        ArrayList<String> list = new ArrayList<>(map.keySet());
                        for(int j =0;j<list.size();j++){
                            Log.d(list.get(j),map.get(list.get(j)));
                        }
                        if(map.containsKey(contactToBeExtracted)){
                            Toast.makeText(context,"Found",Toast.LENGTH_SHORT).show();
                            sendSMS(context, senderNum, "The contact is:- " + contactToBeExtracted + " -> " + map.get(contactToBeExtracted));
                        }else{
                            Toast.makeText(context,"Not found",Toast.LENGTH_SHORT).show();
                        }
                        Log.d("OnReceive", "onReceive: Compatible");
                    }else{
                        Log.d("OnReceive", "onReceive: Incompatible");
                    }
//                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);
//                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context,
//                            "senderNum: "+ senderNum + ", message: " + message, duration);
//                    toast.show();
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getContactList(Context context,HashMap<String,String> map) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        map.put(name, phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
        return map;
    }

    public void sendSMS(Context context,String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
