package com.icaroabreu.quickboxrestapiexample.util;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.icaroabreu.quickboxrestapiexample.R;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by icaroabreu on 16/01/2016.
 * QuickBoxRestApiExample
 */
public class Utils {

	public static boolean validateName( String name )
	{
		return name.matches("[\\p{L}\\p{Z}\\p{P}]{2,}");
	}

	public static boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

    /**
     * Return the string humanly readable
     * for the given date.
     * @param day
     * @return
     */
    public static String humanDate(String day){
        return humanDate(day, "dd/MM");
    }

    /**
     * Return the string humanly readable
     * for the given date.
     * @param day
     * @param parserPattern
     * @return
     */
    public static String humanDate(String day, String parserPattern){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat human = new SimpleDateFormat(parserPattern);
        try {
            Date converted = sdf.parse(day);
            return human.format(converted);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

	/**
	 * Return the string humanly readable
	 * for the given date.
	 * @param day
	 * @return
	 */
	public static String sqlDate(Date day){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.format(day);
		} catch (Exception e) {
			e.printStackTrace();
			return "NOW()";
		}
	}

	/**
	 * Generate HMAC-SHA Auth
	 * Using Bouncy Castle's encoder library
	 * @param msg Body of the message to encrypt
	 * @param key Secret key
	 * @return Computed HMAC (in Hex)
	 */
	public static String hash(String msg, String key) {
		// get instance of the SHA Message Digest object.
		HMac hmac = new HMac(new SHA1Digest());
		byte[] result = new byte[hmac.getMacSize()];
		byte[] msgAry =  msg.getBytes() ;

		KeyParameter kp = new KeyParameter(key.getBytes());
		hmac.init(kp);
		hmac.update(msgAry, 0, msgAry.length);
		hmac.doFinal(result, 0);

		StringBuilder hash = new StringBuilder();
		for (byte aResult : result) {
			String hex = Integer.toHexString(0xFF & aResult);
			if (hex.length() == 1) {
				hash.append('0');
			}
			hash.append(hex);
		}
		return hash.toString();
	}

	/**
	 * Shows a alert message
	 * @param string
	 * @return
	 */
	public static void displayAlert(Context context, String title, String string){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(string);
		alert.setPositiveButton(context.getString(R.string.action_ok), null);
		alert.show();
	}

	/**
	 * Shows a alert message
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void displayAlert(Context context, int title, int message){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setPositiveButton(context.getString(R.string.action_ok), null);
		alert.show();
	}

	/**
	 * Shows a alert message
	 * @param context
	 * @param title
	 * @param message
	 */
	public static void displayAlert(Context context, int title, int message, OnClickListener clickListener){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setPositiveButton(context.getString(R.string.action_ok), clickListener);
		alert.show();
	}
}