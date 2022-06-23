package com.wisa.eOurPetshop.ui.order

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.dekape.core.utils.currentDate
import com.dekape.core.utils.toCalendar
import com.dekape.core.utils.toPriceFormat
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity
import com.tejpratapsingh.pdfcreator.utils.PDFUtil.PDFUtilListener
import com.tejpratapsingh.pdfcreator.views.PDFBody
import com.tejpratapsingh.pdfcreator.views.PDFFooterView
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView
import com.tejpratapsingh.pdfcreator.views.PDFTableView
import com.tejpratapsingh.pdfcreator.views.PDFTableView.PDFTableRowView
import com.tejpratapsingh.pdfcreator.views.basic.*
import com.wisa.eOurPetshop.R
import com.wisa.eOurPetshop.model.Orders
import com.wisa.eOurPetshop.model.PdfOrders
import java.io.File
import java.lang.Exception
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList


class PdfCreateActivity : PDFCreatorActivity() {

    private lateinit var listOrder:ArrayList<Orders>
    val dataTablesRow = arrayListOf<PdfOrders>()
    private lateinit var selectedMonth : String
    private lateinit var selectedYear : String


    companion object{
        fun getStaredIntent(activity: AppCompatActivity, orders: ArrayList<Orders>, selectedMonth:String, selectedYear:String){
            Intent(activity, PdfCreateActivity::class.java)
                .putExtra("data",orders)
                .putExtra("selectedMonth",selectedMonth)
                .putExtra("selectedYear",selectedYear)
                .run { activity?.startActivity(this) }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.show()
        }

        findViewById<Button>(R.id.buttonSendEmail).run {
            text  = "Bagikan"
            setBackgroundColor(resources.getColor(R.color.colorPrimary))
            setTextColor(resources.getColor(R.color.white))


        }

        listOrder = intent.getParcelableArrayListExtra<Orders>("data") as ArrayList<Orders>
        selectedMonth = intent.getStringExtra("selectedMonth") as String
        selectedYear = intent.getStringExtra("selectedYear") as String
        listOrder.forEachIndexed { index, orders ->
            dataTablesRow.add(
                PdfOrders(
                    (index+1).toString(),
                    orders.localeDateNoMonthNameCheckIn.toString(),
                    orders.localeDateddNoMonthNameCheckOut.toString(),
                    orders.namaPelangan,
                    orders.namaPeliharaan,
                    orders.noHpPengirim,
                    orders.catatan,
                    orders.namaPaket,
                    orders.price,
                    orders.status)
            )
        }

        createPDF(currentDate.toCalendar().time.toString(), object : PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
                Toast.makeText(this@PdfCreateActivity, "PDF Report Created", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(
                    this@PdfCreateActivity,
                    "PDF Rerport Fail to Create ${exception.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)
        val horizontalView = PDFHorizontalView(applicationContext)
        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val word = SpannableString("Report")
        word.setSpan(
            ForegroundColorSpan(Color.DKGRAY),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        pdfTextView.text = word
        pdfTextView.setLayout(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
        )
        pdfTextView.view.gravity = Gravity.CENTER_VERTICAL
        pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
        horizontalView.addView(pdfTextView)
        val imageView = PDFImageView(applicationContext)
        val imageLayoutParam = LinearLayout.LayoutParams(
            60,
            60, 0f
        )
        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE)
        imageView.setImageResource(R.mipmap.ic_launcher)
        imageLayoutParam.setMargins(0, 0, 10, 0)
        imageView.setLayout(imageLayoutParam)
        horizontalView.addView(imageView)
        headerView.addView(horizontalView)
        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        headerView.addView(lineSeparatorView1)
        return headerView
    }

    override fun getBodyViews(): PDFBody {
        val pdfBody = PDFBody()
        val pdfCompanyNameView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfCompanyNameView.setText("Laporan Bulanan "+resources.getString(R.string.app_name)+" $selectedMonth $selectedYear")
        pdfBody.addView(pdfCompanyNameView)
        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        pdfBody.addView(lineSeparatorView1)

        val lineSeparatorView2 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        lineSeparatorView2.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0f
            )
        )
        pdfBody.addView(lineSeparatorView2)
        val lineSeparatorView3 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        pdfBody.addView(lineSeparatorView3)

        val widthPercent = intArrayOf(3, 11, 11,18,16,15,14,12) // Sum should be equal to 100%


        val tableHeader = PDFTableRowView(applicationContext)
        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView1 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView2 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView3 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView4 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView5 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView6 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
        val pdfTextView7 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)

        pdfTextView.setText("N")
        pdfTextView1.setText("Tgl Masuk")
        pdfTextView2.setText("Tgl Keluar")
        pdfTextView3.setText("Nama ")
        pdfTextView4.setText("Nomor HP")
        pdfTextView5.setText("N Hewan")
        pdfTextView6.setText("Harga")
        pdfTextView7.setText("Status")

        tableHeader.addToRow(pdfTextView)
        tableHeader.addToRow(pdfTextView1)
        tableHeader.addToRow(pdfTextView2)
        tableHeader.addToRow(pdfTextView3)
        tableHeader.addToRow(pdfTextView4)
        tableHeader.addToRow(pdfTextView5)
        tableHeader.addToRow(pdfTextView6)
        tableHeader.addToRow(pdfTextView7)




        if (dataTablesRow.size > 0){
            val tableRowView1 = PDFTableRowView(applicationContext)
            val tableRowView2 = PDFTableRowView(applicationContext)
           // for (j in 0..8) {
                val pdfTextViewRow = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow1 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow2 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow3 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow4 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow5 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow6 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                val pdfTextViewRow7 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)

            pdfTextViewRow.setText(dataTablesRow[0].no)
            pdfTextViewRow1.setText(dataTablesRow[0].timeCheckIn)
            pdfTextViewRow2.setText(dataTablesRow[0].timeCheckOut)
            pdfTextViewRow3.setText(dataTablesRow[0].namaPelangan)
            pdfTextViewRow4.setText(dataTablesRow[0].noHpPengirim)
            pdfTextViewRow5.setText(dataTablesRow[0].namaHewan)
            pdfTextViewRow6.setText(dataTablesRow[0].price.toPriceFormat())
            pdfTextViewRow7.setText(dataTablesRow[0].status)


            tableRowView1.addToRow(pdfTextViewRow)
                tableRowView1.addToRow(pdfTextViewRow1)
                tableRowView1.addToRow(pdfTextViewRow2)
                tableRowView1.addToRow(pdfTextViewRow3)
                tableRowView1.addToRow(pdfTextViewRow4)
                tableRowView1.addToRow(pdfTextViewRow5)
                tableRowView1.addToRow(pdfTextViewRow6)
                tableRowView1.addToRow(pdfTextViewRow7)
           // }

            var tableView = PDFTableView(applicationContext, tableHeader, tableRowView1)

                dataTablesRow.forEachIndexed { index, pdfOrders ->
                    //pass index 0
                    if (index > 0){
                        val tableRowView = PDFTableRowView(applicationContext)
                        val pdfTextViewRowContinue =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue1 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue2 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue3 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue4 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue5 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue6 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        val pdfTextViewRowContinue7 =
                            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                        pdfTextViewRowContinue.setText(pdfOrders.no)
                        pdfTextViewRowContinue1.setText(pdfOrders.timeCheckIn)
                        pdfTextViewRowContinue2.setText(pdfOrders.timeCheckOut)
                        pdfTextViewRowContinue3.setText(pdfOrders.namaPelangan)
                        pdfTextViewRowContinue4.setText(pdfOrders.noHpPengirim)
                        pdfTextViewRowContinue5.setText(pdfOrders.namaHewan)
                        pdfTextViewRowContinue6.setText(pdfOrders.price.toPriceFormat())
                        pdfTextViewRowContinue7.setText(pdfOrders.status)
                        tableRowView.addToRow(pdfTextViewRowContinue)
                        tableRowView.addToRow(pdfTextViewRowContinue1)
                        tableRowView.addToRow(pdfTextViewRowContinue2)
                        tableRowView.addToRow(pdfTextViewRowContinue3)
                        tableRowView.addToRow(pdfTextViewRowContinue4)
                        tableRowView.addToRow(pdfTextViewRowContinue5)
                        tableRowView.addToRow(pdfTextViewRowContinue6)
                        tableRowView.addToRow(pdfTextViewRowContinue7)
                        tableView.addRow(tableRowView)
                    }
                }
            tableView.setColumnWidth(*widthPercent)
            pdfBody.addView(tableView)
            val lineSeparatorView4 =
                PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.BLACK)
            pdfBody.addView(lineSeparatorView4)
        }

        return pdfBody
    }

    override fun getFooterView(pageIndex: Int): PDFFooterView {
        val footerView = PDFFooterView(applicationContext)
        val pdfTextViewPage = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1))
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL
        footerView.addView(pdfTextViewPage)
        return footerView
    }

    override fun getWatermarkView(forPage: Int): PDFImageView? {
        val pdfImageView = PDFImageView(applicationContext)
        val childLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            200, Gravity.CENTER
        )
        pdfImageView.setLayout(childLayoutParams)
        pdfImageView.setImageResource(R.drawable.logo)
        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER)
        pdfImageView.view.alpha = 0.3f
        return pdfImageView
    }

    override fun onNextClicked(savedPDFFile: File) {
        if (savedPDFFile == null || !savedPDFFile.exists()) {
            Toast.makeText(this, "Pdf Share Error", Toast.LENGTH_SHORT).show()
            return
        }
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val apkURI = FileProvider.getUriForFile(
            applicationContext,
            applicationContext
                .packageName + ".provider", savedPDFFile
        )
        intentShareFile.setDataAndType(
            apkURI,
            URLConnection.guessContentTypeFromName(savedPDFFile.name)
        )
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intentShareFile.putExtra(
            Intent.EXTRA_STREAM,
            apkURI
        )
        startActivity(Intent.createChooser(intentShareFile, "Share File"))
    }
}