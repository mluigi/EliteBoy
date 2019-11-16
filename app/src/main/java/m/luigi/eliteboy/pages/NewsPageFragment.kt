package m.luigi.eliteboy.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.nostra13.universalimageloader.core.DisplayImageOptions
import kotlinx.android.synthetic.main.fragment_news_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import m.luigi.eliteboy.MainActivity
import m.luigi.eliteboy.R
import m.luigi.eliteboy.data.News
import m.luigi.eliteboy.util.getNewsImageUrl
import m.luigi.eliteboy.util.onIO

class NewsPageFragment(private var news: News) : Fragment(),
    CoroutineScope by CoroutineScope(
        Dispatchers.Main
    ) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launch {
            titleTextView.text = news.title
            bodyTextView.text = news.body
            dateTextView.text = news.date
            (activity as MainActivity).initJob.join()

            // It should work without the try-catch, but it crashes on running the first time
            // like this it works
            try {
                (activity as MainActivity).imageLoader.displayImage(
                    getNewsImageUrl(news.image),
                    newsImage,
                    DisplayImageOptions.Builder().showImageOnFail(onIO {
                        (activity as MainActivity).imageLoader.loadImageSync(
                            getNewsImageUrl("NewsImageSidewinderExploring")
                        ).toDrawable(resources)
                    }
                    ).build()
                )
            } catch (e: Exception) {
                (activity as MainActivity).imageLoader.displayImage(
                    getNewsImageUrl("NewsImageSidewinderExploring"),
                    newsImage
                )
            }
        }
    }
}