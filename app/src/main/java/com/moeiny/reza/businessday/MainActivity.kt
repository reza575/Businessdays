package com.moeiny.reza.businessday

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var picker: DatePickerDialog
    lateinit var edt_FirstDate: EditText
    lateinit var edt_SecondDate: EditText
    lateinit var txt_Result: TextView
    lateinit var btn_Calculate: Button
    lateinit var btn_Reset: Button
    var firstDate: Date? = null
    var secondDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edt_FirstDate = findViewById(R.id.edt_firstDate)
        edt_FirstDate.inputType = InputType.TYPE_NULL
        edt_SecondDate = findViewById(R.id.edt_secondDate)
        edt_SecondDate.inputType = InputType.TYPE_NULL
        txt_Result = findViewById(R.id.txt_result)
        btn_Calculate = findViewById(R.id.btn_calculate)
        btn_Reset = findViewById(R.id.btn_reset)
        BusinessDays = ArrayList()
        Hollydays = ArrayList()

        edt_FirstDate.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr.get(Calendar.DAY_OF_MONTH)
            val month = cldr.get(Calendar.MONTH)
            val year = cldr.get(Calendar.YEAR)
            // BusinessDays picker dialog
            picker = DatePickerDialog(this@MainActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    firstDate = null
                    edt_FirstDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    val s1 = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    try {
                        firstDate = SimpleDateFormat("yyyy-MM-dd").parse(s1)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }, year, month, day
            )
            picker.show()
        }


        edt_SecondDate.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr.get(Calendar.DAY_OF_MONTH)
            val month = cldr.get(Calendar.MONTH)
            val year = cldr.get(Calendar.YEAR)

            picker = DatePickerDialog(this@MainActivity,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    secondDate = null
                    edt_SecondDate.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                    val s1 = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    try {
                        secondDate = SimpleDateFormat("yyyy-MM-dd").parse(s1)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }, year, month, day
            )
            picker.show()
        }

       //Calculate Business Days
        btn_Calculate.setOnClickListener {
            BusinessDays.clear()
            Hollydays.clear()
            if (edt_FirstDate.text != null && edt_SecondDate.text != null) {
                val year = firstDate!!.year + 1900
                calculatehollidas(year)
                val count = getWorkingDaysBetweenTwoDates(firstDate, secondDate)
                txt_Result.text = "Businessdays Count : $count"
            }
        }

        btn_Reset.setOnClickListener{
            edt_firstDate.setText("")
            edt_secondDate.setText("")
            firstDate=null
            secondDate=null
            txt_Result.setText("")
        }
    }

    companion object {
        lateinit var BusinessDays: MutableList<Int>
        lateinit var Hollydays: MutableList<Int>

        fun getWorkingDaysBetweenTwoDates(startDate: Date?, endDate: Date?): Int {
            val startCal: Calendar
            val endCal: Calendar

            startDate!!.date = startDate.date + 1
            endDate!!.date = endDate.date - 1

            startCal = Calendar.getInstance()
            startCal.time = startDate
            endCal = Calendar.getInstance()
            endCal.time = endDate

            if (startCal.timeInMillis > endCal.timeInMillis) {
                startCal.time = endDate
                endCal.time = startDate
            }


            // Return 0 if start and end are the same or they are connected to eachother
            if (startCal.timeInMillis > endCal.timeInMillis) {
                return 0
            }

            do {
                if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    BusinessDays.add(startCal.get(Calendar.DAY_OF_YEAR))
                }
                startCal.add(Calendar.DAY_OF_YEAR, 1)

            } while (startCal.timeInMillis <= endCal.timeInMillis)

            for (i in Hollydays.indices) {
                for (j in BusinessDays.indices) {
                    if (BusinessDays[j] === Hollydays[i]) {
                        BusinessDays.removeAt(j)
                    }
                }
            }
            return BusinessDays.size
        }


        fun calculatehollidas(year: Int) {

            //In this function all the Hollydays calculated by their definations :
            // 1. Always on the same day even if it is a weekend (like Anzac Day 25 April every year).
            //2. On the same day as far as it is not a weekend (like New Year 1st of every year unless it
            //is a weekend, then the holiday would be next Monday).
            //3. Certain occurrence on a certain day in a month (like Queen’s Birthday on the second
            //Monday in June every year).

            //List of Hollidays is by attention of google search in site://

            var date = Date()
            var tempCal: Calendar
            tempCal = Calendar.getInstance()
            try {
                date = SimpleDateFormat("yyyy-MM-dd").parse("$year-01-01")
            } catch (e: ParseException) {
                e.printStackTrace()
            }


            tempCal.time = date
            if (tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 2)
            } else if (tempCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 1)
            } else {
                Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR))  //    01/01/
            }

            try {
                date = SimpleDateFormat("yyyy-MM-dd").parse("$year-01-28")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tempCal.time = date
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR))  //    28/01/

            try {
                date = SimpleDateFormat("yyyy-MM-dd").parse("$year-04-19")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tempCal.time = date
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR))    //       19/04/
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 1)   //    20/04/
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 2)   //    21/04/
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 3)   //    22/04/
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 6)   //    25/04/

            try {
                date = SimpleDateFormat("yyyy-MM-dd").parse("$year-06-01")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            var temp = 0
            tempCal = Calendar.getInstance()
            tempCal.time = date
            var m = tempCal.get(Calendar.DAY_OF_WEEK)
            while (temp != 2) {
                if (m == 1) {
                    temp++
                }
                tempCal.add(Calendar.DAY_OF_YEAR, 1)
                m = tempCal.get(Calendar.DAY_OF_WEEK)
            }
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR))   //    Queen's Birthday

            try {
                date = SimpleDateFormat("yyyy-MM-dd").parse("$year-10-07")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tempCal.time = date
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR))

            try {
                date = SimpleDateFormat("yyyy-MM-dd").parse("$year-12-25")
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            tempCal.time = date
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR))         //   25/12/
            Hollydays.add(tempCal.get(Calendar.DAY_OF_YEAR) + 1)     //   26/12/
        }
    }
}
