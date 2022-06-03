package dev.shreyaspatil.firebase.coroutines.ui.dashboard.admin


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.dekape.core.utils.toast
import com.ferfalk.simplesearchview.SimpleSearchView
import com.ferfalk.simplesearchview.utils.DimensUtils.convertDpToPx
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog

import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment.Companion.DATE_CODE_FROM
import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment.Companion.DATE_CODE_TO
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.base.BaseActivity
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentDashboardSupirBinding
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.ui.order.UpdateOrderActivity
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import dev.shreyaspatil.firebase.coroutines.utils.State
import dev.shreyaspatil.firebase.coroutines.utils.onClick
import kotlinx.android.synthetic.main.fragment_dashboard_supir.*
import kotlinx.android.synthetic.main.header_dashboard_supir.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardAdminActivity : BaseActivity<FragmentDashboardSupirBinding>(),
    DashboardAdminFragmentRvAdapters.Interaction,
    DatePickerDialog.OnDateSetListener {


    companion object{
            const val DashboardAdminActivityRequestCode = 1032

            fun getStaredIntent(activity: AppCompatActivity, requestCode:Int,isActionSearch:Boolean){
            Intent(activity, DashboardAdminActivity::class.java)
                .run {
                    putExtra("isActionSearch",isActionSearch)
                    activity.startActivityForResult(this,requestCode) }
            }

    }


    private var rvAdapters =  DashboardAdminFragmentRvAdapters(this)

    private var selectedDateFrom = Calendar.getInstance()
    private var selectedDateFromString = ""
    private var selectedDateTo = Calendar.getInstance()
    private var selectedDateToString = ""
    private var minimumDate = Calendar.getInstance().apply { set(2022, 1, 1) }
    private var maximumDate = Calendar.getInstance().apply { set(2030, 11, 30) }


    private var lasDateCode = DATE_CODE_FROM;

    override fun getLayoutId(): Int = R.layout.fragment_dashboard_supir

    private lateinit var binding: FragmentDashboardSupirBinding

    private var isAlreadyPickDate = false;
    private var isAdmin:Boolean = true
    private var isActionSearch:Boolean = false

    private var keywordSearch:String ?= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewDataBinding()
        setContentView(binding.root)

        userViewModel.getDataUsersLocal(this)?.let {
            isAdmin = it.rule.equals(Constants.RULE_ADMIN)
            //hide_callendar for user
            if (!isAdmin){
                binding.lyCalendar.container.gone()
            }
        }

        isActionSearch = intent.getBooleanExtra("isActionSearch",false)



        binding.lyCalendar.clickDateFrom.onClick {
            openDatePicker(DATE_CODE_FROM)
        }

        binding.lyCalendar.clickDateTo.onClick {
            openDatePicker(DATE_CODE_TO)

        }
        selectedDateFrom = minimumDate
        //selectedDateTo.set(2030,11,28,23,59,59)
        val mFrom = (selectedDateFrom.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val dFrom = (selectedDateFrom.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')

        val mTo = (selectedDateTo.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val dTo = (selectedDateTo.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
        selectedDateFromString = String.format("%s/%s/%d", dFrom,mFrom,selectedDateFrom.get(Calendar.YEAR))
        selectedDateToString = String.format("%s/%s/%d", dTo,mTo,selectedDateTo.get(Calendar.YEAR))
        binding.lyCalendar.txtDateFrom?.text = selectedDateFromString
        binding.lyCalendar.txtDateUntil?.text = selectedDateToString



        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        //searchview handle
        searchView.apply {
            closeSearch(true)
            setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener{
                override fun onQueryTextChange(newText: String): Boolean {

                    /*keywordSearch = newText
                    closeSearch(false)
                    getListParcel(keywordSearch)*/
                    return false

                }

                override fun onQueryTextCleared(): Boolean {
                  //keywordSearch?.clear()
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    keywordSearch = query
                    closeSearch(false)
                    getListParcel(keywordSearch)
                    return true
                }
            })

            setOnSearchViewListener(object :SimpleSearchView.SearchViewListener{
                override fun onSearchViewClosed() {
                }

                override fun onSearchViewClosedAnimation() {

                }

                override fun onSearchViewShown() {
                    //keywordSearch?.clear()
                    binding.searchView.setQuery(keywordSearch,false)

                }

                override fun onSearchViewShownAnimation() {
                    //keywordSearch?.clear()
                }

            })
        }
        // Adding padding to the animation because of the hidden menu item
        val revealCenter = searchView.revealAnimationCenter
        revealCenter!!.x -= convertDpToPx(40, this@DashboardAdminActivity)

        getListParcel()
    }


    override fun onBackPressed() {
        if (binding.searchView.onBackPressed()){
            return
        }else
            super.onBackPressed()
    }



    private fun openDatePicker(lastDateCode:Int){
        this.lasDateCode = lastDateCode
        val now = if(lasDateCode == DATE_CODE_FROM) selectedDateFrom else selectedDateTo
        val dpd: DatePickerDialog = DatePickerDialog.newInstance(
            this,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )
        dpd.show(supportFragmentManager, "Datepickerdialog");
        /*parentFragmentManager?.let {
            DatePickerDialog
                .setTitle("Pilih Tanggal")
                .setButtonText("Pilih")
                .setMaxDate(maximumDate)
                .setMinDate(minimumDate)
                .setSelectedDate(if(lasDateCode == DATE_CODE_FROM) selectedDateFrom else selectedDateTo)
                .build(this)
                .show(it, "picker")
        }*/
    }

    private fun getListParcel(keyword:String?=null) {

        val mFrom = (selectedDateFrom.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val dFrom = (selectedDateFrom.get(Calendar.DAY_OF_MONTH -1)).toString().padStart(2, '0')
        val selectedDateFromString = String.format("%s/%s/%d", dFrom,mFrom,selectedDateFrom.get(Calendar.YEAR))

        val mTo = (selectedDateTo.get(Calendar.MONTH) - 1).toString().padStart(2, '0')
        val dTo = (selectedDateTo.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
        val selectedDateToString = String.format("%s/%s/%d", dTo,mTo,selectedDateTo.get(Calendar.YEAR))

        uiScope.launch {
            showLoadingDialog()
            if (isAdmin){
                if (!isAlreadyPickDate){
                    if (keyword.isNullOrEmpty())
                        orderViewsModel.getAllOrders().collect { showList(it) }
                    else {
                        orderViewsModel.getAllOrdersFilterKewword(keyword).collect { showList(it) }
                    }
                }else{
                    if (keyword.isNullOrEmpty())
                    orderViewsModel.getAllOrdersFilterDate(selectedDateFrom.time,selectedDateTo.time).collect { showList(it) }
                    else
                        orderViewsModel.getAllOrdersFilterKeywordAndDate(keyword,selectedDateFrom.time,selectedDateTo.time).collect { showList(it) }
                }
            }else{
                if (!isAlreadyPickDate){
                    if (keyword.isNullOrEmpty()){
                        userViewModel.getAuth()?.uid?.let {
                            orderViewsModel.getAllOrdersFilterUser(it).collect { state ->
                                showList(state)
                            }
                        }
                    }

                    else{
                        toast("keyword $keyword")
                        userViewModel.getAuth()?.uid?.let {
                            orderViewsModel.getAllOrdersFilterUserAndKeyword(it,keyword)
                                .collect { showList(it) }
                        }
                    }
                }else{
                    if (keyword.isNullOrEmpty())
                        userViewModel.getAuth()?.uid?.let {
                            orderViewsModel.getAllOrdersFilterUserAndDate(
                                it,
                                selectedDateFrom.time,
                                selectedDateTo.time
                            ).collect { showList(it) }
                        }
                    else
                        userViewModel.getAuth()?.uid?.let {
                            orderViewsModel.getAllOrdersFilterUserDateAndKeyword(
                                keyword,
                                it,
                                selectedDateFrom.time,
                                selectedDateTo.time
                            ).collect { showList(it) }
                        }
                }

            }

        }

    }

    private fun showList(state: State<List<Orders>>) {
        when(state){
            is State.Loading -> showLoadingDialog()
            is State.Success -> {
                rvAdapters.submitList(state.data)
                binding.rvParcel.adapter = rvAdapters
                dismissLoadingDialog()
            }
            is State.Failed -> {
                showErrorDialog(state.message)
                dismissLoadingDialog()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            getListParcel()
        }
    }



    override fun onItemSelected(position: Int, item: Orders) {
        UpdateOrderActivity.getStaredIntent(this,item, UpdateOrderActivity.ShowParcelRequestCode)
    }



    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        isAlreadyPickDate = true
        if(lasDateCode == DATE_CODE_FROM){
            selectedDateFrom.set(year, monthOfYear, dayOfMonth,1,1,1)
            val mFrom = (selectedDateFrom.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val dFrom = (selectedDateFrom.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
            selectedDateFromString = String.format("%s/%s/%d", dFrom,mFrom,selectedDateFrom.get(Calendar.YEAR))

            binding.lyCalendar.txtDateFrom?.text = selectedDateFromString

        } else{
            selectedDateTo.set(year, monthOfYear, dayOfMonth,23,59,59)
            val mTo = (selectedDateTo.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
            val dTo = (selectedDateTo.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
            selectedDateToString = String.format("%s/%s/%d", dTo,mTo,selectedDateTo.get(Calendar.YEAR))
            binding.lyCalendar.txtDateUntil?.text = selectedDateToString


        }
        getListParcel()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        if (isActionSearch) {
            binding.lyCalendar.container.gone()
            binding.searchView.visible()
            inflater.inflate(R.menu.search_menu,menu)
            val itemSearch = menu?.findItem(R.id.action_search)
            itemSearch?.let {searchView.setMenuItem(it)
            return true}
        } else{
            binding.lyCalendar.container.visible()
            binding.searchView.gone()
        }


        return false
    }

}
