package m.luigi.eliteboy


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import m.luigi.eliteboy.adapters.NewsPageAdapter
import m.luigi.eliteboy.data.News
import m.luigi.eliteboy.util.hideWithAnimation
import m.luigi.eliteboy.util.onDefault
import m.luigi.eliteboy.util.onIO
import m.luigi.eliteboy.util.runWhenOnline
import java.net.URL

class NewsFragment : Fragment(), CoroutineScope by CoroutineScope(Dispatchers.Main) {

    private var newsList = ArrayList<News>()
    private var getNewsJob: Job = Job()
    private var initLayoutJob: Job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).mainToolbar.title = "News"
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onResume() {
        super.onResume()
        getNewsJob = launch {
            val newsJson = runWhenOnline {
                onIO {
                    URL("https://cms.elitedangerous.com/api/galnet?_format=json").readText()
                }
            }
            newsJson?.let {
                onDefault {
                    var i = 0
                    val array = JsonParser.parseString(it).asJsonArray
                    while (i < 50 && i < array.size()) {
                        array[i].asJsonObject.let {
                            newsList.add(
                                News(
                                    it["title"].asString,
                                    it["body"].asString
                                        .removePrefix("<p>")
                                        .removeSuffix("</p>\n")
                                        .replace("<br />", "")
                                        .replace("<p>", "")
                                        .replace("</p>", ""),
                                    it["image"].asString,
                                    it["date"].asString
                                )
                            )
                        }
                        i++
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLayoutJob = launch {
            newsSpinKit.visibility = View.VISIBLE
            (activity as MainActivity).initJob.join()
            getNewsJob.join()
            newsSpinKit.hideWithAnimation()
            newsPager.adapter = NewsPageAdapter(newsList, childFragmentManager)
            newsDots.attachToViewPager(newsPager)
        }
    }

    override fun onPause() {
        super.onPause()
        getNewsJob.cancel()
        initLayoutJob.cancel()
    }
}
