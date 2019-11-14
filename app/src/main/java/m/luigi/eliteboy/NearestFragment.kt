package m.luigi.eliteboy


import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_nearest.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.PropertyAdapter
import m.luigi.eliteboy.adapters.SystemsSuggestionsAdapter
import m.luigi.eliteboy.elitedangerous.companionapi.EDCompanionApi
import m.luigi.eliteboy.elitedangerous.edsm.EDSMApi
import m.luigi.eliteboy.util.hideWithAnimation
import m.luigi.eliteboy.util.onDefault
import m.luigi.eliteboy.util.onIO

class NearestFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var currentSystem = ""
    private var systemsSuggestions = ArrayList<String>()
    var searchJob: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        launch {
            onIO {
                currentSystem = EDCompanionApi.getLastPosition()
            }
            refSystem.setText(currentSystem)
            (activity as MainActivity).mainToolbar.title = "Nearest (50 ly)"
        }


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch {

            searchTypes.layoutManager =
                LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
            searchTypes.adapter =
                PropertyAdapter(
                    EDSMApi.SearchType.values().map { it.type } as ArrayList<String>,
                    this@NearestFragment,
                    view.context
                )

            val arrayAdapter = SystemsSuggestionsAdapter(
                view.context,
                R.layout.support_simple_spinner_dropdown_item,
                systemsSuggestions
            )
            with(refSystem) {
                threshold = 3
                addTextChangedListener {
                    nearestSpinKit.hideWithAnimation()
                    searchJob.cancel()
                    searchJob = this@NearestFragment.launch {
                        refSystem.clearListSelection()
                        refSystem.dismissDropDown()
                        currentSystem = it.toString()
                        if (currentSystem.length > 2) {
                            nearestSpinKit.visibility = View.VISIBLE
                            arrayAdapter.clear()
                            arrayAdapter.notifyDataSetInvalidated()
                            onDefault {
                                systemsSuggestions.clear()
                                systemsSuggestions.addAll(
                                    EDSMApi.findSystemsByName(
                                        currentSystem,
                                        showInformation = false,
                                        showCoordinates = false,
                                        limit = 5
                                    ).map { it.name!! }
                                )
                            }
                            arrayAdapter.notifyDataSetChanged()
                            nearestSpinKit.hideWithAnimation()
                            if (refSystem.hasFocus()) {
                                refSystem.showDropDown()
                            }
                        }
                    }
                }
                setAdapter(arrayAdapter)
                this.setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        clearFocus()
                        if (systemsSuggestions.isNotEmpty()) {
                            setText(systemsSuggestions[0])
                        }
                        view.let { v ->
                            val imm = getSystemService(view.context, InputMethodManager::class.java)
                            imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        }
                    }
                    true
                }

                setOnItemClickListener { _, _, position, _ ->
                    clearFocus()
                    setText(systemsSuggestions[position])
                    view.let { v ->
                        val imm = getSystemService(view.context, InputMethodManager::class.java)
                        imm?.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
    }
}