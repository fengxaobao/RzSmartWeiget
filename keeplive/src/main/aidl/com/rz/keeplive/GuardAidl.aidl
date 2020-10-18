// GuardAidl.aidl
package com.rz.keeplive;

// Declare any non-default types here with import statements

interface GuardAidl {

     //相互唤醒服务
     void wakeUp(String title, String discription, int iconRes);
}
