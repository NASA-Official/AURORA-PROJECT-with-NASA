package com.nassafy.aro.domain.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.nassafy.aro.data.dto.UserData
import com.nassafy.aro.domain.api.MainApi
import com.nassafy.aro.util.NetworkResult
import javax.inject.Inject

class MainRepository @Inject constructor(private val mainApi: MainApi) {

    private val _logoutNetworkResultLiveData = MutableLiveData<NetworkResult<Unit>>()
    val logoutNetworkResultLiveData: LiveData<NetworkResult<Unit>> = _logoutNetworkResultLiveData

    private val _userInfo = MutableLiveData<NetworkResult<UserData>>()
    val userInfo: LiveData<NetworkResult<UserData>> get() = _userInfo

    private val _getAlarmOptionNetworkResultLiveData = MutableLiveData<NetworkResult<Boolean>>()
    val getAlarmOptionNetworkResultLiveData: LiveData<NetworkResult<Boolean>> get() = _getAlarmOptionNetworkResultLiveData

    private val _getAuroraDisplayOptionNetworkResultLiveData =
        MutableLiveData<NetworkResult<Boolean>>()
    val getAuroraDisplayOptionNetworkResultLiveData: LiveData<NetworkResult<Boolean>> get() = _getAuroraDisplayOptionNetworkResultLiveData

    private val _getCloudDisplayOptionNetworkResultLiveData =
        MutableLiveData<NetworkResult<Boolean>>()
    val getCloudDisplayOptionNetworkResultLiveData: LiveData<NetworkResult<Boolean>> get() = _getCloudDisplayOptionNetworkResultLiveData

    suspend fun logout(grantType: String, accessToken: String, refreshToken: String) {
        val response = mainApi.logout(
            JsonObject().apply {
                addProperty("grantType", grantType)
                addProperty("accessToken", accessToken)
                addProperty("refreshToken", refreshToken)
            }
        )
        _logoutNetworkResultLiveData.postValue(NetworkResult.Loading())
        try {
            when {
                response.isSuccessful -> {
                    _logoutNetworkResultLiveData.postValue(
                        NetworkResult.Success(Unit)
                    ) // End of postValue
                } // End of response.isSuccessful
                response.errorBody() != null -> {
                    _logoutNetworkResultLiveData.postValue(
                        NetworkResult.Error(
                            response.errorBody()!!.string()
                        )
                    )
                } // End of response.errorBody
            } // End of when
        } catch (e: java.lang.Exception) {
            Log.e("ssafy", "getServerCallTest: ${e.message}")
        }
    }

    suspend fun getUserInfo(fcmToken: String) {
        Log.d("ssafy/getInfo", fcmToken)
        val response = mainApi.getUserInfo(JsonObject().apply {
            addProperty("fcmToken", fcmToken)
        })
        _userInfo.postValue(NetworkResult.Loading())

        try {
            when {
                response.isSuccessful -> {
                    _userInfo.postValue(
                        NetworkResult.Success(
                            response.body()!!
                        )
                    ) // End of postValue
                } // End of response.isSuccessful
                response.errorBody() != null -> {
                    _userInfo.postValue(NetworkResult.Error(response.errorBody()!!.string()))
                } // End of response.errorBody
            } // End of when
        } catch (e: java.lang.Exception) {
            Log.e("ssafy", "getServerCallTest: ${e.message}")
        }
    } // End of getUserInfo

    suspend fun getAlarmOption() {
        val response = mainApi.getAlarmOption()
        _getAlarmOptionNetworkResultLiveData.postValue(NetworkResult.Loading())

        try {
            when {
                response.isSuccessful -> {
                    _getAlarmOptionNetworkResultLiveData.postValue(
                        NetworkResult.Success(response.body()!!)
                    ) // End of postValue
                } // End of response.isSuccessful
                response.errorBody() != null -> {
                    _getAlarmOptionNetworkResultLiveData.postValue(
                        NetworkResult.Error(
                            response.errorBody()!!.string()
                        )
                    )
                } // End of response.errorBody
            } // End of when
        } catch (e: java.lang.Exception) {
            Log.e("ssafy", "getServerCallTest: ${e.message}")
        }
    } // End of getAlarmOption

    suspend fun getAuroraDisplayOption() {
        val response = mainApi.getAuroraDisplayOption()
        _getAuroraDisplayOptionNetworkResultLiveData.postValue(NetworkResult.Loading())

        try {
            when {
                response.isSuccessful -> {
                    _getAuroraDisplayOptionNetworkResultLiveData.postValue(
                        NetworkResult.Success(response.body()!!)
                    ) // End of postValue
                } // End of response.isSuccessful
                response.errorBody() != null -> {
                    _getAuroraDisplayOptionNetworkResultLiveData.postValue(
                        NetworkResult.Error(
                            response.errorBody()!!.string()
                        )
                    )
                } // End of response.errorBody
            } // End of when
        } catch (e: java.lang.Exception) {
            Log.e("ssafy", "getServerCallTest: ${e.message}")
        }
    } // End of getAuroraDisplayOption

    suspend fun getCloudDisplayOption() {
        val response = mainApi.getCloudDisplayOption()
        _getCloudDisplayOptionNetworkResultLiveData.postValue(NetworkResult.Loading())

        try {
            when {
                response.isSuccessful -> {
                    _getCloudDisplayOptionNetworkResultLiveData.postValue(
                        NetworkResult.Success(response.body()!!)
                    ) // End of postValue
                } // End of response.isSuccessful
                response.errorBody() != null -> {
                    _getCloudDisplayOptionNetworkResultLiveData.postValue(
                        NetworkResult.Error(
                            response.errorBody()!!.string()
                        )
                    )
                } // End of response.errorBody
            } // End of when
        } catch (e: java.lang.Exception) {
            Log.e("ssafy", "getServerCallTest: ${e.message}")
        }
    } // End of getCloudDisplayOption


} // End of MainRepository class
