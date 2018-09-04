package com.example.restaurantdemo.common;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static String gcmId = "";
    public static String projectGCMiD;
    static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String appPayment = "$ ";

    public static final int PROFILE_CAMERA_IMAGE = 101, PROFILE_GALLERY_IMAGE = 102;
    public static String dd = "";

    public static void displayDialog(String title, String msg, final Context context, final boolean isFinsActivity) {

        if (msg.trim().length() > 1) {

//            final Dialog dialog = new Dialog(context, R.style.TransparantBlackDialog);
//            LayoutInflater li = LayoutInflater.from(context);
//            View view = li.inflate(R.layout.alert_dialog, null);
//            dialog.setContentView(view);
//            dialog.setCancelable(false);
//            Button btnOk;
//            TextView tvTital, tvMsg;
//
//            tvTital = (TextView) view.findViewById(R.id.alert_dialog_tv_tital);
//            tvMsg = (TextView) view.findViewById(R.id.alert_dialog_tv_messahg);
//            btnOk = (Button) view.findViewById(R.id.alert_dialog_btn_ok);
//            tvTital.setText(title);
//
//            tvMsg.setText(Html.fromHtml(msg));
//            tvMsg.setMovementMethod(new ScrollingMovementMethod());
//
//
//            btnOk.setOnClickListener(new OnClickListener() {
//
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    if (isFinsActivity) {
//                        ((Activity) context).finish();
//                    }
//                }
//            });
//            if (!((Activity) context).isFinishing()) {
//                if (!dialog.isShowing()) {
//                    dialog.show();
//                }
//            }
//
//            dialog.show();
        }
    }

    // public static void datePickerDialog(int year1, int month1, final Context
    // context, TextView tvValeus) {
    //
    // final Dialog dialog = new Dialog(context,
    // R.style.TransparantBlackDialog);
    // LayoutInflater li = LayoutInflater.from(context);
    // View view = li.inflate(R.layout.date_picker_dailog, null);
    // dialog.setContentView(view);
    // dialog.setCancelable(false);
    // TextView tvTitle = (TextView)
    // view.findViewById(R.id.datePicker_dailog_questions_tv_title);
    // DatePicker dpDatePicker = (DatePicker)
    // view.findViewById(R.id.datePicker_dailog_questions_datePicker);
    // Button btnSet = (Button)
    // view.findViewById(R.id.datePicker_dailog_questions_btn_set);
    //
    // Calendar cal = Calendar.getInstance();
    //
    // int year = cal.get(Calendar.YEAR);
    // int month = cal.get(Calendar.MONTH);
    // int day = cal.get(Calendar.DAY_OF_MONTH);
    //
    // tvTitle.setText(month + "/" + year);
    //
    // dpDatePicker.updateDate(year, month, day);
    //
    // try {
    // java.lang.reflect.Field[] f =
    // dpDatePicker.getClass().getDeclaredFields();
    // for (java.lang.reflect.Field field : f) {
    // if (field.getName().equals("mDayPicker") ||
    // field.getName().equals("mDaySpinner")) {
    // field.setAccessible(true);
    // Object dmPicker = new Object();
    // dmPicker = field.get(dpDatePicker);
    // ((View) dmPicker).setVisibility(View.GONE);
    //
    // }
    // }
    //
    // dpDatePicker.getMonth();
    // dpDatePicker.getYear();
    //
    // } catch (IllegalAccessException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IllegalArgumentException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    //
    // btnSet.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // // TODO Auto-generated method stub
    // dialog.dismiss();
    // }
    // });
    // dialog.show();
    //
    // }

    public static String setPadding(int data) {
        String result = "";
        if (data < 10)
            result = "0" + data;
        else
            result = String.valueOf(data);
        return result;
    }

    public static int getIndex(Spinner spinner, String myString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); // minus number would decrement the days
        return cal.getTime();
    }

    public static String changeDateFormat(String OLD_PATTERN, String NEW_PATTERN, String date) {

        SimpleDateFormat sdf = new SimpleDateFormat();
        String newDate = "";
        try {
            sdf.applyPattern(OLD_PATTERN);
            Date d = sdf.parse(date);
            sdf.applyPattern(NEW_PATTERN);
            newDate = sdf.format(d);
        } catch (Exception e) {
        }
        return newDate;
    }

    /**
     * method is checking is network availability in device
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean isNetAvailable = false;
        if (context != null) {
            final ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (mConnectivityManager != null) {
                boolean mobileNetwork = false;
                boolean wifiNetwork = false;

                boolean mobileNetworkConnecetd = false;
                boolean wifiNetworkConnecetd = false;

                final NetworkInfo mobileInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                final NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mobileInfo != null) {
                    mobileNetwork = mobileInfo.isAvailable();
                }

                if (wifiInfo != null) {
                    wifiNetwork = wifiInfo.isAvailable();
                }

                if (wifiNetwork || mobileNetwork) {
                    if (mobileInfo != null)
                        mobileNetworkConnecetd = mobileInfo.isConnectedOrConnecting();
                    wifiNetworkConnecetd = wifiInfo.isConnectedOrConnecting();
                }

                isNetAvailable = (mobileNetworkConnecetd || wifiNetworkConnecetd);
            }
        }

        return isNetAvailable;
    }

    public static Calendar getCalendarData(String date) {

        Calendar cal = Calendar.getInstance();

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Integer.parseInt(date.substring(6)), (Integer.parseInt(date.substring(3, 5)) - 1), Integer.parseInt(date.substring(0, 2)));

        return cal;
    }

    /**
     * Comma replace method
     *
     * @param tempProjectCount
     * @return
     */
    public static String newComaReplaceString(String tempProjectCount) {
        // String newString = tempProjectCount.replace(".", ",");
        // return newString;
        return tempProjectCount;
    }

    /**
     * Function is use for Email Id Valid.
     */

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        // String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        // /^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,3})$/
        String expression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNumberValid(String valus) {
        boolean isValid = false;

        // String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        // /^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,3})$/
        String expression = "[0-9]+\\.[0-9]{1,2}";
        CharSequence inputStr = valus;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isNotEmpaty(String valus) {
        boolean isValid = false;
        if (valus.trim().toString().length() > 0) {
            isValid = true;
        }
        return isValid;
    }

    // public static boolean isUserNameValid(String valus, boolean isSpecs) {
    // boolean isValid = true;
    //
    // CharSequence inputStr = valus;
    // Pattern p;
    // if (isSpecs) {
    // p = Pattern.compile("[!%&*()^{}>/<|?:;\"\'=+, ]",
    // Pattern.CASE_INSENSITIVE);
    // } else {
    // p = Pattern.compile("[!%&*()^{}>/<|?:;\"\'=+,]",
    // Pattern.CASE_INSENSITIVE);
    // }
    // Matcher m = p.matcher(inputStr);
    //
    // if (m.find()) {
    // isValid = false;
    // }
    // return isValid;
    // }

    public static String setNameToCapital(String name) {
        String tempName = name.substring(0, 1);
        return tempName.toUpperCase() + name.substring(1);
    }

    public static String setZero(int values) {
        String setString = null;
        if (values < 10) {
            setString = "0" + values;
        } else {
            setString = "" + values;
        }

        return setString;
    }

    /**
     * This function is used for convert UTC date and time to local date time.
     *
     * @return String
     **/
    public static String convertUTCDateTimeToLocal(String date) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            // utcDateTime = null;
            // utcDateTime = sdf.format(sdf.parse(date));

            // gETTING cURRENT tIMEZONE
            Calendar cl = Calendar.getInstance();
            TimeZone localTimeZone = cl.getTimeZone();

            // converting UTC to CURRENT
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy h:mm a");
            sdf2.setTimeZone(localTimeZone);

            result = sdf2.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] getBytes(Bitmap bitmap, String fileName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (fileName.contains(".png")) {
            bitmap.compress(CompressFormat.PNG, 100, stream);
        } else {
            bitmap.compress(CompressFormat.JPEG, 100, stream);
        }
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public static Boolean isVelideDateBoth(String frome, String to) {

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");// 2015-02-10
            // 00:00:00

            Calendar c = Calendar.getInstance();
            String strCurrent = formatter.format(c.getTime());

            if ((formatter.parse(frome)).equals(formatter.parse(strCurrent)) && (formatter.parse(to)).equals(formatter.parse(strCurrent))) {
                return true;
            } else if ((formatter.parse(frome)).before(formatter.parse(strCurrent))
                    && (formatter.parse(to)).after(formatter.parse(strCurrent))) {
                return true;
            } else if ((formatter.parse(frome)).before(formatter.parse(strCurrent))
                    && (formatter.parse(to)).equals(formatter.parse(strCurrent))) {
                return true;
            } else if ((formatter.parse(frome)).equals(formatter.parse(strCurrent))
                    && (formatter.parse(to)).after(formatter.parse(strCurrent))) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean isVelideForToDateBoth(String to) {

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");// 2015-02-10
            // 00:00:00

            Calendar c = Calendar.getInstance();
            String strCurrent = formatter.format(c.getTime());

            if (formatter.parse(to).equals(formatter.parse(strCurrent))) {
                return true;
            } else if (formatter.parse(to).after(formatter.parse(strCurrent))) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static Boolean isVelideForFromDateBoth(String from) {

        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 00:00:00");// 2015-02-10
            // 00:00:00

            Calendar c = Calendar.getInstance();
            String strCurrent = formatter.format(c.getTime());

            if (formatter.parse(from).equals(formatter.parse(strCurrent))) {
                return true;
            } else if (formatter.parse(from).before(formatter.parse(strCurrent))) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }


    public static void Clear_All_Fragment_forall(FragmentManager fragmentmanager) {
        FragmentManager manager = fragmentmanager;
        System.gc();

        Log.e("beck set ui count", manager.getBackStackEntryCount() + "");

        for (int i = 0; i < manager.getBackStackEntryCount(); ++i) {
            manager.popBackStack();
        }
    }


    public static void ClearLastAllFragmentForall(FragmentManager fragmentmanager) {
        FragmentManager manager = fragmentmanager;
        System.gc();

        Log.e("beck set ui count", manager.getBackStackEntryCount() + "");

        for (int i = 1; i < manager.getBackStackEntryCount(); ++i) {
            manager.popBackStack();
        }
    }

    public static void ClearAllNotification(Context context) {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(0);

    }


}
