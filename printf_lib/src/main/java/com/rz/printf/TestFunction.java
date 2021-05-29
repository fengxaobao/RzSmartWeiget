package com.rz.printf;

import android.app.Activity;
import android.graphics.Bitmap;

import com.caysn.autoreplyprint.AutoReplyPrint;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestFunction {

    public static String[] testFunctionOrderedList = new String[]{
            "Test_Label_SampleTicket_58MM_1",
            "Test_Label_SampleTicket_58MM_2",
            "Test_Label_SampleTicket_80MM_1",

            "Test_Port_Write",
            "Test_Port_Read",
            "Test_Port_Available",
            "Test_Port_SkipAvailable",
            "Test_Port_IsConnectionValid",

            "Test_Printer_GetPrinterInfo",
            "Test_Printer_ClearPrinterBuffer",
            "Test_Printer_ClearPrinterError",

            "Test_Pos_QueryPrintResult",

            "Test_Pos_KickOutDrawer",
            "Test_Pos_Beep",

            "Test_Pos_PrintSelfTestPage",

            "Test_Pos_ResetPrinter",
            "Test_Pos_SetPrintSpeed_20",
            "Test_Pos_SetPrintSpeed_50",
            "Test_Pos_SetPrintSpeed_100",
            "Test_Pos_SetPrintDensity_0",
            "Test_Pos_SetPrintDensity_7",
            "Test_Pos_SetPrintDensity_15",

            "Test_Label_EnableLabelMode",
            "Test_Label_DisableLabelMode",
            "Test_Label_CalibrateLabel",
            "Test_Label_FeedLabel",
            "Test_Label_PageBegin_PagePrint",
            "Test_Label_DrawText",
            "Test_Label_DrawBarcode_UPCA",
            "Test_Label_DrawBarcode_UPCE",
            "Test_Label_DrawBarcode_EAN13",
            "Test_Label_DrawBarcode_EAN8",
            "Test_Label_DrawBarcode_CODE39",
            "Test_Label_DrawBarcode_ITF",
            "Test_Label_DrawBarcode_CODEBAR",
            "Test_Label_DrawBarcode_CODE93",
            "Test_Label_DrawBarcode_CODE128",
            "Test_Label_DrawQRCode",
            "Test_Label_DrawPDF417Code",
            "Test_Label_DrawLine",
            "Test_Label_DrawRect",
            "Test_Label_DrawBox",
            "Test_Label_DrawImageFromBitmap",
    };

    public Activity ctx = null;

    private static int nPageID = 1;

    public void Test_Label_SampleTicket_58MM_1(Pointer h, String weight)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_UTF8);

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 200, 0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 200, 1, 1);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 10, 24, 0, "白菜：123");
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 40, 24, 0, "重量 :"+weight+"Kg");
        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 70, AutoReplyPrint.CP_Label_BarcodeType_CODE128, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, 0, "No.300202");
        AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);

        {
            Test_Pos_QueryPrintResult(h);
        }
    }

    public void Test_Label_SampleTicket_58MM_2(Pointer h)
    {
        Bitmap bitmap = TestUtils.getPrintBitmap(ctx, 384, 400);

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, bitmap.getWidth(), bitmap.getHeight(), 0);
        AutoReplyPrint.CP_Label_DrawImageFromData_Helper.DrawImageFromBitmap(h,0,0,bitmap.getWidth(),bitmap.getHeight(),bitmap,AutoReplyPrint.CP_ImageBinarizationMethod_Thresholding);
        AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);

        {
            Test_Pos_QueryPrintResult(h);
        }
    }

    void Test_Label_SampleTicket_80MM_1(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteMode(h);
        AutoReplyPrint.INSTANCE.CP_Pos_SetMultiByteEncoding(h, AutoReplyPrint.CP_MultiByteEncoding_UTF8);

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 576, 240, 0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 576, 240, 1, 1);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 10, 24, 0, "型号：P58A+");
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 40, 24, 0, "MFG ：00");
        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 70, AutoReplyPrint.CP_Label_BarcodeType_CODE128, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, 0, "No.123456");
        AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);

        {
            Test_Pos_QueryPrintResult(h);
        }
    }

    void Test_Port_Write(Pointer h)
    {
        byte cmd[] = { 0x12, 0x54 };
        if (AutoReplyPrint.INSTANCE.CP_Port_Write(h, cmd, cmd.length, 1000) != cmd.length)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Port_Read(Pointer h)
    {
        // send this cmd to query printer status
        byte cmd[] = { 0x10, 0x04, 0x01 };
        AutoReplyPrint.INSTANCE.CP_Port_SkipAvailable(h);
        if (AutoReplyPrint.INSTANCE.CP_Port_Write(h, cmd, cmd.length, 1000) == cmd.length) {
            byte status[] = new byte[1];
            if (AutoReplyPrint.INSTANCE.CP_Port_Read(h, status, 1, 2000) == 1) {
                TestUtils.showMessageOnUiThread(ctx, String.format("Status 0x%02X", status[0] & 0xff));
            } else {
                TestUtils.showMessageOnUiThread(ctx, "Read failed");
            }
        } else {
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
        }
    }

    void Test_Port_Available(Pointer h)
    {
        int available = AutoReplyPrint.INSTANCE.CP_Port_Available(h);
        TestUtils.showMessageOnUiThread(ctx, "available " + available);
    }

    void Test_Port_SkipAvailable(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Port_SkipAvailable(h);
    }

    void Test_Port_IsConnectionValid(Pointer h)
    {
        boolean valid = AutoReplyPrint.INSTANCE.CP_Port_IsConnectionValid(h);
        TestUtils.showMessageOnUiThread(ctx, "valid " + valid);
    }

    void Test_Printer_GetPrinterInfo(Pointer h) {
        String firmware_version = AutoReplyPrint.CP_Printer_GetPrinterFirmwareVersion_Helper.GetPrinterFirmwareVersion(h) + "\r\n";
        IntByReference width_mm = new IntByReference();
        IntByReference height_mm = new IntByReference();
        IntByReference dots_per_mm = new IntByReference();
        LongByReference printer_error_status = new LongByReference();
        LongByReference printer_info_status = new LongByReference();
        IntByReference printer_received_byte_count = new IntByReference();
        IntByReference printer_printed_page_id = new IntByReference();
        LongByReference timestamp_ms_printer_status = new LongByReference();
        LongByReference timestamp_ms_printer_received = new LongByReference();
        LongByReference timestamp_ms_printer_printed = new LongByReference();
        if (AutoReplyPrint.INSTANCE.CP_Printer_GetPrinterResolutionInfo(h, width_mm, height_mm, dots_per_mm) &&
                AutoReplyPrint.INSTANCE.CP_Printer_GetPrinterStatusInfo(h, printer_error_status, printer_info_status, timestamp_ms_printer_status) &&
                AutoReplyPrint.INSTANCE.CP_Printer_GetPrinterReceivedInfo(h, printer_received_byte_count, timestamp_ms_printer_received) &&
                AutoReplyPrint.INSTANCE.CP_Printer_GetPrinterPrintedInfo(h, printer_printed_page_id, timestamp_ms_printer_printed)) {
            Date dt_printer_status = new Date(timestamp_ms_printer_status.getValue());
            Date dt_printer_received = new Date(timestamp_ms_printer_received.getValue());
            Date dt_printer_printed = new Date(timestamp_ms_printer_printed.getValue());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str_printer_resolution = "Width: " + width_mm.getValue() + " Height: " + height_mm.getValue() + " DotsPerMM: " + dots_per_mm.getValue() + "\r\n";
            String str_printer_error_status = simpleDateFormat.format(dt_printer_status) + String.format(" Printer Error Status: 0x%04X\r\n", printer_error_status.getValue() & 0xffff);
            String str_printer_info_status = simpleDateFormat.format(dt_printer_status) + String.format(" Printer Error Status: 0x%04X\r\n", printer_info_status.getValue() & 0xffff);
            String str_printer_received = simpleDateFormat.format(dt_printer_received) + String.format(" Printer Received Byte Count: %d\r\n", printer_received_byte_count.getValue());
            String str_printer_printed = simpleDateFormat.format(dt_printer_printed) + String.format(" Printer Printed Page ID: %d\r\n", printer_printed_page_id.getValue());
            TestUtils.showMessageOnUiThread(ctx, firmware_version + str_printer_resolution + str_printer_error_status + str_printer_info_status + str_printer_received + str_printer_printed);
        }
    }

    void Test_Printer_ClearPrinterBuffer(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Printer_ClearPrinterBuffer(h);
    }

    void Test_Printer_ClearPrinterError(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Printer_ClearPrinterError(h);
    }

    void Test_Pos_QueryPrintResult(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_QueryPrintResult(h, nPageID++, 30000);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Print failed");
        else
            TestUtils.showMessageOnUiThread(ctx, "Print Success");
    }

    void Test_Pos_KickOutDrawer(Pointer h) {
        AutoReplyPrint.INSTANCE.CP_Pos_KickOutDrawer(h, 0, 100, 100);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_KickOutDrawer(h, 1, 100, 100);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_Beep(Pointer h) {
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_Beep(h, 3, 300);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_PrintSelfTestPage(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_ResetPrinter(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_ResetPrinter(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_SetPrintSpeed_20(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetPrintSpeed(h, 20);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_SetPrintSpeed_50(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetPrintSpeed(h, 50);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_SetPrintSpeed_100(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetPrintSpeed(h, 100);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Pos_SetPrintDensity_0(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetPrintDensity(h, 0);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Pos_SetPrintDensity_7(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetPrintDensity(h, 7);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Pos_SetPrintDensity_15(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Pos_SetPrintDensity(h, 15);
        boolean result = AutoReplyPrint.INSTANCE.CP_Pos_PrintSelfTestPage(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_EnableLabelMode(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_EnableLabelMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DisableLabelMode(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_DisableLabelMode(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_CalibrateLabel(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_CalibrateLabel(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_FeedLabel(Pointer h)
    {
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_FeedLabel(h);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_PageBegin_PagePrint(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawText(Pointer h)
    {
        String str = "$$$$$$$$$$$$";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 10, 24, 0, str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 40, 24,
                new AutoReplyPrint.CP_Label_TextStyle(true, true, false, false,
                        AutoReplyPrint.CP_Label_Rotation_0, 1, 1).getStyle(),
                str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 10, 70, 24,
                new AutoReplyPrint.CP_Label_TextStyle(false, false, false, false,
                        AutoReplyPrint.CP_Label_Rotation_0, 2, 2).getStyle(),
                str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 30, 130, 24,
                new AutoReplyPrint.CP_Label_TextStyle(false, false, false, false,
                        AutoReplyPrint.CP_Label_Rotation_90, 1, 1).getStyle(),
                str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 40, 200, 26, 0, str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 40, 240, 28, 0, str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 40, 280, 16, 0, str);
        AutoReplyPrint.INSTANCE.CP_Label_DrawText(h, 40, 320, 21, 0, str);
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawBarcode_UPCA(Pointer h)
    {
        String str = "01234567890";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_UPCA, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_UPCE(Pointer h)
    {
        String str = "123456";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_UPCE, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_EAN13(Pointer h)
    {
        String str = "123456789012";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_EAN13, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_EAN8(Pointer h)
    {
        String str = "1234567";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_EAN8, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_CODE39(Pointer h)
    {
        String str = "123456";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_CODE39, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_ITF(Pointer h)
    {
        String str = "123456";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_ITF, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_CODEBAR(Pointer h)
    {
        String str = "A123456A";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_CODEBAR, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_CODE93(Pointer h)
    {
        String str = "123456";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_CODE93, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }
    void Test_Label_DrawBarcode_CODE128(Pointer h)
    {
        String str = "No.123456";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawBarcode(h, 10, 10, AutoReplyPrint.CP_Label_BarcodeType_CODE128, AutoReplyPrint.CP_Label_BarcodeTextPrintPosition_BelowBarcode, 60, 2, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawQRCode(Pointer h)
    {
        String str = "Hello 你好";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawQRCode(h, 10, 10, 0, AutoReplyPrint.CP_QRCodeECC_L, 8, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawPDF417Code(Pointer h)
    {
        String str = "Hello 你好";

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);

        AutoReplyPrint.INSTANCE.CP_Label_DrawPDF417Code(h, 10, 10, 3, 3, 0, 3, AutoReplyPrint.CP_Label_Rotation_0, str);

        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawLine(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);
        AutoReplyPrint.INSTANCE.CP_Label_DrawLine(h, 20, 20, 100, 300, 1, AutoReplyPrint.CP_Label_Color_Black);
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawRect(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);
        AutoReplyPrint.INSTANCE.CP_Label_DrawRect(h, 20, 20, 200, 10, AutoReplyPrint.CP_Label_Color_Black);
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawBox(Pointer h)
    {
        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, 384, 400, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, 384, 400, 1, AutoReplyPrint.CP_Label_Color_Black);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 30, 30, 300, 200, 1, AutoReplyPrint.CP_Label_Color_Black);
        boolean result = AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        if (!result)
            TestUtils.showMessageOnUiThread(ctx, "Write failed");
    }

    void Test_Label_DrawImageFromBitmap(Pointer h)
    {
        Bitmap bitmap = TestUtils.getImageFromAssetsFile(ctx, "RasterImage/blackwhite.png");
        if ((bitmap == null) || (bitmap.getWidth() == 0) || (bitmap.getHeight() == 0))
            return;

        int printwidth = 384;
        int dstw = printwidth;
        int dsth = (int) (dstw * ((double) bitmap.getHeight() / bitmap.getWidth()));

        AutoReplyPrint.INSTANCE.CP_Label_PageBegin(h, 0, 0, dstw, dsth, AutoReplyPrint.CP_Label_Rotation_0);
        AutoReplyPrint.INSTANCE.CP_Label_DrawBox(h, 0, 0, dstw, dsth, 1, AutoReplyPrint.CP_Label_Color_Black);
        AutoReplyPrint.CP_Label_DrawImageFromData_Helper.DrawImageFromBitmap(h, 0, 0, dstw, dsth, bitmap, AutoReplyPrint.CP_ImageBinarizationMethod_Thresholding);
        AutoReplyPrint.INSTANCE.CP_Label_PagePrint(h, 1);
        Test_Pos_QueryPrintResult(h);
    }

}
