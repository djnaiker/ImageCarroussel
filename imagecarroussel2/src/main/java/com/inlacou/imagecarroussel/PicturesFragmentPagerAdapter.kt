package com.inlacou.imagecarroussel

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.PagerAdapter
import com.inlacou.imagecarroussel.utilities.Constans.CURRENT_PAGE
import com.inlacou.imagecarroussel.utilities.Constans.MAX_PAGES
import com.inlacou.imagecarroussel.utilities.Constans.POSITION_DISPLAY
import com.inlacou.imagecarroussel.utilities.Constans.SHOW_TOP_SHADOW
import com.inlacou.imagecarroussel.utilities.Constans.URL
import com.inlacou.imagecarroussel.model.ItemElement
import com.inlacou.imagecarroussel.types.PositionDisplayMode

import java.util.ArrayList

/**
 * Created by inlacou on 3/05/16.
 */
class PicturesFragmentPagerAdapter @JvmOverloads
constructor(
		fragmentManager: FragmentManager,
		private val urls: ArrayList<ItemElement>,
		private val positionDisplay: PositionDisplayMode,
		private val showTopShadow: Boolean,
		private val infinite: Boolean,
		private val onClick: ((Int) -> Unit)? = null)
	: android.support.v4.app.FragmentStatePagerAdapter(fragmentManager) {

	private val pageCount: Int

	companion object {
		const val INITIAL_MAX_VALUE = 1000
	}

	var maxValue: Int = 0
		private set

	init {
		for (i in urls.indices.reversed()) {
			if (urls[i].url.isNullOrEmpty()) urls.removeAt(i)
		}
		pageCount = urls.size
		calculateMaxValue()
	}

	override fun notifyDataSetChanged() {
		calculateMaxValue()
		super.notifyDataSetChanged()
	}

	private fun calculateMaxValue(){
		maxValue = if(pageCount>INITIAL_MAX_VALUE){
			pageCount
		}else{
			var maxVal = pageCount
			while (maxVal< INITIAL_MAX_VALUE){
				maxVal += pageCount
			}
			maxVal
		}
	}

	/** This method will be invoked when a page is requested to create  */
	override fun getItem(position: Int): Fragment {
		val virtualPos = if(infinite) position%pageCount else position
		val myFragment = PageImageFragment()
		val data = Bundle()
		data.putInt(CURRENT_PAGE, virtualPos)
		data.putInt(MAX_PAGES, pageCount)
		data.putBoolean(SHOW_TOP_SHADOW, showTopShadow)
		data.putInt(POSITION_DISPLAY, positionDisplay.ordinal)
		data.putSerializable(URL, urls[virtualPos])
		myFragment.onClickListener = { onClick?.invoke(virtualPos) }
		myFragment.arguments = data
		return myFragment
	}

	/** Returns the number of pages  */
	override fun getCount(): Int {
		return if(infinite) {
			if(pageCount==0) pageCount else maxValue
		}else {
			pageCount
		}
	}

	override fun getItemPosition(`object`: Any): Int {
		return PagerAdapter.POSITION_NONE
	}
}
