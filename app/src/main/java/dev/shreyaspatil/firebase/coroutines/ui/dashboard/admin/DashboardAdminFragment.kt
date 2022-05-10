package dev.shreyaspatil.firebase.coroutines.ui.dashboard.admin


import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog

import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment.Companion.DATE_CODE_FROM
import dev.shreyaspatil.firebase.coroutines.ui.history.HistoryFragment.Companion.DATE_CODE_TO
import dev.shreyaspatil.firebase.coroutines.R
import dev.shreyaspatil.firebase.coroutines.databinding.FragmentDashboardSupirBinding
import dev.shreyaspatil.firebase.coroutines.model.Orders
import dev.shreyaspatil.firebase.coroutines.base.BaseFragment
import dev.shreyaspatil.firebase.coroutines.model.Users
import dev.shreyaspatil.firebase.coroutines.ui.aktivitas.AktivitasHewanActivity
import dev.shreyaspatil.firebase.coroutines.ui.order.detail.CreateOrderActivity
import dev.shreyaspatil.firebase.coroutines.ui.order.detail.UpdateOrderActivity
import dev.shreyaspatil.firebase.coroutines.utils.Constants
import dev.shreyaspatil.firebase.coroutines.utils.State
import dev.shreyaspatil.firebase.coroutines.utils.getCallerActivity
import dev.shreyaspatil.firebase.coroutines.utils.onClick
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.toast
import java.util.*





/**
 * A simple [Fragment] subclass.
 */
class DashboardAdminFragment : BaseFragment<FragmentDashboardSupirBinding>(),
    DashboardAdminFragmentRvAdapters.Interaction,
    DatePickerDialog.OnDateSetListener {


    companion object{
        fun directionToMeFromClient(activity: FragmentActivity){
            val mainNavView = activity.findViewById<View>(R.id.client_fragment)
            Navigation.findNavController(mainNavView).navigate(R.id.dashboard_client)
        }

        fun directionToMeFromAdmin(activity: FragmentActivity){
            val mainNavView = activity.findViewById<View>(R.id.admin_fragment)
            Navigation.findNavController(mainNavView).navigate(R.id.dashboard_admin)
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
    private var users:Users? = null;



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardSupirBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




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

        binding.toolbar.gone()
        binding.searchView.gone()

        setHasOptionsMenu(true)




        usersviewModel.getDataUsersLocal(context)?.let {
            isAdmin = it.rule.equals(Constants.RULE_ADMIN)
            users = it
            getListParcel()
        }







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
        dpd.show(parentFragmentManager, "Datepickerdialog");
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

    private fun getListParcel() {

        val mFrom = (selectedDateFrom.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val dFrom = (selectedDateFrom.get(Calendar.DAY_OF_MONTH -1)).toString().padStart(2, '0')
        val selectedDateFromString = String.format("%s/%s/%d", dFrom,mFrom,selectedDateFrom.get(Calendar.YEAR))

        val mTo = (selectedDateTo.get(Calendar.MONTH) - 1).toString().padStart(2, '0')
        val dTo = (selectedDateTo.get(Calendar.DAY_OF_MONTH)).toString().padStart(2, '0')
        val selectedDateToString = String.format("%s/%s/%d", dTo,mTo,selectedDateTo.get(Calendar.YEAR))

        uiScope.launch {

                if (!isAlreadyPickDate){
                    if (isAdmin)
                        orderViewsModel.getAllOrders().collect { state -> showList(state) }
                    else
                    { users?.uid?.let {
                            orderViewsModel.getAllOrdersFilterUser(it).collect { state ->
                                showList(state)
                            }
                        }
                    }
                }else{
                    if (isAdmin) orderViewsModel.getAllOrdersFilterDate(selectedDateFrom.time,selectedDateTo.time).collect { state ->
                        showList(state)
                    }
                    else{
                        users?.uid?.let {
                            orderViewsModel.getAllOrdersFilterUserAndDate(it,selectedDateFrom.time,selectedDateTo.time).collect { state -> showList(state) }
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
            when(requestCode){
                UpdateOrderActivity.ShowParcelRequestCode -> {
                    getListParcel()
                }
            }
        }
    }



    override fun onItemSelected(position: Int, item: Orders) {
        UpdateOrderActivity.getStaredIntent(activity,item,UpdateOrderActivity.ShowParcelRequestCode)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu_client,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_search ->{
                DashboardAdminActivity.getStaredIntent(getActivity() as AppCompatActivity,DashboardAdminActivity.DashboardAdminActivityRequestCode,true)
            }
            else -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
