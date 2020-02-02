package com.elevintech.motorbro.Utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    fun convertDateToMilliseconds(year: Int, month: Int, day: Int, hour: Int, minute: Int, seconds: Int): Long{

        val cal = Calendar.getInstance()
        cal.set(year, month, day, hour, minute, seconds)

        return cal.timeInMillis

    }

    fun convertMillisecondsToDate(dateInMilliseconds: Long , datePatternFormat: String) : String{

        val sdf = SimpleDateFormat("$datePatternFormat") // sample date format: MM/dd/yyyy hh:mm
        val netDate = Date(dateInMilliseconds * 1000)

        return sdf.format(netDate).toString()
    }


    fun getCurrentTimestamp():Long{
        return System.currentTimeMillis() / 1000
    }

    fun getAgeFromDate(): Int{
        return 29
    }

    fun convertDateToTimestamp(date: String, format: String):Long{

        val unixTime = SimpleDateFormat(format).parse( date ).time.toString().toLong()
        val timestamp = unixTime / 1000

        return timestamp

    }

    fun convertFromDuration(timeInSeconds: Long): Map<String, Long> {


        var time = timeInSeconds
        val days = time / 86400
        val hours = time / 3600
        time %= 3600
        val minutes = time / 60
        time %= 60
        val seconds = time


        var timeMap = mapOf(
            "days" to days,
            "hour" to hours,
            "minutes" to minutes,
            "seconds" to seconds
        )

        return timeMap
    }

    fun processLastActiveAgoText(lastActiveInMillis: Long): String {

        val duration = Utils().getCurrentTimestamp() - lastActiveInMillis
        val time = Utils().convertFromDuration(duration)

        var lastActiveAgoText = ""

        if (time["days"]!!.toInt() != 0){
            lastActiveAgoText = time["days"].toString() + " days ago"
        } else if (time["hour"]!!.toInt() != 0){
            lastActiveAgoText = time["hours"].toString() + " hours ago"
        }else if (time["minutes"]!!.toInt() != 0){
            lastActiveAgoText = time["minutes"].toString() + " minutes ago"
        }else if (time["seconds"]!!.toInt() != 0){
            lastActiveAgoText = time["seconds"].toString() + " seconds ago"
        }

        return lastActiveAgoText


    }

    fun showProgressDialog(context: Context, message: String): ProgressDialog{
        var progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()

        return progressDialog
    }

    fun generateQrCodeBitmap(content: String): Bitmap {

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 1000, 1000)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }
}