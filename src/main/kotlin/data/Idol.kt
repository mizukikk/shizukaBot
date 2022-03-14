package data


import com.google.gson.annotations.SerializedName

data class Idol(
    @SerializedName("idolType")
    val idolType: Int,
    @SerializedName("name")
    val name: String
)