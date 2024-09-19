package com.ntg.core.network.util

import android.net.http.HttpException
import com.ntg.core.network.util.Call.ERROR_KEY
import com.ntg.core.network.util.Call.MESSAGE_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException

suspend inline fun <T> safeApiCall(emitter: RemoteErrorEmitter? = null, crossinline responseFunction: suspend () -> T): T? {
    return try{
        val response = withContext(Dispatchers.IO){ responseFunction.invoke() }
        response
    }catch (e: HttpException) {
//        emit(NetworkResult.Error(message = "HttpException ::: ${e.message}"))
        withContext(Dispatchers.Main){
            emitter?.onError("ERR")
        }
        null
    } catch (e: IOException) {
        withContext(Dispatchers.Main){
            emitter?.onError("ERR")
        }
        null
//        emit(NetworkResult.Error(message = "IOException ::: ${e.message} --- ${e.printStackTrace()}"))
    } catch (e: Exception) {
        withContext(Dispatchers.Main){
            emitter?.onError(e.message.orEmpty())
        }
        null
//        emit(NetworkResult.Error(message = "Exception ::: ${e.message}"))
    }catch (e: Exception){
        withContext(Dispatchers.Main){
            e.printStackTrace()
//            Log.e("ApiCalls", "Call error: ${e.localizedMessage}", e.cause)
            when(e){
                is HttpException -> {
                    val body = e.message
                    emitter?.onError("ERR")
                }
                is SocketTimeoutException -> emitter?.onError(ErrorType.TIMEOUT)
                is IOException -> emitter?.onError(ErrorType.NETWORK)
                else -> emitter?.onError(ErrorType.UNKNOWN)
            }
        }
        null
    }
}

interface RemoteErrorEmitter {
    fun onError(msg: String)
    fun onError(errorType: ErrorType)
}

enum class ErrorType {
    NETWORK, // IO
    TIMEOUT, // Socket
    UNKNOWN //Anything else
}


object Call{
    const val MESSAGE_KEY = "message"
    const val ERROR_KEY = "error"
}

fun getErrorMessage(responseBody: ResponseBody?): String {
    return try {
        val jsonObject = JSONObject(responseBody!!.string())
        when {
            jsonObject.has(Call.MESSAGE_KEY) -> jsonObject.getString(MESSAGE_KEY)
            jsonObject.has(ERROR_KEY) -> jsonObject.getString(ERROR_KEY)
            else -> "Something wrong happened"
        }
    } catch (e: Exception) {
        "Something wrong happened"
    }
}