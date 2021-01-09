package com.rz.smart;

import java.util.ArrayList;

public class ExtendTest {
    public static void main(String[] args) {

    }
    //java 是单继承多实现的 所以规定 extends 后如果要继承只能放到了首位 & 可以是多个接口
    public <T extends ArrayList & Comparable> T getSum(T a, T b){
         if(a.compareTo(b)>0 ) return a; else return b;
    }
}
