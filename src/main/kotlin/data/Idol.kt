package data


import com.google.gson.annotations.SerializedName

data class Idol(
    @SerializedName("idolType")
    val idolType: Int,
    @SerializedName("name")
    val name: String
){
    object IdolType{
        const val AS = 0
        const val PRINCESS = 1
        const val FAIRY = 2
        const val ANGEL = 3
    }
}