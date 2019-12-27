package com.inlacou.imagecarroussel.model

import android.support.v4.app.FragmentManager
import com.inlacou.imagecarroussel.types.ItemType
import com.inlacou.imagecarroussel.types.PositionDisplayMode

data class ImageCarouselMdl @JvmOverloads constructor(
		val fragmentManager: FragmentManager?,
		val urls: List<ItemElement>,
		val positionDisplay: PositionDisplayMode = PositionDisplayMode.NONE,
		val showTopShadow: Boolean = false,
		val autoSwipe: AutoSwipeMode = AutoSwipeMode(),
		/**
		 * @return true if handled, false if not.
		 */
		val onItemClick: ((Int) -> Boolean)? = null,
		val onPageShown: ((Int) -> Unit)? = null,
		val pagePaddingLeft: Int? = null,
		val pagePaddingRight: Int? = null,
		val pageMargin: Int? = null,
		val enableSound: Boolean = false
)