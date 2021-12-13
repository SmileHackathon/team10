package com.example.smiline.util.json

object Manaba {

    fun createJson(id:String = "",passowrd:String):String
            ="{" +
            "  \"userid\": \"${id}\"," +
            "  \"password\": \"${passowrd}\"" +
            "}"
}