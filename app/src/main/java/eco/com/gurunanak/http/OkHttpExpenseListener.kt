package eco.com.gurunanak.http

/**
 * Created by ss22493 on 14-07-2016.
 */
interface OkHttpExpenseListener {


    fun onOkHttpExpnseResponse(callResponse: String, pageId: Int, position: Int)

    fun onOkHttpExpnseError(error: String)

}
