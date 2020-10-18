package com.rz.utils.interfaces

import java.io.File

/**
 * @author Vondear
 * @date 2017/8/9
 */

interface OnRxCamera {

    fun onBefore()

    fun onSuccessCompress(filePhoto: File)

    fun onSuccessExif(filePhoto: File)
}