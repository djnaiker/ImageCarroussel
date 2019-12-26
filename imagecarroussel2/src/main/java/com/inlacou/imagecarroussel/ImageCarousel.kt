package com.inlacou.imagecarroussel

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.inlacou.imagecarroussel.model.ImageCarouselMdl
import android.os.StrictMode.setThreadPolicy
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.StrictMode


class ImageCarousel @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: FrameLayout(context, attrs, defStyleAttr) {

	private var viewPager: ViewPager? = null
	private var pagerAdapter: PicturesFragmentPagerAdapter? = null

	var model: ImageCarouselMdl = ImageCarouselMdl(null, listOf())
		set(value) {
			field = value
			controller.model = value
			populate()
		}
	private lateinit var controller: ImageCarouselCtrl

	init {
		this.initialize()
		setListeners()
		populate()
	}

	private fun initialize() {
		val rootView = View.inflate(context, R.layout.view_imagecarousel, this)
		viewPager = findViewById(R.id.pager)
		initialize(rootView)

		val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
		StrictMode.setThreadPolicy(policy)
	}

	private fun initialize(view: View) {
		controller = ImageCarouselCtrl(view = this, model = model)
	}

	private fun populate() {
		viewPager?.clipToPadding = false

		/** Instantiating FragmentPagerAdapter  */
		model.fragmentManager?.let { fragmentManager ->
			pagerAdapter = PicturesFragmentPagerAdapter(
					fragmentManager = fragmentManager,
					urls = model.urls.toArrayList(),
					positionDisplay = model.positionDisplay,
					showTopShadow = model.showTopShadow,
					infinite = model.autoSwipe.continuous) {
				controller.onClick(it)
			}

			viewPager?.setPadding(model.pagePaddingLeft ?: 0, 0, model.pagePaddingRight ?: 0, 0)
			model.pageMargin?.let { viewPager?.pageMargin = it }

			/** Setting the pagerAdapter to the pager object  */
			viewPager?.adapter = pagerAdapter
		}
	}

	private fun setListeners() {
		viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
			override fun onPageScrollStateChanged(state: Int) {}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

			override fun onPageSelected(position: Int) {
				controller.onPageShown(position)
			}
		})
	}

	fun shouldLoadNextPage() {
		if(!model.autoSwipe.active || (!model.autoSwipe.continuous && viewPager?.currentItem==model.urls.size-1)){
			/*Do nothing*/
			return
		}
		pagerAdapter?.let {
			if(viewPager?.currentItem==it.maxValue-1){
				viewPager?.setCurrentItem(0, false)
				return
			}
		}
		moveForward()
	}

	private fun moveForward(){
		viewPager?.arrowScroll(View.FOCUS_RIGHT)
	}

	fun stopAutoSwipe(){
		model.autoSwipe.active = false
	}

	fun startAutoSwipe(){
		model.autoSwipe.active = true
	}

	fun switchAutoSwipeStatus(){
		model.autoSwipe.active = !model.autoSwipe.active
	}

	private fun <T> List<T>.toArrayList(): ArrayList<T>{
		val result = ArrayList<T>()
		this.forEach { result.add(it) }
		return result
	}

}