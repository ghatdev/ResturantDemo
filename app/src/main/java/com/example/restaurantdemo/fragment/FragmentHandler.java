package com.example.restaurantdemo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.restaurantdemo.common.AppLog;


public class FragmentHandler {

    public static String RegId = "";

    public enum ANIMATION_TYPE {
        SLIDE_UP_TO_DOWN, SLIDE_DOWN_TO_UP, SLIDE_LEFT_TO_RIGHT, SLIDE_RIGHT_TO_LEFT, NONE
    }

    /**
     * Add Fragment
     *
     * @param fragment
     * @param fragmentToTarget
     * @param bundle
     * @param isAddToBackStack
     * @param tag
     * @param requestcode
     * @param animationType
     */
    public void addFragment(Activity activity, int frameId, Fragment fragment, Fragment fragmentToTarget, Bundle bundle, boolean isAddToBackStack, String tag, int requestcode, ANIMATION_TYPE animationType) {

        //If Already fragment is open so not call again
        AppLog.Log("Add in fragment Count", "add new fragment");

//       if (isOpenFragment(activity,tag))
//        {
        FragmentTransaction fragTrans = activity.getFragmentManager().beginTransaction();
//            switch (animationType){
//                case SLIDE_DOWN_TO_UP:
//                    fragTrans.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down);
//                    break;
//                case SLIDE_UP_TO_DOWN:
//                    fragTrans.setCustomAnimations(R.anim.slide_out_down,R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up);
//                    break;
//                case SLIDE_LEFT_TO_RIGHT:
//                    fragTrans.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
//                    break;
//                case SLIDE_RIGHT_TO_LEFT:
//                    fragTrans.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right);
//                    break;
//                default:
//                    break;
//            }

        //Pass data between fragment
        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        //Set Target fragment
        if (fragmentToTarget != null) {
            fragment.setTargetFragment(fragmentToTarget, requestcode);
        }

        //If you need to add back stack so put here
        if (isAddToBackStack) {
            fragTrans.addToBackStack(tag);
        }

        fragTrans.add(frameId, fragment, tag);
        fragTrans.commit();
    }
//    }

    /**
     * Replace Fragment
     *
     * @param fragment
     * @param fragmentToTarget
     * @param bundle
     * @param isAddToBackStack
     * @param tag
     * @param requestcode      for get value from previous fragment
     * @param animationType
     */
    public void replaceFragment(Activity activity, int frameId, Fragment fragment, Fragment fragmentToTarget, Bundle bundle, boolean isAddToBackStack, String tag, int requestcode, ANIMATION_TYPE animationType) {

        //If Already fragment is open so not call again

        if (isOpenFragment(activity, tag)) {
//            FragmentTransaction fragTrans = activity.getFragmentManager().beginTransaction();
            FragmentTransaction fragTrans = activity.getFragmentManager().beginTransaction();
//            switch (animationType){
//                case SLIDE_DOWN_TO_UP:
//                    fragTrans.setCustomAnimations(R.anim.slide_in_up,R.anim.slide_out_down, R.anim.slide_in_up, R.anim.slide_out_down);
//                    break;
//                case SLIDE_UP_TO_DOWN:
//                    fragTrans.setCustomAnimations(R.anim.slide_out_down,R.anim.slide_in_up, R.anim.slide_out_down, R.anim.slide_in_up);
//                    break;
//                case SLIDE_LEFT_TO_RIGHT:
//                    fragTrans.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
//                    break;
//                case SLIDE_RIGHT_TO_LEFT:
//                    fragTrans.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
//                    break;
//                default:
//                    break;
//            }

            //Pass data between fragment
            if (bundle != null) {
                fragment.setArguments(bundle);
            }

            //Set Target fragment
            if (fragmentToTarget != null) {
                fragment.setTargetFragment(fragmentToTarget, requestcode);
            }

            //If you need to add back stack so put here
            if (isAddToBackStack) {
                fragTrans.addToBackStack(tag);
            }

            fragTrans.replace(frameId, fragment, tag);
            fragTrans.commit();
        }
    }

    //Check fragment is already opened or not
    public boolean isOpenFragment(Activity activity, String fragmentName) {
//        Util.hideSoftKeyboard(activity);
        try {
            if (!activity.getFragmentManager().findFragmentByTag(fragmentName).isVisible()) {
                return true;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public void removeFragment(Activity activity, int frameID) {
        Fragment fragmnt = activity.getFragmentManager().findFragmentById(frameID);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.remove(fragmnt);
        ft.commit();
    }
}