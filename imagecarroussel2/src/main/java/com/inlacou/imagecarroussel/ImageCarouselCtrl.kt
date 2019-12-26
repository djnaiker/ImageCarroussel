package com.inlacou.imagecarroussel

import android.os.Handler
import com.inlacou.imagecarroussel.model.ImageCarouselMdl


class ImageCarouselCtrl(val view: ImageCarousel, var model: ImageCarouselMdl) {

	init {
		val handler = Handler()
		val delay = (model.autoSwipe.interval*1000).toLong() //milliseconds

		handler.postDelayed(object : Runnable {
			override fun run() {
				view.shouldLoadNextPage()
				handler.postDelayed(this, delay)
			}
		}, delay)
	}


	fun onClick(position: Int) {
		model.onItemClick?.invoke(position%model.urls.size)
	}



	fun onPageShown(position: Int) {
		model.onPageShown?.invoke(position%model.urls.size)
	}

}