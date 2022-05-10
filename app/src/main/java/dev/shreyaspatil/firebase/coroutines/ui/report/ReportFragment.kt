package dev.shreyaspatil.firebase.coroutines.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.dekape.core.utils.logD
import com.dekape.core.utils.toCalendar
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentReportBinding
import dev.shreyaspatil.firebase.coroutines.model.GeneralModel
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.base.BaseFragment
import dev.shreyaspatil.firebase.coroutines.ui.order.detail.PdfCreateActivity
import dev.shreyaspatil.firebase.coroutines.utils.State
import dev.shreyaspatil.firebase.coroutines.utils.Utils
import dev.shreyaspatil.firebase.coroutines.utils.onClick
import dev.shreyaspatil.firebase.coroutines.utils.toDate
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ReportFragment : BaseFragment<FragmentReportBinding>() {
    companion object {
        fun directionToMe(view: View?){
            /*view?.let {
                val action = NilaiAcademyFragmentDirections.actionNilaiAcademyFragmentToJadwalPelajaranFragment(dataStudent)
                Navigation.findNavController(it).navigate(action)
            }*/
        }

    }

    override fun getLayoutId(): Int = R.layout.fragment_report
    private var listItemReport = ArrayList<Orders>()
    private var selectedYear = Utils.currentYears
    private val reportRvAdapter:ReportRvAdapters by lazy { ReportRvAdapters() }
    private val listSemester = listOf(GeneralModel(1,"Januari"), GeneralModel(2,"Februari"),
        GeneralModel(3,"Maret"),
        GeneralModel(4,"April"),
        GeneralModel(5,"Mei"),
        GeneralModel(6,"Juni"),
        GeneralModel(7,"Juli"),
        GeneralModel(8,"Agustus"),
        GeneralModel(9,"September"),
        GeneralModel(10,"Oktober"),
        GeneralModel(11,"November"),
        GeneralModel(12,"Desember"),
    )
    private var selectedMonth = listSemester.get(Utils.currentMonth.toInt()-1)
    private val month :String = if (selectedMonth.id<9) "0"+selectedMonth.id else selectedMonth.id.toString()



    private lateinit var binding:FragmentReportBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(inflater,container,false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerview.adapter = reportRvAdapter


            btnDownload.setOnClickListener {
                if (listItemReport.size > 0)
                    PdfCreateActivity.getStaredIntent(activity as AppCompatActivity, listItemReport,selectedMonth.name,selectedYear)
                else
                    toast("Tidak Dapat Menggenerate Data Kosong")
            }

        }





        getReport(selectedYear,selectedMonth)

    }



   fun getReport(selectedYear:String,selectedMonth:GeneralModel) {

       this.selectedMonth = selectedMonth
       val calendarFrom = Calendar.getInstance()
       calendarFrom.set(Calendar.YEAR,selectedYear.toInt())
       calendarFrom.set(Calendar.MONTH,selectedMonth.id-1)
       calendarFrom.set(Calendar.DAY_OF_MONTH,1)

       val calendarTo = Calendar.getInstance()
       calendarTo.set(Calendar.YEAR,selectedYear.toInt())
       calendarTo.set(Calendar.MONTH,selectedMonth.id-1)
       if ((selectedMonth.id-1) == 2 && selectedYear.toInt() % 4 == 0)
           calendarTo.set(Calendar.DAY_OF_MONTH,28)
       else
           calendarTo.set(Calendar.DAY_OF_MONTH,31)


       uiScope.launch {
           orderViewsModel.getAllOrdersFilterDate(calendarFrom.time,calendarTo.time).collect {
               when(it){
                   is State.Loading -> showLoadingDialog()
                   is State.Failed -> {
                       dismissLoadingDialog()
                       showErrorDialog(it.message)
                   }
                   is State.Success -> {
                       dismissLoadingDialog()

                       listItemReport.addAll(it.data)
                       reportRvAdapter.set(it.data as ArrayList<Orders>)

                       logD("data order : ${it.data}")

                       if(!it.data.isEmpty()){
                           //show
                           binding.recyclerview.visible()
                           binding.viewError.gone()
                       }else{
                           //no data
                           binding.recyclerview.gone()
                           binding.viewError.visible()
                       }

                   }
               }
           }
       }
    }

    override fun onResume() {
        super.onResume()
        setupSemesterAdapter()
        setupYearsAdapter()
    }
    fun setupSemesterAdapter() {
        var newList = ArrayList<String>()
        listSemester.forEach { it.name?.let {
            newList.add(it) } }


        val mAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            newList
        )

        binding?.txtDate.apply {
            setText(selectedMonth.name)
            setAdapter(mAdapter)
            onClick {
                showDropDown()
            }
            onItemClickListener = AdapterView.OnItemClickListener { adaperView, ivew, i, l ->
                //change jadwal pelajaran semester
                selectedMonth = listSemester[i]
                getReport(selectedYear,selectedMonth)

            }
        }

    }

    fun setupYearsAdapter() {
        val listyear = ArrayList<Int>()
        var currentYear = Utils.currentYears.toInt()
        for (i in currentYear-5..currentYear){
            listyear.add(i)
        }



        val mAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listyear
        )

        binding?.txtDateYear.apply {
            setText(selectedYear)
            setAdapter(mAdapter)
            onClick {
                showDropDown()
            }
            onItemClickListener = AdapterView.OnItemClickListener { adaperView, ivew, i, l ->
                //change jadwal pelajaran year
                reportRvAdapter.clear()
                ArrayList(listItemReport).clear()
                //ArrayList(listItemReport).addAll(ArrayList(viewModel.subjectScheduleLiveData.value))
                selectedYear = listyear[i].toString()
                getReport(selectedYear,selectedMonth);

            }
        }
    }
}