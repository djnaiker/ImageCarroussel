package com.inlacou.imagecarroussel.model

import com.inlacou.imagecarroussel.types.ItemType
import java.io.Serializable

class ItemElement : Serializable{

	var url: String? = null
	var video : String? = null
	var type: ItemType = ItemType.IMAGE

	constructor(url : String? = null){
		this.url = url
	}


 	constructor(url : String? = null , type: ItemType = ItemType.IMAGE){
		this.url = url
		this.type = type
	}

	constructor(url : String?,  video : String?){
		this.url = url
		this.video = video
		this.type = ItemType.VIDEO
	}

}

