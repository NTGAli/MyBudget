package com.ntg.core.network

import android.util.Log
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.ntg.core.network.model.Result

class NetworkBoundResource @Inject constructor(){
    suspend fun<ResultType> downloadData(ioDispatcher: CoroutineDispatcher,api : suspend () -> Response<ResultType>): Flow<Result<ResultType>> {
        return withContext(ioDispatcher) {
            flow {
                emit(Result.Loading(true))
                val response:Response<ResultType> = api()
                emit(Result.Loading(false))
                Log.d("NetworkBoundResource", "${response.body()}")
                if (response.isSuccessful){
                    Log.d("NetworkBoundResource", "${response.body()}")
                    response.body()?.let {
                        emit(Result.Success(data = it))
                    }?: emit(Result.Error(message = "Unknown error occurred", code = 0))
                }else{
                    emit(Result.Error(message = parserErrorBody(response.errorBody()), code = response.code()))
                }
            }.catch { error->
                emit(Result.Loading(false))
                delay(5)
                emit(Result.Error(message = message(error), code = code(error)))
            }
        }
    }
    private fun parserErrorBody(response: ResponseBody?):String{
        return response?.let {
            val errorMessage = JsonParser.parseString(it.string()).asJsonObject["message"].asString
            errorMessage.ifEmpty { "Whoops! Something went wrong. Please try again." }
            errorMessage
        }?:"Whoops! Unknown error occurred. Please try again"
    }
    private fun message(throwable: Throwable?):String{
        Log.d("NetworkBoundResource", "message: ${throwable?.localizedMessage}")
        when (throwable) {
            is SocketTimeoutException -> return "Whoops! Connection time out. Please try again"
            is IOException -> return "Whoops! No Internet Connection. Please try again"
            is HttpException -> return try {
                val errorJsonString = throwable.response()?.errorBody()?.string()
                val errorMessage = JsonParser.parseString(errorJsonString).asJsonObject["message"].asString
                errorMessage.ifEmpty { "Whoops! Something went wrong. Please try again." }
            }catch (e:Exception){
                "Whoops! Unknown error occurred. Please try again"
            }
        }
        return "Whoops! Unknown error occurred. Please try again"
    }
    private fun code(throwable: Throwable?):Int{
        return if (throwable is HttpException) (throwable).code()
        else  0
    }
}