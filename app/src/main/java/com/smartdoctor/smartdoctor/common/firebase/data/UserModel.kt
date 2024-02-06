package com.donationinstitutions.donationinstitutions.common.firebase.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserModel(
    var email:String? = "",
    var password:String? ="",
    var name:String? ="",
    var userId :String? ="",
    var uri: Uri?=null,
    var profileUrl:String? ="",
    var mobile: String? = "",
    var userType:String?="",
    var city:String? ="",
    var street:String? ="",
    var building:String? ="",
    var block:String? ="",
    var avenues:String? = "",
    var floor:String?="",
    var licenceUrl:String? ="",
    var licenceUri:Uri?=null,
    var validityLicenceUrl:String?="",
    var validityLicenceUri:Uri?=null,
    var whatsApp:String?="",
    var twitter:String?="",
    var insta:String?="",
    var about:String?="",
    var read:Boolean?= false,
    var userState: String?=UserState.Accepted.value
) : Parcelable {
    fun getAddress(): String {

        var address = "City:$city, Block:$block, St:$street, Building:$building"
        if (floor.isNullOrEmpty().not()) {
            address = "$address, Floor:$floor"
        }

        if (avenues.isNullOrEmpty().not()) {
            address = "$address, Avenue:$avenues"
        }

        return address
    }
}

data class DonationCategory(var name:String,var isSelected:Boolean=false)

val donationCategoryList = listOf(DonationCategory("Teach a child"),
    DonationCategory("Input Building a children's hospital"),
    DonationCategory("Treat a child under the age of 18"),
    DonationCategory("Orphan"),
    DonationCategory("Other")
)

const val IndividualDonation = "Individual donation"
const val GroupDonation ="Group donation"

@Parcelize
data class DonationModel(
    var name: String? = "",
    var listOfPhotos:MutableList<String?>?= mutableListOf(),
    var listOfAchievement:MutableList<String?>?= mutableListOf(),
    var listOfUris:MutableList<Uri>?=null,
    var address: String? = "",
    var donationType: String? = "",
    var desc: String? = "",
    var date: String? = "",
    var link: String? = "",
    var value: Double? = 0.0,
    var donationValue: Double? = 0.0,  // for GroupDonation
    var numberOfBeneficiaries: String? = "",
    var donationNumber: Int? = 0,  // for IndividualDonation
    var listOfCategories:MutableList<String?>?= mutableListOf(),
    var hash:String?="",
    var creator:UserModel?= null,
    var isEffort:Boolean?= false,
    var listOfDonors:MutableList<Donor?>?= mutableListOf()
):Parcelable

@Parcelize
data class Donor(
    var donor:UserModel?=null,
    var numberOfDonation:Int?=1,
    var valueOfDonation:Double?=0.0
) : Parcelable

@Parcelize
data class NotificationModel(
    val donation :DonationModel?=null,
    val hash:String?=null,
    val fromCharity:UserModel?=null,
    val toUserId:String?=null,
    val amount:Double?=null,
    var read:Boolean?= false
) : Parcelable



enum class UserType(val value:String){
    HealthCenter("HealthCenter"),Doctor("Doctor"),User("User")
}

enum class UserState(val value:String){
    Pending("Pending"),Accepted("Accepted"),Rejected("Rejected"),Deleted("Deleted")
}


@Parcelize
data class ProductModel(
    var name:String?=null,
    var imageUrl:String?=null,
    var code:String?=null,
    var desc:String?=null,
    var amount:Int?=null,
    var uri:Uri?=null,
    var hash:String?=null,
    var percentage:Double?=null,
    var listOfCategories:MutableList<CategoryModel> = mutableListOf()
) : Parcelable{
    fun getPriceKWD():String= "$percentage%"
}

@Parcelize
data class CategoryModel(
    var name:String?=null,
    var url:String?=null,
    var hash:Long?=null,
    var uri:Uri?=null,
) : Parcelable{

    var selected = false
}


@Parcelize
data class BranchModel(
    var name:String?=null,
    var district:String?="",
    var plot:String?="",
    var street:String?="",
    var building:String?="",
    var floor :String?="",
    var lat:Double?=null,
    var long:Double?=null,
    var hash:String?=null
) : Parcelable{
    fun getAddress(): String {
        return if (floor.isNullOrEmpty()) {
            "City:$district, Block:$plot, St:$street, Building:$building"
        } else {
            "City:$district, Block:$plot, St:$street, Building:$building, Floor:$floor"
        }
    }
}

@Parcelize
data class ProductTakenModel(
    var product: ProductModel?=null,
    var store: UserModel?=null,
    var amount: Int?=null,
    var price:Double?=null,
    var branch: BranchModel?=null,
    var hash: String?=null
) : Parcelable{
    fun getPriceKWD():String= "$price KWD"

}




@Parcelize
data class ProductsToStores(
    var store: UserModel?=null,
    var listOfProducts:MutableList<ProductTakenModel>?= mutableListOf()
) : Parcelable

@Parcelize
data class OrderModel(
    val productTakenModel: ProductTakenModel?= null,
    val client: UserModel?=null,
    val hash:String?=null,
    val date:String?=null,
    val long:Long?=null,
    val amount: Int?=null,
    var state:Boolean?=false,
    val totalAmount:Double?= null
) : Parcelable{
    var isShow:Boolean = false
}


@Parcelize
data class SelectedLocation(
    var lat:Double?=null,
    var long:Double?=null,
    var address:String?=null
) : Parcelable



